package com.github.polypoly.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
    @get:Rule
    var permissionRule2 =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    // Views that we test here
    private val textName = composeTestRule.onNodeWithTag("nameField")
    private val button = composeTestRule.onNodeWithTag("greetButton")

    @Test
    fun greetButtonFiresIntentWithActivity() {
        Intents.init()

        // Fills a non-empty name
        textName.performTextInput("bigflo")

        // Clicking on button
        button.performClick()
        intended(hasComponent(GreetingActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun greetButtonFilesIntentWithName() {
        Intents.init()

        // Fills a non-empty name
        textName.performTextInput("bigflo")

        // Clicking on button
        button.performClick()
        intended(hasExtra("name", "bigflo"))

        Intents.release()
    }

}