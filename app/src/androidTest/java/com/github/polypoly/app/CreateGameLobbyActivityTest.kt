package com.github.polypoly.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.ui.menu.lobby.CreateGameLobbyActivity
import com.github.polypoly.app.ui.menu.lobby.GameLobbyActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateGameLobbyActivityTest: PolyPolyTest(false, false) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<CreateGameLobbyActivity>()

    var ALL_LIST_PICKERS_TITLES = listOf<String>()
    var ALL_NUM_PICKERS_TITLES = listOf<String>()

    @Before
    fun initPickersList() {
        ALL_LIST_PICKERS_TITLES = listOf(
            composeTestRule.activity.getString(R.string.create_game_lobby_round_duration),
            composeTestRule.activity.getString(R.string.create_game_lobby_game_mode),
        )
        ALL_NUM_PICKERS_TITLES = listOf(
            composeTestRule.activity.getString(R.string.create_game_lobby_min_num_players),
            composeTestRule.activity.getString(R.string.create_game_lobby_max_num_players),
            composeTestRule.activity.getString(R.string.create_game_lobby_num_rounds),
            composeTestRule.activity.getString(R.string.create_game_lobby_initial_balance)
        )
    }

    @Before
    fun startIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun createGameLobbyContentDisplayed() {
        composeTestRule.onNodeWithTag("create_game_menu").assertIsDisplayed()
        composeTestRule.onNodeWithTag("game_name_text_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag("private_game_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("private_game_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("private_game_checkbox").assertIsDisplayed()
        composeTestRule.onNodeWithTag("create_game_lobby_column").assertIsDisplayed()
        composeTestRule.onNodeWithTag("create_game_lobby_button").assertIsDisplayed()

        for (pickerTitle in ALL_LIST_PICKERS_TITLES) {
            composeTestRule.onNodeWithTag(pickerTitle + "list_picker_row").assertIsDisplayed()
            composeTestRule.onNodeWithTag(pickerTitle + "left_arrow").assertIsDisplayed()
            composeTestRule.onNodeWithTag(pickerTitle + "left_arrow").assertHasClickAction()
            composeTestRule.onNodeWithTag(pickerTitle + "list_picker_field").assertIsDisplayed()
            composeTestRule.onNodeWithTag(pickerTitle + "right_arrow").assertIsDisplayed()
            composeTestRule.onNodeWithTag(pickerTitle + "right_arrow").assertHasClickAction()
        }

        for (pickerTitle in ALL_NUM_PICKERS_TITLES) {
            composeTestRule.onNodeWithTag(pickerTitle + "number_picker_row").assertIsDisplayed()
            composeTestRule.onNodeWithTag(pickerTitle + "left_arrow").assertIsDisplayed()
            composeTestRule.onNodeWithTag(pickerTitle + "left_arrow").assertHasClickAction()
            composeTestRule.onNodeWithTag(pickerTitle + "number_picker_field").assertIsDisplayed()
            composeTestRule.onNodeWithTag(pickerTitle + "right_arrow").assertIsDisplayed()
            composeTestRule.onNodeWithTag(pickerTitle + "right_arrow").assertHasClickAction()
        }

    }

    @Test
    fun createGameButtonEnabledOnNonEmptyName() {

        // Initially, the create game button should be disabled
        composeTestRule.onNodeWithTag("create_game_lobby_button").assertIsNotEnabled()

        // Enter a valid game name
        composeTestRule.onNodeWithTag("game_name_text_field").performTextInput("yepidiyep")
        composeTestRule.onNodeWithTag("game_name_text_field").performImeAction()

        // The create game button should now be enabled
        composeTestRule.onNodeWithTag("create_game_lobby_button").assertIsEnabled()
    }

    @Test
    fun createGameButtonDisabledOnEmptyName() {

        // Initially, the create game button should be disabled
        composeTestRule.onNodeWithTag("create_game_lobby_button").assertIsNotEnabled()

        // Enter an invalid game name
        composeTestRule.onNodeWithTag("game_name_text_field").performTextInput("")
        composeTestRule.onNodeWithTag("game_name_text_field").performImeAction()


        // The create game button should still be disabled
        composeTestRule.onNodeWithTag("create_game_lobby_button").assertIsNotEnabled()
    }

    @Test
    fun creatingGameLaunchesGameLobbyActivity(){
        composeTestRule.onNodeWithTag("create_game_lobby_button").assertIsNotEnabled()

        composeTestRule.onNodeWithTag("game_name_text_field").performTextInput("yepidiyep")
        composeTestRule.onNodeWithTag("game_name_text_field").performImeAction()

        composeTestRule.onNodeWithTag("create_game_lobby_button").performClick()
        //TODO : modify when DB is done (put the lobby code in the intent)
        Intents.intended(IntentMatchers.hasComponent(GameLobbyActivity::class.java.name))
        Intents.intended(IntentMatchers.hasExtra("lobby_code", "1234"))
    }

    @Test
    fun creatingGameLobbyAddsItToDB(){
        //TODO implement when DB is done
    }

}