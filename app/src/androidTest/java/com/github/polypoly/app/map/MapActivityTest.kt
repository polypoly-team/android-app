package com.github.polypoly.app.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MapActivity>()

    @Test
    fun mapActivity_UIComponents_Displayed() {
        // Check if the map view is displayed
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        // Check if the distance walked UI components are displayed
        composeTestRule.onNodeWithTag("resetButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distanceWalked").assertIsDisplayed()
    }

    @Test
    fun mapActivity_ResetButton_Clicked_DistanceReset() {
        fun formattedDistance(distance: Float): String {
            return if (distance < 1000) "${"%.1f".format(distance)}m"
            else "${"%.1f".format(distance / 1000)}km"
        }

        // Wait for the UI to update
        runBlocking { delay(5000) } // TODO: Find a better way to wait for the UI to update

        // Click the reset button
        composeTestRule.onNodeWithTag("resetButton").performClick()

        // Wait for the UI to update
        runBlocking { delay(500) }

        // Check if the distance walked is reset to zero
        composeTestRule.onNodeWithTag("distanceWalked")
            .assertTextContains("Distance walked: ${formattedDistance(0f)}")
    }
}
