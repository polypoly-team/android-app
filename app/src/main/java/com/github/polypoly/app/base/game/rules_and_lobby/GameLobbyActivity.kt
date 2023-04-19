package com.github.polypoly.app.base.game.rules_and_lobby

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.polypoly.app.base.game.rules_and_lobby.kotlin.GameLobby
import com.github.polypoly.app.ui.theme.PolypolyTheme

class GameLobbyActivity : ComponentActivity() {
    private lateinit var gameLobby: GameLobby

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiveGameLobby()
        setContent { GameLobbyContent() }
    }

    @Preview(showBackground = true)
    @Composable
    private fun GameLobbyPreview() { GameLobbyContent() }

    // ===================================================== MAIN CONTENT
    @Composable
    private fun GameLobbyContent() {
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "QUIT")
                }
            }
        }

    }

    // ===================================================== GAME LOBBY COMPONENTS

    // ===================================================== GAME LOBBY HELPERS
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun receiveGameLobby() {
        //gameLobby = intent.getSerializableExtra("game_lobby", GameLobby::class.java)!!
    }
}