package com.github.polypoly.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun buttonClickFiresIntent() {
        Intents.init()
        onView(withId(R.id.button))
            .perform(click())
        intended(hasComponent(GreetingActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun buttonClickIntentContainsUsername() {
        Intents.init()
        onView(withId(R.id.mainName))
            .perform(replaceText("homme du hall"))
        onView(withId(R.id.button))
            .perform(click())
        intended(allOf(hasComponent(GreetingActivity::class.java.name), hasExtra("name", "homme du hall")))
        Intents.release()
    }
}