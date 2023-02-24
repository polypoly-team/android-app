package com.github.polypoly.app

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*

@RunWith(AndroidJUnit4::class)
class GreetingActivityTest {

    @Test
    fun displaysCorrectGreetingOnIntentReceive() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), GreetingActivity::class.java)
        intent.putExtra("name", "homme du hall")
        ActivityScenario.launch<GreetingActivity>(intent)
        onView(withId(R.id.greetingMessage)).check(matches(withText("Hello homme du hall!")))
    }
}