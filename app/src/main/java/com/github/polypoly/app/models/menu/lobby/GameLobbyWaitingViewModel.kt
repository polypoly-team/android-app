package com.github.polypoly.app.models.menu.lobby

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.models.commons.LoadingModel
import com.github.polypoly.app.network.IRemoteStorage
import com.github.polypoly.app.network.addOnChangeListener
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.utils.global.GlobalInstances
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

/**
 * View model for waiting in a game lobby until enough players are connected
 */
class GameLobbyWaitingViewModel(
    private val lobbyCode: String,
    private val storage: IRemoteStorage,
): LoadingModel() {

    private val gameLobbyData: MutableLiveData<GameLobby> = MutableLiveData(GameLobby())

    private val readyForStartData: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        setLoading(true)
        viewModelScope.launch {
            listenGameLobby()
        }
    }

    /**
     * Encapsulated accessor for the gameLobby the user currently is waiting in
     * @return LiveData<GameLobby> A live data holding the last GameLobby seen in the storage
     */
    fun getGameLobby(): LiveData<GameLobby> {
        return gameLobbyData
    }

    /**
     * Tells whether the current game lobby is ready for start
     * @return LiveData<Boolean> A live data holding true iff the game is ready for start
     */
    fun getReadyForStart(): LiveData<Boolean> {
        return readyForStartData
    }

    fun setGameLobby(gameLobby: GameLobby) {
        setLoading(false)
        // TODO: here we "force" the value change to make sure that it toggles the recomposition
        gameLobbyData.postValue(GameLobby())
        gameLobbyData.postValue(gameLobby)
        readyForStartData.postValue(gameLobby.usersRegistered.size >= gameLobby.rules.minimumNumberOfPlayers)
    }

    private fun listenGameLobby() {
        storage.addOnChangeListener(lobbyCode, "game_lobby_waiting_view_model", ::setGameLobby)
        storage.getValue<GameLobby>(lobbyCode).thenAccept(::setGameLobby)
    }

    companion object {

        private const val POLLING_DELAY = 2000L //> polling delay in millisec until listening to DB is available

        /**
         * Factory object for the GameLobbyWaitingViewModel
         * @see https://developer.android.com/topic/libraries/architecture/viewmodel?hl=fr#viewmodel-with-dependencies for pattern explanation
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GameLobbyWaitingViewModel(
                    GameRepository.gameCode ?: "default-lobby",
                    GlobalInstances.remoteDB
                )
            }
        }
    }
}