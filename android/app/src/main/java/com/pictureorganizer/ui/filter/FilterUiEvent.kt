package com.pictureorganizer.ui.filter

import com.pictureorganizer.model.ImageListSort
import com.pictureorganizer.model.TagFilterCriteria

sealed interface FilterUiEvent {
    data class ToggleTag(
        val name: String,
    ) : FilterUiEvent

    data object ToggleUntagged : FilterUiEvent

    data class NameContainsChanged(
        val value: String,
    ) : FilterUiEvent

    data class SortChanged(
        val sort: ImageListSort,
    ) : FilterUiEvent

    data object Apply : FilterUiEvent

    data object ClearAndApply : FilterUiEvent
}

sealed interface FilterUiEffect {
    data class ApplyAndClose(
        val criteria: TagFilterCriteria,
    ) : FilterUiEffect
}
