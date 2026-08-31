package com.pictureorganizer.util.file

import com.pictureorganizer.model.ImageListItem

object RenamePatternApplier {
    /**
     * 将 [pattern] 中的 `{name}` `{date}` `{tag}` 替换后，拼回原扩展名。
     * [dateYmd] 为 `yyyyMMdd`；[userTags] 取第一个作 `{tag}`。
     */
    fun apply(
        pattern: String,
        fileName: String,
        dateYmd: String,
        userTags: List<String>,
    ): String {
        val base = fileName.substringBeforeLast('.', missingDelimiterValue = fileName)
        val ext = if (fileName.contains('.')) fileName.substringAfterLast('.') else ""
        val tag = userTags.firstOrNull().orEmpty()
        val stem =
            pattern
                .replace("{name}", base)
                .replace("{date}", dateYmd)
                .replace("{tag}", tag)
                .trim()
                .ifEmpty { base }
        return if (ext.isNotEmpty()) "$stem.$ext" else stem
    }

    fun applyForItem(
        pattern: String,
        item: ImageListItem,
        currentFileName: String,
    ): String {
        val dateYmd = item.date.replace("-", "")
        return apply(
            pattern = pattern,
            fileName = currentFileName,
            dateYmd =
                dateYmd.ifBlank {
                    java.time.LocalDate.now().format(
                        java.time.format.DateTimeFormatter
                            .ofPattern("yyyyMMdd"),
                    )
                },
            userTags = ImageListItem.userTagsOf(item.tags),
        )
    }
}
