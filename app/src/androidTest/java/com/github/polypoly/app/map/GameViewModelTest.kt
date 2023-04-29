package com.github.polypoly.app.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.polypoly.app.ui.game.GameViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@InternalCoroutinesApi
class GameViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val testScope = TestScope(testDispatcher)

    private lateinit var gameViewModel: GameViewModel

    @Before
    fun setUp() {
        gameViewModel = GameViewModel(testDispatcher)
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