package com.github.polypoly.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.network.getAllValues
import com.github.polypoly.app.ui.menu.lobby.CreateGameLobbyActivity
import com.github.polypoly.app.ui.menu.lobby.GameLobbyActivity
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateGameLobbyActivityTest: PolyPolyTest(false, false, true) {

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
    fun creatingGameLaunchesGameLobbyActivity() {
        composeTestRule.onNodeWithTag("create_game_lobby_button").assertIsNotEnabled()

        composeTestRule.onNodeWithTag("game_name_text_field").performTextInput("yepidiyep")
        composeTestRule.onNodeWithTag("game_name_text_field").performImeAction()

        composeTestRule.onNodeWithTag("create_game_lobby_button").performClick()

        Intents.intended(IntentMatchers.hasComponent(GameLobbyActivity::class.java.name))
    }

    @Test
    fun numPropertiesNumPickerShowsWhenGameModeIsLandlord() {
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_game_mode) + "right_arrow")
            .performClick()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_game_mode) + "list_picker_field")
            .assertTextEquals(GameMode.LANDLORD.toString())

        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "number_picker_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "left_arrow").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "left_arrow").assertHasClickAction()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "number_picker_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "right_arrow").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "right_arrow").assertHasClickAction()
    }

    @Test
    fun numPropertiesNumPickerOnlyShowsWhenGameModeIsLandlord() {
        numPropertiesNumPickerShowsWhenGameModeIsLandlord()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_game_mode) + "left_arrow")
            .performClick()
        // check round distribution is gone

        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "number_picker_row").assertDoesNotExist()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "left_arrow").assertDoesNotExist()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "number_picker_field").assertDoesNotExist()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_landlord_num_properties) + "right_arrow").assertDoesNotExist()

    }

    @Test
    fun roundsSelectorsAreHiddenWhenLastStandingMode(){
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_game_mode) + "left_arrow")
            .performClick()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "number_picker_row").assertDoesNotExist()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "left_arrow").assertDoesNotExist()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "number_picker_field").assertDoesNotExist()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "right_arrow").assertDoesNotExist()

        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "list_picker_row").assertDoesNotExist()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "left_arrow").assertDoesNotExist()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "list_picker_field").assertDoesNotExist()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "right_arrow").assertDoesNotExist()
    }

    @Test
    fun roundsSelectorsAreVisibleWhenNotLastStandingMode(){
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_num_rounds) + "number_picker_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_num_rounds) + "left_arrow").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_num_rounds) + "left_arrow").assertHasClickAction()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_num_rounds) + "number_picker_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_num_rounds) + "right_arrow").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_num_rounds) + "right_arrow").assertHasClickAction()

        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "list_picker_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "left_arrow").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "left_arrow").assertHasClickAction()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "list_picker_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "right_arrow").assertIsDisplayed()
        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.create_game_lobby_round_duration) + "right_arrow").assertHasClickAction()

    }

    @Test
    fun creatingGameLobbyAddsItToDB() {
        val lobbiesBeforeAdding = remoteDB.getAllValues<GameLobby>()
        val lobbyName = "coco"
        composeTestRule.onNodeWithTag("create_game_lobby_button").assertIsNotEnabled()

        composeTestRule.onNodeWithTag("game_name_text_field").performTextInput(lobbyName)
        composeTestRule.onNodeWithTag("game_name_text_field").performImeAction()

        composeTestRule.onNodeWithTag("create_game_lobby_button").performClick()
        Intents.intended(IntentMatchers.hasComponent(GameLobbyActivity::class.java.name))

        val lobbiesAfterAdding = remoteDB.getAllValues<GameLobby>()
        lobbiesAfterAdding.thenAccept{
            assertEquals(it.size + 1, it.size)
        }
       lobbiesBeforeAdding.thenAccept{ it ->
           assertTrue(it.any { it.name == lobbyName })
       }
    }

}