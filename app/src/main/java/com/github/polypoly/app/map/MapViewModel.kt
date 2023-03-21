package com.github.polypoly.app.map

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private var _distanceWalked = mutableStateOf(0f)
    val distanceWalked: State<Float> get() = _distanceWalked

    fun updateDistanceWalked(distance: Float) {
        viewModelScope.launch {
            _distanceWalked.value += distance
        }
    }

    fun resetDistanceWalked() {
        viewModelScope.launch {
            _distanceWalked.value = 0f
        }
    }
}