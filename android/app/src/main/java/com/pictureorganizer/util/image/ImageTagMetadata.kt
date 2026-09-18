package com.pictureorganizer.util.image

import androidx.exifinterface.media.ExifInterface
import com.pictureorganizer.model.ImageListItem
import org.json.JSONArray
import java.io.File
import java.io.InputStream

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

    fun readUserTags(inputStream: InputStream): List<String> =
        runCatching {
            val exif = ExifInterface(inputStream)
            val raw = exif.getAttribute(ExifInterface.TAG_USER_COMMENT).orEmpty()
            parseUserCommentJson(raw)
        }.getOrDefault(emptyList())

    fun readUserTags(fileDescriptor: java.io.FileDescriptor): List<String> =
        runCatching {
            val exif = ExifInterface(fileDescriptor)
            val raw = exif.getAttribute(ExifInterface.TAG_USER_COMMENT).orEmpty()
            parseUserCommentJson(raw)
        }.getOrDefault(emptyList())

    fun parseUserCommentJson(raw: String): List<String> {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return emptyList()
        return runCatching {
            val array = JSONArray(trimmed)
            val parsed =
                buildList {
                    for (i in 0 until array.length()) {
                        val tag = array.optString(i).trim()
                        if (tag.isNotEmpty()) add(tag)
                    }
                }
            ImageListItem.userTagsOf(parsed)
        }.getOrDefault(emptyList())
    }

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
