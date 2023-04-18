package com.github.polypoly.app.menu
//
//import android.content.Intent
//import androidx.compose.ui.test.*
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.test.core.app.ActivityScenario
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.intent.Intents
//import androidx.test.espresso.intent.matcher.IntentMatchers
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.github.polypoly.app.commons.PolyPolyTest
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class ProfileModifyingActivityTest: PolyPolyTest() {
//    @get:Rule
//    val composeTestRule = createAndroidComposeRule<ProfileModifyingActivity>()
//
//    // Views that we test here
//    private val nicknameText = composeTestRule.onNodeWithTag("nicknameText")
//    private val descriptionText = composeTestRule.onNodeWithTag("descriptionText")
//    private val button = composeTestRule.onNodeWithTag("registerInfoButton")
//    private val nickname = composeTestRule.onNodeWithTag("nickname")
//
//    @Test
//    fun nicknameTextDisplaysIntendedText() {
//        Intents.init()
//
//        // An intent with a name is sent to the activity
//        val testIntent =
//            Intent(ApplicationProvider.getApplicationContext(), ProfileModifyingActivity::class.java)
//        testIntent.putExtra("userId", 1)
//        ActivityScenario.launch<ProfileModifyingActivity>(testIntent)
//
//        // Check that the display test is correct
//        nicknameText.assert(hasText(FakeRemoteStorage.instance.user.name))
//        Intents.release()
//    }
//
//    @Test
//    fun descriptionTextDisplaysIntendedText() {
//        Intents.init()
//
//        // An intent with a name is sent to the activity
//        val testIntent =
//            Intent(ApplicationProvider.getApplicationContext(), ProfileModifyingActivity::class.java)
//        testIntent.putExtra("userId", 1)
//        ActivityScenario.launch<ProfileModifyingActivity>(testIntent)
//
//        // Check that the display test is correct
//        descriptionText.assert(hasText("J'ai besoin de beaucoup beaucoup beaucoup de sommeil"))
//        Intents.release()
//    }
//
//    @Test
//    fun validateProfileButtonGoToTheProfilePage() {
//
//        Intents.init()
//
//        // An intent with a name is sent to the activity
//        val testIntent =
//            Intent(ApplicationProvider.getApplicationContext(), ProfileModifyingActivity::class.java)
//        testIntent.putExtra("userId", 1)
//        ActivityScenario.launch<ProfileModifyingActivity>(testIntent)
//
//        // Clicking on button
//        button.performClick()
//        Intents.intended(IntentMatchers.hasComponent(ProfileActivity::class.java.name))
//
//        Intents.release()
//
//    }
//
//    @Test
//    fun validateProfileButtonGoToTheProfilePageAndModifyTheName() {
//
//        Intents.init()
//
//        // An intent with a name is sent to the activity
//        val testIntent =
//            Intent(ApplicationProvider.getApplicationContext(), ProfileModifyingActivity::class.java)
//        testIntent.putExtra("userId", 1)
//        ActivityScenario.launch<ProfileModifyingActivity>(testIntent)
//
//        // Fills a non-empty name
//        nicknameText.performTextReplacement("bigflo")
//
//        // Clicking on button
//        button.performClick()
//        nickname.assert(hasText("bigflo"))
//
//        Intents.release()
//    }
//
//    @Test
//    fun cantValidateTheProfileIfThePlayerGiveAnEmptyNickName() {
//
//        Intents.init()
//
//        // An intent with a name is sent to the activity
//        val testIntent =
//            Intent(ApplicationProvider.getApplicationContext(), ProfileModifyingActivity::class.java)
//        testIntent.putExtra("n kqnd", 1)
//        ActivityScenario.launch<ProfileModifyingActivity>(testIntent)
//
//        nicknameText.performTextReplacement("")
//
//        // Clicking on button
//        button.performClick()
//        Intents.intended(IntentMatchers.hasComponent(ProfileModifyingActivity::class.java.name))
//
//        Intents.release()
//    }
//}