package com.pictureorganizer.data.repository

import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus

interface ImageRepository {
    fun getItems(status: ImageStatus): List<ImageListItem>
    fun moveItems(ids: Set<String>, from: ImageStatus, to: ImageStatus)
}
