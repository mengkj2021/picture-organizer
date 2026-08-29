package com.pictureorganizer.ui.imagedetail

import com.pictureorganizer.model.ImageListItem

data class ImageDetailUiState(
    val currentId: String = "",
    val current: ImageListItem? = null,
    val siblings: List<ImageListItem> = emptyList(),
    val renameDraft: String = "",
    val tagDraft: String = "",
    /** null = 新增模式；非 null = 编辑 siblings 中用户标签的下标（不含状态标签） */
    val editingUserTagIndex: Int? = null,
    val isBusy: Boolean = false,
    val notFound: Boolean = false
)

sealed interface ImageDetailUiEvent {
    data class SelectSibling(val id: String) : ImageDetailUiEvent
    data class RenameDraftChanged(val value: String) : ImageDetailUiEvent
    data object SaveRename : ImageDetailUiEvent
    data class TagDraftChanged(val value: String) : ImageDetailUiEvent
    data class StartEditTag(val userTagIndex: Int) : ImageDetailUiEvent
    data object CancelEditTag : ImageDetailUiEvent
    data object SaveTag : ImageDetailUiEvent
    data class DeleteTag(val userTagIndex: Int) : ImageDetailUiEvent
}

sealed interface ImageDetailUiEffect {
    data class ShowMessage(val messageResId: Int) : ImageDetailUiEffect
    data class ShowMessageText(val text: String) : ImageDetailUiEffect
}
