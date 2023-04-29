package com.github.polypoly.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.ui.menu.lobby.GameLobbyActivity
import com.github.polypoly.app.ui.menu.lobby.JoinGameLobbyActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinGameLobbyActivityTest: PolyPolyTest(true, true) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<JoinGameLobbyActivity>()
    val ALL_JOINABLE_LOBBIES = ALL_TEST_GAME_LOBBIES.filter { !it.private && it.usersRegistered.size < it.rules.maximumNumberOfPlayers }

    @Before
    fun startIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() { Intents.release() }

    @Test
    fun launchActivityComponentsDisplayed() {
        composeTestRule.onNodeWithTag("gameLobbyCodeField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("noGameLobbyCodeText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("showGameLobbiesButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("logo").assertIsDisplayed()
    }

    @Test
    fun inputInvalidGameLobbyCodeDisplaysWarningMessage() {
        composeTestRule.onNodeWithTag("gameLobbyCodeField").performTextInput("emixam67")
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").performClick()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.game_lobby_does_not_exist)).assertIsDisplayed()
    }

    @Test
    fun inputEmptyGameLobbyCodeDisplaysWarningMessage() {
        // Leave the game lobby code field empty
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").performClick()
        // Check that a warning message is displayed
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.game_lobby_code_is_empty)).assertIsDisplayed()
    }

    @Test
    fun inputValidGameLobbyCodeInTextFieldJoinsGameLobbyRoom() {
        val lobbyCode = TEST_GAME_LOBBY_AVAILABLE_1.code
        composeTestRule.onNodeWithTag("gameLobbyCodeField").performTextInput(lobbyCode)
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").performClick()

        Intents.intended(IntentMatchers.hasComponent(GameLobbyActivity::class.java.name))
        Intents.intended(IntentMatchers.hasExtra("lobby_code", lobbyCode))
    }

    @Test
    fun inputFullGameLobbyCodeDisplayWarningMessage() {
        val lobbyCode = TEST_GAME_LOBBY_FULL.code
        composeTestRule.onNodeWithTag("gameLobbyCodeField").performTextInput(lobbyCode)
        composeTestRule.onNodeWithTag("JoinGameLobbyButton").performClick()

        // Check that a message that the group is full is displayed
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.game_lobby_is_full)).assertIsDisplayed()
    }

    @Test
    fun clickOnGameLobbiesListButtonOpensGameLobbiesList() {
        composeTestRule.onNodeWithTag("showGameLobbiesButton").performClick()
        composeTestRule.onNodeWithTag("gameLobbiesList")
            .assertIsDisplayed()
    }

    @Test
    fun allJoinableGameLobbiesAreDisplayed(){
        composeTestRule.onNodeWithTag("showGameLobbiesButton").performClick()
        for(lobby in ALL_JOINABLE_LOBBIES){
            composeTestRule.onNodeWithText(lobby.name).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnGameLobbyHeaderOpensGameLobbyInfo() {
        composeTestRule.onNodeWithTag("showGameLobbiesButton").performClick()
        for(lobby in ALL_JOINABLE_LOBBIES){
            val lobbyHeader = composeTestRule.onNodeWithText(lobby.name)
            lobbyHeader.performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onAllNodesWithTag("gameLobbyCardDetails", useUnmergedTree = true).assertCountEquals(1)
            composeTestRule.onAllNodesWithTag("players_title", useUnmergedTree = true).assertCountEquals(1)
            composeTestRule.onAllNodesWithTag("player_name", useUnmergedTree = true).assertCountEquals(lobby.usersRegistered.size)
            composeTestRule.onAllNodesWithTag("playerIcon", useUnmergedTree = true).assertCountEquals(lobby.usersRegistered.size)
            for (player in lobby.usersRegistered) {
                composeTestRule.onNodeWithText(player.name).assertIsDisplayed()
                composeTestRule.onNodeWithContentDescription("${player.name} icon").assertIsDisplayed()
            }
            composeTestRule.onNodeWithText("Round duration: ", useUnmergedTree = true).assertIsDisplayed()
            composeTestRule.onNodeWithText(lobby.rules.roundDuration.toString(), useUnmergedTree = true).assertIsDisplayed()
            composeTestRule.onNodeWithText("Game mode: ", useUnmergedTree = true).assertIsDisplayed()
            composeTestRule.onNodeWithText(lobby.rules.gameMode.toString(), useUnmergedTree = true).assertIsDisplayed()
            composeTestRule.onNodeWithTag("joinGameLobbyButton", useUnmergedTree = true).assertIsDisplayed().assertHasClickAction()
        }

    }

    @Test
    fun clickJoinButtonInLobbyInfoJoinsGameLobby(){
        composeTestRule.onNodeWithTag("showGameLobbiesButton").performClick()
        val lobby = ALL_JOINABLE_LOBBIES[1]
        val lobbyHeader = composeTestRule.onNodeWithText(lobby.name)
        lobbyHeader.performClick()
        composeTestRule.onNodeWithTag("joinGameLobbyButton", useUnmergedTree = true).performClick()
        Intents.intended(IntentMatchers.hasComponent(GameLobbyActivity::class.java.name))
        Intents.intended(IntentMatchers.hasExtra("lobby_code", lobby.code))
    }

    @Test
    fun onlyOneCardOpenAtATime(){
        composeTestRule.onNodeWithTag("showGameLobbiesButton").performClick()
        for(lobby in ALL_JOINABLE_LOBBIES){
            val lobbyHeader = composeTestRule.onNodeWithText(lobby.name)
            lobbyHeader.performClick()
            composeTestRule.onAllNodesWithTag("gameLobbyCardDetails", useUnmergedTree = true).assertCountEquals(1)
        }
    }

    @Test
    fun clickBackOnCardClosesCard(){
        composeTestRule.onNodeWithTag("showGameLobbiesButton").performClick()
        for(lobby in ALL_JOINABLE_LOBBIES){
            val lobbyHeader = composeTestRule.onNodeWithText(lobby.name)
            composeTestRule.onAllNodesWithTag("gameLobbyCardDetails", useUnmergedTree = true).assertCountEquals(0)
            lobbyHeader.performClick()
            composeTestRule.onAllNodesWithTag("gameLobbyCardDetails", useUnmergedTree = true).assertCountEquals(1)
            lobbyHeader.performClick()
            composeTestRule.onAllNodesWithTag("gameLobbyCardDetails", useUnmergedTree = true).assertCountEquals(0)
        }
    }

}
