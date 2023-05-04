package com.github.polypoly.app.models.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.models.commons.LoadingModel
import com.github.polypoly.app.network.IRemoteStorage
import com.github.polypoly.app.network.RemoteDB
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GameViewModel(
    private val game: Game,
    private val player: Player,
    storage: IRemoteStorage
): LoadingModel() {

    private val gameData: MutableLiveData<Game> = MutableLiveData(game)

    private val playerData: MutableLiveData<Player> = MutableLiveData(player)

    fun getGameData(): LiveData<Game> {
        return gameData
    }

    fun getPlayerData(): LiveData<Player> {
        return playerData
    }

    companion object {
        /**
         * Factory object for the GameLobbyWaitingViewModel
         * @see https://developer.android.com/topic/libraries/architecture/viewmodel?hl=fr#viewmodel-with-dependencies for pattern explanation
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                requireNotNull(GameRepository.game)
                requireNotNull(GameRepository.player)
                GameViewModel(
                    GameRepository.game!!,
                    GameRepository.player!!,
                    remoteDB
                )
            }
        }
    }

}