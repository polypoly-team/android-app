package com.github.polypoly.app.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.ui.map.MapUI
import com.github.polypoly.app.ui.map.VisitedMapActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.views.overlay.Marker

@RunWith(AndroidJUnit4::class)
class VisitedMapActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<VisitedMapActivity>()

    @Before
    fun startIntents() { Intents.init() }

    @After
    fun releaseIntents() { Intents.release() }

    @Test
    fun mapIsDisplayed() {
        composeTestRule.onNodeWithTag("map").assertIsDisplayed()
    }

    @Test
    fun openDialogWhenInteractingWithPropertyTrue() {
        val marker = MapUI.mapView.overlays.first { it is Marker} as Marker
        composeTestRule.activity.mapViewModel.selectedMarker = marker
        composeTestRule.activity.interactingWithProperty.value = true
        runBlocking { delay(500) }
        composeTestRule.onNodeWithTag("building_description_dialog").assertIsDisplayed()
    }

    @Test
    fun openCorrectDialogWithLocationWithDescription() {
        val locationWithDescription = LocationPropertyRepository.getZones()
            .first { zone -> zone.locationProperties.any { location -> location.description != "" } }
            .locationProperties.first {location -> location.description != ""}
        val locationName = locationWithDescription.name
        val marker = MapUI.mapView.overlays.first { it is Marker && it.title == locationName } as Marker
        composeTestRule.activity.mapViewModel.selectedMarker = marker
        composeTestRule.activity.interactingWithProperty.value = true
        runBlocking { delay(500) }
        composeTestRule.onNodeWithText(locationName)
        composeTestRule.onNodeWithText(locationWithDescription.description)
        composeTestRule.onNodeWithText(locationWithDescription.positivePoint)
        composeTestRule.onNodeWithText(locationWithDescription.negativePoint)
    }

    @Test
    fun openCorrectDialogWithLocationWithoutDescription() {
        // try catch because maybe all locations will have a description
        try {
            val locationWithoutDescription = LocationPropertyRepository.getZones()
                .first { zone -> zone.locationProperties.any { location -> location.description == "" } }
                .locationProperties.first { location -> location.description == "" }
            val locationName = locationWithoutDescription.name
            val marker =
                MapUI.mapView.overlays.first { it is Marker && it.title == locationName } as Marker
            composeTestRule.activity.mapViewModel.selectedMarker = marker
            composeTestRule.activity.interactingWithProperty.value = true
            runBlocking { delay(500) }
            composeTestRule.onNodeWithText(locationName)
            composeTestRule.onNodeWithText("No Info about this building")
        } catch (_: NoSuchElementException) {}
    }

    @Test
    fun clickOnCloseButtonCloseTheDialog() {
        val marker = MapUI.mapView.overlays.first { it is Marker} as Marker
        composeTestRule.activity.mapViewModel.selectedMarker = marker
        composeTestRule.activity.interactingWithProperty.value = true
        runBlocking { delay(500) }
        composeTestRule.onNodeWithTag("building_description_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("close_building_description_dialog").performClick()
        runBlocking { delay(500) }
        composeTestRule.onNodeWithTag("building_description_dialog").assertIsNotDisplayed()
    }
}