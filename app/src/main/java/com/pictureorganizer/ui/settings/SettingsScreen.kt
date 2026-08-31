package com.pictureorganizer.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pictureorganizer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToTagManage: () -> Unit,
    onNavigateToRenameTemplates: () -> Unit,
    onNavigateToDefaultTags: () -> Unit,
    onNavigateToTutorial: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
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
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            item {
                SettingsRow(
                    title = stringResource(R.string.settings_tag_manage),
                    desc = stringResource(R.string.settings_tag_manage_desc),
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    onClick = onNavigateToTagManage,
                )
            }
            item {
                SettingsRow(
                    title = stringResource(R.string.settings_rename_templates),
                    desc = stringResource(R.string.settings_rename_templates_desc),
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    onClick = onNavigateToRenameTemplates,
                )
            }
            item {
                SettingsRow(
                    title = stringResource(R.string.settings_default_tags),
                    desc = stringResource(R.string.settings_default_tags_desc),
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    onClick = onNavigateToDefaultTags,
                )
            }
            item {
                SettingsRow(
                    title = stringResource(R.string.settings_tutorial),
                    desc = stringResource(R.string.settings_tutorial_desc),
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    onClick = onNavigateToTutorial,
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    desc: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(desc) },
        leadingContent = icon,
        trailingContent = {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
}
