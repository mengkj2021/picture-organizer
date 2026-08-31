package com.pictureorganizer.ui.imagedetail

import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.RenameTemplate
import com.pictureorganizer.model.Tag
import com.pictureorganizer.model.TagTemplate

data class ImageDetailUiState(
    val currentId: String = "",
    val current: ImageListItem? = null,
    val siblings: List<ImageListItem> = emptyList(),
    val renameDraft: String = "",
    val tagDraft: String = "",
    /** null = 新增模式；非 null = 编辑自定义用户标签的下标（仅非库内标签） */
    val editingUserTagIndex: Int? = null,
    val libraryTags: List<Tag> = emptyList(),
    val templates: List<TagTemplate> = emptyList(),
    val renameTemplates: List<RenameTemplate> = emptyList(),
    val isBusy: Boolean = false,
    val notFound: Boolean = false,
)

sealed interface ImageDetailUiEvent {
    data class SelectSibling(
        val id: String,
    ) : ImageDetailUiEvent

    data class RenameDraftChanged(
        val value: String,
    ) : ImageDetailUiEvent

    data object SaveRename : ImageDetailUiEvent

    data class TagDraftChanged(
        val value: String,
    ) : ImageDetailUiEvent

    data class StartEditTag(
        val userTagIndex: Int,
    ) : ImageDetailUiEvent

    data object CancelEditTag : ImageDetailUiEvent

    data object SaveTag : ImageDetailUiEvent

    data class DeleteTag(
        val userTagIndex: Int,
    ) : ImageDetailUiEvent

    data class ToggleLibraryTag(
        val name: String,
    ) : ImageDetailUiEvent

    data class ApplyTemplate(
        val templateId: String,
    ) : ImageDetailUiEvent

    data class ApplyRenameTemplate(
        val templateId: String,
    ) : ImageDetailUiEvent
}

sealed interface ImageDetailUiEffect {
    data class ShowMessage(
        val messageResId: Int,
    ) : ImageDetailUiEffect

    data class ShowMessageText(
        val text: String,
    ) : ImageDetailUiEffect
}
