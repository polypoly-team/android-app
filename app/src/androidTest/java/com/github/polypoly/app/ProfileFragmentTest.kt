package com.github.polypoly.app

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ProfileFragmentTest {
    @Test
    fun displaysCorrectMessage() {
        Intents.init()
        onView(ViewMatchers.withId(R.id.mainName))
            .perform(ViewActions.replaceText("la personne cachée sous la bâche dans le hall qui éternue"))
        onView(ViewMatchers.withId(R.id.button))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.textView)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    "Hello la personne cachée sous la bâche dans le hall qui éternue!"
                )
            )
        )
    }
}