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
    onEditClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onMoveTo: (ImageStatus) -> Unit,
    onImportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TabActionBar(
            currentTab = currentTab,
            isEditMode = isEditMode,
            hasSelection = selectedIds.isNotEmpty(),
            onEditClick = onEditClick,
            onSelectAllClick = onSelectAllClick,
            onMoveTo = onMoveTo,
            onImportClick = onImportClick,
            onDeleteClick = onDeleteClick
        )

        if (items.isEmpty()) {
            EmptyListPlaceholder(
                emptyMessage = emptyMessage,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items, key = { it.id }) { item ->
                    ImageListItemRow(
                        item = item,
                        isEditMode = isEditMode,
                        isSelected = item.id in selectedIds,
                        onToggleSelect = { onToggleSelect(item.id) }
                    )
                }
            }
        }
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
            onEditClick = {},
            onSelectAllClick = {},
            onMoveTo = {},
            onImportClick = {},
            onDeleteClick = {},
            onToggleSelect = {}
        )
    }
}
