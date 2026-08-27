package com.pictureorganizer.model

data class ImageListItem(    val id: String,
    val date: String,
    val description: String,
    val tags: List<String>,
    val placeholderColorArgb: Long,
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
        private val STATUS_TAGS = setOf("待处理", "已确认", "不修改", "待修改", "已修改")

        fun statusTagFor(status: ImageStatus): String = when (status) {
            ImageStatus.Pending -> "待处理"
            ImageStatus.Confirmed -> "已确认"
            ImageStatus.NoModify -> "不修改"
        }
    }
}
