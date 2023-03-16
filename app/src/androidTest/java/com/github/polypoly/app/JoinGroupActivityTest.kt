package com.github.polypoly.app

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class JoinGroupActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<JoinGroupActivity>()

    @Test
    fun launchActivity_componentsDisplayed() {
        composeTestRule.onNodeWithTag("groupCodeField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("JoinGroupButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("logo").assertIsDisplayed()
    }

    @Test
    fun inputInvalidGroupCode_displayWarningMessage() {
        composeTestRule.onNodeWithTag("groupCodeField").performTextInput("polypoly")
        composeTestRule.onNodeWithTag("JoinGroupButton").performClick()

        val recordedLogs = mutableListOf<String>()
        Timber.plant(object : Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                recordedLogs.add(message)
            }
        })

    }

    @Test
    fun inputEmptyGroupCode_displayWarningMessage() {
        // Leave the group code field empty
        composeTestRule.onNodeWithTag("JoinGroupButton").performClick()

        // Check that a warning message is displayed
        composeTestRule.onNodeWithText("Group code cannot be empty!").assertIsDisplayed()
    }

    @Test
    fun inputValidGroupCode_joinGroupRoom() {
        // Enter a valid group code
        // TODO: Check for a valid group code in the DB once we have the queries set
        composeTestRule.onNodeWithTag("groupCodeField").performTextInput("abcd")
        composeTestRule.onNodeWithTag("JoinGroupButton").performClick()

        // Check that a message with the joined group code is displayed
        composeTestRule.onNodeWithText("Joined group with code abcd").assertIsDisplayed()
    }

    }
