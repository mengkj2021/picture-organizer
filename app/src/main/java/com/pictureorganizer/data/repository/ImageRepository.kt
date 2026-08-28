package com.pictureorganizer.data.repository

import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun observeItems(status: ImageStatus): Flow<List<ImageListItem>>
    suspend fun getItems(status: ImageStatus): List<ImageListItem>
    suspend fun moveItems(ids: Set<String>, from: ImageStatus, to: ImageStatus)
    suspend fun deleteItems(ids: Set<String>)
    suspend fun insert(item: ImageListItem, filePath: String, fileName: String, importedAt: Long)
    suspend fun migrateFlatPathsIfNeeded()
}
