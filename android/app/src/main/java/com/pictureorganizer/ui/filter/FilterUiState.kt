package com.pictureorganizer.ui.filter

data class FilterUiState(
    val availableTags: List<String> = emptyList(),
    val selectedTagNames: Set<String> = emptySet(),
    val includeUntagged: Boolean = false,
)
