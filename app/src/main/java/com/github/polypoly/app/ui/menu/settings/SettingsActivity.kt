package com.github.polypoly.app.ui.menu.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.GameMusic
import com.github.polypoly.app.ui.menu.MenuActivity
import com.github.polypoly.app.ui.menu.SignInActivity
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.isSignedIn
import com.google.firebase.auth.FirebaseAuth

/**
 * This activity represents the "settings" menu of the game.
 * Here you'll be able to login, logoff, change the volume of the music (or mute it)
 *
 * TODO: edit this when adding more settings
 */
class SettingsActivity : MenuActivity("Settings") {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenuContent {
                SettingsContent()
            }
        }
    }

    // ===================================================== MAIN CONTENT
    /**
     * Displays all the contents of the settings. The different part are separated in
     * columns. If the screen isn't big enough to show all information, we make it scrollable
     */
    @Composable
    private fun SettingsContent() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column {
                    Text(text = "Song settings")
                    MusicSlider()
                    if(isSignedIn) {
                        SignOutButton()
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
    private fun MusicSlider() {
        var sliderValue by remember { mutableStateOf(GameMusic.getVolume()) }
        Row(
            modifier = Modifier.fillMaxWidth(),
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
    private fun MusicMuter() {
        var isMute by remember { mutableStateOf(GameMusic.getMuteState()) }
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        isMute = if (isMute) {
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
                imageVector =
                    if (isMute) {
                        Icons.Default.VolumeOff
                    } else {
                        Icons.Default.VolumeUp
                    }
                ,
                contentDescription = "mute_icon",
                modifier = Modifier.size(30.dp)
            )
        }

    }


    /**
     * Signs the user out of the app and returns to the welcome screen
     */
    @Composable
    private fun SignOutButton(){
        val mContext = LocalContext.current
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(Padding.large)
            ) {
                Button(
                    onClick = {
                        if (FirebaseAuth.getInstance().currentUser != null){
                            FirebaseAuth.getInstance().signOut()
                        }
                        currentUser = null
                        finish()
                        GameMusic.stopSong()
                        val backToSignIn = Intent(mContext, SignInActivity::class.java)
                        startActivity(backToSignIn)
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(50.dp),
                ) {
                    Text(text = "Sign out")
                }
            }
        }
    }

}