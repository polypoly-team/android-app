package com.github.polypoly.app.game_and_map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.viewmodels.game.GameViewModel
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class GameActivityTest : PolyPolyTest(true, false) {

    init {
        val newGame = Game.launchFromPendingGame(TEST_GAME_LOBBY_AVAILABLE_4)
        GameRepository.game = newGame
        GameRepository.player = newGame.getPlayer(newGame.admin.id)
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<GameActivity>()

    @Before
    fun setUp() {
        forceChangePlayerState(PlayerState.ROLLING_DICE).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
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
        forceOpenMarkerDialog().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        composeTestRule.onNodeWithTag("building_info_dialog").assertIsDisplayed()
    }

    @Test
    fun mapActivity_Hides_Marker_Info_View_On_Close_Button_Click() {
        forceOpenMarkerDialog().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        composeTestRule.onNodeWithTag("building_info_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("close_button").performClick()
        composeTestRule.onNodeWithTag("building_info_dialog").assertDoesNotExist()
    }

    @Test
    fun mapActivityDisplaysErrorOnInvalidBidAmount() {
        forceOpenMarkerDialog().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        forceChangePlayerState(PlayerState.INTERACTING).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        composeTestRule.onNodeWithTag("bid_button").performClick()

        composeTestRule.onNodeWithTag("bid_input").performTextInput("10")
        composeTestRule.onNodeWithTag("confirm_bid_button", true).performClick()

        composeTestRule.onNodeWithTag("bid_error_message", true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("close_bid_button", true).performClick()
        composeTestRule.onNodeWithTag("bid_dialog", true).assertDoesNotExist()
    }

    @Test // could be looped for extensive testing
    fun mapActivity_Displays_Success_On_Valid_Bet_Amount() {
        forceOpenMarkerDialog().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        forceChangePlayerState(PlayerState.INTERACTING).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        composeTestRule.onNodeWithTag("bid_button").performClick()
        // TODO: Replace by future MAX_BET or similar
        composeTestRule.onNodeWithTag("bid_input").performTextInput("3000")
        composeTestRule.onNodeWithTag("confirm_bid_button", true).performClick()
        composeTestRule.onNodeWithTag("bid_dialog", true).assertDoesNotExist()
    }

    // While it may be better for grades to have a test for each component,
    // it would multiply the time these tests take to run by a lot, due to how long it takes to
    // start the activity. This is why I have chosen to group them by state.

    @Test
    fun mapActivity_Displays_Only_Necessary_UI_Components_INIT() {
        forceChangePlayerState(PlayerState.INIT).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distance_walked_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_player").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_other_players_and_game").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_game_menu").assertIsDisplayed()
    }

    @Test
    fun mapActivity_Displays_Only_Necessary_UI_Components_ROLLING_DICE() {
        forceChangePlayerState(PlayerState.ROLLING_DICE).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distance_walked_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_player").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_other_players_and_game").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_game_menu").assertIsDisplayed()
        composeTestRule.onNodeWithTag("roll_dice_button").assertIsDisplayed()
    }

    @Test
    fun mapActivity_Displays_Only_Necessary_UI_Components_MOVING() {
        forceOpenMarkerDialog().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        forceChangePlayerState(PlayerState.MOVING).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distance_walked_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_player").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_other_players_and_game").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_game_menu").assertIsDisplayed()
        composeTestRule.onNodeWithTag("interactable_location_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("going_to_location_text").assertIsDisplayed()
    }

    @Test
    fun mapActivity_Displays_Only_Necessary_UI_Components_INTERACTING() {
        forceOpenMarkerDialog().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        forceChangePlayerState(PlayerState.INTERACTING).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        composeTestRule.onNodeWithTag("distance_walked_row").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_player").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_other_players_and_game").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hud_game_menu").assertIsDisplayed()
        composeTestRule.onNodeWithTag("interactable_location_text").assertIsDisplayed()
    }

    @Test
    fun cantOpenTradeInOtherModeThanLandlord() {
        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("asking_for_a_trade_dialog").assertDoesNotExist()
        composeTestRule.onNodeWithTag("trade_button").assertDoesNotExist()
    }

    @Test
    fun successfulBidNotificationIsNotDisplayedByDefault() {
        composeTestRule.onNodeWithTag("successful_bid_alert").assertDoesNotExist()
    }

    @Test
    fun turnFinishedIsNotDisplayedByDefault() {
        composeTestRule.onNodeWithTag("turn_finished_notification").assertDoesNotExist()
    }

    @Test
    fun turnFinishedIsDisplayedAtEndOfTurn() {
        forceChangePlayerState(PlayerState.TURN_FINISHED).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        composeTestRule.onNodeWithTag("turn_finished_notification").assertIsDisplayed()
    }

    private fun forceOpenMarkerDialog(): CompletableFuture<Boolean> {
        return execInMainThread {
            GameActivity.mapViewModel.selectLocation(getRandomLocation())
            GameActivity.mapViewModel.goingToLocationProperty = getRandomLocation()
            waitForUIToUpdate()
        }
    }

    private fun applyPlayerStateChange(gameViewModel: GameViewModel, playerState: PlayerState) {
        gameViewModel.resetTurnState()
        if (playerState == PlayerState.ROLLING_DICE) return

        gameViewModel.diceRolled()
        if (playerState == PlayerState.MOVING) return

        gameViewModel.locationReached()
        if (playerState == PlayerState.INTERACTING) return

        gameViewModel.startBidding()
        if (playerState == PlayerState.BIDDING) return

        gameViewModel.endBidding()
        if (playerState == PlayerState.TURN_FINISHED) return

        // TODO add other states support when needed
    }

    private fun forceChangePlayerState(playerState: PlayerState): CompletableFuture<Boolean> {
        return execInMainThread {
            applyPlayerStateChange(composeTestRule.activity.gameModel, playerState)
            waitForUIToUpdate()
        }
    }
}