package com.github.polypoly.app.base.game

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.game.rules_and_lobby.GameLobbyActivity
import com.github.polypoly.app.ui.map.MapActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameLobbyActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<GameLobbyActivity>()

    @Before
    fun startIntents() { Intents.init() }

    @After
    fun releaseIntents() { Intents.release() }

    // Composables used in tests
    private val goButton = composeTestRule.onNodeWithText("GO!")

    @Test
    fun goButtonLaunchesGameActivity() {
        goButton.performClick()
        Intents.intended(IntentMatchers.hasComponent(MapActivity::class.java.name))
    }

}