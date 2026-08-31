package com.pictureorganizer.model

/**
 * 标签筛选条件（主画面一览 / 筛选画面 / 日后 C7 共用）。
 * 多选为 OR；[includeUntagged] 表示无用户标签的项。
 */
data class TagFilterCriteria(
    val selectedTagNames: Set<String> = emptySet(),
    val includeUntagged: Boolean = false,
) {
    val isActive: Boolean
        get() = selectedTagNames.isNotEmpty() || includeUntagged

    /** 摘要展示用：标签名 + 可选「未打标签」文案由 UI 层插入 */
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
