@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.imageviewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.example.squeezr.compressionViewModel
import com.example.squeezr.onCompressButtonClick

@Composable
fun ImageViewerWithSettings(
    imageModel: Uri,
    compressionViewModel: compressionViewModel,
    workManager: WorkManager,
    navHostController: NavHostController
) {
    val bytes = LocalContext.current.contentResolver.openInputStream(imageModel)?.use {
        it.readBytes()
    }
    var bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes?.size ?: 0)

    val sizeSliderState = remember { mutableStateOf(bytes?.size?.div(1024) ?: 0) }
    val widthSliderState = remember { mutableStateOf(bitmap.width) }
    val heightSliderState = remember { mutableStateOf(bitmap.height) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Image that takes remaining space above the panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Takes all available space above panel
        ) {

            AsyncImage(
                model = imageModel, // Replace with your image resource
                contentDescription = "Main Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
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
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            text = "Adjust compression settings",
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                        IconButton(
                            onClick = {},
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close",modifier = Modifier.padding(0.dp))
                        }
                    }

                    SliderRow(
                        label = "Size",
                        valueState = sizeSliderState,
                        unit = "kb",
                        maxValue = bytes?.size?.div(1024) ?: 0
                    )

                    // Resolution Settings section
                    Text(
                        text = "Resolution Settings:",
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )

                    // Width slider
                    SliderRow(
                        label = "width",
                        maxValue = bitmap.width,
                        valueState = widthSliderState,
                        unit = "px"
                    )

                    // Height slider
                    SliderRow(
                        label = "height",
                        maxValue = bitmap.height,
                        valueState = heightSliderState,
                        unit = "px"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Compress button
                    Button(
                        onClick = {
                            onCompressButtonClick(compressionViewModel, workManager,navHostController,
                                sizeSliderState.value,
                                widthSliderState.value,
                                heightSliderState.value
                            )
                            println(widthSliderState.value)
                            println(heightSliderState.value)
                            println(sizeSliderState.value)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = "Compress",
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
fun SliderRow(
    label: String,
    maxValue: Int,
    valueState: MutableState<Int>,
    unit: String = "",
) {
    var textValue by remember { mutableStateOf(valueState.value.toString()) }
    var sliderValue by remember { mutableStateOf(valueState.value) }
    var isDragging by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.width(40.dp)
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {
            Slider(
                value = sliderValue.toFloat(),
                onValueChange = {
                    sliderValue = it.toInt()
                    textValue = it.toString()
                    valueState.value = sliderValue
                    isDragging = true

                },
                onValueChangeFinished = {
                    isDragging = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    activeTrackColor = Color.Black,
                    inactiveTrackColor = Color.Gray
                ),
                valueRange = 0f..maxValue.toFloat()
            )

            // Value bubble that appears when dragging
            if (isDragging) {
                val sliderWidth = 200.dp // Approximate slider track width
                val thumbPosition = (sliderValue.toFloat() / maxValue.toFloat()) * sliderWidth

                Box(
                    modifier = Modifier
                        .offset(
                            x = thumbPosition - 15.dp, // Center the bubble on thumb
                            y = (-40).dp // Position above slider
                        )
                        .background(
                            Color.Black,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .wrapContentSize()
                ) {
                    Text(
                        text = "${((sliderValue.toFloat() /maxValue.toFloat())*100).toInt()}",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }

        BasicTextField(
            value = textValue.toFloat().toInt().toString(),
            onValueChange = { newValue ->
                textValue = newValue

                // Handle empty string or invalid input
                if (newValue.isBlank()) {
                    // Don't update slider when text is empty, wait for user to enter value
                    return@BasicTextField
                }

                // Try to parse the float value safely
                newValue.toIntOrNull()?.let { intValue ->
                    // Clamp the value between 0 and maxValue
                    sliderValue = intValue.coerceIn(0, valueState.value)
                    valueState.value = sliderValue
                } ?: run {
                    // If parsing fails, revert to previous valid value
                    textValue = sliderValue.toString()
                    valueState.value = sliderValue
                }
            },
            modifier = Modifier
                .width(70.dp)
                .padding(start = 8.dp)
                .background(
                    Color.Gray.copy(alpha = 0.1f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 4.dp),
            textStyle = TextStyle(
                fontSize = 11.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    if (textValue.isEmpty()) {
                        Text(
                            text = "0",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )

        Text(
            text = unit,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.wrapContentSize().padding(start = 4.dp)
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ImageViewerPreview() {
//    MaterialTheme {
//        ImageViewerWithSettings()
//    }
//}

