package com.github.polypoly.app.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.game.GameActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

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

    /*@Test
    fun testLandlord() {

    }*/

    @Test
    fun whenClickingOnOtherPlayerYouCanChooseToTrade() {
        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("asking_for_a_trade_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("trade_button").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCanSeeAPopUpWithBuildingsToChoose() {
        clearLocationOfThePlayerIfAny()
        giveLocationsToPlayer(2)

        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Choose a location to trade").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCanSeeTheEntirePopUpEvenWithALotOfBuildings() {
        clearLocationOfThePlayerIfAny()
        giveLocationsToPlayer(16)

        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Choose a location to trade").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun whenClickingOnTradeYouCantSeeThePopUpIfYouDoNotHaveBuildings() {
        clearLocationOfThePlayerIfAny()

        composeTestRule.onNodeWithTag("other_players_and_game_hud").performClick()
        composeTestRule.onAllNodesWithTag("other_player_hud")[0].performClick()
        composeTestRule.onNodeWithTag("trade_button").performClick()
        composeTestRule.onNodeWithTag("locations_list_dialog").assertDoesNotExist()
    }

    /**
     * Clear the location of the player if any
     */
    private fun clearLocationOfThePlayerIfAny() {
        val gameViewModel = composeTestRule.activity.gameModel
        for (location in gameViewModel.getGameData().value!!.getOwnedLocations(currentPlayer)) {
            currentPlayer.looseLocation(location)
        }
        val remains = gameViewModel.getGameData().value!!.inGameLocations.filter { it.owner == currentPlayer }
        // refresh game data to reflect change
        execInMainThread { gameViewModel.refreshGameData() }.orTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }

    /**
     * Give locations to the player, could give less if there is not enough locations
     * @param numberOfLocations the number of locations to give
     */
    private fun giveLocationsToPlayer(numberOfLocations: Int) {
        repeat(numberOfLocations) {
            val unownedLocation = currentGame.inGameLocations.filter { it.owner == null }
            if (unownedLocation.isEmpty()) {
                return
            }
            val inGameLocation = unownedLocation[0]
            currentPlayer.earnNewLocation(inGameLocation)
        }
        // refresh game data to reflect change
        execInMainThread { composeTestRule.activity.gameModel.refreshGameData() }.orTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }
}
