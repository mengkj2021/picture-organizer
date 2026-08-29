package com.pictureorganizer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pictureorganizer.PictureOrganizerApplication
import com.pictureorganizer.ui.imagedetail.ImageDetailScreen
import com.pictureorganizer.ui.importimages.ImportScreen
import com.pictureorganizer.ui.main.MainScreen
import com.pictureorganizer.ui.main.MainViewModel
import com.pictureorganizer.ui.splash.SplashScreen

@Composable
fun PictureOrganizerNavHost() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as PictureOrganizerApplication
    val repository = app.imageRepository

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MAIN) {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(repository)
            )
            MainScreen(
                viewModel = mainViewModel,
                onNavigateToImport = {
                    navController.navigate(Routes.IMPORT_IMAGES)
                },
                onNavigateToDetail = { imageId ->
                    navController.navigate(Routes.imageDetail(imageId))
                }
            )
        }
        composable(Routes.IMPORT_IMAGES) {
            ImportScreen(
                onBack = { navController.popBackStack() },
                onImportFinished = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.IMAGE_DETAIL,
            arguments = listOf(navArgument("imageId") { type = NavType.StringType })
        ) { entry ->
            val imageId = entry.arguments?.getString("imageId").orEmpty()
            ImageDetailScreen(
                imageId = imageId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
