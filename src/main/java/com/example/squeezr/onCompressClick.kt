package com.example.squeezr

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf


fun onCompressButtonClick(
    compressionViewModel: compressionViewModel,
    workManager: WorkManager,
    navController: NavHostController,
    maxSizeInkb: Int,
    maxWidth: Int,
    maxHeight: Int
){
    val uri = compressionViewModel.uncompressedUri
    val request = OneTimeWorkRequestBuilder<PhotoCompressionWorker>()
        .setInputData(
            workDataOf(
                PhotoCompressionWorker.KEY_CONTENT_URI to uri.toString(),
                PhotoCompressionWorker.KEY_COMPRESSION_THRESHOLD to 1024 * maxSizeInkb,
                PhotoCompressionWorker.KEY_COMPRESSION_WIDTH to maxWidth,
                PhotoCompressionWorker.KEY_COMPRESSION_HEIGHT to maxHeight
            )
        )
        .build()

    compressionViewModel.updateWorkId(request.id)
    workManager.enqueue(request)

    navController.navigate(Screens.Preview)
}