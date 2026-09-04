package com.pictureorganizer.util.image

import androidx.exifinterface.media.ExifInterface
import com.pictureorganizer.model.ImageListItem
import org.json.JSONArray
import java.io.File
import java.io.InputStream

/**
 * 用户标签与 JPEG Exif `UserComment` 的双向同步（JSON 数组）。
 * 不含状态标签（待处理 / 已确认 / 回收站）。
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
            val raw = exif.getAttribute(ExifInterface.TAG_USER_COMMENT).orEmpty()
            parseUserCommentJson(raw)
        }.getOrDefault(emptyList())
    }

    /** 从输入流读 UserComment（导入时在压缩前读源 Uri）。失败 / 非本应用格式 → 空列表。 */
    fun readUserTags(inputStream: InputStream): List<String> =
        runCatching {
            val exif = ExifInterface(inputStream)
            val raw = exif.getAttribute(ExifInterface.TAG_USER_COMMENT).orEmpty()
            parseUserCommentJson(raw)
        }.getOrDefault(emptyList())

    /** 从已打开的文件描述符读 UserComment（相册 Uri 常用）。 */
    fun readUserTags(fileDescriptor: java.io.FileDescriptor): List<String> =
        runCatching {
            val exif = ExifInterface(fileDescriptor)
            val raw = exif.getAttribute(ExifInterface.TAG_USER_COMMENT).orEmpty()
            parseUserCommentJson(raw)
        }.getOrDefault(emptyList())

    /**
     * 解析本应用写入的 UserComment JSON 数组。
     * 空串 / 非数组 / 坏 JSON → 空列表（不抛错）。
     */
    fun parseUserCommentJson(raw: String): List<String> {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return emptyList()
        return runCatching {
            val array = JSONArray(trimmed)
            buildList {
                for (i in 0 until array.length()) {
                    val tag = array.optString(i).trim()
                    if (tag.isNotEmpty() && tag !in ImageListItem.STATUS_TAGS) add(tag)
                }
            }
        }.getOrDefault(emptyList())
    }

    /**
     * 导入合并：默认标签 ∪ Exif 回读，去重保序（默认在前），剔状态保留字与空白。
     */
    fun mergeImportTags(
        defaultTags: List<String>,
        exifTags: List<String>,
    ): List<String> {
        val cleaned =
            (defaultTags + exifTags)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        return ImageListItem.userTagsOf(cleaned).distinct()
    }
}
