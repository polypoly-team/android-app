package com.github.polypoly.app.models.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.firebase.ui.auth.data.model.User
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.models.commons.LoadingModel
import com.github.polypoly.app.ui.game.PlayerState
import com.github.polypoly.app.utils.global.GlobalInstances

class GameViewModel(
    game: Game,
    player: Player
): LoadingModel() {

    private val gameData: MutableLiveData<Game> = MutableLiveData(game)

    private val playerData: MutableLiveData<Player> = MutableLiveData(player)

    private val roundTurnData: MutableLiveData<Int> = MutableLiveData(game.currentRound)

    private val gameEndedData: MutableLiveData<Boolean> = MutableLiveData(false)

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