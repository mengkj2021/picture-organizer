package com.pictureorganizer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey val id: String,
    val filePath: String,
    val fileName: String,
    val description: String,
    val status: String,
    val importedAt: Long,
    val tagsJson: String,
    /** F11：导入时 Uri DISPLAY_NAME；历史/未知为 null */
    val originalName: String? = null,
)
