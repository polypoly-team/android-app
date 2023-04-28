package com.github.polypoly.app.ui.menu

import android.content.Intent
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
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.menu.rules_and_lobby.GameLobby
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.ui.game.MapActivity
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.util.toDp

val PLAYER_ICON_SIZE = IntSize(50, 60)

/**
 * A game lobby is the place the user sees before beginning a game. One is able to see the players
 * that join the game in real time.
 *
 * When there are enough players as specified in [GameRules], the admin (the one who created the game)
 * can start the game.
 */
class GameLobbyActivity : ComponentActivity() {
    private lateinit var gameLobby: MutableState<GameLobby>


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

    /**
     * Displays all the UI of the GameLobby
     */
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

    /**
     * Creates the grid of players that is updated in real time.
     * Depending on the screen size, a constant number of users per row will be displayed,
     * if the row hasn't enough players to fill, "blank" players are displayed
     */
    @Composable
    private fun PlayerGrid() {
        val sidePadding = 50
        val interPadding = 10

        val numberOfPlayers = gameLobby.value.usersRegistered.size
        var playersPerRow by remember { mutableStateOf(3)}
        var numberOfRows by remember { mutableStateOf(1) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .height(400.dp)
                .onGloballyPositioned { coordinates ->
                    val width = coordinates.size.width.toDp
                    while (
                        playersPerRow > 1 &&
                        width < (PLAYER_ICON_SIZE.width*playersPerRow + 2*sidePadding + (playersPerRow + 1)*interPadding)
                    ) {
                        playersPerRow--
                    }
                    numberOfRows = (numberOfPlayers + (numberOfPlayers%playersPerRow)) / playersPerRow
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(sidePadding.dp))
            Column(
                modifier = Modifier
                    .padding(interPadding.dp)
                    .fillMaxWidth()
                    .background(Color.DarkGray),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var playerIdx = 0
                repeat(numberOfRows) { itRow ->
                    Row {
                        repeat(playersPerRow) { itCol ->
                            if (playerIdx >= numberOfPlayers) {
                                PlayerIcon(user = null)
                            } else {
                                PlayerIcon(user = gameLobby.value.usersRegistered[playerIdx])
                            }
                            playerIdx++
                            if(itCol != playersPerRow - 1) {
                                Spacer(modifier = Modifier.width(interPadding.dp))
                            }
                        }
                    }
                    if(itRow != numberOfRows - 1) {
                        Spacer(modifier = Modifier.height(interPadding.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.width(sidePadding.dp))
        }
    }

    /**
     * As icon composed with the profile picture of the user and their name. The icon size is constant
     * so that it can be nicely displayed in the grid
     */
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

    /**
     * If there are enough players in the game, launches it, else it displays an error message.
     * Note that this button is only visible to the admin user
     *
     * TODO: so far a dummy button that redirects to MapActivity, handle real behavior as checking if there are enough players
     */
    @Composable
    private fun GoButton() {
        val mContext = LocalContext.current
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                val gameIntent = Intent(mContext, MapActivity::class.java)
                startActivity(gameIntent)
            }) {
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
        /*remoteDB.getSnapshot(DB_GAME_LOBBIES_PATH + lobbyCode).thenAccept { data ->
            run {
                gameLobby.value = data.getValue(GameLobby::class.java)!!
                data.ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        gameLobby.value = dataSnapshot.getValue(GameLobby::class.java)!!
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }*/ // TODO: react to DB in real time
    }
}