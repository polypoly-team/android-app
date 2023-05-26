package com.github.polypoly.app.viewmodels.game

import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.viewmodels.commons.LoadingModel
import com.github.polypoly.app.database.getValue
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.osmdroid.util.GeoPoint
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class GameViewModelTest: PolyPolyTest(true, false) {

    private val testGame = Game.launchFromPendingGame(TEST_GAME_LOBBY_AVAILABLE_2.copy(rules =
        TEST_GAME_LOBBY_AVAILABLE_2.rules.copy(roundDuration = 1000)))
    private val testPlayer = Player(TEST_USER_0)

    private fun waitForDataSync(model: LoadingModel) {
        model.waitForSync().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun playerStateFSMWorks() = runTest(UnconfinedTestDispatcher()) {
        val model = GameViewModel(testGame, testPlayer, coroutineScope = this)

        execInMainThread { model.resetTurnState() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)

        execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.MOVING, model.getPlayerStateData().value)

        execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)

        execInMainThread { model.startBidding() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.BIDDING, model.getPlayerStateData().value)

        execInMainThread { model.cancelBidding() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)

        execInMainThread { model.resetTurnState() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun playerStateFSMIsRobustAgainstWrongStateTransitions() = runTest(UnconfinedTestDispatcher()) {
        val model = GameViewModel(testGame, testPlayer, coroutineScope = this)

        waitForDataSync(model)
        execInMainThread { model.resetTurnState() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.startBidding() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.cancelBidding() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.ROLLING_DICE, model.getPlayerStateData().value)

        execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.MOVING, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.startBidding() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.MOVING, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.cancelBidding() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.MOVING, model.getPlayerStateData().value)

        execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.cancelBidding() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.INTERACTING, model.getPlayerStateData().value)

        execInMainThread { model.startBidding() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(PlayerState.BIDDING, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.diceRolled() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.BIDDING, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.locationReached() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.BIDDING, model.getPlayerStateData().value)

        assertThrows(ExecutionException::class.java) {
            execInMainThread { model.startBidding() }.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        }
        assertEquals(PlayerState.BIDDING, model.getPlayerStateData().value)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun closestLocationWorks() = runTest(UnconfinedTestDispatcher()) {
        val model = GameViewModel(testGame, testPlayer, coroutineScope = this)

        val location = getRandomLocation()
        val locationFound = model.computeClosestLocation(location.position()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(location, locationFound)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun closestLocationReturnsNullIfLocationIsTooFar() = runTest(UnconfinedTestDispatcher()) {
        val model = GameViewModel(testGame, testPlayer, coroutineScope = this)

        val positionOut = GeoPoint(0.toDouble(), 0.toDouble())
        val locationFound = model.computeClosestLocation(positionOut).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertNull(locationFound)
    }

    @Test
    fun nexTurnSyncsDB() {
        val model = GameViewModel(testGame, testPlayer)

        remoteDB.setValue(testGame).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        val oldRound = remoteDB.getValue<Game>(TEST_GAME_LOBBY_AVAILABLE_2.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS).currentRound

        model.nextTurn()
        waitForUIToUpdate()

        val updatedRound = remoteDB.getValue<Game>(TEST_GAME_LOBBY_AVAILABLE_2.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS).currentRound

        assertEquals(oldRound + 1, updatedRound)
    }

    @Test
    fun nexTurnDoesNotSyncsDBIfUpToDateGameIsPresent() {
        val model = GameViewModel(testGame, testPlayer)

        remoteDB.setValue(testGame).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        val oldGame = remoteDB.getValue<Game>(testGame.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        oldGame.nextTurn()
        oldGame.nextTurn()
        remoteDB.setValue(oldGame).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        model.nextTurn() // only does currentRound +1 instead of +2
        waitForUIToUpdate()

        val roundFound = remoteDB.getValue<Game>(testGame.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS).currentRound

        assertEquals(oldGame.currentRound, roundFound)
        assertEquals(oldGame.currentRound, roundFound)
    }
}