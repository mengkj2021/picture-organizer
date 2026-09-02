package com.pictureorganizer.model

/**
 * 列表筛选匹配（主画面 / 打包画面共用）。
 * 标签多选为 OR；[includeUntagged] 匹配无用户标签的项；
 * [nameContains] 非空时还要求文件名或 description 包含（忽略大小写）。
 * 无任何条件时恒真。
 */
fun ImageListItem.matchesTagFilter(
    selectedTagNames: Set<String>,
    includeUntagged: Boolean,
    nameContains: String = "",
): Boolean {
    val query = nameContains.trim()
    if (query.isNotEmpty()) {
        val haystack = listOf(description, filePath.substringAfterLast('/')).joinToString("\u0000")
        if (!haystack.contains(query, ignoreCase = true)) return false
    }
    if (selectedTagNames.isEmpty() && !includeUntagged) return true
    val user = ImageListItem.userTagsOf(tags)
    val hitTag = selectedTagNames.any { it in user }
    val hitUntagged = includeUntagged && user.isEmpty()
    return hitTag || hitUntagged
}

fun ImageListItem.matchesFilter(criteria: TagFilterCriteria): Boolean =
    matchesTagFilter(
        selectedTagNames = criteria.selectedTagNames,
        includeUntagged = criteria.includeUntagged,
        nameContains = criteria.nameContains,
    )

fun List<ImageListItem>.sortedByFilter(sort: ImageListSort): List<ImageListItem> =
    when (sort) {
        ImageListSort.ImportedAtDesc -> sortedByDescending { it.importedAt }
        ImageListSort.ImportedAtAsc -> sortedBy { it.importedAt }
        ImageListSort.NameAsc ->
            sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) {
                    it.description.ifBlank { it.filePath.substringAfterLast('/') }
                },
            )
    }
