package com.pictureorganizer.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pictureorganizer.R
import com.pictureorganizer.model.ImageListSort
import com.pictureorganizer.model.TagFilterCriteria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    viewModel: FilterViewModel,
    onBack: () -> Unit,
    onApplied: (TagFilterCriteria) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FilterUiEffect.ApplyAndClose -> onApplied(effect.criteria)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.filter_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
        bottomBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { viewModel.onEvent(FilterUiEvent.ClearAndApply) }) {
                    Text(stringResource(R.string.action_filter_clear))
                }
                TextButton(onClick = { viewModel.onEvent(FilterUiEvent.Apply) }) {
                    Text(stringResource(R.string.filter_apply))
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
        ) {
            OutlinedTextField(
                value = state.nameContains,
                onValueChange = { viewModel.onEvent(FilterUiEvent.NameContainsChanged(it)) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                singleLine = true,
                label = { Text(stringResource(R.string.filter_name_contains)) },
            )
            Text(
                text = stringResource(R.string.filter_sort_label),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
            SortOption(
                label = stringResource(R.string.filter_sort_imported_desc),
                selected = state.sort == ImageListSort.ImportedAtDesc,
                onClick = {
                    viewModel.onEvent(FilterUiEvent.SortChanged(ImageListSort.ImportedAtDesc))
                },
            )
            SortOption(
                label = stringResource(R.string.filter_sort_imported_asc),
                selected = state.sort == ImageListSort.ImportedAtAsc,
                onClick = {
                    viewModel.onEvent(FilterUiEvent.SortChanged(ImageListSort.ImportedAtAsc))
                },
            )
            SortOption(
                label = stringResource(R.string.filter_sort_name_asc),
                selected = state.sort == ImageListSort.NameAsc,
                onClick = {
                    viewModel.onEvent(FilterUiEvent.SortChanged(ImageListSort.NameAsc))
                },
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.includeUntagged,
                    onCheckedChange = { viewModel.onEvent(FilterUiEvent.ToggleUntagged) },
                )
                Text(stringResource(R.string.filter_untagged))
            }
            state.availableTags.forEach { name ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = name in state.selectedTagNames,
                        onCheckedChange = { viewModel.onEvent(FilterUiEvent.ToggleTag(name)) },
                    )
                    Text(name)
                }
            }
            if (state.availableTags.isEmpty()) {
                Text(
                    text = stringResource(R.string.filter_no_library_tags),
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun SortOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    role = Role.RadioButton,
                ).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
    }
}
