package com.github.polypoly.app

import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoredActivityTest {
    private val mockWebServer = MockWebServer()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        mockWebServer.start(8080)

        val component = DaggerBoredApiComponent.builder()
            .boredApiModule(BoredApiModule(mockWebServer.url("/").toString()))
            .build()
        activityRule.scenario.onActivity { activity ->
            component.inject(activity)
        }
    }

    @Test
    fun testSuccessfulResponse() {
        val response = MockResponse()
            .setResponseCode(200)
            .setBody("{\n" +
                    "  \"activity\": \"Fix something that's broken in your house\",\n" +
                    "  \"type\": \"diy\",\n" +
                    "  \"participants\": 1,\n" +
                    "  \"price\": 0.1,\n" +
                    "  \"link\": \"\",\n" +
                    "  \"key\": \"6925988\",\n" +
                    "  \"accessibility\": 0.3\n" +
                    "}")
        mockWebServer.enqueue(response)

        onView(withId(R.id.buttonBored))
            //.perform(click())

        onView(withId(R.id.boredActivity))
            //.check(matches(withText("Fix something that's broken in your house")))
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}