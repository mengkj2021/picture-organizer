package com.pictureorganizer.ui.main

import com.pictureorganizer.model.ImageListItem

const val MAIN_PAGE_SIZE = 30

data class MainUiState(
    val selectedTab: MainTab = MainTab.Pending,
    val isEditMode: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val availableTags: List<String> = emptyList(),
    val selectedTagNames: Set<String> = emptySet(),
    val includeUntagged: Boolean = false,
    val pageItems: List<ImageListItem> = emptyList(),
    val pageIndex: Int = 0,
    val totalCount: Int = 0,
    val totalPages: Int = 1,
) {
    val isFilterActive: Boolean
        get() = selectedTagNames.isNotEmpty() || includeUntagged

    val canGoPrev: Boolean get() = pageIndex > 0
    val canGoNext: Boolean get() = pageIndex < totalPages - 1
}
