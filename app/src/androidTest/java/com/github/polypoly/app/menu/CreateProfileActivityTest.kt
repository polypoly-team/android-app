package com.github.polypoly.app.menu

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.ui.menu.GuestMenuActivity
import com.github.polypoly.app.ui.menu.SignInActivity
import com.github.polypoly.app.ui.menu.profile.CreateProfileActivity
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.isSignedIn
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateProfileActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<CreateProfileActivity>()

    @Before
    fun startIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() { Intents.release() }

    @Test
    fun everythingIsDisplayed() {
        composeTestRule.onNodeWithTag("nickname_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("validate_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("go_back_button").assertIsDisplayed()
        composeTestRule.onNodeWithText("How do you want\n" +
                "to be named?").assertIsDisplayed()
    }

    @Test
    fun cantValidateIfTheUserGiveAnEmptyNickName() {
        composeTestRule.onNodeWithTag("nickname_text").performTextReplacement("")

        composeTestRule.onNodeWithTag("validate_button").assertIsNotEnabled()
    }

    @Test
    fun goToSignInActivityWhenClickOnGoBackButton() {
        composeTestRule.onNodeWithTag("go_back_button").performClick()
        Intents.intended(IntentMatchers.hasComponent(SignInActivity::class.java.name))
    }

    @Test
    fun goToGuestMenuActivityWhenClickOnValidateButtonAndNotSignIn() {
        isSignedIn = false
        composeTestRule.onNodeWithTag("nickname_text").performTextReplacement("test")
        composeTestRule.onNodeWithTag("validate_button").performClick()
        Intents.intended(IntentMatchers.hasComponent(GuestMenuActivity::class.java.name))
    }
}