package com.github.polypoly.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.github.polypoly.app.menu.SettingsActivity
import org.junit.Rule
import org.junit.Test

/**
 * TODO: as GameMusic depends on a context, it is very hard to test it so I let it like that for now
 */
class SettingsActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<SettingsActivity>()

    // Composables used in tests
    private val musicSection = composeTestRule.onNodeWithText("Song settings")
    private val musicSlider = composeTestRule.onNodeWithTag("music_slider")
    private val muteIcon = composeTestRule.onNodeWithContentDescription("mute_icon")
    private val musicMuter = composeTestRule.onNodeWithTag("music_muter")

    // ========================================================= Display checks
    @Test
    fun musicSectionIsDisplayed() {
        musicSection.assertIsDisplayed()
    }

    @Test
    fun musicSliderIsDisplayed() {
        musicSlider.assertIsDisplayed()
    }

    @Test
    fun muteIconIsDisplayed() {
        muteIcon.assertIsDisplayed()
    }

    @Test
    fun musicMuterIsClickable() {
        musicMuter.assertHasClickAction()
    }
}