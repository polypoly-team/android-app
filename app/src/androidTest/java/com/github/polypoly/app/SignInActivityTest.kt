package com.github.polypoly.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.intent.Intents
import com.github.polypoly.app.commons.PolyPolyTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignInActivityTest : PolyPolyTest(false, false, false){
    @get:Rule
    val composeTestRule = createAndroidComposeRule<SignInActivity>()

    @Before
    fun startIntents() {
        Intents.init()
    }

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

}