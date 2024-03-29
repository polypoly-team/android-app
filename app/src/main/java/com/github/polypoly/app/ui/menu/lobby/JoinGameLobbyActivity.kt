package com.github.polypoly.app.ui.menu.lobby

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.R
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.database.getAllValues
import com.github.polypoly.app.database.getValue
import com.github.polypoly.app.database.keyExists
import com.github.polypoly.app.ui.menu.MenuActivity
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_CODE_LENGTH
import com.github.polypoly.app.ui.theme.UIElements
import com.github.polypoly.app.ui.theme.UIElements.GameLogo
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.concurrent.CompletableFuture

/**
 * Activity where the user can join a gameLobby
 */
class JoinGameLobbyActivity : MenuActivity("Join a game") {
    companion object {
        const val POLLING_INTERVAL = 5000L
    }

    /**
     * The attributes of the class
     */
    private var gameLobbyCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MenuContent{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(100.dp))

                        GameLogo(140.dp)

                        Spacer(modifier = Modifier.height(50.dp))
                        GameLobbyForm()
                        GameLobbyListButton()
                    }
                }
            }
        }
    }


    /**
     * Component where the user can write the gameLobby number. If the gameLobby number is valid,
     * the button lets the player join the gameLobby. Otherwise, it displays a warning message.
     */
    @Composable
    fun GameLobbyForm() {
        val mContext = LocalContext.current
        val warningState = remember { mutableStateOf("") }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameLobbyTextField(GAME_LOBBY_CODE_LENGTH, warningState)
                Spacer(modifier = Modifier.height(10.dp))
                RectangleButton(
                    onClick = {
                        gameLobbyCodeButtonOnClick(warningState, mContext)
                    }
                    , description = getString(R.string.join_game_lobby_button_text)
                    , testTag = "JoinGameLobbyButton")
                Text(
                    text = warningState.value,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.testTag("warningMessage")
                )
            }
        }
    }


    /**
     * This function returns the TextField where the user prompts their gameLobby code.
     */
    @Composable
    fun GameLobbyTextField(maxLength: Int, warningState : MutableState<String>) {
        val focusManager = LocalFocusManager.current
        val mContext = LocalContext.current
        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            value = text,
            modifier = Modifier
                .width(200.dp)
                .testTag("gameLobbyCodeField"),
            // When user clicks on enter button, the focus is removed and the button is clicked
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                gameLobbyCodeButtonOnClick(warningState, mContext)
            }),
            label = { Text("Enter a lobby code") },
            singleLine = true,
            // text can only be letters and numbers (avoids ghost characters as the Enter key)
            onValueChange = { newText : String ->
                text = if (newText.matches(Regex("[a-zA-Z\\d]*")) && newText.length <= maxLength) newText else text
                gameLobbyCode = text
            },
            colors = UIElements.outlineTextFieldColors()
        )

    }


    /**
     * This function returns the button that lets the user open the gameLobbies list.
     * The gameLobbies list is a dialog that shows the public gameLobbies that the user can join.
     * The gameLobbies list is refreshed every 5 seconds.
     */
    @Composable
    fun GameLobbyListButton() {
        var openList by remember { mutableStateOf(false) }
        var openCardIndex by remember { mutableStateOf(-1) }
        var gameLobbies by remember { mutableStateOf(listOf<GameLobby>()) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Don't have a lobby code?"
                , style = MaterialTheme.typography.body2
                , modifier = Modifier.testTag("noGameLobbyCodeText"))
            RectangleButton(
                onClick = {
                    openList = true
                },
                description = "Show GameLobbies",
                testTag = "showGameLobbiesButton"
            )
        }

        if (openList) {
            Dialog(
                onDismissRequest = {
                    openList = false
                    openCardIndex = -1
                },
            ) {

                LaunchedEffect(Unit) {
                    while (openList) {
                        // TODO: only to this once and then subscribe to events instead of polling
                        remoteDB.getAllValues<GameLobby>().thenAccept { lobbies ->
                            gameLobbies = lobbies.filter { lobby -> !lobby.private && !gameLobbyIsFull(lobby) }
                        }
                        Timber.tag("GameLobbyList")
                            .d("Refreshing gameLobbies list")
                        delay(POLLING_INTERVAL)
                    }
                }

                Surface(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.95f)
                        .testTag("gameLobbiesList"),
                ) {
                    LazyColumn(modifier = Modifier.padding(20.dp)) {
                        item {
                            Text(
                                text = getString(R.string.game_lobby_list_title),
                                style = MaterialTheme.typography.h4,
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        items(
                            items = gameLobbies,
                            itemContent = {
                                    item ->
                                val index = gameLobbies.indexOf(item)
                                GameLobbyCardComponent(
                                    gameLobby = item,
                                    isOpen = index == openCardIndex,
                                    onOpenChange = { open ->
                                        openCardIndex = if (open) index else -1
                                    },
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        )
                    }
                }
            }
        }
    }




    /**
     * This function returns the gameLobby card. It contains the header and the details of the gameLobby.
     * @param gameLobby the game that holds the name and the number of players
     */
    @Composable
    fun GameLobbyCardComponent(
        gameLobby: GameLobby,
        isOpen: Boolean,
        onOpenChange: (Boolean) -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("${gameLobby.name}/gameLobbyCard"),
            elevation = 10.dp,
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenChange(!isOpen) }
            ) {
                GameLobbyCardHeader(gameLobby)
                AnimatedVisibility(visible = isOpen) {
                    GameLobbyCardDetails(gameLobby)
                }
            }
        }
    }


    /**
     * This function returns the header of the gameLobby card. It contains the name of the gameLobby and the number of players.
     * @param gameLobby the game that holds the name and the number of players
     */
    @Composable
    private fun GameLobbyCardHeader(gameLobby: GameLobby) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("${gameLobby.name}/gameLobbyCardHeader"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = gameLobby.name,
                style = MaterialTheme.typography.h5
            )
            GameLobbyCardPlayerCount(gameLobby)
        }
    }

    /**
     * This function returns the number of players that are currently in the gameLobby.
     * @param gameLobby the game that holds the number of players
     */
    @Composable
    private fun GameLobbyCardPlayerCount(gameLobby: GameLobby) {
        Row {
            Image(
                imageVector = Icons.Default.People,
                contentDescription = "people icon",
                modifier = Modifier
                    .size(30.dp)
                    .testTag("${gameLobby.name}/peopleIcon")
                    .padding(top = 5.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
            )
            Text(
                text = "${gameLobby.usersRegistered.size}/${gameLobby.rules.maximumNumberOfPlayers}",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }

    /**
     * This function returns the hidden content of the gameLobby card, that un-rolls when the user clicks on the card.
     * @param gameLobby the game that the card will contain the details of
     */
    @Composable
    private fun GameLobbyCardDetails(gameLobby: GameLobby) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .testTag("${gameLobby.name}/gameLobbyCardDetails")
        ) {
            Divider(
                modifier = Modifier.padding(bottom = 5.dp),
                thickness = 1.dp,
                color = androidx.compose.ui.graphics.Color.Black
            )
            Text(
                text = "Players:",
                style = MaterialTheme.typography.h6,
                fontSize = 16.sp,
                modifier = Modifier.testTag("${gameLobby.name}/players_title")
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameLobbyCardPlayerList(gameLobby)

            Divider(
                modifier = Modifier.padding(vertical = 5.dp),
                thickness = 1.dp,
                color = androidx.compose.ui.graphics.Color.Black
            )

            GameLobbyCardRoundDuration(gameLobby)

            Spacer(modifier = Modifier.height(5.dp))

            GameLobbyCardGameMode(gameLobby)

            GameLobbyCardJoinButton(gameLobby.code, gameLobby)
        }
    }

    /**
     * Creates a button to join the gameLobby
     * @param code the code of the gameLobby to join
     */
    @Composable
    private fun GameLobbyCardJoinButton(code: String, gameLobby: GameLobby) {
        val mContext = LocalContext.current
        val warningState = remember { mutableStateOf("") }
        gameLobbyCode = code

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RectangleButton(
                onClick = { gameLobbyCodeButtonOnClick(warningState,mContext)},
                description = getString(R.string.join_game_lobby_button_text),
                testTag = "${gameLobby.name}/joinGameLobbyButton"
            )
            if(warningState.value != "") {
                Text(
                    text = warningState.value,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .testTag("warningMessage")
                        .padding(bottom = 5.dp)
                )
                Text(text = "Updating in a few seconds")
            }
        }
    }

    /**
     * Creates a text that displays the list of players in the gameLobby.
     * @param gameLobby the gameLobby to display the players of
     */
    @Composable
    fun GameLobbyCardPlayerList(gameLobby: GameLobby) {
        for (player in gameLobby.usersRegistered) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.tmp_happysmile),
                    contentDescription = "${gameLobby.name}/${player.name} icon",
                    modifier = Modifier
                        .size(UIElements.smallIconSize)
                        .testTag("${gameLobby.name}/playerIcon")
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    modifier = Modifier.testTag("${gameLobby.name}/player_name"),
                    text = player.name,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }

    /**
     * Creates a text that displays the round duration of the game.
     * @param gameLobby the gameLobby to display the round duration of
     */
    @Composable
    fun GameLobbyCardRoundDuration(gameLobby: GameLobby) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Round duration: ",
                style = MaterialTheme.typography.h6,
                fontSize = 16.sp
            )
            Text(
                text = "${gameLobby.rules.getRoundDurationValue()}",
                style = MaterialTheme.typography.body1
            )
        }
    }

    /**
     * Creates a text that displays the game mode of the game.
     * @param gameLobby the gameLobby to display the game mode of
     */
    @Composable
    fun GameLobbyCardGameMode(gameLobby: GameLobby) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Game mode: ",
                style = MaterialTheme.typography.h6,
                fontSize = 16.sp
            )
            Text(
                text = "${gameLobby.rules.gameMode}",
                style = MaterialTheme.typography.body1
            )
        }
    }

    /**
     * Creates a rectangle button with a text and a linked action.
     * @param onClick (Function): The action to be performed when the button is clicked
     * @param description (String): The text to be displayed
     * @param testTag (String): The test tag to be used for testing
     */
    @Composable
    fun RectangleButton(onClick: () -> Unit, description: String = "", testTag: String = "Undefined") {
        Button(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .testTag(testTag)
                .semantics { contentDescription = description },
            onClick = onClick
        ) {
            Text(text = description)
        }
    }

    /**
     * This function changes the warning state according to the game lobby code input by the user
     * If the gameLobby code is empty, or the code is not in the DB, or the gameLobby is full,
     * it displays a warning message.
     * Otherwise, it calls the function to join the gameLobby.
     * @param warningState (MutableState<String>): The state of the warning message
     * @param mContext (Context): The context of the activity
     */
    @SuppressLint("NewApi")
    private fun gameLobbyCodeButtonOnClick(warningState: MutableState<String>, mContext: Context) {
        if (gameLobbyCode.isEmpty()) {
            warningState.value = getString(R.string.game_lobby_code_is_empty)
        } else {
            val lobbyKey = gameLobbyCode
            remoteDB.keyExists<GameLobby>(lobbyKey).thenCompose { keyExists ->
                if (!keyExists) {
                    warningState.value = getString(R.string.game_lobby_does_not_exist)
                    CompletableFuture.failedFuture(IllegalAccessException())
                } else {
                    remoteDB.getValue<GameLobby>(lobbyKey)
                }
            }.thenAccept { gameLobby ->
                if(gameLobbyIsFull(gameLobby)){
                    warningState.value = getString(R.string.game_lobby_is_full)
                } else {
                    warningState.value = ""
                    joinGameLobbyRoom(mContext)
                }
            }
        }
    }

    /**
     * This function launches the gameLobby room activity and passes the gameLobby code to it.
     * @param mContext The context of the activity
     */
    private fun joinGameLobbyRoom(mContext: Context) {
        remoteDB.getValue<GameLobby>(gameLobbyCode).thenAccept { gameLobby ->
            gameLobby.addUser(currentUser!!)

            //launch the gameLobby room activity
            remoteDB.updateValue(gameLobby).thenAccept {
                val gameLobbyIntent = Intent(mContext, GameLobbyActivity::class.java)
                GameRepository.gameCode = gameLobbyCode

                startActivity(gameLobbyIntent)
                finish()
            }
        }
    }

    /**
     * This function checks if the gameLobby is full.
     * @param gameLobby (GameLobby): The gameLobby to check
     * @return (Boolean): True if the gameLobby is full, false otherwise
     */
    private fun gameLobbyIsFull(gameLobby: GameLobby): Boolean {
        return gameLobby.usersRegistered.size >= gameLobby.rules.maximumNumberOfPlayers
    }

}