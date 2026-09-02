package com.pictureorganizer.ui.main

import com.pictureorganizer.model.ImageStatus
import com.pictureorganizer.model.TagFilterCriteria

sealed interface MainUiEvent {
    data class SelectTab(
        val tab: MainTab,
    ) : MainUiEvent

    data object ToggleEditMode : MainUiEvent

    data object SelectAll : MainUiEvent

    data class ToggleSelect(
        val id: String,
    ) : MainUiEvent

    data class MoveSelectedTo(
        val status: ImageStatus,
    ) : MainUiEvent

    data object ImportImages : MainUiEvent

    data object DeleteSelected : MainUiEvent

    data class SetTagFilter(
        val criteria: TagFilterCriteria,
    ) : MainUiEvent

    data object ClearTagFilter : MainUiEvent

    data object PrevPage : MainUiEvent

    data object NextPage : MainUiEvent
}
