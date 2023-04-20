package com.github.polypoly.app.base.game.rules_and_lobby

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.rules_and_lobby.kotlin.GameLobby
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.global.Settings.Companion.DB_GAME_LOBBIES_PATH
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.utils.Padding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class GameLobbyActivity : ComponentActivity() {
    private lateinit var gameLobby: MutableState<GameLobby>

    private val playerPerRow = 3

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
        gameLobby = remember { mutableStateOf(GameLobby()) }
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Game Lobby name: ${gameLobby.value.name}")
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "QUIT")
                    }
                    PlayerGrid()
                    GoButton()
                }
            }
        }

    }

    // ===================================================== GAME LOBBY COMPONENTS

    @Composable
    private fun PlayerGrid() {
        val numberOfPlayers = gameLobby.value.usersRegistered.size
        val numberOfRows: Int = numberOfPlayers / playerPerRow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .height(400.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(Padding.large)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for(i in 0 until numberOfRows) {
                    Row {
                        for (j in 0 until playerPerRow) {
                            val index = i * playerPerRow + j
                            if (index >= numberOfPlayers) {
                                PlayerIcon(user = null)
                            } else {
                                PlayerIcon(user = gameLobby.value.usersRegistered[index])
                            }
                            if(j != playerPerRow - 1) {
                                Spacer(modifier = Modifier.width(Padding.medium))
                            }
                        }
                    }
                    if(i != numberOfRows - 1) {
                        Spacer(modifier = Modifier.height(Padding.medium))
                    }
                }
            }
        }
    }

    @Composable
    private fun PlayerIcon(user: User?) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.Gray)
                .size(100.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (user != null) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
                        shape = CircleShape
                    )
                    .padding(20.dp)
                    .size(30.dp)
            )
            if(user != null) {
                Text(text = user.name)
            }
        }
    }

    @Composable
    private fun GoButton() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "GO!")
            }
            Text(text = "${gameLobby.value.usersRegistered.size} / ${gameLobby.value.rules.minimumNumberOfPlayers}")
        }
    }

    // ===================================================== GAME LOBBY HELPERS
    /**
     * This function gets the corresponding GameLobby from the DB and updates our variable each time
     * it is changed in the DB
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun receiveGameLobby() {
        val lobbyCode = intent.getStringExtra("lobby_code")
        remoteDB.getSnapshot(DB_GAME_LOBBIES_PATH + lobbyCode).thenAccept { data ->
            run {
                gameLobby.value = data.getValue(GameLobby::class.java)!!
                data.ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        gameLobby.value = dataSnapshot.getValue(GameLobby::class.java)!!
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
    }
}