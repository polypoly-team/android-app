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
import com.github.polypoly.app.game.user.allTrophies
import com.github.polypoly.app.network.FakeRemoteStorage

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ProfileActivity>()

    private val user = FakeRemoteStorage.instance.user

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
        statisticsAndTrophies.onChildren().filter(hasTestTag("Trophy${user.trophiesWon[1]}"))[0]
            .performScrollTo().performClick()
        composeTestRule.onNodeWithText(allTrophies[user.trophiesWon[1]].toString())
            .assertIsDisplayed()
    }

    @Test
    fun cantSeeTheDescriptionOfANotWonTrophy() {
        val notWonTrophy = allTrophies.first { !user.hasTrophy(it.getId()) }
        composeTestRule.onNodeWithTag("Trophy${notWonTrophy.getId()}").performScrollTo()
            .performClick()
        composeTestRule.onNodeWithText("???").assertIsDisplayed()
    }

    @Test
    fun canSeeTheCorrectGamePlayedOfThePlayer() {
        composeTestRule.onNodeWithText(user.stats.numberOfGames.toString()).assertIsDisplayed()
    }

    @Test
    fun canSeeTheCorrectGameWonOfThePlayer() {
        composeTestRule.onNodeWithText(user.stats.numberOfWins.toString()).assertIsDisplayed()
    }

    @Test
    fun canSeeTheCorrectKilometersTraveledOfThePlayer() {
        composeTestRule.onNodeWithText(user.stats.kilometersTraveled.toString()).assertIsDisplayed()
    }

    @Test
    fun canSeeTheCorrectNumberOfTrophiesWon() {
        composeTestRule.onNodeWithText(user.trophiesWon.size.toString()).assertIsDisplayed()
    }

    @Test
    fun canSeeTheChosenDisplayedTrophies() {
        user.trophiesDisplay.forEach {
            val profileSurface = composeTestRule.onNodeWithTag("profileSurface")
            profileSurface.onChildren().filter(hasTestTag("Trophy${it}")).assertCountEquals(1)
        }
    }

    @Test
    fun seeAnEmptySlotIfThePlayerHaveLessThanThreeTrophies() {
        user.trophiesDisplay.clear()
        user.trophiesDisplay.add(allTrophies.first().getId())
        val profileSurface = composeTestRule.onNodeWithTag("profileSurface")

        // check that there is the first trophy displayed in the header
        profileSurface.onChildren().filter(hasTestTag("Trophy${allTrophies.first().getId()}")).assertCountEquals(1)

        // check that there is the empty slots in the header
        composeTestRule.onNodeWithTag("emptySlot1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emptySlot2").assertIsDisplayed()
    }

}
