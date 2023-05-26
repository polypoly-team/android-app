package com.github.polypoly.app.game_and_map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.polypoly.app.base.game.GameMilestoneRewardTransaction
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.map.MapViewModel
import com.github.polypoly.app.utils.global.GlobalInstances
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@InternalCoroutinesApi
class MapViewModelTest {

    private val emptySkin = Skin(0,0,0)
    private val zeroStats = Stats(0,0,0,0,0)
    private val testUser1 = User("42042042", "test_user1", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser2 = User("42042043", "test_user2", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser3 = User("42042044", "test_user3", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser4 = User("42042045", "test_user4", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser5 = User("42042046", "test_user5", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser6 = User("42042047", "test_user6", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testDuration = 2
    private val gameRules = GameParameters(
        GameMode.RICHEST_PLAYER, 3, 7,
        testDuration, 10, LocationPropertyRepository.getZones(), 200)
    private val gameLobby = GameLobby(testUser1, gameRules, "test_game", "123456", false)

    @Before
    fun initLobby() {
        gameLobby.addUser(testUser2)
        gameLobby.addUser(testUser3)
        gameLobby.addUser(testUser4)
        gameLobby.addUser(testUser5)
        gameLobby.addUser(testUser6)
        GlobalInstances.currentUser = testUser1
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val testScope = TestScope(testDispatcher)

    private lateinit var gameViewModel: MapViewModel

    @Before
    fun setUp() {
        gameViewModel = MapViewModel(testDispatcher)
    }

    @Test
    fun testUpdateDistanceWalked() = testDispatcher.run {
        val initialDistance = 0f
        val addedDistance = 5f
        val expectedDistance = initialDistance + addedDistance

        gameViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance, gameViewModel.distanceWalked.value, 0.001f)
    }

    @Test
    fun reachMilestonesAddsToMilestonesToDisplay() = testDispatcher.run {
        val game = gameLobby.start()
        val addedDistance = 5 * GameMilestoneRewardTransaction.milestoneRewardDistance

        gameViewModel.addDistanceWalked(addedDistance.toFloat())
        testScope.advanceUntilIdle()
        assert(gameViewModel.newMilestonesToDisplay.value.size == 5)

    }

    @Test
    fun milestonesReachedAddsToGameTransactions(){
        val game = gameLobby.start()
        val addedDistance = 5 * GameMilestoneRewardTransaction.milestoneRewardDistance
        val player = GameRepository.player
        val playerBalanceBefore = player!!.getBalance()

        gameViewModel.addDistanceWalked(addedDistance.toFloat())
        testScope.advanceUntilIdle()

        game.nextTurn()

        val playerBalanceAfter = player.getBalance()
        assert(playerBalanceAfter == playerBalanceBefore + 5 * GameMilestoneRewardTransaction.milestoneRewardValue)
    }

    @Test
    fun testResetDistanceWalked() = testDispatcher.run {
        val initialDistance = 0f
        val addedDistance = 5f
        val expectedDistance = initialDistance + addedDistance

        gameViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance, gameViewModel.distanceWalked.value, 0.001f)

        gameViewModel.resetDistanceWalked()
        testScope.advanceUntilIdle()

        assertEquals(initialDistance, gameViewModel.distanceWalked.value, 0.001f)
    }

    @Test
    fun testUpdateDistanceWalkedMultipleTimes() = testDispatcher.run {
        val initialDistance = 0f
        val addedDistance = 5f
        val expectedDistance = initialDistance + addedDistance

        gameViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance, gameViewModel.distanceWalked.value, 0.001f)

        gameViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance + addedDistance, gameViewModel.distanceWalked.value, 0.001f)
    }

    @Test
    fun testUpdateDistanceWalkedMultipleTimesWithReset() = testDispatcher.run {
        val initialDistance = 0f
        val addedDistance = 5f
        val expectedDistance = initialDistance + addedDistance

        gameViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance, gameViewModel.distanceWalked.value, 0.001f)

        gameViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance + addedDistance, gameViewModel.distanceWalked.value, 0.001f)

        gameViewModel.resetDistanceWalked()
        testScope.advanceUntilIdle()

        assertEquals(initialDistance, gameViewModel.distanceWalked.value, 0.001f)
    }
}