package com.github.polypoly.app.models.game

import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.models.commons.LoadingModel
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import junit.framework.TestCase.assertNull
import org.junit.Assert.assertEquals
import org.junit.Test
import org.osmdroid.util.GeoPoint
import java.util.concurrent.TimeUnit

class GameViewModelTest: PolyPolyTest(true, false) {

    private val testGame = Game.launchFromPendingGame(TEST_GAME_LOBBY_AVAILABLE_2)
    private val testPlayer = Player(TEST_USER_0)

    private fun waitForDataSync(model: LoadingModel) {
        model.waitForSync().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }

    @Test
    fun playerStateFSMWorks() {
        val model = GameViewModel(testGame, testPlayer)

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

    @Test
    fun playerStateFSMIsRobustAgainstWrongStateTransitions() {
        val model = GameViewModel(testGame, testPlayer)

        assertEquals(PlayerState.INIT, model.getPlayerStateData().value)

        waitForDataSync(model)

        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)

        execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)
        execInMainThread { model.startBetting() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)
        execInMainThread { model.cancelBetting() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)

        execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.MOVING, model.getPlayerStateData().value)
        execInMainThread { model.startBetting() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.MOVING, model.getPlayerStateData().value)
        execInMainThread { model.cancelBetting() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.MOVING, model.getPlayerStateData().value)

        execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)
        execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)
        execInMainThread { model.cancelBetting() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)

        execInMainThread { model.startBetting() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.BETTING, model.getPlayerStateData().value)

        execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.BETTING, model.getPlayerStateData().value)
        execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.BETTING, model.getPlayerStateData().value)
        execInMainThread { model.startBetting() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.BETTING, model.getPlayerStateData().value)
    }

    @Test
    fun closestLocationWorks() {
        val model = GameViewModel(testGame, testPlayer)
        waitForDataSync(model)

        val location = getRandomLocation()
        val locationFound = model.computeClosestLocation(location.position()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(location, locationFound)
    }

    @Test
    fun closestLocationReturnsNullIfLocationIsTooFar() {
        val model = GameViewModel(testGame, testPlayer)
        waitForDataSync(model)

        val positionOut = GeoPoint(0.toDouble(), 0.toDouble())
        val locationFound = model.computeClosestLocation(positionOut).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertNull(locationFound)
    }

    @Test
    fun nexTurnSyncsDB() {
        val model = GameViewModel(testGame, testPlayer)
        waitForDataSync(model)

        remoteDB.setValue(testGame).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        val oldRound = remoteDB.getValue<Game>(TEST_GAME_LOBBY_AVAILABLE_2.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS).currentRound

        model.nextTurn()
        waitForDataSync(model)

        val updatedRound = remoteDB.getValue<Game>(TEST_GAME_LOBBY_AVAILABLE_2.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS).currentRound

        assertEquals(oldRound + 1, updatedRound)
    }

    @Test
    fun nexTurnDoesNotSyncsDBIfUpToDateGameIsPresent() {
        val model = GameViewModel(testGame, testPlayer)
        waitForDataSync(model)

        remoteDB.setValue(testGame).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        val oldGame = remoteDB.getValue<Game>(TEST_GAME_LOBBY_AVAILABLE_2.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        oldGame.nextTurn()
        oldGame.nextTurn()
        remoteDB.setValue(oldGame).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        model.nextTurn() // only does currentRound +1 instead of +2
        waitForDataSync(model)

        val roundFound = remoteDB.getValue<Game>(TEST_GAME_LOBBY_AVAILABLE_2.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS).currentRound

        assertEquals(oldGame.currentRound, roundFound)
    }
}