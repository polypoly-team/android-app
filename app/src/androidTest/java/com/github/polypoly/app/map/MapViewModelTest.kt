package com.github.polypoly.app.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.polypoly.app.ui.game.MapViewModel
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

    private lateinit var mapViewModel: MapViewModel

    @Before
    fun setUp() {
        mapViewModel = MapViewModel(testDispatcher)
    }

    @Test
    fun testUpdateDistanceWalked() = testDispatcher.run {
        val initialDistance = 0f
        val addedDistance = 5f
        val expectedDistance = initialDistance + addedDistance

        mapViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance, mapViewModel.distanceWalked.value, 0.001f)
    }

    @Test
    fun testResetDistanceWalked() = testDispatcher.run {
        val initialDistance = 0f
        val addedDistance = 5f
        val expectedDistance = initialDistance + addedDistance

        mapViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance, mapViewModel.distanceWalked.value, 0.001f)

        mapViewModel.resetDistanceWalked()
        testScope.advanceUntilIdle()

        assertEquals(initialDistance, mapViewModel.distanceWalked.value, 0.001f)
    }

    @Test
    fun testUpdateDistanceWalkedMultipleTimes() = testDispatcher.run {
        val initialDistance = 0f
        val addedDistance = 5f
        val expectedDistance = initialDistance + addedDistance

        mapViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance, mapViewModel.distanceWalked.value, 0.001f)

        mapViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance + addedDistance, mapViewModel.distanceWalked.value, 0.001f)
    }

    @Test
    fun testUpdateDistanceWalkedMultipleTimesWithReset() = testDispatcher.run {
        val initialDistance = 0f
        val addedDistance = 5f
        val expectedDistance = initialDistance + addedDistance

        mapViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance, mapViewModel.distanceWalked.value, 0.001f)

        mapViewModel.addDistanceWalked(addedDistance)
        testScope.advanceUntilIdle()

        assertEquals(expectedDistance + addedDistance, mapViewModel.distanceWalked.value, 0.001f)

        mapViewModel.resetDistanceWalked()
        testScope.advanceUntilIdle()

        assertEquals(initialDistance, mapViewModel.distanceWalked.value, 0.001f)
    }
}