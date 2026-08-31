package com.pictureorganizer.ui.renametemplate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pictureorganizer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameTemplateManageScreen(
    viewModel: RenameTemplateManageViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RenameTemplateManageUiEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(context.getString(effect.messageResId))
                is RenameTemplateManageUiEffect.ShowMessageText ->
                    snackbarHostState.showSnackbar(effect.text)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.rename_template_manage_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(RenameTemplateManageUiEvent.OpenAdd) },
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
            }
        },
    ) { innerPadding ->
        if (state.templates.isEmpty()) {
            Text(
                text = stringResource(R.string.rename_template_empty),
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(24.dp),
            )
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            ) {
                items(state.templates, key = { it.id }) { template ->
                    ListItem(
                        headlineContent = { Text(template.name) },
                        supportingContent = {
                            Text(
                                buildString {
                                    append(template.pattern)
                                    if (template.isDefault) {
                                        append(" · ")
                                        append(context.getString(R.string.tag_manage_default_badge))
                                    }
                                },
                            )
                        },
                        trailingContent = {
                            Row {
                                if (!template.isDefault) {
                                    IconButton(
                                        onClick = {
                                            viewModel.onEvent(
                                                RenameTemplateManageUiEvent.SetDefault(template.id),
                                            )
                                        },
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null)
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            RenameTemplateManageUiEvent.OpenEdit(template),
                                        )
                                    },
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            RenameTemplateManageUiEvent.RequestDelete(template.id),
                                        )
                                    },
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                }
                            }
                        },
                        modifier =
                            Modifier.clickable {
                                viewModel.onEvent(RenameTemplateManageUiEvent.OpenEdit(template))
                            },
                    )
                }
            }
        }
    }

    state.dialog?.let { dialog ->
        RenameTemplateEditDialog(
            dialog = dialog,
            onNameChanged = {
                viewModel.onEvent(RenameTemplateManageUiEvent.NameChanged(it))
            },
            onPatternChanged = {
                viewModel.onEvent(RenameTemplateManageUiEvent.PatternChanged(it))
            },
            onDefaultChanged = {
                viewModel.onEvent(RenameTemplateManageUiEvent.DefaultChanged(it))
            },
            onSave = { viewModel.onEvent(RenameTemplateManageUiEvent.Save) },
            onDismiss = { viewModel.onEvent(RenameTemplateManageUiEvent.DismissDialog) },
        )
    }

    state.confirmDeleteId?.let {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(RenameTemplateManageUiEvent.CancelDelete)
            },
            title = { Text(stringResource(R.string.action_delete)) },
            text = { Text(stringResource(R.string.rename_template_confirm_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(RenameTemplateManageUiEvent.ConfirmDelete)
                }) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onEvent(RenameTemplateManageUiEvent.CancelDelete)
                }) {
                    Text(stringResource(R.string.detail_tag_cancel))
                }
            },
        )
    }
}

@Composable
private fun RenameTemplateEditDialog(
    dialog: RenameTemplateDialogState,
    onNameChanged: (String) -> Unit,
    onPatternChanged: (String) -> Unit,
    onDefaultChanged: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val title =
        if (dialog.editingId == null) {
            stringResource(R.string.rename_template_add)
        } else {
            stringResource(R.string.rename_template_edit)
        }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = dialog.name,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.tag_manage_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = dialog.pattern,
                    onValueChange = onPatternChanged,
                    label = { Text(stringResource(R.string.rename_template_pattern_label)) },
                    supportingText = {
                        Text(stringResource(R.string.rename_template_pattern_hint))
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = dialog.isDefault,
                        onCheckedChange = onDefaultChanged,
                    )
                    Text(stringResource(R.string.tag_manage_as_default))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(stringResource(R.string.detail_tag_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.detail_tag_cancel))
            }
        },
    )
}
