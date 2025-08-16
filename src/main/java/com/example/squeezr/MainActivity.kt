package com.example.squeezr

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.example.imageviewer.ImageViewerWithSettings
import com.example.squeezr.ui.theme.SqueezrTheme

class MainActivity : ComponentActivity() {

    private lateinit var workManager: WorkManager
    private val viewModel by viewModels<compressionViewModel>()
    val settingsViewModel by viewModels<SqueezeSettingsViewModel>()
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate called")
        workManager = WorkManager.getInstance(applicationContext)

        // Handle intent when app is first launched
        handleIntent(intent)

        enableEdgeToEdge()
        setContent {
            Scaffold { innerpadding->
                SqueezrTheme {
                    Log.d(TAG, "App Started!!!")

                    val workerResult = viewModel.workId?.let { id ->
                        workManager.getWorkInfoByIdLiveData(id).observeAsState().value
                    }

                    LaunchedEffect(key1 = workerResult?.outputData) {
                        if (workerResult?.outputData != null) {
                            val filePath = workerResult.outputData.getString(
                                PhotoCompressionWorker.KEY_RESULT_PATH
                            )
                            filePath?.let {
                                val bitmap = BitmapFactory.decodeFile(it)
                                viewModel.updateCompressedBitmap(bitmap)
                            }
                        }
                    }


                    // Show a default message if no image is loaded
                    if (viewModel.uncompressedUri == null && viewModel.compressedBitmap == null) {
                        Column(/////////////////////////////////////////////////
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Share an image with this app to compress it!")

                        }
                    } else {
                        viewModel.uncompressedUri?.let { image ->
                            Box (
                                modifier = Modifier.fillMaxSize().padding(innerpadding)
                            ){
                                Navigation(
                                    image,
                                    viewModel = viewModel,
                                    workManager = workManager,
                                    settingsViewModel = settingsViewModel
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent called with action: ${intent.action}")
        setIntent(intent) // Important: update the activity's intent
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Log.d(TAG, "handleIntent called with action: ${intent.action}")

        when (intent.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    Log.d(TAG, "Received image sharing intent")
                    handleSingleImage(intent)
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                if (intent.type?.startsWith("image/") == true) {
                    Log.d(TAG, "Received multiple image sharing intent")
                    handleMultipleImages(intent)
                }
            }
        }
    }

    private fun handleSingleImage(intent: Intent) {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }

        uri?.let { imageUri ->
            Log.d(TAG, "Processing image URI: $imageUri")
            processImage(imageUri)
        } ?: run {
            Log.w(TAG, "No image URI found in intent")
        }
    }

    private fun handleMultipleImages(intent: Intent) {
        val uris = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
        }

        // For now, just handle the first image
        uris?.firstOrNull()?.let { imageUri ->
            Log.d(TAG, "Processing first image from multiple: $imageUri")
            processImage(imageUri)
        } ?: run {
            Log.w(TAG, "No image URIs found in multiple intent")
        }
    }

    private fun processImage(uri: Uri) {
        viewModel.updateUncompressedUri(uri)
    }
}