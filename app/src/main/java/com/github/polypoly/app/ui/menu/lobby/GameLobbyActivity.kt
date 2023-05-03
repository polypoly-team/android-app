package com.github.polypoly.app.ui.menu.lobby

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.models.menu.lobby.GameLobbyWaitingViewModel
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.util.toDp
import androidx.compose.runtime.livedata.observeAsState

val PLAYER_ICON_SIZE = IntSize(50, 60)

/**
 * A game lobby is the place the user sees before beginning a game. One is able to see the players
 * that join the game in real time.
 *
 * When there are enough players as specified in [GameRules], the admin (the one who created the game)
 * can start the game.
 */
class GameLobbyActivity() : ComponentActivity() {

    private val gameLobbyWaitingModel: GameLobbyWaitingViewModel by viewModels { GameLobbyWaitingViewModel.Factory }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val gameLobby = gameLobbyWaitingModel.getGameLobby().observeAsState().value
        val readyForStart = gameLobbyWaitingModel.getReadyForStart().observeAsState().value

        if (gameLobby != null && readyForStart != null) {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Game Lobby name: ${gameLobby.name}")
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "QUIT")
                        }
                        PlayerGrid(gameLobby.usersRegistered)
                        GoButton(readyForStart)
                        Text("${gameLobby.usersRegistered.size} users (min ${gameLobby.rules.minimumNumberOfPlayers} to start)")
                    }
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
    private fun PlayerGrid(players: List<User>) {
        val sidePadding = 50
        val interPadding = 10

        val numberOfPlayers = players.size
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
                        width < (PLAYER_ICON_SIZE.width * playersPerRow + 2 * sidePadding + (playersPerRow + 1) * interPadding)
                    ) {
                        playersPerRow--
                    }
                    numberOfRows =
                        (numberOfPlayers + (numberOfPlayers % playersPerRow)) / playersPerRow
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
                                PlayerIcon(user = players[playerIdx])
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
     * If there are enough players in the game, launches it
     */
    @Composable
    private fun GoButton(enabled: Boolean) {
        val mContext = LocalContext.current
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                if (enabled) {
                    val gameIntent = Intent(mContext, GameActivity::class.java)
                    startActivity(gameIntent)
                }
            }) {
                if (enabled) {
                    Text(text = "GO!")
                } else {
                    Text(text = "Still waiting for more players before starting...")
                }
            }
        }
    }
}