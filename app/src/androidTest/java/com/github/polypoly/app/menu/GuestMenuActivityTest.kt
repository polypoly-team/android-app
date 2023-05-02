package com.github.polypoly.app.menu

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.RulesObject
import com.github.polypoly.app.ui.menu.GuestMenuActivity
import com.github.polypoly.app.ui.menu.SignInActivity
import com.github.polypoly.app.ui.menu.settings.SettingsActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuestMenuActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<GuestMenuActivity>()

    @Before
    fun startIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() { Intents.release() }

    @Test
    fun buttonsAreDisplayed() {
        composeTestRule.onNodeWithTag("rules_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("discover_map_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("return_sign_in_button").assertIsDisplayed()
    }

    @Test
    fun goToSettingsActivityWhenClickOnSettingsButton() {
        composeTestRule.onNodeWithTag("settings_button").performClick()
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun goToSignInActivityWhenClickOnReturnSignInButton() {
        composeTestRule.onNodeWithTag("return_sign_in_button").performClick()
        Intents.intended(IntentMatchers.hasComponent(SignInActivity::class.java.name))
    }

    @Test
    fun rulesButtonsTogglesOnRules() {
        val rules = composeTestRule.onNodeWithText(RulesObject.rulesTitle)

        rules.assertDoesNotExist()
        composeTestRule.onNodeWithTag("rules_button").performClick()
        rules.assertIsDisplayed()
    }

    @Test
    fun welcomeTextDisplayCorrectName() {
        val guestNameTest = "Test"
        val testIntent =
            Intent(ApplicationProvider.getApplicationContext(), GuestMenuActivity::class.java)
        testIntent.putExtra("user_nickname", guestNameTest)
        ActivityScenario.launch<GuestMenuActivity>(testIntent)

        composeTestRule.onNodeWithText("Welcome $guestNameTest").assertIsDisplayed()
    }

    @Test
    fun welcomeTextDisplayDefaultNameWhenNull() {
        val guestNameTest: String? = null
        val testIntent =
            Intent(ApplicationProvider.getApplicationContext(), GuestMenuActivity::class.java)
        testIntent.putExtra("userNickname", guestNameTest)
        ActivityScenario.launch<GuestMenuActivity>(testIntent)

        composeTestRule.onNodeWithText("Welcome ${GuestMenuActivity.Companion.DEFAULT_NICKNAME}")
            .assertIsDisplayed()
    }
}