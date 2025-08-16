package com.example.squeezr

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.example.imageviewer.ImageViewerWithSettings

@Composable
fun Navigation(
    imageModel: Uri,
    viewModel: compressionViewModel,
    workManager: WorkManager,
    settingsViewModel: SqueezeSettingsViewModel
){

    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Settings
    ) {
        composable(Screens.Settings){
            ImageViewerWithSettings(
                imageModel, viewModel, workManager,
                navHostController = navController
            )
        }
        composable(Screens.Preview) {
            ResultPreview(viewModel)
        }
    }

}