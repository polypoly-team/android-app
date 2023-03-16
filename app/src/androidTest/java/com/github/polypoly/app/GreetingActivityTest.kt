
package com.github.polypoly.app

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GreetingActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<GreetingActivity>()

    // Views that we test here
    private val greetText = composeTestRule.onNodeWithTag("greetingText")

    @Test
    fun greetTextDisplaysIntendedText() {
        // An intent with a name is sent to the activity
        val testIntent =
            Intent(ApplicationProvider.getApplicationContext(), GreetingActivity::class.java)
        testIntent.putExtra("name", "oli")
        ActivityScenario.launch<GreetingActivity>(testIntent)

        // Check that the display test is correct
        greetText.assert(hasText("Good morning oli"))
    }
}