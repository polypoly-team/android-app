package com.github.polypoly.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class WelcomeActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<WelcomeActivity>()

    // Composables used in tests
    private val mainLogo = composeTestRule.onNodeWithContentDescription("game_logo")
    private val buttonJoinGame = composeTestRule.onNodeWithText("Join Game!")
    private val buttonCreateGame = composeTestRule.onNodeWithText("Create Game?")

    private val optionButtonRules = composeTestRule.onNodeWithContentDescription("Show Rules")
    private val optionButton2 = composeTestRule.onNodeWithContentDescription("optionButton2")
    private val optionButton3 = composeTestRule.onNodeWithContentDescription("optionButton3")
    private val optionButton4 = composeTestRule.onNodeWithContentDescription("optionButton4")

    private val rules = composeTestRule.onNodeWithText(RulesObject.rulesTitle)

    // ========================================================= Display checks

    @Test
    fun mainLogoIsDisplayed() {
        mainLogo.assertIsDisplayed()
    }

    @Test
    fun mainButtonsAreDisplayed() {
        buttonJoinGame.assertIsDisplayed()
        buttonCreateGame.assertIsDisplayed()
    }

    @Test
    fun optionButtonsAreDisplayed() {
        optionButtonRules.assertIsDisplayed()
        optionButton2.assertIsDisplayed()
        optionButton3.assertIsDisplayed()
        optionButton4.assertIsDisplayed()
    }
    // ========================================================================

    @Test
    fun rulesButtonsTogglesOnRules() {
        rules.assertDoesNotExist()
        optionButtonRules.performClick()
        rules.assertIsDisplayed()
    }

    @Test
    fun clickingOutsideRulesTogglesOffThem() {
        // First display the rules
        optionButtonRules.performClick()

        // TODO: find a way to click outside the rules
    }

    @Test
    fun clickingInsideRulesDoesNothing() {
        // First display the rules
        optionButtonRules.performClick()

        rules.performClick()
        rules.assertIsDisplayed()
    }
}