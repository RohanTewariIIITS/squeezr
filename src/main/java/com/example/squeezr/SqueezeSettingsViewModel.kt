package com.example.squeezr

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SqueezeSettingsViewModel(): ViewModel() {

    val sizeSliderState = mutableStateOf(100L)

    val widthSliderState = mutableStateOf(1920)

    val heightSliderState = mutableStateOf(1080)
}