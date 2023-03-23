package com.github.polypoly.app.map

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel for the Map screen that stores the distance walked by the user.
 */
class MapViewModel : ViewModel() {
    private var _distanceWalked = mutableStateOf(0f)
    val distanceWalked: State<Float> get() = _distanceWalked

    /**
     * Updates the distance walked by the user by adding the given value to the current value.
     *
     * @param distance The distance to add to the current distance walked.
     */
    fun updateDistanceWalked(distance: Float) {
        viewModelScope.launch {
            _distanceWalked.value += distance
        }
    }

    /**
     * Resets the distance walked by the user to zero.
     */
    fun resetDistanceWalked() {
        viewModelScope.launch {
            _distanceWalked.value = 0f
        }
    }
}