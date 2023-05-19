package com.github.polypoly.app.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.ui.game.PlayerState
import com.github.polypoly.app.ui.map.MapUI
import com.github.polypoly.app.utils.global.GlobalInstances
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.views.overlay.Marker

@RunWith(AndroidJUnit4::class)
class GameActivityTest : PolyPolyTest(true, false) {

    init {
        GameRepository.game = Game.launchFromPendingGame(TEST_GAME_LOBBY_AVAILABLE_3)
        GameRepository.player =
            GameRepository.game?.getPlayer(GameRepository.game?.admin?.id ?: 0) ?: Player()
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<GameActivity>()

    @Before
    fun setUp() {
        runBlocking { delay(5000) } // TODO: Find a better way to wait for the UI to update
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
    fun mapActivity_UIComponents_Displayed() {
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distance_walked_row").assertIsDisplayed()
    }

    @Test
    fun mapActivity_InfoView_Displayed_On_Marker_Click() {
        forceOpenMarkerDialog()
        composeTestRule.onNodeWithTag("buildingInfoDialog").assertIsDisplayed()
    }

    @Test
    fun mapActivity_Hides_Marker_Info_View_On_Close_Button_Click() {
        forceOpenMarkerDialog()
        composeTestRule.onNodeWithTag("buildingInfoDialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("closeButton").performClick()
        composeTestRule.onNodeWithTag("buildingInfoDialog").assertDoesNotExist()
    }

    @Test
    fun mapActivity_Displays_Error_On_Invalid_Bet_Amount() {
        forceOpenMarkerDialog()
        composeTestRule.onNodeWithTag("betButton").performClick()

        composeTestRule.onNodeWithTag("betInput").performTextInput("10")
        composeTestRule.onNodeWithTag("confirmBetButton", true).performClick()

        composeTestRule.onNodeWithTag("betErrorMessage", true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("closeBetButton", true).performClick()
        // composeTestRule.onNodeWithTag("betDialog", true).assertDoesNotExist()
    }

    @Test // could be looped for extensive testing
    fun mapActivity_Displays_Success_On_Valid_Bet_Amount() {
        forceOpenMarkerDialog()
        composeTestRule.onNodeWithTag("betButton").performClick()
        // TODO: Replace by future MAX_BET or similar
        composeTestRule.onNodeWithTag("betInput").performTextInput("3000")
        composeTestRule.onNodeWithTag("confirmBetButton", true).performClick()
        // composeTestRule.onNodeWithTag("betDialog", true).assertDoesNotExist()
    }

    // While it may be better for grades to have a test for each component,
    // it would multiply the time these tests take to run by a lot, due to how long it takes to
    // start the activity. This is why I have chosen to group them by state.

    @Test
    fun mapActivity_Displays_Only_Necessary_UI_Components_INIT() {
        setCurrentPlayerState(PlayerState.INIT)
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distance_walked_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud").assertIsDisplayed()
    }

    @Test
    fun mapActivity_Displays_Only_Necessary_UI_Components_ROLLING_DICE() {
        setCurrentPlayerState(PlayerState.ROLLING_DICE)
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distance_walked_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud").assertIsDisplayed()
        composeTestRule.onNodeWithTag("roll_dice_button").assertIsDisplayed()
    }

    @Test
    fun mapActivity_Displays_Only_Necessary_UI_Components_MOVING() {
        GameActivity.mapViewModel.setInteractableLocation(getRandomLocationProperty())
        GameActivity.mapViewModel.goingToLocationProperty = getRandomLocationProperty()
        setCurrentPlayerState(PlayerState.MOVING)
        waitForUIToUpdate()
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distance_walked_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud").assertIsDisplayed()
        composeTestRule.onNodeWithTag("interactable_location_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("going_to_location_text").assertIsDisplayed()
    }

    @Test
    fun mapActivity_Displays_Only_Necessary_UI_Components_INTERACTING() {
        setCurrentPlayerState(PlayerState.INTERACTING)
        GameActivity.mapViewModel.setInteractableLocation(getRandomLocationProperty())
        waitForUIToUpdate()
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distance_walked_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud").assertIsDisplayed()
        composeTestRule.onNodeWithTag("interactable_location_text").assertIsDisplayed()
    }

    // --- Trading tests --- //

    @Test
    fun whenClickingOnOtherPlayerYouCanChooseToTrade() {
        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onNodeWithTag("other_player_hud_12").performClick()
        composeTestRule.onNodeWithTag("asking_for_a_trade_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("trade_button").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCanSeeAPopUpWithBuildingsToChoose() {
        // give some location to the player
        GameRepository.player?.getOwnedLocations()?.clear()
        for(i in 0..2) {
            val inGameLocation = GameRepository.game?.getInGameLocation()?.get(i)!!
            GameRepository.player?.getOwnedLocations()?.add(inGameLocation)
        }

        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onNodeWithTag("other_player_hud_12").performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Choose a location to trade").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCanSeeTheEntirePopUpEvenWithALotOfBuildings() {
        // give some location to the player
        GameRepository.player?.getOwnedLocations()?.clear()
        for(i in 0..15) {
            val inGameLocation = GameRepository.game?.getInGameLocation()?.get(i)!!
            GameRepository.player?.getOwnedLocations()?.add(inGameLocation)
        }

        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onNodeWithTag("other_player_hud_12").performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Choose a location to trade").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCantSeeThePopUpIfYouDoNotHaveBuildings() {
        // give no location to the player
        GameRepository.player?.getOwnedLocations()?.clear()

        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onNodeWithTag("other_player_hud_12").performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertDoesNotExist()
    }
    

    // --- Utility functions --- //

    private fun waitForUIToUpdate() {
        runBlocking { delay(500) }
    }

    private fun getRandomLocationProperty(): LocationProperty {
        return GameActivity.mapViewModel.markerToLocationProperty[getRandomMarker()]!!
    }

    private fun setCurrentPlayerState(state: PlayerState) {
        GlobalInstances.playerState.value = state
    }

    private fun getRandomMarker(): Marker {
        val mapView = MapUI.mapView
        val n = mapView.overlays.filterIsInstance<Marker>().size
        val random = (0 until n).random()
        return mapView.overlays.filterIsInstance<Marker>()[random]
    }

    private fun forceOpenMarkerDialog() {
        GameActivity.mapViewModel.selectedMarker = getRandomMarker()
        GameActivity.interactingWithProperty.value = true
        waitForUIToUpdate()
    }
}
