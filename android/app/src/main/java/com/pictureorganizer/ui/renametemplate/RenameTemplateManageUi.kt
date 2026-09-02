package com.pictureorganizer.ui.renametemplate

import com.pictureorganizer.model.RenameTemplate

data class RenameTemplateManageUiState(
    val templates: List<RenameTemplate> = emptyList(),
    val dialog: RenameTemplateDialogState? = null,
    val confirmDeleteId: String? = null,
    val isBusy: Boolean = false,
)

data class RenameTemplateDialogState(
    val editingId: String? = null,
    val name: String = "",
    val pattern: String = "{date}_{name}",
    val isDefault: Boolean = false,
)

sealed interface RenameTemplateManageUiEvent {
    data object OpenAdd : RenameTemplateManageUiEvent

    data class OpenEdit(
        val template: RenameTemplate,
    ) : RenameTemplateManageUiEvent

    data class NameChanged(
        val value: String,
    ) : RenameTemplateManageUiEvent

    data class PatternChanged(
        val value: String,
    ) : RenameTemplateManageUiEvent

    data class DefaultChanged(
        val value: Boolean,
    ) : RenameTemplateManageUiEvent

    data object Save : RenameTemplateManageUiEvent

    data object DismissDialog : RenameTemplateManageUiEvent

    data class SetDefault(
        val id: String,
    ) : RenameTemplateManageUiEvent

    data class RequestDelete(
        val id: String,
    ) : RenameTemplateManageUiEvent

    data object ConfirmDelete : RenameTemplateManageUiEvent

    data object CancelDelete : RenameTemplateManageUiEvent
}

sealed interface RenameTemplateManageUiEffect {
    data class ShowMessage(
        val messageResId: Int,
    ) : RenameTemplateManageUiEffect

    data class ShowMessageText(
        val text: String,
    ) : RenameTemplateManageUiEffect
}
