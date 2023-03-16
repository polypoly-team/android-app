package com.github.polypoly.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
        //TODO: Check for a group code that is not in the DB once we have the queries set
        composeTestRule.onNodeWithTag("groupCodeField").performTextInput("polypoly")
        composeTestRule.onNodeWithTag("JoinGroupButton").performClick()


        composeTestRule.onNodeWithText("Group does not exist").assertIsDisplayed()

    }

    @Test
    fun inputEmptyGroupCode_displayWarningMessage() {
        // Leave the group code field empty
        composeTestRule.onNodeWithTag("JoinGroupButton").performClick()

        // Check that a warning message is displayed
        composeTestRule.onNodeWithText("Group code is empty").assertIsDisplayed()
    }

    @Test
    fun inputValidGroupCode_joinGroupRoom() {
        // Enter a valid group code
        //TODO: Check for a valid group code in the DB once we have the queries set
        val groupCode = "abcd"
        composeTestRule.onNodeWithTag("groupCodeField").performTextInput(groupCode)
        composeTestRule.onNodeWithTag("JoinGroupButton").performClick()

        // Check that a message with the joined group code is displayed
        composeTestRule.onNodeWithText("Joined group with code $groupCode").assertIsDisplayed()
    }

    }
