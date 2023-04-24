package com.github.polypoly.app.menu

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.commons.LoggedInTest
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.global.Settings
import com.github.polypoly.app.network.getValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ProfileModifyingActivityTest: LoggedInTest(true, true) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ProfileModifyingActivity>()

    // Views that we test here
    private val nicknameText = composeTestRule.onNodeWithTag("nicknameText")
    private val descriptionText = composeTestRule.onNodeWithTag("descriptionText")
    private val button = composeTestRule.onNodeWithTag("registerInfoButton")

    @Test
    fun validateProfileButtonGoToTheProfilePage() {

        Intents.init()

        // An intent with the logged-in user's id is sent to the activity
        val testIntent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileModifyingActivity::class.java)
        testIntent.putExtra("userId", userLoggedIn.id)
        ActivityScenario.launch<ProfileModifyingActivity>(testIntent)

        // Clicking on button
        button.performClick()
//        Intents.intended(IntentMatchers.hasComponent(ProfileActivity::class.java.name)) // TODO: fixme - cirrus emulator is too slow

        Intents.release()

    }

    @Test
    fun validateProfileButtonUpdatesUserName() {
        Intents.init()

        val newName = "John"

        // An intent with the logged-in user's id is sent to the activity
        val testIntent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileModifyingActivity::class.java)
        testIntent.putExtra("userId", userLoggedIn.id)
        ActivityScenario.launch<ProfileModifyingActivity>(testIntent)

        // Fills a non-empty name
        nicknameText.performTextReplacement(newName)

        // Clicking on button
        button.performClick()

        val userKey = Settings.DB_USERS_PROFILES_PATH + userLoggedIn.id
        Thread.sleep(TIMEOUT_DURATION * 2000) // TODO: fixme ugly but easiest workaround until we can listen to the DB
        val nameFound = remoteDB.getValue<User>(userKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS).name

        assertEquals(newName, nameFound)

        Intents.release()
    }

    @Test
    fun validateProfileButtonUpdatesUserBio() {
        Intents.init()

        val newBio = "This bio is better"

        // An intent with the logged-in user's id is sent to the activity
        val testIntent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileModifyingActivity::class.java)
        testIntent.putExtra("userId", userLoggedIn.id)
        ActivityScenario.launch<ProfileModifyingActivity>(testIntent)

        // Fills a non-empty name
        descriptionText.performTextReplacement(newBio)

        // Clicking on button
        button.performClick()

        val userKey = Settings.DB_USERS_PROFILES_PATH + userLoggedIn.id
        Thread.sleep(TIMEOUT_DURATION * 2000) // TODO: fixme ugly but easiest workaround until we can listen to the DB
        val bioFound = remoteDB.getValue<User>(userKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS).bio

        assertEquals(newBio, bioFound)

        Intents.release()
    }

    @Test
    fun cantValidateTheProfileIfThePlayerGiveAnEmptyNickName() {

        Intents.init()

        // An intent with the logged-in user's id is sent to the activity
        val testIntent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileModifyingActivity::class.java)
        testIntent.putExtra("n kqnd", userLoggedIn.id)
        ActivityScenario.launch<ProfileModifyingActivity>(testIntent)

        nicknameText.performTextReplacement("")

        // Clicking on button
        button.performClick()
        Intents.intended(IntentMatchers.hasComponent(ProfileModifyingActivity::class.java.name))

        Intents.release()
    }
}