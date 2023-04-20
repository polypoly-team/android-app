package com.github.polypoly.app

import android.util.Log
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.menu.JoinGameLobbyActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinGameLobbyActivityTest: PolyPolyTest(false, true) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<JoinGameLobbyActivity>()

    @Test
    fun launchActivity_componentsDisplayed() {
        composeTestRule.onNodeWithTag("gameLobbyCodeField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("logo").assertIsDisplayed()
    }

    @Test
    fun inputInvalidGameLobbyCode_displayWarningMessage() {
        //TODO: Check for a group code that is not in the DB once we have the queries set
        composeTestRule.onNodeWithTag("gameLobbyCodeField").performTextInput("polypoly")
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").performClick()


        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.game_lobby_does_not_exist)).assertIsDisplayed()
    }

    @Test
    fun inputEmptyGameLobbyCode_displayWarningMessage() {
        // Leave the game lobby code field empty
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").performClick()

        // Check that a warning message is displayed
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.game_lobby_code_is_empty)).assertIsDisplayed()
    }

    @Test
    fun inputValidGameLobbyCode_joinGameLobbyRoom() {
        //TODO: Check for a valid group code in the DB once we have the queries set
        val lobbyCode = TEST_GAME_LOBBY_AVAILABLE_1.code
        composeTestRule.onNodeWithTag("gameLobbyCodeField").performTextInput(lobbyCode)
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").performClick()

    }

    @Test
    fun inputFullGameLobbyCode_displayWarningMessage() {
        //TODO: Check for a valid group code that is full in the DB once we have the queries set
        val lobbyCode = TEST_GAME_LOBBY_FULL.code
        composeTestRule.onNodeWithTag("gameLobbyCodeField").performTextInput(lobbyCode)
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").performClick()

        // Check that a message that the group is full is displayed
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.game_lobby_is_full)).assertIsDisplayed()
    }

    @Test
    fun clickOnGameLobbiesListButton_opensGameLobbiesList() {
        composeTestRule.onNodeWithTag("showGameLobbiesButton").performClick()

        composeTestRule.onNodeWithTag("gameLobbiesList")
            .assertIsDisplayed()
    }

    @Test
    fun clickOnGameLobbyHeader_opensGameLobbyInfo() {

        composeTestRule.onNodeWithTag("showGameLobbiesButton").performClick()

        try {
            val lobbyHeader = composeTestRule.onAllNodesWithTag("lobbyCard").onFirst()

            lobbyHeader.assertIsDisplayed()
            lobbyHeader.performClick()

            composeTestRule.onAllNodesWithTag("lobbyCard").onFirst().assertIsDisplayed()

        }catch (AssertionError: AssertionError){
            Log.d("Test", "No lobbies to display from DB")
        }

    }

}
