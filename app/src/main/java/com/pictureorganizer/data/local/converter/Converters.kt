package com.pictureorganizer.data.local.converter

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromTagsJson(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        return json.split(TAG_SEPARATOR)
    }

    @TypeConverter
    fun toTagsJson(tags: List<String>): String {
        return tags.joinToString(TAG_SEPARATOR)
    }

    companion object {
        private const val TAG_SEPARATOR = "\u001F"
    }
}
