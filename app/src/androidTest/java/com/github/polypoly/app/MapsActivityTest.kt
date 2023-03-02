package com.github.polypoly.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapsActivityTest {

    @get:Rule
    var mActivityRule = ActivityScenarioRule(MapsActivity::class.java)

    @Test
    fun testMapDisplayed() {
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }
}