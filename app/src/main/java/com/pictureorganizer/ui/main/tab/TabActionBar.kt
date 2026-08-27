package com.pictureorganizer.ui.main.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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

@Composable
fun TabActionBar(
    currentTab: MainTab,
    isEditMode: Boolean,
    hasSelection: Boolean,
    onEditClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onMoveTo: (ImageStatus) -> Unit,
    onImportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var moveMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onEditClick) {
                Text(
                    if (isEditMode) stringResource(R.string.action_done)
                    else stringResource(R.string.action_edit)
                )
            }
            TextButton(
                onClick = onSelectAllClick,
                enabled = isEditMode
            ) {
                Text(stringResource(R.string.action_select_all))
            }
            TextButton(
                onClick = { moveMenuExpanded = true },
                enabled = isEditMode && hasSelection
            ) {
                Text(stringResource(R.string.action_move_to))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = moveMenuExpanded,
                onDismissRequest = { moveMenuExpanded = false }
            ) {
                moveTargets(currentTab).forEach { target ->
                    DropdownMenuItem(
                        text = { Text(moveTargetLabel(target)) },
                        onClick = {
                            moveMenuExpanded = false
                            onMoveTo(target)
                        }
                    )
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
                TextButton(
                    onClick = onDeleteClick,
                    enabled = isEditMode && hasSelection
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            }
            MainTab.Confirmed -> Unit
        }
    }
}

private fun moveTargets(currentTab: MainTab): List<ImageStatus> = when (currentTab) {
    MainTab.Pending -> listOf(ImageStatus.Confirmed, ImageStatus.NoModify)
    MainTab.Confirmed -> listOf(ImageStatus.Pending, ImageStatus.NoModify)
    MainTab.NoModify -> listOf(ImageStatus.Pending, ImageStatus.Confirmed)
}

@Composable
private fun moveTargetLabel(status: ImageStatus): String = when (status) {
    ImageStatus.Pending -> stringResource(R.string.move_to_pending)
    ImageStatus.Confirmed -> stringResource(R.string.move_to_confirmed)
    ImageStatus.NoModify -> stringResource(R.string.move_to_no_modify)
}
