package com.pictureorganizer.model

/**
 * 标签筛选匹配（主画面 / 打包画面共用；原 `MainViewModel.matchesTagFilter`）。
 * 多选为 OR；[includeUntagged] 匹配无用户标签的项；无任何条件时恒真。
 */
fun ImageListItem.matchesTagFilter(
    selectedTagNames: Set<String>,
    includeUntagged: Boolean,
): Boolean {
    if (selectedTagNames.isEmpty() && !includeUntagged) return true
    val user = ImageListItem.userTagsOf(tags)
    val hitTag = selectedTagNames.any { it in user }
    val hitUntagged = includeUntagged && user.isEmpty()
    return hitTag || hitUntagged
}
