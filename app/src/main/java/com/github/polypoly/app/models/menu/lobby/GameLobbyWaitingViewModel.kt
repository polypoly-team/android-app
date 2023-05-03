package com.github.polypoly.app.models.menu.lobby

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.network.IRemoteStorage
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.utils.global.GlobalInstances
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameLobbyWaitingViewModel(
    private val lobbyCode: String,
    private val storage: IRemoteStorage,
): ViewModel() {

    private val pollingDelay = 5000L //> polling delay in millisec until listening to DB is available

    private val gameLobbyData: MutableLiveData<GameLobby> = MutableLiveData(GameLobby())
    private val readyForStartData: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        viewModelScope.launch {
            pollGameLobby()
        }
    }

    fun getGameLobby(): LiveData<GameLobby> {
        return gameLobbyData
    }

    fun getReadyForStart(): LiveData<Boolean> {
        return readyForStartData
    }

    private suspend fun pollGameLobby() {
        //> Polls the storage for updates every pollingDelay millisecs until coroutine is terminated
        while (true) {
            val pollingFuture = storage.getValue<GameLobby>(lobbyCode)

            pollingFuture.thenApply { gameLobby ->
                gameLobbyData.value = gameLobby
                readyForStartData.value = gameLobby.usersRegistered.size >= gameLobby.rules.minimumNumberOfPlayers
            }

            delay(pollingDelay)

            if (!pollingFuture.isDone)
                pollingFuture.cancel(true)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GameLobbyWaitingViewModel(
                    GameRepository.gameCode ?: "",
                    GlobalInstances.remoteDB
                )
            }
        }
    }
}