package com.pictureorganizer.ui.exportzip

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pictureorganizer.R
import com.pictureorganizer.model.TagFilterCriteria
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExportZipScreen(
    viewModel: ExportZipViewModel,
    onBack: () -> Unit,
    onNavigateToFilter: (TagFilterCriteria) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val untaggedLabel = stringResource(R.string.filter_untagged)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ExportZipUiEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(context.getString(effect.messageResId))
                is ExportZipUiEffect.ShowMessageText ->
                    snackbarHostState.showSnackbar(effect.text)
                is ExportZipUiEffect.ShareFile -> {
                    val file = File(effect.absolutePath)
                    val uri =
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file,
                        )
                    val intent =
                        Intent(Intent.ACTION_SEND).apply {
                            type = "application/zip"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, effect.fileName)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            context.getString(R.string.export_zip_share),
                        ),
                    )
                }
            }
        }
    }

    BackHandler {
        if (!state.isExporting) onBack()
    }

    val summaryLabels =
        buildList {
            addAll(state.filter.selectedTagNames.sorted())
            if (state.filter.includeUntagged) add(untaggedLabel)
            val q = state.filter.nameContains.trim()
            if (q.isNotEmpty()) add(stringResource(R.string.filter_name_contains) + ": $q")
        }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.export_zip_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        enabled = !state.isExporting,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
        ) {
            Text(
                text =
                    stringResource(
                        R.string.export_zip_stats,
                        state.confirmedCount,
                        state.matchedCount,
                    ),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = { onNavigateToFilter(state.filter) },
                    enabled = !state.isExporting,
                ) {
                    Text(stringResource(R.string.action_filter))
                }
                if (state.isFilterActive) {
                    TextButton(
                        onClick = { viewModel.onEvent(ExportZipUiEvent.ClearFilter) },
                        enabled = !state.isExporting,
                    ) {
                        Text(stringResource(R.string.action_filter_clear))
                    }
                }
            }
            if (summaryLabels.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    summaryLabels.forEach { label ->
                        AssistChip(
                            onClick = { onNavigateToFilter(state.filter) },
                            label = { Text(label) },
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.export_zip_no_filter_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.onEvent(ExportZipUiEvent.StartExport) },
                enabled = !state.isExporting && state.matchedCount > 0,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.export_zip_start))
            }

            if (state.isExporting) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator()
                    Text(
                        text =
                            stringResource(
                                R.string.export_zip_progress,
                                state.progressCurrent,
                                state.progressTotal,
                            ),
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            }

            if (state.results.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.export_zip_results),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.results, key = { it.absolutePath }) { result ->
                        ListItem(
                            headlineContent = { Text(result.label) },
                            supportingContent = {
                                Text(
                                    stringResource(
                                        R.string.export_zip_result_item,
                                        result.fileName,
                                        result.entryCount,
                                    ),
                                )
                            },
                            trailingContent = {
                                TextButton(onClick = { viewModel.requestShare(result) }) {
                                    Text(stringResource(R.string.export_zip_share))
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
