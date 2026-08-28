package com.pictureorganizer.model

data class ImageListItem(
    val id: String,
    // 以下字段在写库 / 读库时由 data/mapper 统一派生，构造期可省略
    val date: String = "",
    val description: String,
    val tags: List<String> = emptyList(),
    val placeholderColorArgb: Long = 0,
    val status: ImageStatus
) {
    fun withStatus(newStatus: ImageStatus): ImageListItem {
        val statusTag = statusTagFor(newStatus)
        val otherTags = tags.filter { it !in STATUS_TAGS }
        return copy(
            status = newStatus,
            tags = listOf(statusTag) + otherTags
        )
    }

    companion object {
        // 仅包含当前三个状态的标签；新增状态时在此同步
        private val STATUS_TAGS = setOf("待处理", "已确认", "不修改")

        fun statusTagFor(status: ImageStatus): String = when (status) {
            ImageStatus.Pending -> "待处理"
            ImageStatus.Confirmed -> "已确认"
            ImageStatus.NoModify -> "不修改"
        }
    }
}
