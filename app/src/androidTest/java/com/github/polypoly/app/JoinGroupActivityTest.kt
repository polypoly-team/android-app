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


        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.group_does_not_exist)).assertIsDisplayed()
    }

    @Test
    fun inputEmptyGroupCode_displayWarningMessage() {
        // Leave the group code field empty
        composeTestRule.onNodeWithTag("JoinGroupButton").performClick()

        // Check that a warning message is displayed
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.group_code_is_empty)).assertIsDisplayed()
    }

    @Test
    fun inputValidGroupCode_joinGroupRoom() {
        //TODO: Check for a valid group code in the DB once we have the queries set
        val groupCode = "1234"
        composeTestRule.onNodeWithTag("groupCodeField").performTextInput(groupCode)
        composeTestRule.onNodeWithTag("JoinGroupButton").performClick()

        // Check that a message with the joined group code is displayed
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.joined_group_with_code) + groupCode).assertIsDisplayed()
    }

    @Test
    fun inputFullGroupCode_displayWarningMessage() {
        //TODO: Check for a valid group code that is full in the DB once we have the queries set
        val groupCode = "abcd"
        composeTestRule.onNodeWithTag("groupCodeField").performTextInput(groupCode)
        composeTestRule.onNodeWithTag("JoinGroupButton").performClick()

        // Check that a message that the group is full is displayed
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.group_is_full)).assertIsDisplayed()
    }

    }
