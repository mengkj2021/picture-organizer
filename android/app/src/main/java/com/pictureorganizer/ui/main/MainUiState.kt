package com.pictureorganizer.ui.main

import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageListSort
import com.pictureorganizer.model.TagFilterCriteria

const val MAIN_PAGE_SIZE = 30

data class MainUiState(
    val selectedTab: MainTab = MainTab.Pending,
    val isEditMode: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val availableTags: List<String> = emptyList(),
    val filter: TagFilterCriteria = TagFilterCriteria(),
    val pageItems: List<ImageListItem> = emptyList(),
    val pageIndex: Int = 0,
    val totalCount: Int = 0,
    val totalPages: Int = 1,
) {
    val selectedTagNames: Set<String> get() = filter.selectedTagNames
    val includeUntagged: Boolean get() = filter.includeUntagged
    val nameContains: String get() = filter.nameContains
    val sort: ImageListSort get() = filter.sort

    val isFilterActive: Boolean
        get() = filter.isActive

    val canGoPrev: Boolean get() = pageIndex > 0
    val canGoNext: Boolean get() = pageIndex < totalPages - 1
}
