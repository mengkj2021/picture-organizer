package com.pictureorganizer.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pictureorganizer.PictureOrganizerApplication
import com.pictureorganizer.model.TagFilterCriteria
import com.pictureorganizer.ui.defaulttags.DefaultTagsScreen
import com.pictureorganizer.ui.exportzip.ExportZipScreen
import com.pictureorganizer.ui.exportzip.ExportZipUiEvent
import com.pictureorganizer.ui.exportzip.ExportZipViewModel
import com.pictureorganizer.ui.filter.FilterScreen
import com.pictureorganizer.ui.filter.FilterViewModel
import com.pictureorganizer.ui.imagedetail.ImageDetailScreen
import com.pictureorganizer.ui.importimages.ImportScreen
import com.pictureorganizer.ui.main.MainScreen
import com.pictureorganizer.ui.main.MainUiEvent
import com.pictureorganizer.ui.main.MainViewModel
import com.pictureorganizer.ui.renametemplate.RenameTemplateManageScreen
import com.pictureorganizer.ui.renametemplate.RenameTemplateManageViewModel
import com.pictureorganizer.ui.settings.SettingsScreen
import com.pictureorganizer.ui.splash.SplashScreen
import com.pictureorganizer.ui.tagmanage.TagManageScreen
import com.pictureorganizer.ui.tutorial.TutorialScreen
import kotlinx.coroutines.launch

@Composable
fun PictureOrganizerNavHost() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as PictureOrganizerApplication
    val repository = app.imageRepository
    val tagRepository = app.tagRepository
    val userPreferences = app.userPreferencesRepository
    val renameTemplateRepository = app.renameTemplateRepository
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                resolveTutorialCompleted = { userPreferences.isTutorialCompleted() },
                onNavigateNext = { tutorialCompleted ->
                    val destination =
                        if (tutorialCompleted) {
                            Routes.MAIN
                        } else {
                            Routes.tutorial(fromSettings = false)
                        }
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = Routes.TUTORIAL,
            arguments =
                listOf(
                    navArgument("fromSettings") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                ),
        ) { entry ->
            val fromSettings = entry.arguments?.getBoolean("fromSettings") ?: false
            TutorialScreen(
                onFinished = {
                    scope.launch {
                        if (!fromSettings) {
                            userPreferences.setTutorialCompleted(true)
                            navController.navigate(Routes.MAIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                },
            )
        }
        composable(Routes.MAIN) { entry ->
            val mainViewModel: MainViewModel =
                viewModel(
                    factory = MainViewModel.Factory(repository, tagRepository),
                )
            val filterApplied by entry.savedStateHandle
                .getStateFlow(FilterResultKeys.APPLIED, false)
                .collectAsStateWithLifecycle()

            LaunchedEffect(filterApplied) {
                if (!filterApplied) return@LaunchedEffect
                val tagsRaw = entry.savedStateHandle.get<String>(FilterResultKeys.TAGS).orEmpty()
                val untagged = entry.savedStateHandle.get<Boolean>(FilterResultKeys.UNTAGGED) ?: false
                mainViewModel.onEvent(
                    MainUiEvent.SetTagFilter(
                        selectedTagNames = TagFilterCriteria.parseTagsParam(tagsRaw),
                        includeUntagged = untagged,
                    ),
                )
                entry.savedStateHandle[FilterResultKeys.APPLIED] = false
            }

            MainScreen(
                viewModel = mainViewModel,
                onNavigateToImport = {
                    navController.navigate(Routes.IMPORT_IMAGES)
                },
                onNavigateToDetail = { imageId ->
                    navController.navigate(Routes.imageDetail(imageId))
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToFilter = { criteria ->
                    navController.navigate(Routes.filter(criteria))
                },
                onNavigateToExportZip = {
                    navController.navigate(Routes.EXPORT_ZIP)
                },
            )
        }
        composable(Routes.EXPORT_ZIP) { entry ->
            val exportVm: ExportZipViewModel =
                viewModel(
                    factory = ExportZipViewModel.Factory(repository, app.fileManager),
                )
            val filterApplied by entry.savedStateHandle
                .getStateFlow(FilterResultKeys.APPLIED, false)
                .collectAsStateWithLifecycle()

            LaunchedEffect(filterApplied) {
                if (!filterApplied) return@LaunchedEffect
                val tagsRaw = entry.savedStateHandle.get<String>(FilterResultKeys.TAGS).orEmpty()
                val untagged = entry.savedStateHandle.get<Boolean>(FilterResultKeys.UNTAGGED) ?: false
                exportVm.onEvent(
                    ExportZipUiEvent.SetFilter(
                        TagFilterCriteria(
                            selectedTagNames = TagFilterCriteria.parseTagsParam(tagsRaw),
                            includeUntagged = untagged,
                        ),
                    ),
                )
                entry.savedStateHandle[FilterResultKeys.APPLIED] = false
            }

            ExportZipScreen(
                viewModel = exportVm,
                onBack = { navController.popBackStack() },
                onNavigateToFilter = { criteria ->
                    navController.navigate(Routes.filter(criteria))
                },
            )
        }
        composable(Routes.IMPORT_IMAGES) {
            ImportScreen(
                onBack = { navController.popBackStack() },
                onImportFinished = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.IMAGE_DETAIL,
            arguments = listOf(navArgument("imageId") { type = NavType.StringType }),
        ) { entry ->
            val imageId = entry.arguments?.getString("imageId").orEmpty()
            ImageDetailScreen(
                imageId = imageId,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToTagManage = {
                    navController.navigate(Routes.TAG_MANAGE)
                },
                onNavigateToRenameTemplates = {
                    navController.navigate(Routes.RENAME_TEMPLATE_MANAGE)
                },
                onNavigateToDefaultTags = {
                    navController.navigate(Routes.DEFAULT_TAGS)
                },
                onNavigateToTutorial = {
                    navController.navigate(Routes.tutorial(fromSettings = true))
                },
            )
        }
        composable(Routes.TAG_MANAGE) {
            TagManageScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.RENAME_TEMPLATE_MANAGE) {
            val vm: RenameTemplateManageViewModel =
                viewModel(
                    factory = RenameTemplateManageViewModel.Factory(renameTemplateRepository),
                )
            RenameTemplateManageScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.DEFAULT_TAGS) {
            DefaultTagsScreen(
                tagRepository = tagRepository,
                userPreferences = userPreferences,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = Routes.FILTER,
            arguments =
                listOf(
                    navArgument("tags") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("untagged") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                ),
        ) { entry ->
            val tagsRaw = Uri.decode(entry.arguments?.getString("tags").orEmpty())
            val untagged = entry.arguments?.getBoolean("untagged") ?: false
            val initial =
                TagFilterCriteria(
                    selectedTagNames = TagFilterCriteria.parseTagsParam(tagsRaw),
                    includeUntagged = untagged,
                )
            val filterViewModel: FilterViewModel =
                viewModel(
                    factory = FilterViewModel.Factory(tagRepository, initial),
                )
            FilterScreen(
                viewModel = filterViewModel,
                onBack = { navController.popBackStack() },
                onApplied = { criteria ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set(FilterResultKeys.TAGS, criteria.encodeTagsParam())
                        set(FilterResultKeys.UNTAGGED, criteria.includeUntagged)
                        set(FilterResultKeys.APPLIED, true)
                    }
                    navController.popBackStack()
                },
            )
        }
    }
}
