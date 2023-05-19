package com.github.polypoly.app.base.game

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.polypoly.app.R
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.ui.menu.lobby.GameLobbyActivity
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class GameLobbyActivityTest: PolyPolyTest(true, false) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<GameLobbyActivity>()

    private var gameSettingsDisplayedTitles = listOf<String>()
    private val baseGameLobby = TEST_GAME_LOBBY_CURRENT_USER_ADMIN
    private val lobbyCode = baseGameLobby.code
    private val users = listOf(currentUser, TEST_USER_1, TEST_USER_2, TEST_USER_4)

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
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun gameLobbyContentIsDisplayed(){

        val syncFuture = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()

        resetGameLobby()

        syncFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        composeTestRule.waitForIdle()

        val gameLobby = composeTestRule.activity.gameLobbyWaitingModel.getGameLobby().value!!

        composeTestRule.onNodeWithTag("game_lobby_background").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_app_bar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_leave_button", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_app_bar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_body").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_settings_menu", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_settings_menu_arrow", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_settings_menu_title", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_settings_menu_expanded", useUnmergedTree = true).assertDoesNotExist()

        composeTestRule.onNodeWithTag("game_lobby_players_list").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header_icon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header_count").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_players_header_count")
            .assertTextEquals(composeTestRule.activity.getString(R.string.game_lobby_players_count, gameLobby.usersRegistered.size.toString(), gameLobby.rules.maximumNumberOfPlayers.toString()))

        for(player in gameLobby.usersRegistered){
            composeTestRule.onNodeWithTag("${player.name}_game_lobby_player_row").assertIsDisplayed()
            composeTestRule.onNodeWithTag("${player.name}_game_lobby_player_row_icon").assertIsDisplayed()
            composeTestRule.onNodeWithTag("${player.name}_game_lobby_player_row_name").assertTextEquals(player.name)
            if(player.id == gameLobby.admin.id){
                composeTestRule.onNodeWithTag("${player.name}_game_lobby_player_row_admin").assertIsDisplayed()
            } else {
                composeTestRule.onNodeWithTag("${player.name}_game_lobby_player_row_admin").assertDoesNotExist()
            }
        }
        composeTestRule.onAllNodesWithTag("game_lobby_empty_player_slot").assertCountEquals(gameLobby.rules.maximumNumberOfPlayers - gameLobby.usersRegistered.size)
        composeTestRule.onAllNodesWithTag("game_lobby_empty_player_slot_icon").assertCountEquals(gameLobby.rules.maximumNumberOfPlayers - gameLobby.usersRegistered.size)
        composeTestRule.onNodeWithTag("game_lobby_start_game_button_content").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_start_game_button_button").assertIsDisplayed()

        composeTestRule.onNodeWithTag("game_lobby_start_game_button_lobby_code_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_lobby_start_game_button_lobby_code").assertTextEquals(gameLobby.code)
        composeTestRule.onNodeWithTag("game_lobby_start_game_button_lobby_code_title").assertIsDisplayed()
    }

    @Test
    fun gameLobbySettingsMenuIsDisplayedWhenClickedAndDisappearsWhenClickedAgain(){
        val syncFuture = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()
        resetGameLobby()
        syncFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.waitForIdle()

        for(title in gameSettingsDisplayedTitles){
            composeTestRule.onNodeWithTag("${title}_game_lobby_settings_menu_item_title").assertDoesNotExist()
            composeTestRule.onNodeWithTag("${title}_game_lobby_settings_menu_item_value").assertDoesNotExist()
        }

        composeTestRule.onNodeWithTag("game_lobby_settings_menu_arrow", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("game_lobby_settings_menu_expanded", useUnmergedTree = true).assertIsDisplayed()
        for(title in gameSettingsDisplayedTitles){
            composeTestRule.onNodeWithTag("${title}_game_lobby_settings_menu_item_title", useUnmergedTree = true).assertIsDisplayed()
            composeTestRule.onNodeWithTag("${title}_game_lobby_settings_menu_item_value", useUnmergedTree = true).assertIsDisplayed()
        }


        composeTestRule.onNodeWithTag("game_lobby_settings_menu_arrow", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("game_lobby_settings_menu_expanded", useUnmergedTree = true).assertDoesNotExist()
        for(title in gameSettingsDisplayedTitles){
            composeTestRule.onNodeWithTag("${title}_game_lobby_settings_menu_item_title", useUnmergedTree = true).assertDoesNotExist()
            composeTestRule.onNodeWithTag("${title}_game_lobby_settings_menu_item_value", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun gameLobbyIsClosedWhenLeaveButtonIsClickedAndUserNotAnymoreInLobby() {
        val syncFuture = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()
        resetGameLobby()
        syncFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.waitForIdle()

        val gameLobbyBeforeClick = composeTestRule.activity.gameLobbyWaitingModel.getGameLobby().value!!

        assert(gameLobbyBeforeClick.usersRegistered.contains(currentUser))

        composeTestRule.onNodeWithTag("game_lobby_leave_button", useUnmergedTree = true)
            .performClick()

        val gameLobbyAfterClick = composeTestRule.activity.gameLobbyWaitingModel.getGameLobby().value!!

        composeTestRule.onNodeWithTag("game_lobby_background").assertDoesNotExist()
        for (player in gameLobbyAfterClick.usersRegistered) {
            if(player.id == currentUser.id){
                assert(!gameLobbyAfterClick.usersRegistered.contains(player))
            } else  {
                assert(gameLobbyAfterClick.usersRegistered.contains(player))
            }

        }
    }

    @Test
    fun adminIsRedistributedWhenAdminLeavesLobby() {
        val syncFuture = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()
        resetGameLobby()
        syncFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.waitForIdle()

        val gameLobbyBeforeLeave = composeTestRule.activity.gameLobbyWaitingModel.getGameLobby().value!!
        assert(gameLobbyBeforeLeave.admin.id == currentUser.id)
        assert(gameLobbyBeforeLeave.usersRegistered.contains(currentUser))

        composeTestRule.onNodeWithTag("game_lobby_leave_button", useUnmergedTree = true)
            .performClick()

        val gameLobbyAfterClick = composeTestRule.activity.gameLobbyWaitingModel.getGameLobby().value!!
        assert(!gameLobbyAfterClick.usersRegistered.contains(currentUser))
        assert(gameLobbyAfterClick.admin != currentUser)
        assert(gameLobbyAfterClick.usersRegistered.count{it.id == gameLobbyAfterClick.admin.id} == 1)

        resetGameLobby()
    }

    @Test
    fun buttonDisabledWhenNotEnoughPlayers() {
        val syncFuture = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()
        resetGameLobby()
        syncFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("game_lobby_start_game_button_button").performClick()
    }

    @Test
    fun buttonIsEnabledWhenEnoughPlayers(){
        val syncFuture = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()
        resetGameLobby()
        syncFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.waitForIdle()

        val syncFutureNext = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()
        val gameLobbyBeforeNewUser = composeTestRule.activity.gameLobbyWaitingModel.getGameLobby().value!!
        gameLobbyBeforeNewUser.addUser(TEST_USER_3)
        addGameLobbyToDB(gameLobbyBeforeNewUser)
        syncFutureNext.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        composeTestRule.waitForIdle()

        adminCLickOnButtonLaunchesGame()
    }

    @Test
    fun adminCLickOnButtonLaunchesGame(){
        val syncFuture = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()
        resetGameLobby()
        syncFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.waitForIdle()

        val syncFutureNext = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()
        val gameLobbyBeforeNewUser = composeTestRule.activity.gameLobbyWaitingModel.getGameLobby().value!!
        gameLobbyBeforeNewUser.addUser(TEST_USER_5)
        addGameLobbyToDB(gameLobbyBeforeNewUser)
        syncFutureNext.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        composeTestRule.onNodeWithTag("game_lobby_start_game_button_button", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        Intents.intended(IntentMatchers.hasComponent(GameActivity::class.java.name))

    }

    fun resetGameLobby(){
        for (user in baseGameLobby.usersRegistered){
            baseGameLobby.removeUser(user.id)
        }
        for (user in users){
            baseGameLobby.addUser(user)
        }
        addGameLobbyToDB(baseGameLobby)
    }

}