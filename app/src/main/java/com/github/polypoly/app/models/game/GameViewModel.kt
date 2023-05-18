package com.github.polypoly.app.models.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.models.commons.LoadingModel
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.utils.global.GlobalInstances
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.utils.global.Settings.Companion.NUMBER_OF_LOCATIONS_ROLLED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.concurrent.CompletableFuture
import kotlin.random.Random
import kotlin.random.nextInt

class GameViewModel(
    game: Game,
    player: Player
): LoadingModel() {

    private val gameData: MutableLiveData<Game> = MutableLiveData(game)

    private val playerData: MutableLiveData<Player> = MutableLiveData(player)

    private val roundTurnData: MutableLiveData<Int> = MutableLiveData(game.currentRound)

    private val gameEndedData: MutableLiveData<Boolean> = MutableLiveData(false)

    private val playerStateData: MutableLiveData<PlayerState> = MutableLiveData(PlayerState.INIT)

    //used to determine if the player is close enough to a location to interact with it
    private val MAX_INTERACT_DISTANCE = 10.0 // meters

    init {
        setLoading(true)
        viewModelScope.launch {
            gameLoop()
        }
    }

    fun getGameData(): LiveData<Game> {
        return gameData
    }

    fun getPlayerData(): LiveData<Player> {
        return playerData
    }

    fun getRoundTurnData(): LiveData<Int> {
        return roundTurnData
    }

    fun getGameFinishedData(): LiveData<Boolean> {
        return gameEndedData
    }

    fun getPlayerStateData(): LiveData<PlayerState> {
        return playerStateData
    }

    private suspend fun gameLoop() {
        var currentGame = gameData.value

        while (currentGame != null && !currentGame.isGameFinished()) {
            playerStateData.value = PlayerState.ROLLING_DICE
            setLoading(false)

            delay(currentGame.rules.roundDuration.toLong() * 1000 * 60)

            playerStateData.value = PlayerState.TURN_FINISHED

            nextTurn()

            currentGame = gameData.value
        }
    }

    /**
     * Computes the next turn state and synchronizes with the other players
     */
    fun nextTurn() {
        setLoading(true)
        viewModelScope.launch {
            gameData.value?.nextTurn()

            synchronizeGame().thenApply { syncSucceeded ->
                if (syncSucceeded) {
                    roundTurnData.value = gameData.value?.currentRound ?: -1
                    gameEndedData.value = gameData.value?.isGameFinished() ?: false
                }
                setLoading(false)
            }
        }
    }

    private fun synchronizeGame(): CompletableFuture<Boolean> {
        val gameUpdated = gameData.value ?: return CompletableFuture.completedFuture(false)
        return remoteDB.getValue<Game>(gameUpdated.key).thenCompose { gameFound ->
            if (gameFound.currentRound < gameUpdated.currentRound) {
                gameData.value = gameUpdated
                remoteDB.setValue(gameUpdated)
            } else { // up to date version already on live db
                gameData.value = gameFound
                CompletableFuture.completedFuture(true)
            }
        }
    }

    private fun playerStateFSMTransition(expectedFrom: PlayerState, to: PlayerState) {
        if (playerStateData.value != expectedFrom) {
            throw IllegalStateException("Illegal state transition from ${playerStateData.value} instead of $expectedFrom to $to")
        }
        playerStateData.value = to
    }

    /**
     * Ends ROLLING_DICE state and moves to MOVING
     */
    fun diceRolled() {
        playerStateFSMTransition(PlayerState.ROLLING_DICE, PlayerState.MOVING)
    }

    /**
     * Ends MOVING state and moves to INTERACTING
     */
    fun locationReached() {
        playerStateFSMTransition(PlayerState.MOVING, PlayerState.INTERACTING)
    }

    /**
     * Ends INTERACTING state and moves to BETTING
     */
    fun startBetting() {
        playerStateFSMTransition(PlayerState.INTERACTING, PlayerState.BETTING)
    }

    /**
     * Ends BETTING state and moves back to INTERACTING
     */
    fun cancelBetting() {
        playerStateFSMTransition(PlayerState.BETTING, PlayerState.INTERACTING)
    }

    /**
     * Resets player state back to the beginning of a turn (ie ROLLING_DICE)
     */
    fun resetTurnState() {
        playerStateData.value = PlayerState.ROLLING_DICE
    }

    /**
     * Computes the closest location to the given position among the existing locations in the game.
     * @param position Reference position
     * @return A future holding the closest location found or null if the closest location is farther than MAX_INTERACT_DISTANCE
     */
    fun computeClosestLocation(position: GeoPoint): CompletableFuture<LocationProperty?> {
        val result = CompletableFuture<LocationProperty?>()

        viewModelScope.launch {
            var closestLocation: LocationProperty? = null
            var closestDistance = Double.MAX_VALUE

            val allLocations = gameData.value?.allLocations ?: listOf()
            for (location in allLocations) {
                val distance = position.distanceToAsDouble(location.position())
                if (distance < closestDistance) {
                    closestLocation = location
                    closestDistance = distance
                }
            }

            if (closestDistance > MAX_INTERACT_DISTANCE) {
                closestLocation = null
            }

            result.complete(closestLocation)
        }

        return result
    }

    /**
     * Rolls the dice and returns the location that corresponds to the sum of 2 dice rolls, 3 times
     * ensuring that the player does not visit the same location twice.
     * @param currentLocation current location that the player can interact with. May be null if no such location exist
     */
    fun rollDiceLocations(currentLocation: LocationProperty?): CompletableFuture<List<LocationProperty>> {
        val result = CompletableFuture<List<LocationProperty>>()

        viewModelScope.launch {
            val locationsNotToVisitName = mutableListOf<String>()
            if (currentLocation != null)
                locationsNotToVisitName.add(currentLocation.name)

            val allLocations = gameData.value?.allLocations ?: listOf()

            val locationsToVisit = mutableListOf<LocationProperty>()
            for (i in 1..NUMBER_OF_LOCATIONS_ROLLED) {
                val closestLocations = allLocations
                    .filter { !locationsNotToVisitName.contains(it.name) }
                    .sortedBy { it.position().distanceToAsDouble(currentLocation?.position() ?: it.position()) }
                val diceRoll = Random.Default.nextInt(allLocations.indices)

                locationsToVisit.add(closestLocations[diceRoll])
                locationsNotToVisitName.add(closestLocations[diceRoll].name)
            }

            result.complete(locationsToVisit)
        }

        return result
    }

    companion object {
        /**
         * Factory object for the GameLobbyWaitingViewModel
         * @see https://developer.android.com/topic/libraries/architecture/viewmodel?hl=fr#viewmodel-with-dependencies for pattern explanation
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GameRepository.player = Player(
                    GlobalInstances.currentUser ?: User(),
                    GameRepository.game?.rules?.initialPlayerBalance ?: -1)

                requireNotNull(GameRepository.game)
                requireNotNull(GameRepository.player)

                remoteDB.setValue(GameRepository.game!!)

                GameViewModel(
                    GameRepository.game!!,
                    GameRepository.player!!
                )
            }
        }
    }

}