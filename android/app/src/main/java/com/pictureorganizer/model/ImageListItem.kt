package com.pictureorganizer.model

data class ImageListItem(
    val id: String,
    // 以下字段在写库 / 读库时由 data/mapper 统一派生，构造期可省略
    val date: String = "",
    val description: String,
    val tags: List<String> = emptyList(),
    val placeholderColorArgb: Long = 0,
    val status: ImageStatus,
    /** 相对 `filesDir/images/` 的路径，如 `pending/xxx.jpg` */
    val filePath: String = "",
    /** 导入时间毫秒（F6 排序） */
    val importedAt: Long = 0L,
    /** F11：导入前原图名（DISPLAY_NAME）；历史可为 null */
    val originalName: String? = null,
) {
    fun withStatus(newStatus: ImageStatus): ImageListItem =
        copy(
            status = newStatus,
            // S1：状态由 status 字段表达，不再写入标签；顺带剔除历史残留的状态词
            tags = userTagsOf(tags),
        )

    companion object {
        // 三个状态名为保留字（S1「状态不算标签」；S3 显示名「不修改」→「回收站」）：
        // 仅由 status 字段分类，不作为用户标签出现；禁止用户打为标签，并对历史数据兜底过滤
        val STATUS_TAGS = setOf("待处理", "已确认", "回收站")

        fun userTagsOf(tags: List<String>): List<String> = tags.filter { it !in STATUS_TAGS }

        /** F8：从标签列表精确去掉指定名（trim 后比对）；空目标不改动。 */
        fun removeTagName(
            tags: List<String>,
            name: String,
        ): List<String> {
            val target = name.trim()
            if (target.isEmpty()) return tags
            return tags.filter { it != target }
        }

        /** F8：统计「标签列表精确含该名」的图片张数。 */
        fun countImagesWithTag(
            imageTagLists: List<List<String>>,
            name: String,
        ): Int {
            val target = name.trim()
            if (target.isEmpty()) return 0
            return imageTagLists.count { target in it }
        }
    }
}
