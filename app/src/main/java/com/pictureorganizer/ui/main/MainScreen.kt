package com.pictureorganizer.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pictureorganizer.R
import com.pictureorganizer.data.repository.MockImageRepository
import com.pictureorganizer.ui.main.tab.ImageListTab
import com.pictureorganizer.ui.theme.PictureOrganizerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToImport: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MainUiEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(effect.messageResId)
                    )
                }
                MainUiEffect.NavigateToImport -> onNavigateToImport()
            }
        }
    }

    val currentItems = state.itemsForTab(state.selectedTab)
    val emptyMessage = when (state.selectedTab) {
        MainTab.Pending -> stringResource(R.string.empty_pending)
        MainTab.Confirmed -> stringResource(R.string.empty_confirmed)
        MainTab.NoModify -> stringResource(R.string.empty_no_modify)
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = state.selectedTab == MainTab.Pending,
                    onClick = { viewModel.onEvent(MainUiEvent.SelectTab(MainTab.Pending)) },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_pending)) }
                )
                NavigationBarItem(
                    selected = state.selectedTab == MainTab.Confirmed,
                    onClick = { viewModel.onEvent(MainUiEvent.SelectTab(MainTab.Confirmed)) },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_confirmed)) }
                )
                NavigationBarItem(
                    selected = state.selectedTab == MainTab.NoModify,
                    onClick = { viewModel.onEvent(MainUiEvent.SelectTab(MainTab.NoModify)) },
                    icon = { Icon(Icons.Default.Block, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_no_modify)) }
                )
            }
        }
    ) { innerPadding ->
        ImageListTab(
            currentTab = state.selectedTab,
            items = currentItems,
            emptyMessage = emptyMessage,
            isEditMode = state.isEditMode,
            selectedIds = state.selectedIds,
            onEditClick = { viewModel.onEvent(MainUiEvent.ToggleEditMode) },
            onSelectAllClick = { viewModel.onEvent(MainUiEvent.SelectAll) },
            onMoveTo = { viewModel.onEvent(MainUiEvent.MoveSelectedTo(it)) },
            onImportClick = { viewModel.onEvent(MainUiEvent.ImportImages) },
            onDeleteClick = { viewModel.onEvent(MainUiEvent.DeleteSelected) },
            onToggleSelect = { viewModel.onEvent(MainUiEvent.ToggleSelect(it)) },
            onItemClick = onNavigateToDetail,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    PictureOrganizerTheme {
        MainScreen(
            viewModel = MainViewModel(MockImageRepository)
        )
    }
}
