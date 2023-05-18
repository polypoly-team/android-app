package com.github.polypoly.app.base.game

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import com.github.polypoly.app.R
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.menu.lobby.GameLobbyActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class GameLobbyActivityTest: PolyPolyTest(true, true) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<GameLobbyActivity>()

    private val gameLobby = TEST_GAME_LOBBY_AVAILABLE_3
    private val lobbyCode = gameLobby.code
    private var gameSettingsDisplayedTitles = listOf<String>()

    override fun _prepareTest() {
        gameSettingsDisplayedTitles = listOf(
            composeTestRule.activity.getString(R.string.create_game_lobby_game_mode),
            composeTestRule.activity.getString(R.string.create_game_lobby_num_rounds),
            composeTestRule.activity.getString(R.string.create_game_lobby_round_duration),
            composeTestRule.activity.getString(R.string.create_game_lobby_initial_balance),
        )
        GameRepository.gameCode = lobbyCode
    }

    @Before
    fun setup(){
        Intents.init()
    }

    @After
    fun releaseIntents() { Intents.release() }

    @Test
    fun gameLobbyContentIsDisplayed(){

        val syncFuture = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()

        //Setup game lobby ready for start
        addGameLobbyToDB(TEST_GAME_LOBBY_AVAILABLE_3)

        syncFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("game_lobby_background").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_app_bar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_leave_button", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_app_bar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_body").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_settings_menu_arrow").assertExists()
        composeTestRule.onNodeWithTag("game_lobby_settings_menu_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_settings_menu_expanded").assertDoesNotExist()
        for(title in gameSettingsDisplayedTitles){
            composeTestRule.onNodeWithTag("${title}_game_lobby_settings_menu_item_title").assertDoesNotExist()
            composeTestRule.onNodeWithTag("${title}_game_lobby_settings_menu_item_value").assertDoesNotExist()
        }
        composeTestRule.onNodeWithTag("game_lobby_players_list").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header_icon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header_count").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header_count")
            .assertTextEquals(composeTestRule.activity.getString(R.string.game_lobby_players_count, gameLobby.usersRegistered.size.toString(), gameLobby.rules.maximumNumberOfPlayers.toString()))

    }

}