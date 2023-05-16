package com.github.polypoly.app.ui.menu.lobby

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.R
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.models.menu.lobby.GameLobbyWaitingViewModel
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.ui.commons.CircularLoader
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements.BigButton
import com.github.polypoly.app.ui.theme.UIElements.smallIconSize
import com.github.polypoly.app.utils.global.GlobalInstances
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser

/**
 * A game lobby is the place the user sees before beginning a game. One is able to see the players
 * that join the game in real time.
 *
 * When there are enough players as specified in [GameParameters], the admin (the one who created the game)
 * can start the game.
 */
class GameLobbyActivity : ComponentActivity() {

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
        val dataLoading = gameLobbyWaitingModel.getIsLoading().observeAsState().value

        if (gameLobby != null && readyForStart != null) {
            val playersList = remember{mutableStateOf(gameLobby.usersRegistered)}
            PolypolyTheme {
                Column {
                    TopAppBar(
                        title = { Text(text = gameLobby.name) },
                        navigationIcon = {
                            IconButton(onClick = {
                                leaveLobby()
                            }) {
                                Icon(Icons.Filled.Logout, "leave_lobby_icon")
                            }
                        },
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.background,
                        elevation = 10.dp
                    )
                    Box {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {

                            if (dataLoading == true) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularLoader()
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column() {
                                        SettingsMenu(gameLobby.rules)
                                        Spacer(modifier = Modifier.padding(Padding.large))
                                        PlayersList(playersList, gameLobby.rules.maximumNumberOfPlayers, gameLobby.admin.id)
                                    }

                                    StartGameButton(
                                        gameLobby.usersRegistered,
                                        gameLobby.rules.minimumNumberOfPlayers,
                                        gameLobby.rules.maximumNumberOfPlayers,
                                        gameLobby.code,
                                        gameLobby.admin.id
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Clickable menu that unrolls to show the game settings
     * @param gameParameters the game parameters to display
     */
    @Composable
    fun SettingsMenu(gameParameters: GameParameters) {
        var expanded by remember { mutableStateOf(false) }
        val arrowRotation by animateDpAsState(targetValue = if (expanded) 90.dp else 0.dp)

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onPrimary) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon: ImageVector = Icons.Default.ArrowCircleRight
                    Image(
                        imageVector = icon,
                        contentDescription = "Settings Arrow",
                        modifier = Modifier
                            .size(30.dp)
                            .rotate(arrowRotation.value),
                        colorFilter = tint(MaterialTheme.colors.onPrimary)
                    )

                    Text(
                        text = "Game Settings",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column {
                        Divider(modifier = Modifier.padding(Padding.medium), thickness = Padding.small)

                        SettingsItem(title = getString(R.string.create_game_lobby_game_mode), value = gameParameters.gameMode.toString())
                        SettingsItem(title = getString(R.string.create_game_lobby_num_rounds), value = gameParameters.maxRound.toString())
                        SettingsItem(title = getString(R.string.create_game_lobby_round_duration), value = gameParameters.getRoundDurationValue().toString())
                        SettingsItem(title = getString(R.string.create_game_lobby_initial_balance), value = gameParameters.initialPlayerBalance.toString())
                    }
                }
            }
        }

    }

    /**
     * Row for settings menu that displays the title and the value of a given setting
     * @param title the title of the setting
     * @param value the value of the setting
     */
    @Composable
    fun SettingsItem(title: String, value: String) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }

    /**
     * list of players that have joined the lobby
     * @param players the list of players
     */
    @Composable
    fun PlayersList(players: MutableState<List<User>>, maximumNumberOfPlayers: Int, adminId : Long){
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Players:",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(vertical = Padding.small)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.People,
                        contentDescription = "Player Count Icon",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colors.primary
                        )

                    Text(
                        text = "${players.value.size}/$maximumNumberOfPlayers",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(Padding.small),
                        color = MaterialTheme.colors.primary,
                        )
                }
            }


