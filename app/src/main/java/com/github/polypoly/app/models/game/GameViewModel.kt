package com.github.polypoly.app.models.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.models.commons.LoadingModel
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.network.RemoteDB
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.utils.global.GlobalInstances
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class GameViewModel(
    game: Game,
    player: Player
): LoadingModel() {

    private val gameData: MutableLiveData<Game> = MutableLiveData(game)

    private val playerData: MutableLiveData<Player> = MutableLiveData(player)

    private val roundTurnData: MutableLiveData<Int> = MutableLiveData(game.currentRound)

    private val gameEndedData: MutableLiveData<Boolean> = MutableLiveData(false)

    private val playerStateData: MutableLiveData<PlayerState> = MutableLiveData(PlayerState.INIT)

    init {
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

    fun getPlayerState(): LiveData<PlayerState> {
        return playerStateData
    }

    suspend fun gameLoop() {
        var currentGame = gameData.value

        while (currentGame != null && !currentGame.isGameFinished()) {
            playerStateData.value = PlayerState.ROLLING_DICE

            delay(currentGame.rules.roundDuration.toLong() * 1000 * 60)

            playerStateData.value = PlayerState.TURN_FINISHED

            nextTurn()

            currentGame = gameData.value
        }
    }

    private fun nextTurn() {
        viewModelScope.launch {
            gameData.value?.nextTurn()

            synchronizeGame().thenApply { syncSucceeded ->
                if (syncSucceeded) {
                    roundTurnData.value = gameData.value?.currentRound ?: -1
                    gameEndedData.value = gameData.value?.isGameFinished() ?: false
                }
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

    fun diceRolled() {
        if (playerStateData.value != PlayerState.ROLLING_DICE)
            return
        playerStateData.value = PlayerState.MOVING
    }

    fun locationReached() {
        if (playerStateData.value != PlayerState.MOVING)
            return
        playerStateData.value = PlayerState.INTERACTING
    }

    fun startBetting() {
        if (playerStateData.value != PlayerState.INTERACTING)
            return
        playerStateData.value = PlayerState.BETTING
    }

    fun cancelBetting() {
        if (playerStateData.value != PlayerState.BETTING)
            return
        playerStateData.value = PlayerState.INTERACTING
    }

    companion object {
        /**
         * Factory object for the GameLobbyWaitingViewModel
         * @see https://developer.android.com/topic/libraries/architecture/viewmodel?hl=fr#viewmodel-with-dependencies for pattern explanation
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GameRepository.player = Player(
                    GlobalInstances.currentUser,
                    GameRepository.game?.rules?.initialPlayerBalance ?: -1
                )
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