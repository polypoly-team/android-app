package com.github.polypoly.app.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.views.overlay.Marker

@RunWith(AndroidJUnit4::class)
class MapActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MapActivity>()

    private val dropDownButton = composeTestRule.onNodeWithTag("dropDownButton")
    private val gameInfoButton = composeTestRule.onNodeWithTag("gameInfoButton")
    private val playerInfoButton = composeTestRule.onNodeWithTag("playerInfoButton")

    @Before
    fun setUp() {
        runBlocking { delay(5000) } // TODO: Find a better way to wait for the UI to update
    }

    @Test
    fun hudIsDisplayed() {
        dropDownButton.assertIsDisplayed()
        playerInfoButton.assertIsDisplayed()
    }

    @Test
    fun gameInfoAndOtherPlayersInfoAreDisplayedOnDropDownButtonClick() {
        gameInfoButton.assertDoesNotExist()
        dropDownButton.performClick()
        gameInfoButton.assertIsDisplayed()
    }

    @Test
    fun gameInfoAndOtherPlayersInfoAreCollapsedWhenDropDownButtonIsClickedAgain() {
        dropDownButton.performClick()
        gameInfoButton.assertIsDisplayed()
        dropDownButton.performClick()
        gameInfoButton.assertDoesNotExist()
    }

    @Test
    fun mapActivity_UIComponents_Displayed() {
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
        //composeTestRule.onNodeWithTag("resetButton").assertIsDisplayed()
        //composeTestRule.onNodeWithTag("distanceWalked").assertIsDisplayed()
    }

    //@Test
    //fun mapActivity_ResetButton_Clicked_DistanceReset() {
    //    fun formattedDistance(distance: Float): String {
    //        return if (distance < 1000) "${"%.1f".format(distance)}m"
    //        else "${"%.1f".format(distance / 1000)}km"
    //    }

    //    composeTestRule.onNodeWithTag("resetButton").performClick()

    //    runBlocking { delay(500) }

    //    composeTestRule.onNodeWithTag("distanceWalked")
    //        .assertTextContains("Distance walked: ${formattedDistance(0f)}")
    //}

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
        val mapView = composeTestRule.activity.mapView
        val n = mapView.overlays.filterIsInstance<Marker>().size
        val random = (0 until n).random()
        return mapView.overlays.filterIsInstance<Marker>()[random]
    }

    private fun forceOpenMarkerDialog() {
        composeTestRule.activity.showDialog.value = true
        composeTestRule.activity.currentMarker = getRandomMarker()
        runBlocking { delay(500) }
    }
}
