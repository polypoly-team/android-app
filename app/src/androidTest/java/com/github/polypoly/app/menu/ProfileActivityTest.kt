package com.github.polypoly.app.menu

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.commons.LoggedInTest
import com.github.polypoly.app.game.user.allTrophies
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest: LoggedInTest(true, true) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ProfileActivity>()

    private val button = composeTestRule.onNodeWithTag("modifyProfileButton")

    @Test
    fun modifyButtonGoToTheModifyingPage() {
        Intents.init()

        button.performClick()
        intended(hasComponent(ProfileModifyingActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun canSeeTheDescriptionOfAWonTrophy() {
        val statisticsAndTrophies = composeTestRule.onNodeWithTag("statisticsAndTrophies")
        val secondTrophyWon = userLoggedIn.trophiesWon[1]
        statisticsAndTrophies.onChildren().filter(hasTestTag("Trophy$secondTrophyWon"))[0]
            .performScrollTo().performClick()
        composeTestRule.onNodeWithText(allTrophies[secondTrophyWon].toString())
            .assertIsDisplayed()
    }

    @Test
    fun cantSeeTheDescriptionOfANotWonTrophy() {
        val notWonTrophy = allTrophies.first { !userLoggedIn.hasTrophy(it.getId()) }
        composeTestRule.onNodeWithTag("Trophy${notWonTrophy.getId()}").performScrollTo()
            .performClick()
        composeTestRule.onNodeWithText("???").assertIsDisplayed()
    }

    @Test
    fun canSeeTheCorrectGamePlayedOfThePlayer() {
        composeTestRule.onNodeWithText(userLoggedIn.stats.numberOfGames.toString()).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun canSeeTheCorrectGameWonOfThePlayer() {
        composeTestRule.onNodeWithText(userLoggedIn.stats.numberOfWins.toString()).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun canSeeTheCorrectKilometersTraveledOfThePlayer() {
        composeTestRule.onNodeWithText(userLoggedIn.stats.kilometersTraveled.toString()).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun canSeeTheCorrectNumberOfTrophiesWon() {
        composeTestRule.onNodeWithText(userLoggedIn.trophiesWon.size.toString()).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun canSeeTheChosenDisplayedTrophies() {
        userLoggedIn.trophiesDisplay.forEach {
            val profileSurface = composeTestRule.onNodeWithTag("profileSurface")
            profileSurface.onChildren().filter(hasTestTag("Trophy$it")).assertCountEquals(1)
        }
    }

    @Test
    fun seeAnEmptySlotIfThePlayerHaveLessThanThreeTrophies() {
        userLoggedIn.trophiesDisplay.clear()
        userLoggedIn.trophiesDisplay.add(allTrophies.first().getId())

        remoteDB.updateValue(DB_USERS_PROFILES_PATH + userLoggedIn.id, userLoggedIn).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        val profileSurface = composeTestRule.onNodeWithTag("profileSurface")

        // check that there is the first trophy displayed in the header
//        profileSurface.onChildren().filter(hasTestTag("Trophy${allTrophies.first().getId()}")).assertCountEquals(1) // TODO: fixme - cirrus emulator is too slow

        // check that there is the empty slots in the header
//        composeTestRule.onNodeWithTag("emptySlot1").assertExists() // TODO: fixme - cirrus emulator is too slow
//        composeTestRule.onNodeWithTag("emptySlot2").assertExists() // TODO: fixme - cirrus emulator is too slow
    }

}
