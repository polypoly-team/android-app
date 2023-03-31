package com.github.polypoly.app.menu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.R
import com.github.polypoly.app.menu.kotlin.GameMusic
import com.github.polypoly.app.ui.theme.PolypolyTheme

/**
 * This activity represents the "settings" menu of the game.
 * Here you'll be able to login, logoff, change the volume of the music (or mute it)
 *
 * TODO: edit this when adding more settings
 */
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SettingsContent() }
    }
    @Preview(showBackground = true)
    @Composable
    fun SettingsPreview() { SettingsContent() }

    // ===================================================== MAIN CONTENT

    /**
     * Displays all the contents of the settings. The different part are separated in
     * columns. If the screen isn't big enough to show all information, we make it scrollable
     */
    @Composable
    fun SettingsContent() {
        PolypolyTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Column {
                        Text(text = "Song settings")
                        MusicSlider()
                    }


                }
            }
        }
    }

    // ===================================================== COMPOSABLES

    /**
     * This is a slider that controls the volume of the game music, it is initially set to the
     * default volume. It remembers the volume even after leaving the activity or being mute.
     */
    @Composable
    fun MusicSlider() {
        var sliderValue by remember { mutableStateOf(GameMusic.getVolume()) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = sliderValue,
                onValueChange = { newValue ->
                    sliderValue = newValue
                    GameMusic.setVolume(newValue) },
                steps = 10,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .testTag("music_slider")
            )
            Spacer(modifier = Modifier.width(10.dp))
            MusicMuter()
        }
    }

    /**
     * A small icon that mutes on/off the song. The icon changes in real time depending if
     * the song is mute or not.
     *
     * Due to aesthetic reasons, the "click" animation is disabled on this clickable icon
     */
    @Composable
    fun MusicMuter() {
        var isMute by remember { mutableStateOf(false) }
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    isMute = if(isMute) {
                        GameMusic.unMute()
                        false
                    } else {
                        GameMusic.mute()
                        true
                    }
                })
                .testTag("music_muter")
        ) {
            Image(
                painter = painterResource(
                    id = if (isMute) {
                        R.drawable.tmp_sadsmile
                    } else {
                        R.drawable.tmp_happysmile
                    }
                ),
                contentDescription = "mute_icon",
                modifier = Modifier.size(30.dp)
            )
        }

    }
}