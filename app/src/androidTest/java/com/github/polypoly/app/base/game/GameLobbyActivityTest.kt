package com.github.polypoly.app.base.game

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.ui.menu.lobby.GameLobbyActivity
import com.github.polypoly.app.utils.global.Settings
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class GameLobbyActivityTest: PolyPolyTest(true, false) {

    val lobbyCode = "default-lobby"
    val lobbyKey = Settings.DB_GAME_LOBBIES_PATH + lobbyCode

    @get:Rule
    val composeTestRule = createAndroidComposeRule<GameLobbyActivity>()

    override fun _prepareTest() {
        GameRepository.gameCode = lobbyCode
    }

    @Before
    fun startIntents() { Intents.init() }

    @After
    fun releaseIntents() { Intents.release() }

    // Composables used in tests
    private val goButton = composeTestRule.onNodeWithTag("go_button")

    // TODO: use framework for dependency injection as it fails on cirrus
//    @Test
//    fun goButtonLaunchesGameActivityWhenGameCanStart() {
//        val syncFuture = composeTestRule.activity.gameLobbyWaitingModel.waitForSync()
//
//        // Setup game lobby ready for start
//        addDataToDB(TEST_GAME_LOBBY_AVAILABLE_1, lobbyKey)
//
//        syncFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
//
//        composeTestRule.waitForIdle()
//
//        goButton.assertTextEquals("GO!")
//
//        goButton.performClick()
//        Intents.intended(IntentMatchers.hasComponent(GameActivity::class.java.name))
//    }

    @Test
    fun goButtonIsDisabledWhenGameCannotStart() {
        // Setup game lobby not ready for start
        addDataToDB(TEST_GAME_LOBBY_AVAILABLE_2)

        composeTestRule.waitForIdle()

        goButton.assertIsNotEnabled()
    }
}