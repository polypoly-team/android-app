package com.github.polypoly.app.models.game

import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.models.commons.LoadingModel
import com.github.polypoly.app.models.menu.lobby.GameLobbyWaitingViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class GameViewModelTest: PolyPolyTest(true, false) {

    private fun waitForDataSync(model: LoadingModel) {
        model.waitForSync().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }

    @Test
    fun playerStateFSMWorks() {
        val model = setupGameLobby()

        assertEquals(PlayerState.INIT, model.getPlayerStateData().value)

        waitForDataSync(model)

        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)

        execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.MOVING, model.getPlayerStateData().value)

        execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)

        execInMainThread { model.startBetting() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.BETTING, model.getPlayerStateData().value)

        execInMainThread { model.cancelBetting() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)

        execInMainThread { model.resetTurnState() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)
    }

    private fun setupGameLobby(): GameViewModel {
        return GameViewModel(
            Game.launchFromPendingGame(TEST_GAME_LOBBY_AVAILABLE_2),
            Player(TEST_USER_0)
        )
    }
}