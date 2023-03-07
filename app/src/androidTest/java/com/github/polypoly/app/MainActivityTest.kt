package com.github.polypoly.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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