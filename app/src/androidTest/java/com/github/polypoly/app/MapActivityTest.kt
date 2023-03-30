package com.github.polypoly.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class MapActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MapActivity>()

    // Composables used in tests
    private val dropDownButton = composeTestRule.onNodeWithTag("dropDownButton")
    private val gameInfoButton = composeTestRule.onNodeWithTag("gameInfoButton")
    private val playerInfoButton = composeTestRule.onNodeWithTag("playerInfoButton")

    @Test
    fun hudIsDisplayed() {
        dropDownButton.assertIsDisplayed()
        playerInfoButton.assertIsDisplayed()
    }

    @Test
    fun gameInfoAndOtherPlayersInfoAreDisplayedOnDropDownButtonClick() {
        gameInfoButton.assertDoesNotExist()
        dropDownButton.performClick()
        gameInfoButton.assertIsDisplayed()
    }

    @Test
    fun gameInfoAndOtherPlayersInfoAreCollapsedWhenDropDownButtonIsClickedAgain() {
        dropDownButton.performClick()
        gameInfoButton.assertIsDisplayed()
        dropDownButton.performClick()
        gameInfoButton.assertDoesNotExist()
    }
}