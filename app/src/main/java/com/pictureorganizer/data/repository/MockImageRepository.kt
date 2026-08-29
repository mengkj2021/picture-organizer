package com.pictureorganizer.data.repository

import com.pictureorganizer.data.mapper.tagsForStatus
import com.pictureorganizer.data.mock.MockImageListData
import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * 内存假数据，仅供 Preview / 测试。生产路径使用 [RoomImageRepository]。
 */
object MockImageRepository : ImageRepository {

    private val items = MutableStateFlow(
        MockImageListData.pendingItems() +
            MockImageListData.confirmedItems() +
            MockImageListData.noModifyItems()
    )

    override fun observeItems(status: ImageStatus): Flow<List<ImageListItem>> {
        return items.map { list -> list.filter { it.status == status } }
    }

    override fun observeItem(id: String): Flow<ImageListItem?> {
        return items.map { list -> list.find { it.id == id } }
    }

    override suspend fun getItems(status: ImageStatus): List<ImageListItem> {
        return items.value.filter { it.status == status }
    }

    override suspend fun getItem(id: String): ImageListItem? {
        return items.value.find { it.id == id }
    }

    override suspend fun moveItems(ids: Set<String>, from: ImageStatus, to: ImageStatus) {
        if (ids.isEmpty() || from == to) return
        items.update { list ->
            list.map { item ->
                if (item.id in ids && item.status == from) item.withStatus(to) else item
            }
        }
    }

    override suspend fun deleteItems(ids: Set<String>) {
        if (ids.isEmpty()) return
        items.update { list -> list.filterNot { it.id in ids } }
    }

    override suspend fun insert(
        item: ImageListItem,
        filePath: String,
        fileName: String,
        importedAt: Long
    ) {
        items.update { it + item.copy(filePath = filePath) }
    }

    override suspend fun rename(id: String, newFileName: String) {
        val trimmed = newFileName.trim()
        require(trimmed.isNotEmpty()) { "文件名不能为空" }
        items.update { list ->
            list.map { item ->
                if (item.id != id) item
                else {
                    val dir = item.filePath.substringBeforeLast('/', missingDelimiterValue = "")
                    val newPath = if (dir.isEmpty()) trimmed else "$dir/$trimmed"
                    item.copy(
                        description = trimmed,
                        filePath = newPath
                    )
                }
            }
        }
    }

    override suspend fun updateTags(id: String, tags: List<String>): Boolean {
        items.update { list ->
            list.map { item ->
                if (item.id != id) item
                else item.copy(tags = tagsForStatus(item.status, tags))
            }
        }
        return true
    }

    override suspend fun migrateFlatPathsIfNeeded() = Unit
}
