package com.github.polypoly.app.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.base.RulesObject
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.map.MapUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.views.overlay.Marker

@RunWith(AndroidJUnit4::class)
class GameActivityTest: PolyPolyTest(true, false) {

    init {
        GameRepository.game = Game.launchFromPendingGame(TEST_GAME_LOBBY_AVAILABLE_4)
        GameRepository.player = GameRepository.game?.getPlayer(GameRepository.game?.admin?.id ?: 0) ?: Player()
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<GameActivity>()

    private val otherPlayersAndGameDropDownButton = composeTestRule.onNodeWithTag("otherPlayersAndGameDropDownButton")
    private val gameInfoButton = composeTestRule.onNodeWithTag("gameInfoButton")
    private val playerInfoButton = composeTestRule.onNodeWithTag("playerInfoButton")
    private val gameMenuDropDownButton = composeTestRule.onNodeWithTag("gameMenuDropDownButton")

    private val menuButtonRules = composeTestRule.onNodeWithContentDescription("Show Rules")
    private val menuButtonRankings = composeTestRule.onNodeWithContentDescription("Open Rankings")
    private val menuButtonProfile = composeTestRule.onNodeWithContentDescription("Open Profile")
    private val menuButtonSettings = composeTestRule.onNodeWithContentDescription("Open Settings")

    private val rules = composeTestRule.onNodeWithText(RulesObject.rulesTitle)

    @Before
    fun setUp() {
        runBlocking { delay(5000) } // TODO: Find a better way to wait for the UI to update
    }

    @Before
    fun startIntents() { Intents.init() }

    @After
    fun releaseIntents() { Intents.release() }

    /*@Test
    fun hudIsDisplayed() {
        otherPlayersAndGameDropDownButton.assertIsDisplayed()
        playerInfoButton.assertIsDisplayed()
        gameMenuDropDownButton.assertIsDisplayed()
    }

    @Test
    fun gameInfoAndOtherPlayersInfoAreDisplayedOnDropDownButtonClick() {
        gameInfoButton.assertDoesNotExist()
        otherPlayersAndGameDropDownButton.performClick()
        gameInfoButton.assertIsDisplayed()
    }

    @Test
    fun gameInfoAndOtherPlayersInfoAreCollapsedWhenDropDownButtonIsClickedAgain() {
        otherPlayersAndGameDropDownButton.performClick()
        gameInfoButton.assertIsDisplayed()
        otherPlayersAndGameDropDownButton.performClick()
        gameInfoButton.assertDoesNotExist()
    }

    @Test
    fun gameMenuIsDisplayedOnDropDownButtonClick() {
        gameMenuDropDownButton.performClick()
        menuButtonRules.assertIsDisplayed()
        menuButtonRankings.assertIsDisplayed()
        menuButtonProfile.assertIsDisplayed()
        menuButtonSettings.assertIsDisplayed()
    }

    @Test
    fun gameMenuRulesButtonDisplaysRules() {
        gameMenuDropDownButton.performClick()
        menuButtonRules.performClick()
        rules.assertIsDisplayed()
    }

    @Test
    fun gameMenuRulesDismissOnOutsideClick() {
        gameMenuDropDownButton.performClick()
        menuButtonRules.performClick()
        rules.assertIsDisplayed()
        gameMenuDropDownButton.performClick()
        rules.assertDoesNotExist()
    }

    @Test
    fun gameMenuProfileButtonDisplaysActivity() {
        gameMenuDropDownButton.performClick()
        menuButtonProfile.performClick()
        Intents.intended(IntentMatchers.hasComponent(ProfileActivity::class.java.name))
    }

    @Test
    fun gameMenuSettingsButtonDisplaysActivity() {
        gameMenuDropDownButton.performClick()
        menuButtonSettings.performClick()
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.name))
    }*/

    @Test
    fun mapActivity_UIComponents_Displayed() {
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        //composeTestRule.onNodeWithTag("distanceWalked").assertIsDisplayed()
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
        composeTestRule.onNodeWithTag("betDialog", true).assertDoesNotExist()
    }

    @Test // could be looped for extensive testing
    fun mapActivity_Displays_Success_On_Valid_Bet_Amount() {
        forceOpenMarkerDialog()
        composeTestRule.onNodeWithTag("betButton").performClick()
        // TODO: Replace by future MAX_BET or similar
        composeTestRule.onNodeWithTag("betInput").performTextInput("3000")
        composeTestRule.onNodeWithTag("confirmBetButton", true).performClick()
        composeTestRule.onNodeWithTag("betDialog", true).assertDoesNotExist()
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
        runBlocking { delay(500) }
    }
}
