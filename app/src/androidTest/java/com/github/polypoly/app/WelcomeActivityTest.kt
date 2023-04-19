package com.github.polypoly.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.polypoly.app.game.user.Skin
import com.github.polypoly.app.game.user.Stats
import com.github.polypoly.app.game.user.User
import com.github.polypoly.app.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.global.GlobalInstances.Companion.isSignedIn
import com.github.polypoly.app.menu.JoinGameLobbyActivity
import com.github.polypoly.app.menu.ProfileActivity
import com.github.polypoly.app.menu.SettingsActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
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
    private val optionButtonRankings = composeTestRule.onNodeWithContentDescription("Open Rankings")
    private val optionButtonProfile = composeTestRule.onNodeWithContentDescription("Open Profile")
    private val optionButtonSettings = composeTestRule.onNodeWithContentDescription("Open Settings")

    private val rules = composeTestRule.onNodeWithText(RulesObject.rulesTitle)


    init {
        isSignedIn = true
    }
    @Before
    fun startIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() { Intents.release() }

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
        optionButtonRankings.assertIsDisplayed()
        optionButtonProfile.assertIsDisplayed()
        optionButtonSettings.assertIsDisplayed()
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

    // ========================================================================

    @Test
    fun settingsButtonOpensActivity() {
        // Clicking on button
        optionButtonSettings.performClick()
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun profileButtonOpensActivity() {
        // Clicking on button
        optionButtonProfile.performClick()
        Intents.intended(IntentMatchers.hasComponent(ProfileActivity::class.java.name))
    }

    /* TODO: enable when RankingsActivity exists
    @Test
    fun rankingsButtonOpensActivity() {
        // Clicking on button
        optionButtonRankings.performClick()
        Intents.intended(IntentMatchers.hasComponent(RankingsActivity::class.java.name))
    }*/

    @Test
    fun joinGameButtonOpensActivity() {
        // Clicking on button
        buttonJoinGame.performClick()
        Intents.intended(IntentMatchers.hasComponent(JoinGameLobbyActivity::class.java.name))
    }

    /* TODO: enable when CreateGroupActivity exists
    @Test
    fun createGameButtonOpensActivity() {
        // Clicking on button
        buttonCreateGame.performClick()
        Intents.intended(IntentMatchers.hasComponent(CreateGroupActivity::class.java.name))
    }*/
}