package com.example.squeezr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream



@Composable
fun ResultPreview(
    viewModel: compressionViewModel
){
    val context = LocalContext.current
    val bitmap: Bitmap? = viewModel.compressedBitmap

    // Get original file size from URI
    val originalSizeBytes = viewModel.uncompressedUri?.let { uri ->
        getFileSizeFromUri(context, uri)
    } ?: 0
    val mimeType = viewModel.uncompressedUri?.let { uri ->
        context.contentResolver.getType(uri)
    }
    // Calculate actual compressed file size using byte array
    val compressedByteArray = bitmap?.let {
        it.toByteArray(
            format = when(mimeType){
                "image/jpeg" -> Bitmap.CompressFormat.JPEG
                "image/png" -> Bitmap.CompressFormat.PNG
                else -> Bitmap.CompressFormat.JPEG
            } ?: Bitmap.CompressFormat.JPEG,
            quality = 100
        )
    }
    val compressedSizeBytes = compressedByteArray?.size ?: 0

    // Format file sizes
    val compressedSizeText = formatFileSize(compressedSizeBytes)
    val originalSizeText = formatFileSize(originalSizeBytes)
    val compressionRatio = if (originalSizeBytes > 0) {
        ((originalSizeBytes - compressedSizeBytes).toFloat() / originalSizeBytes * 100).toInt()
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Image that takes remaining space above the panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Takes all available space above panel
            contentAlignment = Alignment.Center
        ) {
            viewModel.compressedBitmap?.let {
                AsyncImage(model = it, contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        }

        // Settings Panel at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .offset(y = 20.dp) // Push bottom part outside view
                .zIndex(1f)
        ) {
            Surface(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header for the settings panel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Image Compressed!!",
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                        IconButton(
                            onClick = {
                                // Handle close action
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                modifier = Modifier.padding(0.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    // File Size Information
                    /*Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Original: $originalSizeText",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Compressed: $compressedSizeText",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }

                    if (compressionRatio > 0) {
                        Text(
                            text = "Size reduced by $compressionRatio%",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50), // Green color
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Image Dimensions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Width: ${bitmap?.width ?: 0}px",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Height: ${bitmap?.height ?: 0}px",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
*/
                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                // Save compressed byte array to storage
                                compressedByteArray?.let { byteArray ->
                                    // Handle save to storage with byte array
                                    // saveByteArrayToFile(context, byteArray, "compressed_image.jpg")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Text(
                                text = "Save",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = {
                                // Share compressed byte array
                                compressedByteArray?.let { byteArray ->
                                    // Handle share with byte array
                                    // shareByteArray(context, byteArray, "image/jpeg")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text(
                                text = "Share",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Get file size from URI using ContentResolver
 * @param context Android context
 * @param uri The file URI
 * @return Size in bytes
 */
fun getFileSizeFromUri(context: Context, uri: Uri): Long {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            cursor.getLong(sizeIndex)
        } ?: 0L
    } catch (e: Exception) {
        // Fallback: try to read the file and measure
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.available().toLong()
            } ?: 0L
        } catch (e2: Exception) {
            0L
        }
    }
}

/**
 * Calculate the actual compressed file size of a bitmap
 * @param bitmap The bitmap to compress
 * @param quality Compression quality (0-100)
 * @param format Compression format (default: JPEG)
 * @return Size in bytes
 */
fun calculateCompressedSize(
    bitmap: Bitmap,
    quality: Int,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
): Int {
    val stream = ByteArrayOutputStream()
    bitmap.compress(format, quality, stream)
    val byteArray = stream.toByteArray()
    stream.close()
    return byteArray.size
}

/**
 * Format file size to human readable format
 * @param bytes Size in bytes (can be Long or Int)
 * @return Formatted string (e.g., "1.2 MB", "345 KB")
 */
fun formatFileSize(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 -> {
            val mb = bytes / (1024.0 * 1024.0)
            String.format("%.2f MB", mb)
        }
        bytes >= 1024 -> {
            val kb = bytes / 1024.0
            String.format("%.1f KB", kb)
        }
        else -> "$bytes B"
    }
}

// Overloaded function for Int parameter
fun formatFileSize(bytes: Int): String = formatFileSize(bytes.toLong())

/**
 * Extension function to convert Bitmap to ByteArray
 */
fun Bitmap.toByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 80
): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(format, quality, stream)
    val byteArray = stream.toByteArray()
    stream.close()
    return byteArray
}