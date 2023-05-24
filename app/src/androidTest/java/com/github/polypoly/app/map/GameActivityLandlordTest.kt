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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameActivityLandlordTest : PolyPolyTest(true, false) {

    init {
        GameRepository.game = Game.launchFromPendingGame(TEST_GAME_LOBBY_AVAILABLE_5)
        GameRepository.player =
            GameRepository.game?.getPlayer(GameRepository.game?.admin?.id ?: "0") ?: Player()
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
    fun whenClickingOnOtherPlayerYouCanChooseToTrade() {
        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("asking_for_a_trade_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("trade_button").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCanSeeAPopUpWithBuildingsToChoose() {
        // give some location to the player
        GameRepository.player?.getOwnedLocations()?.clear()
        for(i in 0..2) {
            val inGameLocation = GameRepository.game?.inGameLocations?.get(i)!!
            GameRepository.player?.getOwnedLocations()?.add(inGameLocation)
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
        // give some location to the player
        GameRepository.player?.getOwnedLocations()?.clear()
        for(i in 0..15) {
            val inGameLocation = GameRepository.game?.inGameLocations?.get(i)!!
            GameRepository.player?.getOwnedLocations()?.add(inGameLocation)
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
        // give no location to the player
        GameRepository.player?.getOwnedLocations()?.clear()

        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertDoesNotExist()
    }
}
