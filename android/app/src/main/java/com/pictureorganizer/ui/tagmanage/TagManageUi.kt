package com.pictureorganizer.ui.tagmanage

import com.pictureorganizer.model.Tag
import com.pictureorganizer.model.TagTemplate

enum class TagManageTab {
    Tags,
    Templates,
}

data class TagManageUiState(
    val selectedTab: TagManageTab = TagManageTab.Tags,
    val tags: List<Tag> = emptyList(),
    val templates: List<TagTemplate> = emptyList(),
    val tagDialog: TagDialogState? = null,
    val templateDialog: TemplateDialogState? = null,
    val confirmDeleteTagId: String? = null,
    /** F8：有引用时的第二层确认 */
    val cascadeDeleteTag: CascadeDeleteTagState? = null,
    val confirmDeleteTemplateId: String? = null,
    val isBusy: Boolean = false,
)

data class TagDialogState(
    val editingId: String? = null,
    val name: String = "",
)

data class TemplateDialogState(
    val editingId: String? = null,
    val name: String = "",
    val selectedTagNames: Set<String> = emptySet(),
    val isDefault: Boolean = false,
)

data class CascadeDeleteTagState(
    val tagId: String,
    val tagName: String,
    val imageCount: Int,
)

sealed interface TagManageUiEvent {
    data class SelectTab(
        val tab: TagManageTab,
    ) : TagManageUiEvent

    data object OpenAddTag : TagManageUiEvent

    data class OpenEditTag(
        val tag: Tag,
    ) : TagManageUiEvent

    data class TagNameChanged(
        val value: String,
    ) : TagManageUiEvent

    data object SaveTag : TagManageUiEvent

    data object DismissTagDialog : TagManageUiEvent

    data class RequestDeleteTag(
        val id: String,
    ) : TagManageUiEvent

    data object ConfirmDeleteTag : TagManageUiEvent

    data object CancelDeleteTag : TagManageUiEvent

    data object ConfirmCascadeDeleteTag : TagManageUiEvent

    data object CancelCascadeDeleteTag : TagManageUiEvent

    data object OpenAddTemplate : TagManageUiEvent

    data class OpenEditTemplate(
        val template: TagTemplate,
    ) : TagManageUiEvent

    data class TemplateNameChanged(
        val value: String,
    ) : TagManageUiEvent

    data class ToggleTemplateTag(
        val name: String,
    ) : TagManageUiEvent

    data class TemplateDefaultChanged(
        val value: Boolean,
    ) : TagManageUiEvent

    data object SaveTemplate : TagManageUiEvent

    data object DismissTemplateDialog : TagManageUiEvent

    data class SetDefaultTemplate(
        val id: String,
    ) : TagManageUiEvent

    data class RequestDeleteTemplate(
        val id: String,
    ) : TagManageUiEvent

    data object ConfirmDeleteTemplate : TagManageUiEvent

    data object CancelDeleteTemplate : TagManageUiEvent
}

sealed interface TagManageUiEffect {
    data class ShowMessage(
        val messageResId: Int,
    ) : TagManageUiEffect

    data class ShowMessageText(
        val text: String,
    ) : TagManageUiEffect
}
