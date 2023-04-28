package com.github.polypoly.app.ui.menu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.R
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.isSignedIn
import com.github.polypoly.app.base.GameMusic
import com.github.polypoly.app.ui.theme.PolypolyTheme

/**
 * This activity is the view that a player will see when launching the app, the idea is that
 * this screen represents the "hub" from where all the main actions are made.
 *
 * These actions may be: creating a game, joining a game, logging in, settings, rules, leaderboards etc.
 */
class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Global initialization of the database
        setContent { WelcomeContent() }
    }

    @Preview(showBackground = true)
    @Composable
    fun WelcomePreview() {
        WelcomeContent()
    }

    /**
     * This function is called when the activity is resumed
     * If no more user are logged in, the activity must finish
     */
    override fun onResume() {
        super.onResume()
        if (!isSignedIn) {
            finish()
        }
    }

    // ===================================================== MAIN CONTENT
    @Composable
    fun WelcomeContent() {
        GameMusic.setSong(LocalContext.current, R.raw.mocksong)
        GameMusic.startSong()
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // The first element is the logo of the game
                    GameLogo()
                    Spacer(modifier = Modifier.weight(1f))
                    // Then the game buttons are in the center of the screen
                    GameButtons()
                    Spacer(modifier = Modifier.weight(1f))
                    MenuComposable.RowButtons()
                }
            }
        }
    }

    // ===================================================== WELCOME COMPONENTS
    /**
     * This composable is the main image of the game, the polypoly logo that'll be displayed
     * in the welcome screen (i.e. Welcome Activity)
     */
    @Composable
    fun GameLogo() {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.tmp_happysmile),
                contentDescription = "game_logo",
            )
        }
    }

    // TODO: add the real activity directions to the buttons
    /**
     * So far, the player has two main options, join an existing game or create a new one,
     * these buttons are then used for these purposes and have a fixed size.
     */
    @Composable
    fun GameButtons() {
        val mContext = LocalContext.current
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(2.dp)
            ) {
                // Join button
                GameButton(onClick = {
                    val joinGroupIntent = Intent(mContext, JoinGameLobbyActivity::class.java)
                    startActivity(joinGroupIntent)
                }, text = "Join Game!")
                Spacer(modifier = Modifier.height(20.dp))
                // Create button
                GameButton(onClick = {
                    // TODO: dummy button that sends to GameLobbyActivity, temporary
                    val gameLobbyIntent = Intent(mContext, GameLobbyActivity::class.java)
                    gameLobbyIntent.putExtra("lobby_code", "1234abc")
                    startActivity(gameLobbyIntent)
                }, text = "Create Game?")
            }
        }
    }

    // ============================================================= HELPERS

    /**
     * Simply a common button that'll be used for important purposes
     */
    @Composable
    fun GameButton(onClick: () -> Unit, text: String) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .width(200.dp)
                .height(70.dp),
        ) {
            Text(text = text)
        }
    }
}
