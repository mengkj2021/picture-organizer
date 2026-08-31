package com.pictureorganizer.ui.exportzip

import com.pictureorganizer.model.TagFilterCriteria

data class ExportZipUiState(
    val filter: TagFilterCriteria = TagFilterCriteria(),
    val confirmedCount: Int = 0,
    val matchedCount: Int = 0,
    val isExporting: Boolean = false,
    val progressCurrent: Int = 0,
    val progressTotal: Int = 0,
    val results: List<ExportZipResult> = emptyList(),
    val errorMessage: String? = null,
) {
    val isFilterActive: Boolean get() = filter.isActive
}

data class ExportZipResult(
    val label: String,
    val fileName: String,
    val absolutePath: String,
    val entryCount: Int,
)

sealed interface ExportZipUiEvent {
    data class SetFilter(
        val filter: TagFilterCriteria,
    ) : ExportZipUiEvent

    data object ClearFilter : ExportZipUiEvent

    data object StartExport : ExportZipUiEvent

    data object ClearError : ExportZipUiEvent
}

sealed interface ExportZipUiEffect {
    data class ShareFile(
        val absolutePath: String,
        val fileName: String,
    ) : ExportZipUiEffect

    data class ShowMessage(
        val messageResId: Int,
    ) : ExportZipUiEffect

    data class ShowMessageText(
        val text: String,
    ) : ExportZipUiEffect
}
