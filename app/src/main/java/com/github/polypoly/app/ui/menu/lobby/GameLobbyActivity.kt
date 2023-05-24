package com.github.polypoly.app.ui.menu.lobby

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.github.polypoly.app.network.addOnChangeListener
import com.github.polypoly.app.network.removeValue
import com.github.polypoly.app.ui.commons.CircularLoader
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements.BigButton
import com.github.polypoly.app.ui.theme.UIElements.smallIconSize
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB

/**
 * A game lobby is the place the user sees before beginning a game. One is able to see the players
 * that join the game in real time.
 *
 * When there are enough players as specified in [GameParameters], the admin (the one who created the game)
 * can start the game.
 */
class GameLobbyActivity : ComponentActivity() {

    val gameLobbyWaitingModel: GameLobbyWaitingViewModel by viewModels { GameLobbyWaitingViewModel.Factory }
    val user = currentUser!!

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameLobbyContent() }
    }

    @Preview(showBackground = true)
    @Composable
    private fun GameLobbyPreview() {
        GameLobbyContent()
    }

    /**
     * Displays all the UI of the GameLobby
     */
    @Composable
    private fun GameLobbyContent() {
        val gameLobby = gameLobbyWaitingModel.getGameLobby().observeAsState().value
        val readyForStart = gameLobbyWaitingModel.getReadyForStart().observeAsState().value
        val dataLoading = gameLobbyWaitingModel.getIsLoading().observeAsState().value
        val context = LocalContext.current

        if (gameLobby != null && readyForStart != null){

            //TODO: implement as advised in https://github.com/polypoly-team/android-app/pull/154#discussion_r1198327499
            remoteDB.addOnChangeListener<GameLobby>(gameLobby.code, "started_game_listener") {
                if (it.started && it.admin.id != user.id) {
                    navigateToGame(context, gameLobby)
                }
            }
            PolypolyTheme {
                Column {
                    GameLobbyAppBar(gameLobby)

                    Box {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("game_lobby_background"),
                            color = MaterialTheme.colors.background
                        ) {
                            when (dataLoading) {
                                true -> LoadingContent()
                                false -> GameLobbyBody(gameLobby)
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * wraps the body in top app bar display and sets the leave lobby button
     * @param gameLobby the game lobby to display
     */
    @Composable
    private fun GameLobbyAppBar(gameLobby: GameLobby) {
        val gameLobbyName = gameLobby.name
        TopAppBar(
            title = { Text(text = gameLobbyName) },
            modifier = Modifier.testTag("game_lobby_app_bar"),
            navigationIcon = {
                IconButton(
                    onClick = {
                        leaveLobby(gameLobby)
                    },
                    modifier = Modifier.rotate(180f)
                ) {
                    Icon(Icons.Filled.Logout,"leave_lobby_icon", modifier = Modifier.testTag("game_lobby_leave_button"))
                }
            },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.background,
            elevation = 10.dp,
        )
    }

    @Composable
    private fun LoadingContent() {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularLoader()
        }
    }

    /**
     * The game lobby menu UI structure
     * @param gameLobby the game lobby to display
     */
    @Composable
    private fun GameLobbyBody(gameLobby: GameLobby) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("game_lobby_body"),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                SettingsMenu(gameLobby.rules)
                Spacer(modifier = Modifier.padding(Padding.large))
                PlayersList(gameLobby.usersRegistered, gameLobby.rules.maximumNumberOfPlayers, gameLobby.admin.id)
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

    /**
     * Clickable menu that unrolls to show the game settings
     * @param gameParameters the game parameters to display
     */
    @Composable
    private fun SettingsMenu(gameParameters: GameParameters) {
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
                    .testTag("game_lobby_settings_menu")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("game_lobby_settings_menu_row"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon: ImageVector = Icons.Default.ArrowCircleRight
                    Image(
                        imageVector = icon,
                        contentDescription = "Settings Arrow",
                        modifier = Modifier
                            .size(30.dp)
                            .rotate(arrowRotation.value)
                            .testTag("game_lobby_settings_menu_arrow"),
                        colorFilter = tint(MaterialTheme.colors.onPrimary)
                    )

                    Text(
                        text = getString(R.string.game_lobby_settings_title),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("game_lobby_settings_menu_title"),
                        textAlign = TextAlign.Center
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.testTag("game_lobby_settings_menu_expanded")) {
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
    private fun SettingsItem(title: String, value: String) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .testTag("${title}_game_lobby_settings_menu_item_title")
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .testTag("${title}_game_lobby_settings_menu_item_value"),
                textAlign = TextAlign.End
            )
        }
    }

    /**
     * Displays the list of players in the game and its title
     * @param players the list of players in the game
     * @param maximumNumberOfPlayers the maximum number of players allowed in the game
     * @param adminId the id of the admin of the game
     */
    @Composable
    private fun PlayersList(players: List<User>, maximumNumberOfPlayers: Int, adminId: String) {
        Column ( modifier = Modifier.testTag("game_lobby_players_list")) {
            PlayerHeader(players, maximumNumberOfPlayers)
            PlayerRows(players, adminId)
            EmptyPlayerSlots(players, maximumNumberOfPlayers)
        }
    }

    /**
     * Displays the Players list title and the number of players in the game, with the maximum number of players
     * @param players the list of players in the game
     * @param maximumNumberOfPlayers the maximum number of players allowed in the game
     */
    @Composable
    private fun PlayerHeader(players: List<User>, maximumNumberOfPlayers: Int) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("game_lobby_players_header"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = getString(R.string.game_lobby_players_title),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .padding(vertical = Padding.small)
                    .testTag("game_lobby_players_header_title")
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.People,
                    contentDescription = "Player Count Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("game_lobby_players_header_icon"),
                    tint = MaterialTheme.colors.primary
                )

                Text(
                    text = getString(R.string.game_lobby_players_count, players.size.toString(), maximumNumberOfPlayers.toString()),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(Padding.small)
                        .testTag("game_lobby_players_header_count"),
                    color = MaterialTheme.colors.primary,
                )
            }
        }
    }

    /**
     * Computes players rows
     * @param players the players to display
     * @param adminId the id of the admin of the game
     */
    @Composable
    private fun PlayerRows(players: List<User>, adminId: String) {
        for (player in players) {
            PlayerRow(player, adminId)
        }
    }

    /**
     * Computes a player's row and its animations
     * @param player the player to display
     * @param adminId the id of the admin of the game
     */
    @Composable
    private fun PlayerRow(player: User, adminId: String) {
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
        AnimatedVisibility(
            visibleState = remember { MutableTransitionState(false).apply { targetState = true } },
            enter = fadeIn() + expandVertically(
                expandFrom = Alignment.Top
            ) + expandHorizontally(
                expandFrom = Alignment.Start
            )
        ){
            SinglePlayerRow(player, adminId, animatedColor)
        }
    }

    /**
     * Row for a single player in the game lobby
     * if the player is admin, he will have a star icon next to his name
     * @param player the player to display
     * @param adminId the id of the admin of the game lobby
     * @param animatedColor the color to flash when a player joins or leaves the game lobby
     */
    @Composable
    private fun SinglePlayerRow(player: User, adminId: String, animatedColor: Color) {

        val secondary = MaterialTheme.colors.secondary

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
                .padding(vertical = Padding.small)
                .testTag("${player.name}_game_lobby_player_row"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.tmp_happysmile),
                contentDescription = "${player.name} icon",
                modifier = Modifier
                    .size(smallIconSize)
                    .testTag("${player.name}_game_lobby_player_row_icon")
            )

            Spacer(modifier = Modifier.width(Padding.medium))

            Text(
                text = player.name,
                style = MaterialTheme.typography.body1.copy(fontSize = MaterialTheme.typography.body1.fontSize),
                modifier = Modifier
                    .testTag("${player.name}_game_lobby_player_row_name")
            )

            Spacer(modifier = Modifier.width(Padding.medium))

            if(player.id == adminId) {
                Image(
                    imageVector = Icons.Filled.Stars,
                    contentDescription = "admin star",
                    modifier = Modifier
                        .size(smallIconSize)
                        .testTag("${player.name}_game_lobby_player_row_admin"),
                    colorFilter = tint(secondary),
                )
            }
        }
    }

    /**
     * Computes and displays empty player slots
     * @param players the list of players in the game
     * @param maximumNumberOfPlayers the maximum number of players in the game
     */
    @Composable
    private fun EmptyPlayerSlots(players: List<User>, maximumNumberOfPlayers: Int) {
        repeat(maximumNumberOfPlayers - players.size) {
            AnimatedVisibility(
                visibleState = remember { MutableTransitionState(false).apply { targetState = true } },
                enter = fadeIn() + expandVertically(
                    expandFrom = Alignment.Bottom
                ) + expandHorizontally(
                    expandFrom = Alignment.Start
                )
            )
            {
                EmptyPlayerSlot()
            }
        }
    }

    /**
     * Displays an empty player slot
     */
    @Composable
    private fun EmptyPlayerSlot() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Padding.small)
                .testTag("game_lobby_empty_player_slot"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Default.PermIdentity,
                contentDescription = "free_player_slot",
                modifier = Modifier
                    .size(smallIconSize)
                    .testTag("game_lobby_empty_player_slot_icon")
            )
        }
    }

    /**
     * Displays the button to start the game and the text around it
     * @param players the list of players in the lobby
     * @param minRequiredPlayers the minimum number of players required to start the game
     * @param maxPlayers the maximum number of players allowed in the game
     * @param lobbyCode the code of the lobby
     * @param adminId the id of the admin of the lobby
     */
    @Composable
    private fun StartGameButton(players: List<User>, minRequiredPlayers: Int, maxPlayers: Int, lobbyCode: String, adminId: String,) {
        val morePlayersNeeded = minRequiredPlayers - players.size
        val mContext = LocalContext.current

        val isAdmin = user.id == adminId

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("game_lobby_start_game_button_content"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LobbyCodeDisplay(lobbyCode)

            Spacer(modifier = Modifier.height(Padding.small))

            val buttonText = getButtonText(isAdmin, players.size, minRequiredPlayers)
            val onClickAction = getOnClickAction(isAdmin, mContext)

            BigButton(
                onClick = onClickAction,
                enabled = players.size >= minRequiredPlayers,
                text = buttonText,
                testTag = ("game_lobby_start_game_button_button")
            )

            Spacer(modifier = Modifier.height(Padding.small))

            PlayerStatusDisplay(morePlayersNeeded, players, maxPlayers)
        }
    }

    /**
     * Computes the text to display above the button
     * Displays the group code
     * @param lobbyCode the lobby code
     */
    @Composable
    private fun LobbyCodeDisplay(lobbyCode: String) {
        Row {
            Text(
                text = getString(R.string.game_lobby_group_code_title),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .testTag("game_lobby_start_game_button_lobby_code_title")
            )
            Text(
                text = lobbyCode,
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .testTag("game_lobby_start_game_button_lobby_code")
            )
        }
    }

    /**
     * Computes the text to display in the button
     * @param isAdmin true if the user is the admin of the lobby
     * @param playerCount the number of players in the lobby
     * @param minRequiredPlayers the minimum number of players required to start the game
     */
    private fun getButtonText(isAdmin: Boolean, playerCount: Int, minRequiredPlayers: Int): String {
        return if (isAdmin) {
            if (playerCount >= minRequiredPlayers) getString(R.string.game_lobby_can_start_game)
            else getString(R.string.game_lobby_cannot_start_game)
        } else {
            if (playerCount >= minRequiredPlayers) getString(R.string.game_lobby_admin_can_start_game)
            else getString(R.string.game_lobby_admin_cannot_start_game)
        }
    }

    /**
     * Computes the action to perform when the button is clicked
     * If the user is not the admin, the button should do nothing
     * If the user is the admin, the button should launch the game activity
     * @param isAdmin true if the user is the admin of the lobby
     * @param mContext the context of the activity
     */
    private fun getOnClickAction(isAdmin: Boolean, mContext: Context): () -> Unit {
        return  { if (true) launchGameActivity(mContext) }
    }

    /**
     * Computes the message to display under the button
     * @param morePlayersNeeded the number of players needed to start the game
     * @param players the list of players in the lobby
     * @param maxPlayers the maximum number of players allowed in the lobby
     */
    @Composable
    private fun PlayerStatusDisplay(morePlayersNeeded: Int, players: List<User>, maxPlayers: Int) {
        if (morePlayersNeeded > 0) {
            MorePlayersNeededDisplay(morePlayersNeeded)
        } else {
            CurrentPlayerCountDisplay(players, maxPlayers)
        }
    }

    /**
     * Computes the message to display if there are not enough players to start the game
     */
    @Composable
    private fun MorePlayersNeededDisplay(morePlayersNeeded: Int) {
        Row {
            Text(
                text = getString(R.string.game_lobby_more_players_prefix),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .testTag("game_lobby_start_game_button_more_players_needed_prefix")
            )
            Text(
                text = getString(R.string.game_lobby_more_players_count, morePlayersNeeded.toString()),
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .testTag("game_lobby_start_game_button_more_players_needed_count")
            )
            Text(
                text = getString(R.string.game_lobby_more_players_suffix),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .testTag("game_lobby_start_game_button_more_players_needed_suffix")
            )
        }
    }

    /**
     * Display the current player count in relation to the maximum player count.
     * @param players the list of players in the game lobby.
     * @param maxPlayers the maximum number of players allowed in the game lobby.
     */
    @Composable
    private fun CurrentPlayerCountDisplay(players: List<User>, maxPlayers: Int) {
        Row {
            Text(
                text = getString(R.string.game_lobby_players_title),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .testTag("game_lobby_start_game_button_current_player_count_title")
            )
            Text(
                text =  getString(R.string.game_lobby_players_count, players.size.toString(), maxPlayers.toString()),
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.primary
                ),
                modifier = Modifier
                    .testTag("game_lobby_start_game_button_current_player_count")
            )
        }
    }

    /**
     * Launch the game activity and finish this activity. Only available to the admin.
     * will set the game lobby as started in the database.
     */
    private fun launchGameActivity(packageContext: Context) {
        val completedLobby = gameLobbyWaitingModel.getGameLobby().value
        if (completedLobby != null) {
            //TODO adapt start() in GameLobby as adviser in https://github.com/polypoly-team/android-app/pull/154#discussion_r1198335312
            completedLobby.start()
            remoteDB.updateValue(completedLobby)

            navigateToGame(packageContext, completedLobby)
        }
    }

    /**
     * Leave the lobby and return to the main menu.
     * Makes sure that the admin is replaced by another user if the admin leaves.
     */
    private fun leaveLobby(gameLobby: GameLobby) {
        val newUsersRegistered = gameLobby.usersRegistered.filter {it.id != user.id}
        if (newUsersRegistered.isEmpty()){
            remoteDB.removeValue<GameLobby>(gameLobby.code)
        } else {
            val newAdmin =
                if (user.id == gameLobby.admin.id && !gameLobby.started) newUsersRegistered.random() else gameLobby.admin
            val newGameLobby = gameLobby.copy(admin = newAdmin)
            for (user in newUsersRegistered.filter { it.id != newAdmin.id }) {
                newGameLobby.addUser(user)
            }
            remoteDB.updateValue(newGameLobby)
        }

        GameRepository.gameCode = if (gameLobby.started) gameLobby.code else null
        finish()
    }

    /**
     * Navigate to the game activity and finish this activity.
     */
    private fun navigateToGame(packageContext: Context, gameLobby: GameLobby){
        val gameIntent = Intent(packageContext, GameActivity::class.java)
        startActivity(gameIntent)
        leaveLobby(gameLobby)
    }

}