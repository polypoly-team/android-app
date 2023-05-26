package com.github.polypoly.app.game_and_map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.polypoly.app.base.game.GameMilestoneRewardTransaction
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.map.MapViewModel
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
        val addedDistance = 5 * GameMilestoneRewardTransaction.milestoneRewardDistance

        gameViewModel.addDistanceWalked(addedDistance.toFloat())
        testScope.advanceUntilIdle()
        assert(gameViewModel.newMilestonesToDisplay.value.size == 5)
    }

    @Test
    fun milestonesReachedAddsToPlayerBalanceWhenRoundEnds(){
        val addedDistance = 5 * GameMilestoneRewardTransaction.milestoneRewardDistance
        val player = GameRepository.player
        val playerBalanceBefore = player!!.getBalance()

        gameViewModel.addDistanceWalked(addedDistance.toFloat())
        testScope.advanceUntilIdle()

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