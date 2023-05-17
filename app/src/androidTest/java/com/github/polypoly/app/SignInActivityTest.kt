package com.github.polypoly.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.ui.menu.SignInActivity
import com.github.polypoly.app.ui.menu.profile.CreateProfileActivity
import com.github.polypoly.app.ui.menu.profile.ProfileModifyingActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignInActivityTest : PolyPolyTest(false, false, false){
    @get:Rule
    val composeTestRule = createAndroidComposeRule<SignInActivity>()

    @Before
    fun startIntents() { Intents.init() }

    @After
    fun releaseIntents() { Intents.release() }

    @Test
    fun mainLogoIsDisplayed() {
        composeTestRule.onNodeWithContentDescription("game_logo").assertIsDisplayed()
    }

    @Test
    fun signInButtonIsDisplayed() {
        composeTestRule.onNodeWithTag("sign_in_button").assertIsDisplayed()
    }

    @Test
    fun guestButtonIsDisplayed() {
        composeTestRule.onNodeWithTag("guest_button").assertIsDisplayed()
    }

    @Test
    fun goToCreateProfileActivityWhenClickOnGuestButton() {
        composeTestRule.onNodeWithTag("guest_button").performClick()
        Intents.intended(IntentMatchers.hasComponent(CreateProfileActivity::class.java.name))
    }

}