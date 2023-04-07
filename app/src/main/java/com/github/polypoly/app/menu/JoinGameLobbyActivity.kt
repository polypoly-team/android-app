package com.github.polypoly.app.menu

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.github.polypoly.app.game.GameLobby
import com.github.polypoly.app.game.Skin
import com.github.polypoly.app.game.Stats
import com.github.polypoly.app.game.User
import com.github.polypoly.app.network.FakeRemoteStorage
import com.github.polypoly.app.ui.theme.PolypolyTheme
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Activity where the user can join a gameLobby
 */
class JoinGameLobbyActivity : ComponentActivity() {

    /**
     * The attributes of the class
     */
    private var gameLobbyCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(100.dp))
                        Image(
                            painter = painterResource(id = R.drawable.super_cool_logo),
                            contentDescription = "polypoly logo",
                            modifier = Modifier
                                .testTag("logo"),
                            )
                        Spacer(modifier = Modifier.height(50.dp))
                        GameLobbyForm()
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
                GameLobbyTextField(15, warningState) // TODO: create a constant for the max length -> create a class for the constants
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            GameLobbyListButton()
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
            modifier = Modifier
                .width(200.dp)
                .testTag("gameLobbyCodeField"),
            // When user clicks on enter button, the focus is removed and the button is clicked
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                gameLobbyCodeButtonOnClick(warningState, mContext)
            }),
            value = text,
            label = { Text("Enter a lobby code") },
            singleLine = true,
            // text can only be letters and numbers (avoids ghost characters as the Enter key)
            onValueChange = { newText ->
                text = if (newText.matches(Regex("[a-zA-Z\\d]*")) && newText.length <= maxLength) newText else text
                gameLobbyCode = text
            }

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

        var gameLobbies by remember { mutableStateOf(getPublicGameLobbiesFromDB()) }

        val refreshInterval = 5000L

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
                        delay(refreshInterval)
                        gameLobbies = getPublicGameLobbiesFromDB()
                        Timber.tag("GameLobbyList")
                            .d("Refreshing gameLobbies list")
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
                .testTag("gameLobbyCard"),
            elevation = 10.dp,
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenChange(!isOpen) }
            ) {
                GameLobbyCardHeader(gameLobby)
                if (isOpen) {
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
                .testTag("gameLobbyCardHeader"),
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
        Row() {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "people icon",
                modifier = Modifier
                    .size(30.dp)
                    .testTag("peopleIcon")
                    .padding(top = 5.dp)
            )
            Text(
                text = "${gameLobby.usersRegistered.size}/${gameLobby.maximumNumberOfPlayers}",
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
                .testTag("gameLobbyCardDetails")
        ) {
            Divider(
                modifier = Modifier.padding(bottom = 5.dp),
                thickness = 1.dp,
                color = androidx.compose.ui.graphics.Color.Black
            )
            Text(
                text = "Players:",
                style = MaterialTheme.typography.h6,
                fontSize = 16.sp
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

            GameLobbyCardJoinButton(gameLobby.code)
        }
    }

    /**
     * Creates a button to join the gameLobby
     * @param code the code of the gameLobby to join
     */
    @Composable
    private fun GameLobbyCardJoinButton(code: String) {
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
                testTag = "joinGameLobbyButton"
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
            Row() {
                Image(
                    painter = painterResource(id = R.drawable.tmp_happysmile),
                    contentDescription = "${player.name} icon",
                    modifier = Modifier
                        .size(20.dp)
                        .testTag("playerIcon")
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
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
                text = "${gameLobby.roundDuration}",
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
                text = "${gameLobby.gameMode}",
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
    fun RectangleButton(onClick: () -> Unit, description: String = "", testTag: String = "Undefined",) {
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
     * This function is called when the user clicks on the button to join a gameLobby.
     * If the gameLobby code is empty, or the code is not in the DB, or the gameLobby is full,
     * it displays a warning message.
     * Otherwise, it calls the function to join the gameLobby.
     * @param warningState (MutableState<String>): The state of the warning message
     * @param mContext (Context): The context of the activity
     * @return (String): The warning message to be displayed
     */
    private fun gameLobbyCodeButtonOnClick(warningState: MutableState<String>, mContext: Context) {
        return if (gameLobbyCode.isEmpty()) {
            warningState.value = getString(R.string.game_lobby_code_is_empty)
        } else if (!dbContainsGameLobbyCode(gameLobbyCode)) {
            warningState.value = getString(R.string.game_lobby_does_not_exist)
        } else if(gameLobbyIsFull(gameLobbyCode)){
            warningState.value = getString(R.string.game_lobby_is_full)
        } else {
            warningState.value = ""
            joinGameLobbyRoom(mContext)
        }
    }

    /**
     * This function launches the gameLobby room activity and passes the gameLobby code to it.
     * @param mContext (Context): The context of the activity
     */
    private fun joinGameLobbyRoom(mContext: Context) {
        //TODO: clean this up when DB is really implemented
        val gameLobby =  mockDb.getGameLobbyWithCode(gameLobbyCode).get()
        val newGameLobby = GameLobby(gameLobby.admin, gameLobby.gameMode, gameLobby.minimumNumberOfPlayers, gameLobby.maximumNumberOfPlayers, gameLobby.roundDuration
            , gameLobby.gameMap, gameLobby.initialPlayerBalance, gameLobby.name, gameLobby.code, gameLobby.private)

        for (player in gameLobby.usersRegistered) {
            if (player != newGameLobby.admin) {
                newGameLobby.addUser(player)
            }
        }
        newGameLobby.addUser(authenticated_user)
        mockDb.updateGameLobby(newGameLobby)
        // TODO: link to the gameLobby room activity
    }

    /**
     * This function fetches the public gameLobbys from the database.
     * @return (List<GameLobby>): The list of public gameLobbys
     */
    private fun getPublicGameLobbiesFromDB(): List<GameLobby> {
        val gameLobbies = mockDb.getAllGameLobbies().get() ?: return listOf()
        return gameLobbies.filter { !it.private  && !gameLobbyIsFull(it) }
    }

    /**
     * This function checks if the gameLobby code is in the database.
     * @param gameLobbyCode (String): The gameLobby code to check
     * @return (Boolean): True if the gameLobby code is in the database, false otherwise
     * TODO: rewrite this function to check the real database
     */
    private fun dbContainsGameLobbyCode(gameLobbyCode: String): Boolean {
        return mockDb.getAllGameLobbiesCodes().get()?.contains(gameLobbyCode)?: false
    }

    /**
     * This function checks if the gameLobby is full.
     * @param gameLobbyCode (String): The gameLobby code to check
     * @return (Boolean): True if the gameLobby is full, false otherwise
     * TODO: rewrite this function to check the real database
     */
    private fun gameLobbyIsFull(gameLobbyCode: String): Boolean {

        val gameLobby = mockDb.getGameLobbyWithCode(gameLobbyCode).get() ?: return false
        return gameLobbyIsFull(gameLobby)
    }

    /**
     * This function checks if the gameLobby is full.
     * @param gameLobby (GameLobby): The gameLobby to check
     * @return (Boolean): True if the gameLobby is full, false otherwise
     */
    private fun gameLobbyIsFull(gameLobby: GameLobby): Boolean {
        return gameLobby.usersRegistered.size >= gameLobby.maximumNumberOfPlayers
    }

}

// MOCK DATA
private val authenticated_user = User(7, "current_user", "", Skin(0, 0, 0), Stats())
private val mockDb = FakeRemoteStorage()


