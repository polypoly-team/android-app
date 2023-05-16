package com.github.polypoly.app.ui.map

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.LocationProperty
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Marker

/**
 * ViewModel for the Map screen that stores the distance walked by the user, as well as
 * the closest location to the user.
 */
class MapViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private var _distanceWalked = mutableStateOf(0f)
    val distanceWalked: State<Float> get() = _distanceWalked

    private var _interactableProperty = mutableStateOf(null as LocationProperty?)
    val interactableProperty: State<LocationProperty?> get() = _interactableProperty

    lateinit var selectedMarker: Marker

    var goingToLocationProperty: LocationProperty? = null

    var currentPlayer: Player? = null

    val markerToLocationProperty = mutableMapOf<Marker, LocationProperty>()

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
     * @param locationProperty The location to set as the closest location.
     */
    fun setCloseLocation(locationProperty: LocationProperty?) {
        viewModelScope.launch(dispatcher) {
            _interactableProperty.value = locationProperty
        }
    }
}