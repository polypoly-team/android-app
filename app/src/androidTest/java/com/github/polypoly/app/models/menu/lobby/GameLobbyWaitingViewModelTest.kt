package com.github.polypoly.app.models.menu.lobby

import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.utils.global.Settings
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class GameLobbyWaitingViewModelTest: PolyPolyTest(true, false) {

    private fun waitForDataSync(model: GameLobbyWaitingViewModel) {
        model.waitForSync().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }

    @Test
    fun gameLobbyDataSyncsWithStorage() {
        val lobbyCode = "some_code"
        addDataToDB(TEST_GAME_LOBBY_AVAILABLE_1, Settings.DB_GAME_LOBBIES_PATH + lobbyCode)

        val model = GameLobbyWaitingViewModel(lobbyCode, remoteDB)
        waitForDataSync(model)

        addDataToDB(TEST_GAME_LOBBY_AVAILABLE_2, Settings.DB_GAME_LOBBIES_PATH + lobbyCode)
        waitForDataSync(model)

        assertEquals(TEST_GAME_LOBBY_AVAILABLE_2, model.getGameLobby().value)
    }

    @Test
    fun gameLobbyDataReadyForStartSyncsWithStorage() {
        val lobbyCode = "some_code"
        val lobby = TEST_GAME_LOBBY_AVAILABLE_4
        addDataToDB(lobby, Settings.DB_GAME_LOBBIES_PATH + lobbyCode)

        val model = GameLobbyWaitingViewModel(lobbyCode, remoteDB)
        waitForDataSync(model)

        assertFalse(model.getReadyForStart().value!!)

        lobby.addUser(TEST_USER_1)

        addDataToDB(lobby, Settings.DB_GAME_LOBBIES_PATH + lobbyCode)
        waitForDataSync(model)

        assertTrue(model.getReadyForStart().value!!)
    }
}