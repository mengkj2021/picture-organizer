package com.pictureorganizer.model

/**
 * 列表筛选排序（主画�?/ 筛选画�?/ 打包共用）�? */
enum class ImageListSort {
    /** 导入日期新→旧（默认，与 DAO 顺序一致） */
    ImportedAtDesc,

    /** 导入日期旧→�?*/
    ImportedAtAsc,

    /** 文件�?A→Z（按 description / 路径末段，忽略大小写�?*/
    NameAsc,
    ;

    companion object {
        fun fromParam(raw: String?): ImageListSort = entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: ImportedAtDesc
    }
}

/**
 * 标签 / 文件名筛选条件（主画面一�?/ 筛选画�?/ 打包共用）�? * 标签多选为 OR；[includeUntagged] 表示无用户标签的项�? * F6：[nameContains] 匹配文件名或 description（忽略大小写）；[sort] 控制排序�? */
data class TagFilterCriteria(
    val selectedTagNames: Set<String> = emptySet(),
    val includeUntagged: Boolean = false,
    val nameContains: String = "",
    val sort: ImageListSort = ImageListSort.ImportedAtDesc,
) {
    val isActive: Boolean
        get() =
            selectedTagNames.isNotEmpty() ||
                includeUntagged ||
                nameContains.isNotBlank()

    /** 摘要展示用：标签�?+ 可选「未打标签」文案由 UI 层插�?*/
    fun summaryTagNames(): List<String> = selectedTagNames.sorted()

    fun encodeTagsParam(): String = selectedTagNames.joinToString(",")

    companion object {
        fun parseTagsParam(raw: String?): Set<String> {
            if (raw.isNullOrBlank()) return emptySet()
            return raw
                .split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
        }
    }
}