            for (player in players.value) {

                val secondary = MaterialTheme.colors.secondary
                val backGround = MaterialTheme.colors.background
                var flashColor by remember { mutableStateOf(secondary) }
                var textSize by remember { mutableStateOf(0f) }
                val animatedColor by animateColorAsState(
                    targetValue = flashColor,
                    animationSpec = repeatable(
                        iterations = 3,
                        animation = tween(durationMillis = 350),
                        repeatMode = RepeatMode.Reverse)
                )

                LaunchedEffect(key1 = player) {
                    textSize = 1f
                    flashColor = backGround
                }
                AnimatedVisibility(visibleState = remember { MutableTransitionState(false).apply { targetState = true } }){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        animatedColor,
                                        animatedColor,
                                        Color.Transparent
                                    ),
                                    startX = 0f
                                )
                            )
                            .padding(vertical = Padding.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tmp_happysmile),
                            contentDescription = "${player.name} icon",
                            modifier = Modifier.size(smallIconSize)
                        )

                        Spacer(modifier = Modifier.width(Padding.medium))

                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.body1.copy(fontSize = MaterialTheme.typography.body1.fontSize)
                        )

                        Spacer(modifier = Modifier.width(Padding.medium))

                        if(player.id == adminId) {
                            Image(
                                imageVector = Icons.Filled.Stars,
                                contentDescription = "admin star",
                                modifier = Modifier.size(smallIconSize),
                                colorFilter = ColorFilter.tint(secondary)
                            )
                        }
                    }
                }
            }

            repeat(maximumNumberOfPlayers - players.value.size) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Padding.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = Icons.Default.PermIdentity,
                        contentDescription = "free_player_slot",
                        modifier = Modifier.size(smallIconSize)
                    )
                }
            }
        }
    }



    @Composable
    fun StartGameButton(players: List<User>, minRequiredPlayers: Int, maxPlayers: Int, lobbyCode: String, adminId: Long,) {
        val morePlayersNeeded = minRequiredPlayers - players.size
        val mContext = LocalContext.current

        val isAdmin = currentUser.id == adminId

        val buttonText = if (isAdmin) {
            if (players.size >= minRequiredPlayers) "Start Game!" else "Can't start Game :("
        } else {
            if (players.size >= minRequiredPlayers) "Admin can start!" else "Admin can't start..."
        }
        val onClickAction = if (isAdmin) {
            { launchGameActivity(mContext) }
        } else {
            {}
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(
                    text = "Group code is: ",
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = lobbyCode,
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )

            }


            Spacer(modifier = Modifier.height(Padding.small))

            BigButton(
                onClick = onClickAction,
                enabled = players.size >= minRequiredPlayers,
                text = buttonText
            )

            Spacer(modifier = Modifier.height(Padding.small))

            if (morePlayersNeeded > 0) {
                Row {
                    Text(
                        text = "At least ",
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = "$morePlayersNeeded more",
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = " players needed to start...",
                        style = MaterialTheme.typography.body1
                    )
                }
            } else {
                Row {
                    Text(
                        text = "Players: ",
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = "${players.size} / $maxPlayers",
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.primary
                        )
                    )
                }

            }
        }
    }



    private fun launchGameActivity(packageContext: Context) {
        val completedLobby = gameLobbyWaitingModel.getGameLobby().value
        if (completedLobby != null) {
            GameRepository.game = Game.launchFromPendingGame(completedLobby)
            val gameIntent = Intent(packageContext, GameActivity::class.java)
            startActivity(gameIntent)
            finish()
        }
    }

    /**
     * Leave the lobby and return to the main menu.
     * Makes sure that the admin is replaced by another user if the admin leaves.
     */
    private fun leaveLobby() {
        val currentLobby = gameLobbyWaitingModel.getGameLobby().value!!
        val currentLobbyKey = currentLobby.code
        GlobalInstances.remoteDB.getValue<GameLobby>(currentLobbyKey).thenAccept { gameLobby ->
            gameLobby.removeUser(currentUser.id)
            val newAdmin = if (currentUser.id == gameLobby.admin.id) gameLobby.usersRegistered.random() else currentUser
            val newGameLobby = GameLobby(
                newAdmin,
                gameLobby.rules,
                gameLobby.name,
                gameLobby.code,
                gameLobby.private
            )
            for ( user in gameLobby.usersRegistered.filter { it.id != newAdmin.id }) {
                newGameLobby.addUser(user)
            }
            GlobalInstances.remoteDB.updateValue(currentLobbyKey, gameLobby)
            GameRepository.gameCode = null
        }
        finish()
    }

}