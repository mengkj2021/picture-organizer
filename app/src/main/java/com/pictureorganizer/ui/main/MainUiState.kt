package com.pictureorganizer.ui.main

import com.pictureorganizer.model.ImageListItem

data class MainUiState(
    val selectedTab: MainTab = MainTab.Pending,
    val isEditMode: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val pendingItems: List<ImageListItem> = emptyList(),
    val confirmedItems: List<ImageListItem> = emptyList(),
    val noModifyItems: List<ImageListItem> = emptyList()
) {
    fun itemsForTab(tab: MainTab): List<ImageListItem> = when (tab) {
        MainTab.Pending -> pendingItems
        MainTab.Confirmed -> confirmedItems
        MainTab.NoModify -> noModifyItems
    }
}
