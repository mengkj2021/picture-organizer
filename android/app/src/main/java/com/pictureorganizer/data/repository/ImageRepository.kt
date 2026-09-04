package com.pictureorganizer.data.repository

import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun observeItems(status: ImageStatus): Flow<List<ImageListItem>>

    fun observeItem(id: String): Flow<ImageListItem?>

    suspend fun getItems(status: ImageStatus): List<ImageListItem>

    suspend fun getItem(id: String): ImageListItem?

    suspend fun moveItems(
        ids: Set<String>,
        from: ImageStatus,
        to: ImageStatus,
    )

    suspend fun deleteItems(ids: Set<String>)

    suspend fun insert(
        item: ImageListItem,
        filePath: String,
        fileName: String,
        importedAt: Long,
    )

    suspend fun migrateFlatPathsIfNeeded()

    /** @throws IllegalArgumentException / IllegalStateException 校验或文件失败 */
    suspend fun rename(
        id: String,
        newFileName: String,
    )

    /**
     * 更新标签（含状态标签规范化）。
     * @return `true` 表示 Exif 也写入成功；`false` 表示仅 DB 成功
     */
    suspend fun updateTags(
        id: String,
        tags: List<String>,
    ): Boolean

    /** F8：`tagsJson` 精确含该标签名的图片张数。 */
    suspend fun countImagesWithTag(tagName: String): Int

    /**
     * F8：从所有引用图片去掉该标签名（写 `tagsJson` + Exif）。
     * @return Exif 写入失败的张数（DB 仍会更新）
     */
    suspend fun removeTagFromAllImages(tagName: String): Int
}
