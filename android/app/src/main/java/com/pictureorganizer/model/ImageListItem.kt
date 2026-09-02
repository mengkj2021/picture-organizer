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
) {
    fun withStatus(newStatus: ImageStatus): ImageListItem =
        copy(
            status = newStatus,
            // S1：状态由 status 字段表达，不再写入标签；顺带剔除历史残留的状态词
            tags = userTagsOf(tags),
        )

    companion object {
        // 三个状态名为保留字（S1「状态不算标签」）：
        // 仅由 status 字段分类，不作为用户标签出现；禁止用户打为标签，并对历史数据兜底过滤
        val STATUS_TAGS = setOf("待处理", "已确认", "不修改")

        fun userTagsOf(tags: List<String>): List<String> = tags.filter { it !in STATUS_TAGS }
    }
}
