package com.github.polypoly.app.map

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.ui.map.VisitMapActivity
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class VisitMapActivityTest : PolyPolyTest(true, true) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<VisitMapActivity>()

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
        setLocationInMapViewModel(getRandomLocation()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        runBlocking { delay(500) }

        composeTestRule.onNodeWithTag("building_description_dialog").assertIsDisplayed()
    }

    @Test
    fun openCorrectDialogWithLocationWithDescription() {
        val locationWithDescription = LocationPropertyRepository.getZones()
            .first { zone -> zone.locationProperties.any { location -> location.description != "" } }
            .locationProperties.first {location -> location.description != ""}
        val locationName = locationWithDescription.name

        setLocationInMapViewModel(getRandomLocation()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
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

            setLocationInMapViewModel(locationWithoutDescription).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

            runBlocking { delay(500) }
            composeTestRule.onNodeWithText(locationName)
            composeTestRule.onNodeWithText("No Info about this building")
        } catch (_: NoSuchElementException) {}
    }

    @Test
    fun clickOnCloseButtonCloseTheDialog() {
        setLocationInMapViewModel(getRandomLocation()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        runBlocking { delay(500) }
        composeTestRule.onNodeWithTag("building_description_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("close_building_description_dialog").performClick()
        runBlocking { delay(500) }
        composeTestRule.onNodeWithTag("building_description_dialog").assertDoesNotExist()
    }

    private fun setLocationInMapViewModel(location: LocationProperty): CompletableFuture<Boolean> {
        return execInMainThread { composeTestRule.activity.mapViewModel.selectLocation(location) }
    }
}