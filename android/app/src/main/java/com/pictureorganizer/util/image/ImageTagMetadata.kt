package com.pictureorganizer.util.image

import androidx.exifinterface.media.ExifInterface
import com.pictureorganizer.model.ImageListItem
import org.json.JSONArray
import java.io.File

/**
 * 用户标签与 JPEG Exif `UserComment` 的双向同步（JSON 数组）。
 * 不含状态标签（待处理 / 已确认 / 不修改）。
 */
object ImageTagMetadata {
    fun writeUserTags(
        file: File,
        tags: List<String>,
    ): Boolean {
        if (!file.exists() || !file.isFile) return false
        return runCatching {
            val userTags = ImageListItem.userTagsOf(tags)
            val json = JSONArray().apply { userTags.forEach { put(it) } }.toString()
            val exif = ExifInterface(file)
            exif.setAttribute(ExifInterface.TAG_USER_COMMENT, json)
            exif.saveAttributes()
            true
        }.getOrDefault(false)
    }

    fun readUserTags(file: File): List<String> {
        if (!file.exists() || !file.isFile) return emptyList()
        return runCatching {
            val exif = ExifInterface(file)
            val raw = exif.getAttribute(ExifInterface.TAG_USER_COMMENT)?.trim().orEmpty()
            if (raw.isEmpty()) return emptyList()
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val tag = array.optString(i).trim()
                    if (tag.isNotEmpty() && tag !in ImageListItem.STATUS_TAGS) add(tag)
                }
            }
        }.getOrDefault(emptyList())
    }
}
