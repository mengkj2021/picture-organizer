package com.pictureorganizer.ui.main.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pictureorganizer.data.mock.MockImageListData
import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus
import com.pictureorganizer.ui.main.MainTab
import com.pictureorganizer.ui.theme.PictureOrganizerTheme

@Composable
fun ImageListTab(
    currentTab: MainTab,
    items: List<ImageListItem>,
    emptyMessage: String,
    isEditMode: Boolean,
    selectedIds: Set<String>,
    isFilterActive: Boolean,
    filterSummaryLabels: List<String>,
    pageIndex: Int,
    totalPages: Int,
    canGoPrev: Boolean,
    canGoNext: Boolean,
    onEditClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onMoveTo: (ImageStatus) -> Unit,
    onImportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleSelect: (String) -> Unit,
    onFilterClick: () -> Unit,
    onClearFilter: () -> Unit,
    onExportClick: () -> Unit,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TabActionBar(
            currentTab = currentTab,
            isEditMode = isEditMode,
            hasSelection = selectedIds.isNotEmpty(),
            isFilterActive = isFilterActive,
            filterSummaryLabels = filterSummaryLabels,
            onEditClick = onEditClick,
            onSelectAllClick = onSelectAllClick,
            onMoveTo = onMoveTo,
            onImportClick = onImportClick,
            onDeleteClick = onDeleteClick,
            onExportClick = onExportClick,
            onFilterClick = onFilterClick,
            onClearFilter = onClearFilter,
        )

        if (items.isEmpty()) {
            EmptyListPlaceholder(
                emptyMessage = emptyMessage,
                modifier = Modifier.weight(1f),
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items, key = { it.id }) { item ->
                    ImageListItemRow(
                        item = item,
                        isEditMode = isEditMode,
                        isSelected = item.id in selectedIds,
                        onToggleSelect = { onToggleSelect(item.id) },
                        onClick = { onItemClick(item.id) },
                    )
                }
            }
        }

        ListPageBar(
            pageIndex = pageIndex,
            totalPages = totalPages,
            canGoPrev = canGoPrev,
            canGoNext = canGoNext,
            onPrevClick = onPrevPage,
            onNextClick = onNextPage,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageListTabPreview() {
    PictureOrganizerTheme {
        ImageListTab(
            currentTab = MainTab.Pending,
            items = MockImageListData.pendingItems(),
            emptyMessage = "暂无待处理图片",
            isEditMode = false,
            selectedIds = emptySet(),
            isFilterActive = true,
            filterSummaryLabels = listOf("风景", "未打标签"),
            pageIndex = 0,
            totalPages = 1,
            canGoPrev = false,
            canGoNext = false,
            onEditClick = {},
            onSelectAllClick = {},
            onMoveTo = {},
            onImportClick = {},
            onDeleteClick = {},
            onToggleSelect = {},
            onFilterClick = {},
            onClearFilter = {},
            onExportClick = {},
            onPrevPage = {},
            onNextPage = {},
        )
    }
}
