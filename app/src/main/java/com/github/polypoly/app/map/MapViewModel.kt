package com.github.polypoly.app.map

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.polypoly.app.game.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for the Map screen that stores the distance walked by the user, as well as
 * the closest location to the user.
 */
class MapViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private var _distanceWalked = mutableStateOf(0f)
    val distanceWalked: State<Float> get() = _distanceWalked

    private var _closeLocation = mutableStateOf(null as Location?)
    val closeLocation: State<Location?> get() = _closeLocation

    /**
     * Updates the distance walked by the user by adding the given value to the current value.
     *
     * @param distance The distance to add to the current distance walked.
     */
    fun addDistanceWalked(distance: Float) {
        viewModelScope.launch(dispatcher) {
            _distanceWalked.value += distance
        }
    }

    /**
     * Resets the distance walked by the user to zero.
     */
    fun resetDistanceWalked() {
        viewModelScope.launch(dispatcher) {
            _distanceWalked.value = 0f
        }
    }

    /**
     * Sets the closest location to the given location.
     *
     * @param location The location to set as the closest location.
     */
    fun setCloseLocation(location: Location?) {
        viewModelScope.launch(dispatcher) {
            _closeLocation.value = location
        }
    }
}