package com.github.polypoly.app.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.game.GameActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameActivityLandlordTest : PolyPolyTest(true, false) {

    private val currentPlayer: Player
    private val currentGame: Game = Game.launchFromPendingGame(TEST_GAME_LOBBY_AVAILABLE_5)

    init {
        GameRepository.game = currentGame
        currentPlayer = currentGame.getAdmin()
        GameRepository.player = currentPlayer
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<GameActivity>()

    @Before
    fun startIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    /**
     * Clear the location of the player if any to make sure the next test will not be impacted
     * by the previous one
     */
    @After
    fun clearLocationOfThePlayerIfAny() {
        for (location in currentPlayer.getOwnedLocations()) {
            currentPlayer.looseLocation(location)
        }
    }

    @Test
    fun whenClickingOnOtherPlayerYouCanChooseToTrade() {
        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("asking_for_a_trade_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("trade_button").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCanSeeAPopUpWithBuildingsToChoose() {
        for(i in 0..2) {
            val inGameLocation = currentGame.inGameLocations[i]
            currentPlayer.earnNewLocation(inGameLocation)
        }

        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Choose a location to trade").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCanSeeTheEntirePopUpEvenWithALotOfBuildings() {
        for(i in 0..15) {
            val inGameLocation = currentGame.inGameLocations[i]
            currentPlayer.earnNewLocation(inGameLocation)
        }

        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Choose a location to trade").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCantSeeThePopUpIfYouDoNotHaveBuildings() {
        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertDoesNotExist()
    }
}
