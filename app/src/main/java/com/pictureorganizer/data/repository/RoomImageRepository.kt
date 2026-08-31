package com.pictureorganizer.data.repository

import com.pictureorganizer.data.local.converter.Converters
import com.pictureorganizer.data.local.dao.ImageDao
import com.pictureorganizer.data.mapper.tagsForStatus
import com.pictureorganizer.data.mapper.toEntity
import com.pictureorganizer.data.mapper.toListItem
import com.pictureorganizer.data.mapper.toStorage
import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus
import com.pictureorganizer.util.file.AppFileManager
import com.pictureorganizer.util.image.ImageTagMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomImageRepository(
    private val imageDao: ImageDao,
    private val fileManager: AppFileManager,
) : ImageRepository {
    private val converters = Converters()

    override fun observeItems(status: ImageStatus): Flow<List<ImageListItem>> =
        imageDao.observeByStatus(status.toStorage()).map { entities ->
            entities.map { it.toListItem() }
        }

    override fun observeItem(id: String): Flow<ImageListItem?> = imageDao.observeById(id).map { it?.toListItem() }

    override suspend fun getItems(status: ImageStatus): List<ImageListItem> =
        imageDao.getByStatus(status.toStorage()).map { it.toListItem() }

    override suspend fun getItem(id: String): ImageListItem? = imageDao.findById(id)?.toListItem()

    override suspend fun moveItems(
        ids: Set<String>,
        from: ImageStatus,
        to: ImageStatus,
    ) {
        if (ids.isEmpty() || from == to) return
        withContext(Dispatchers.IO) {
            for (id in ids) {
                val entity = imageDao.findById(id) ?: continue
                if (entity.status != from.toStorage()) continue
                val newPath =
                    runCatching {
                        fileManager.moveToStatus(entity.filePath, entity.fileName, to)
                    }.getOrElse {
                        // 文件缺失时仍更新库路径到目标目录约定
                        fileManager.relativePath(to, entity.fileName)
                    }
                val updated = entity.toListItem().withStatus(to)
                imageDao.insert(
                    updated.toEntity(
                        filePath = newPath,
                        fileName = entity.fileName,
                        importedAt = entity.importedAt,
                    ),
                )
            }
        }
    }

    override suspend fun deleteItems(ids: Set<String>) {
        if (ids.isEmpty()) return
        withContext(Dispatchers.IO) {
            for (id in ids) {
                val entity = imageDao.findById(id) ?: continue
                fileManager.deleteRelative(entity.filePath)
            }
            imageDao.deleteByIds(ids.toList())
        }
    }

    override suspend fun insert(
        item: ImageListItem,
        filePath: String,
        fileName: String,
        importedAt: Long,
    ) {
        val withTags = item.copy(tags = tagsForStatus(item.status, item.tags), filePath = filePath)
        imageDao.insert(withTags.toEntity(filePath, fileName, importedAt))
    }

    override suspend fun rename(
        id: String,
        newFileName: String,
    ) {
        withContext(Dispatchers.IO) {
            val entity = imageDao.findById(id) ?: error("图片不存在")
            val resolvedName = resolveFileName(entity.fileName, newFileName)
            val newPath = fileManager.renameInPlace(entity.filePath, resolvedName)
            imageDao.insert(
                entity.copy(
                    filePath = newPath,
                    fileName = resolvedName,
                    description = resolvedName,
                ),
            )
        }
    }

    override suspend fun updateTags(
        id: String,
        tags: List<String>,
    ): Boolean =
        withContext(Dispatchers.IO) {
            val entity = imageDao.findById(id) ?: error("图片不存在")
            val status =
                runCatching {
                    ImageStatus.valueOf(entity.status)
                }.getOrDefault(ImageStatus.Pending)
            val normalized = tagsForStatus(status, tags)
            imageDao.insert(
                entity.copy(tagsJson = converters.toTagsJson(normalized)),
            )
            val file = fileManager.absoluteFile(entity.filePath)
            ImageTagMetadata.writeUserTags(file, normalized)
        }

    override suspend fun migrateFlatPathsIfNeeded() {
        withContext(Dispatchers.IO) {
            fileManager.ensureAllDirs()
            val all = imageDao.getAll()
            for (entity in all) {
                if (entity.filePath.contains('/')) continue
                val status =
                    runCatching {
                        ImageStatus.valueOf(entity.status)
                    }.getOrDefault(ImageStatus.Pending)
                val flat = fileManager.absoluteFile(entity.filePath)
                val targetRelative = fileManager.relativePath(status, entity.fileName)
                val target = fileManager.absoluteFile(targetRelative)
                if (flat.exists() && flat.absolutePath != target.absolutePath) {
                    target.parentFile?.mkdirs()
                    if (!flat.renameTo(target)) {
                        flat.copyTo(target, overwrite = true)
                        flat.delete()
                    }
                }
                if (entity.filePath != targetRelative) {
                    imageDao.insert(entity.copy(filePath = targetRelative))
                }
            }
            // images/ 根下残留的扁平文件迁入 pending
            val root = fileManager.imagesRoot()
            root.listFiles()?.forEach { file ->
                if (!file.isFile) return@forEach
                val dest = fileManager.createDestFile(ImageStatus.Pending, file.name)
                if (dest.exists()) {
                    file.delete()
                } else {
                    val ok = file.renameTo(dest)
                    if (!ok) {
                        file.copyTo(dest, overwrite = false)
                        file.delete()
                    }
                }
            }
        }
    }

    private fun resolveFileName(
        currentFileName: String,
        requested: String,
    ): String {
        val trimmed = requested.trim()
        require(trimmed.isNotEmpty()) { "文件名不能为空" }
        val currentExt = currentFileName.substringAfterLast('.', missingDelimiterValue = "")
        val hasExt =
            trimmed.contains('.') &&
                trimmed.substringAfterLast('.').isNotEmpty() &&
                !trimmed.endsWith('.')
        return if (hasExt || currentExt.isEmpty()) {
            trimmed
        } else {
            "$trimmed.$currentExt"
        }
    }
}
