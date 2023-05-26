package com.github.polypoly.app.ui.map

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.polypoly.app.base.game.GameMilestoneRewardTransaction
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.data.GameRepository
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

    @SuppressLint("MutableCollectionMutableState")
    private var _milestonesToDisplay = mutableStateOf(arrayListOf<GameMilestoneRewardTransaction>())
    val newMilestonesToDisplay: State<ArrayList<GameMilestoneRewardTransaction>> get() = _milestonesToDisplay

    private var _interactableProperty = mutableStateOf(null as LocationProperty?)
    val interactableProperty: State<LocationProperty?> get() = _interactableProperty

    var goingToLocationProperty: LocationProperty? = null

    var currentPlayer: Player? = null

    private var locationSelectedData = MutableLiveData<LocationProperty>(null)

    fun getLocationSelected(): LiveData<LocationProperty> {
        return locationSelectedData
    }

    /**
     * Updates the distance walked by the user by adding the given value to the current value.
     *
     * @param distance The distance to add to the current distance walked.
     */
    fun addDistanceWalked(distance: Float) {
        viewModelScope.launch(dispatcher) {
            if(distance < 10000){
                computeMilestones(distance, _distanceWalked.value)
                _distanceWalked.value += distance
            }

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
     * Computes the milestones reached by the user and adds them to the list of new milestones to display.
     */
    private fun computeMilestones(addedDistance: Float, oldDistance : Float) {
        viewModelScope.launch(dispatcher) {
            val milestoneDistance = GameMilestoneRewardTransaction.milestoneRewardDistance
            val distanceToNextMilestone = oldDistance % milestoneDistance
            val milestonesNewlyReached = ((distanceToNextMilestone + addedDistance) / milestoneDistance).toInt()
            val newMilestones = arrayListOf<GameMilestoneRewardTransaction>()
            for (i in 1..milestonesNewlyReached) {
                GameRepository.player?.incrementLastMilestone()
                val milestoneTransaction = GameMilestoneRewardTransaction(
                    if (GameRepository.player != null) GameRepository.player!!.getLastMilestone() else 0
                )
                GameRepository.game?.transactions?.add(milestoneTransaction)
                newMilestones.add(milestoneTransaction)
            }
            newMilestones.addAll(_milestonesToDisplay.value)
            _milestonesToDisplay.value = newMilestones
        }
    }

    /**
     * Sets the closest location to the given location.
     *
     * @param locationProperty The location to set as the closest location.
     */
    fun setInteractableLocation(locationProperty: LocationProperty?) {
        viewModelScope.launch(dispatcher) {
            _interactableProperty.value = locationProperty
        }
    }

    /**
     * Picks the given location as the one selected. Pass null to unselect the current location.
     * @param location location to select
     */
    fun selectLocation(location: LocationProperty?) {
        locationSelectedData.value = location
    }
}