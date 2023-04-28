package com.github.polypoly.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.ui.SignInActivity
import org.junit.Rule
import org.junit.Test

class SignInActivityTest : PolyPolyTest(false, false, false){
    @get:Rule
    val composeTestRule = createAndroidComposeRule<SignInActivity>()

    @Test
    fun mainLogoIsDisplayed() {
        composeTestRule.onNodeWithContentDescription("game_logo").assertIsDisplayed()
    }

    @Test
    fun signInButtonIsDisplayed() {
        composeTestRule.onNodeWithTag("sign_in_button").assertIsDisplayed()
    }

}