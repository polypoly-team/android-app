package com.github.polypoly.app.models.game

import android.content.BroadcastReceiver
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.firebase.ui.auth.data.model.User
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.TradeRequest
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.models.commons.LoadingModel
import com.github.polypoly.app.network.getAllValues
import com.github.polypoly.app.network.getValues
import com.github.polypoly.app.ui.game.PlayerState
import com.github.polypoly.app.utils.global.GlobalInstances
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
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

    private val tradeRequestData: MutableLiveData<TradeRequest> = MutableLiveData()

    init {
        viewModelScope.launch {
            listenToTradeRequest()
        }
    }

    fun getTradeRequestData(): LiveData<TradeRequest> {
        return tradeRequestData
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

    fun nextTurn() {
        gameData.value?.nextTurn()
        roundTurnData.value = gameData.value?.currentRound ?: -1
        gameEndedData.value = gameData.value?.isGameFinished() ?: false
    }

    private suspend fun listenToTradeRequest() {
        while (gameData.value?.isGameFinished() == true) {
            remoteDB.getAllValues<TradeRequest>().thenAccept { tradeRequests ->
                tradeRequests.forEach { tradeRequest ->
                    if (tradeRequest.playerReceiver.user.name == playerData.value?.user?.name) {
                        tradeRequestData.value = tradeRequest
                    }
                }
            }
            delay(2500)
        }
    }

    fun createATradeRequest(playerReceiver: Player, locationGiven: InGameLocation): CompletableFuture<Boolean> {
        val playerDataValue = playerData.value ?: return CompletableFuture.completedFuture(false)
        val tradeRequest = TradeRequest(
            playerApplicant = playerDataValue,
            playerReceiver = playerReceiver,
            locationGiven = locationGiven,
            locationReceived = null,
            currentPlayerApplicantAcceptation = null,
            currentPlayerReceiverAcceptation = null,
            code = "${playerReceiver.user.name}${playerDataValue.user.name}",
        )
        return remoteDB.setValue(tradeRequest)
    }

    companion object {
        /**
         * Factory object for the GameLobbyWaitingViewModel
         * @see https://developer.android.com/topic/libraries/architecture/viewmodel?hl=fr#viewmodel-with-dependencies for pattern explanation
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // TODO: Remove this when the game is created from the lobby
                GameRepository.game = Game.launchFromPendingGame(
                    GameLobby(
                        admin = GlobalInstances.currentUser
                    )
                )
                GameRepository.player = Player(
                    GlobalInstances.currentUser,
                    // hardcoded values for the testing
                    3000,
                    ownedLocations = listOf(Game.gameInProgress?.getInGameLocation()?.get(0)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(1)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(2)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(5)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(8)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(9)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(10)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(11)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(12)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(13)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(14)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(15)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(16)!!,
                        Game.gameInProgress?.getInGameLocation()?.get(17)!!,),
                )
                GameRepository.player?.playerState?.value = PlayerState.ROLLING_DICE
                requireNotNull(GameRepository.game)
                requireNotNull(GameRepository.player)
                GameViewModel(
                    GameRepository.game!!,
                    GameRepository.player!!
                )
            }
        }
    }

}