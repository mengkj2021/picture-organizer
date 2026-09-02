package com.pictureorganizer.ui.main.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pictureorganizer.R
import com.pictureorganizer.model.ImageStatus
import com.pictureorganizer.ui.main.MainTab

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TabActionBar(
    currentTab: MainTab,
    isEditMode: Boolean,
    hasSelection: Boolean,
    isFilterActive: Boolean,
    filterSummaryLabels: List<String>,
    onEditClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onMoveTo: (ImageStatus) -> Unit,
    onImportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onExportClick: () -> Unit,
    onFilterClick: () -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var moveMenuExpanded by remember { mutableStateOf(false) }
    var moreMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onEditClick) {
                    Text(
                        if (isEditMode) {
                            stringResource(R.string.action_done)
                        } else {
                            stringResource(R.string.action_edit)
                        },
                    )
                }
                if (isEditMode) {
                    TextButton(
                        onClick = { moveMenuExpanded = true },
                        enabled = hasSelection,
                    ) {
                        Text(stringResource(R.string.action_move_to))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                        )
                    }
                    DropdownMenu(
                        expanded = moveMenuExpanded,
                        onDismissRequest = { moveMenuExpanded = false },
                    ) {
                        moveTargets(currentTab).forEach { target ->
                            DropdownMenuItem(
                                text = { Text(moveTargetLabel(target)) },
                                onClick = {
                                    moveMenuExpanded = false
                                    onMoveTo(target)
                                },
                            )
                        }
                    }
                }
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.action_filter),
                    )
                }
                // F5：全选 / 删除等次要操作进溢出菜单
                Box {
                    IconButton(onClick = { moreMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.action_more),
                        )
                    }
                    DropdownMenu(
                        expanded = moreMenuExpanded,
                        onDismissRequest = { moreMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_select_all)) },
                            enabled = isEditMode,
                            onClick = {
                                moreMenuExpanded = false
                                onSelectAllClick()
                            },
                        )
                        if (currentTab == MainTab.NoModify) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_delete)) },
                                enabled = isEditMode && hasSelection,
                                onClick = {
                                    moreMenuExpanded = false
                                    onDeleteClick()
                                },
                            )
                        }
                    }
                }
            }

            when (currentTab) {
                MainTab.Pending -> {
                    TextButton(onClick = onImportClick) {
                        Text(stringResource(R.string.action_import))
                    }
                }
                MainTab.NoModify -> {
                    // 删除已收入「更多」
                }
                MainTab.Confirmed -> {
                    TextButton(onClick = onExportClick) {
                        Text(stringResource(R.string.action_export_zip))
                    }
                }
            }
        }

        if (isFilterActive && filterSummaryLabels.isNotEmpty()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    filterSummaryLabels.forEach { label ->
                        AssistChip(
                            onClick = onFilterClick,
                            label = { Text(label) },
                        )
                    }
                }
                TextButton(onClick = onClearFilter) {
                    Text(stringResource(R.string.action_filter_clear))
                }
            }
        }
    }
}

private fun moveTargets(currentTab: MainTab): List<ImageStatus> =
    when (currentTab) {
        MainTab.Pending -> listOf(ImageStatus.Confirmed, ImageStatus.NoModify)
        MainTab.Confirmed -> listOf(ImageStatus.Pending, ImageStatus.NoModify)
        MainTab.NoModify -> listOf(ImageStatus.Pending, ImageStatus.Confirmed)
    }

@Composable
private fun moveTargetLabel(status: ImageStatus): String =
    when (status) {
        ImageStatus.Pending -> stringResource(R.string.move_to_pending)
        ImageStatus.Confirmed -> stringResource(R.string.move_to_confirmed)
        ImageStatus.NoModify -> stringResource(R.string.move_to_no_modify)
    }
