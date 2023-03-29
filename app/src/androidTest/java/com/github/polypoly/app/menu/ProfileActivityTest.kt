package com.github.polypoly.app.menu

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ProfileActivity>()

    private val button = composeTestRule.onNodeWithTag("modifyProfileButton")

    @Test
    fun modifyButtonGoToTheModifyingPage() {
        Intents.init()

        // Clicking on button
        button.performClick()
        intended(hasComponent(ProfileModifyingActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun canScrollOnTheStatisticsAndTrophiesPart() {

    }

    @Test
    fun canSelectATrophy() {

    }

    @Test
    fun canSeeTheDescriptionOfAWonTrophy() {

    }

    @Test
    fun cantSeeTheDescriptionOfANotWonTrophy() {

    }

    @Test
    fun canSeeTheCorrectGamePlayedOfThePlayer() {

    }

    @Test
    fun canSeeTheCorrectGameWonOfThePlayer() {

    }

    @Test
    fun canSeeTheCorrectKilometersTraveledOfThePlayer() {

    }

    @Test
    fun canSeeTheCorrectNumberOfTrophiesWon() {

    }

    @Test
    fun canSeeTheChosenDisplayedTrophies() {

    }

    @Test
    fun seeAnEmptySlotIfThePlayerHaveLessThanThreeTrophies() {

    }

}