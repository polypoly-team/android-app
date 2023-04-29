package com.github.polypoly.app.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.RulesObject
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.ui.game.MapActivity
import com.github.polypoly.app.ui.menu.profile.ProfileActivity
import com.github.polypoly.app.ui.menu.settings.SettingsActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HudTest: PolyPolyTest(true, true) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MapActivity>()

    private val otherPlayersAndGameDropDownButton = composeTestRule.onNodeWithTag("otherPlayersAndGameDropDownButton")
    private val gameInfoButton = composeTestRule.onNodeWithTag("gameInfoButton")
    private val playerInfoButton = composeTestRule.onNodeWithTag("playerInfoButton")
    private val gameMenuDropDownButton = composeTestRule.onNodeWithTag("gameMenuDropDownButton")
    private val menuButtonRules = composeTestRule.onNodeWithContentDescription("Show Rules")
    private val menuButtonRankings = composeTestRule.onNodeWithContentDescription("Open Rankings")
    private val menuButtonProfile = composeTestRule.onNodeWithContentDescription("Open Profile")
    private val menuButtonSettings = composeTestRule.onNodeWithContentDescription("Open Settings")
    private val rules = composeTestRule.onNodeWithText(RulesObject.rulesTitle)

    @Before
    fun startIntents() { Intents.init() }

    @After
    fun releaseIntents() { Intents.release() }

    @Test
    fun hudIsDisplayed() {
        otherPlayersAndGameDropDownButton.assertIsDisplayed()
        playerInfoButton.assertIsDisplayed()
        gameMenuDropDownButton.assertIsDisplayed()
    }

    @Test
    fun gameInfoAndOtherPlayersInfoAreDisplayedOnDropDownButtonClick() {
        gameInfoButton.assertDoesNotExist()
        otherPlayersAndGameDropDownButton.performClick()
        gameInfoButton.assertIsDisplayed()
    }

    @Test
    fun gameInfoAndOtherPlayersInfoAreCollapsedWhenDropDownButtonIsClickedAgain() {
        otherPlayersAndGameDropDownButton.performClick()
        gameInfoButton.assertIsDisplayed()
        otherPlayersAndGameDropDownButton.performClick()
        gameInfoButton.assertDoesNotExist()
    }

    @Test
    fun gameMenuIsDisplayedOnDropDownButtonClick() {
        gameMenuDropDownButton.performClick()
        menuButtonRules.assertIsDisplayed()
        menuButtonRankings.assertIsDisplayed()
        menuButtonProfile.assertIsDisplayed()
        menuButtonSettings.assertIsDisplayed()
    }

    @Test
    fun gameMenuRulesButtonDisplaysRules() {
        gameMenuDropDownButton.performClick()
        menuButtonRules.performClick()
        rules.assertIsDisplayed()
    }

    @Test
    fun gameMenuRulesDismissOnOutsideClick() {
        gameMenuDropDownButton.performClick()
        menuButtonRules.performClick()
        rules.assertIsDisplayed()
        gameMenuDropDownButton.performClick()
        rules.assertDoesNotExist()
    }

    @Test
    fun gameMenuProfileButtonDisplaysActivity() {
        gameMenuDropDownButton.performClick()
        menuButtonProfile.performClick()
        Intents.intended(IntentMatchers.hasComponent(ProfileActivity::class.java.name))
    }

    @Test
    fun gameMenuSettingsButtonDisplaysActivity() {
        gameMenuDropDownButton.performClick()
        menuButtonSettings.performClick()
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }
}