package com.github.polypoly.app.models.menu.lobby

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.models.commons.LoadingModel
import com.github.polypoly.app.network.IRemoteStorage
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.utils.global.GlobalInstances
import com.github.polypoly.app.utils.global.Settings.Companion.DB_GAME_LOBBIES_PATH
import kotlinx.coroutines.delay
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

    private val waitingForSyncPromise: ArrayList<CompletableFuture<Boolean>> = arrayListOf()

    init {
        setLoading(true)
        viewModelScope.launch {
            pollGameLobby()
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

    private suspend fun pollGameLobby() {
        // Polls the storage for updates every pollingDelay millisecs until coroutine is terminated
        // TODO: replace this with listening to the storage once available

        delay(POLLING_DELAY) // For demo only

        while (true) {
            val pollingFuture = storage.getValue<GameLobby>(DB_GAME_LOBBIES_PATH + lobbyCode)

            pollingFuture.thenApply { gameLobby ->

                gameLobby.addUser(User(id = 123456789)) // For demo only

                gameLobbyData.value = gameLobby
                readyForStartData.value = gameLobby.usersRegistered.size >= gameLobby.rules.minimumNumberOfPlayers

                for (future in waitingForSyncPromise) {
                    future.complete(true)
                }
                waitingForSyncPromise.clear()

                setLoading(false)
            }

            delay(POLLING_DELAY)

            if (!pollingFuture.isDone)
                pollingFuture.cancel(true)
        }
    }

    /**
     * Registers a callback that will be called next time a data is synchronized with the storage
     * This function is primarily but not exclusively aimed for used in unit tests
     * @return a promise that completes when the data is synced again with the storage
     */
    fun waitForSync(): CompletableFuture<Boolean> {
        waitingForSyncPromise.add(CompletableFuture())
        return waitingForSyncPromise.last()
    }

    companion object {

        private const val POLLING_DELAY = 3000L //> polling delay in millisec until listening to DB is available

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