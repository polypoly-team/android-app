package com.github.polypoly.app.menu

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.github.polypoly.app.game.*
import com.github.polypoly.app.ui.theme.PolypolyTheme
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.time.Duration.Companion.hours

class JoinGroupActivity : ComponentActivity() {

    /**
     * The attributes of the class
     */
    private var groupCode: String = ""

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
                        GroupForm()
                    }
                }
            }
        }
    }


    /**
     * Component where the user can write the group number. If the group number is valid,
     * the button lets the player join the group. Otherwise, it displays a warning message.
     */
    @Composable
    fun GroupForm() {
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
                GroupTextField(15, warningState) // TODO: create a constant for the max length -> create a class for the constants
                Spacer(modifier = Modifier.height(10.dp))
                RectangleButton(
                    onClick = {
                        groupCodeButtonOnClick(warningState, mContext)
                    }
                    , description = getString(R.string.join_group_button_text)
                    , testTag = "JoinGroupButton")
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
            GroupListButton()
        }
    }


    /**
     * This function returns the TextField where the user prompts their group code.
     */
    @Composable
    fun GroupTextField(maxLength: Int, warningState : MutableState<String>) {
        val focusManager = LocalFocusManager.current
        val mContext = LocalContext.current
        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier
                .width(200.dp)
                .testTag("groupCodeField"),
            // When user clicks on enter button, the focus is removed and the button is clicked
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                groupCodeButtonOnClick(warningState, mContext)
            }),
            value = text,
            label = { Text("Enter a group code") },
            singleLine = true,
            // text can only be letters and numbers (avoids ghost characters as the Enter key)
            onValueChange = { newText ->
                text = if (newText.matches(Regex("[a-zA-Z\\d]*")) && newText.length <= maxLength) newText else text
                groupCode = text
            }

        )

    }


    /**
     * This function returns the button that lets the user open the groups list.
     * The groups list is a dialog that shows the public groups that the user can join.
     * The groups list is refreshed every 5 seconds.
     */
    @Composable
    fun GroupListButton() {
        var openList by remember { mutableStateOf(false) }
        var openCardIndex by remember { mutableStateOf(-1) }

        var groups by remember { mutableStateOf(getPublicGroupsFromDB()) }

        val refreshInterval = 5000L

        RectangleButton(
            onClick = {
                openList = true
            },
            description = "Show Groups",
            testTag = "showGroupsButton"
        )

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
                        groups = getPublicGroupsFromDB()
                        Timber.tag("GroupList")
                            .d("Refreshing groups list")
                    }
                }

                Surface(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.95f)
                ) {
                    LazyColumn(modifier = Modifier.padding(20.dp)) {
                        item {
                            Text(
                                text = getString(R.string.group_list_title),
                                style = MaterialTheme.typography.h4
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        items(
                            items = groups,
                            itemContent = {
                                    item ->
                                val index = groups.indexOf(item)
                                GroupCardComponent(
                                    group = item,
                                    isOpen = index == openCardIndex,
                                    onOpenChange = { open ->
                                        openCardIndex = if (open) index else -1
                                    }
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
     * This function returns the group card. It contains the header and the details of the group.
     * @param group the game that holds the name and the number of players
     */
    @Composable
    fun GroupCardComponent(
        group: PendingGame,
        isOpen: Boolean,
        onOpenChange: (Boolean) -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("groupCard"),
            elevation = 10.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenChange(!isOpen) }
            ) {
                GroupCardHeader(group)
                if (isOpen) {
                    GroupCardDetails(group)
                }
            }
        }
    }


    /**
     * This function returns the header of the group card. It contains the name of the group and the number of players.
     * @param group the game that holds the name and the number of players
     */
    @Composable
    private fun GroupCardHeader(group: PendingGame) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.h5
            )
            GroupCardPlayerCount(group)
        }
    }

    /**
     * This function returns the number of players that are currently in the group.
     * @param group the game that holds the number of players
     */
    @Composable
    private fun GroupCardPlayerCount(group: PendingGame) {
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
                text = "${group.usersRegistered.size}/${group.maximumNumberOfPlayers}",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }

    /**
     * This function returns the hidden content of the group card, that un-rolls when the user clicks on the card.
     * @param group the game that the card will contain the details of
     */
    @Composable
    private fun GroupCardDetails(group: PendingGame) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
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

            GroupCardPlayerList(group)

            Divider(
                modifier = Modifier.padding(vertical = 5.dp),
                thickness = 1.dp,
                color = androidx.compose.ui.graphics.Color.Black
            )

            GroupCardRoundDuration(group)

            Spacer(modifier = Modifier.height(5.dp))

            GroupCardGameMode(group)

            GroupCardJoinButton(group.code)
        }
    }

    /**
     * Creates a button to join the group
     * @param code the code of the group to join
     */
    @Composable
    private fun GroupCardJoinButton(code: String) {
        val mContext = LocalContext.current
        val warningState = remember { mutableStateOf("") }
        groupCode = code

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RectangleButton(
                onClick = { groupCodeButtonOnClick(warningState,mContext)},
                description = "Join Group",
                testTag = "joinGroupButton"
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
     * Creates a text that displays the list of players in the group.
     * @param group the group to display the players of
     */
    @Composable
    fun GroupCardPlayerList(group: PendingGame) {
        for (player in group.usersRegistered) {
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
     * @param group the group to display the round duration of
     */
    @Composable
    fun GroupCardRoundDuration(group: PendingGame) {
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
                text = "${group.roundDuration}",
                style = MaterialTheme.typography.body1
            )
        }
    }

    /**
     * Creates a text that displays the game mode of the game.
     * @param group the group to display the game mode of
     */
    @Composable
    fun GroupCardGameMode(group: PendingGame) {
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
                text = "${group.gameMode}",
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
     * This function is called when the user clicks on the button to join a group.
     * If the group code is empty, or the code is not in the DB, or the group is full,
     * it displays a warning message.
     * Otherwise, it calls the function to join the group.
     * @param warningState (MutableState<String>): The state of the warning message
     * @param mContext (Context): The context of the activity
     * @return (String): The warning message to be displayed
     */
    private fun groupCodeButtonOnClick(warningState: MutableState<String>, mContext: Context) {
        return if (groupCode.isEmpty()) {
            warningState.value = getString(R.string.group_code_is_empty)
        } else if (!dbContainsGroupCode(groupCode)) {
            warningState.value = getString(R.string.group_does_not_exist)
        } else if(groupIsFull(groupCode)){
            warningState.value = getString(R.string.group_is_full)
        } else {
            warningState.value = ""
            joinGroupRoom(mContext)
        }
    }

    /**
     * This function launches the group room activity and passes the group code to it.
     * @param mContext (Context): The context of the activity
     */
    private fun joinGroupRoom(mContext : Context) {
        // TODO: link to the group room activity
    }

    /**
     * This function fetches the public groups from the database.
     * @return (List<PendingGame>): The list of public groups
     */
    private fun getPublicGroupsFromDB(): List<PendingGame> {
        return mockPendingGames.values.toList().filter { !it.private  && !groupIsFull(it) }
    }

    /**
     * This function checks if the group code is in the database.
     * @param groupCode (String): The group code to check
     * @return (Boolean): True if the group code is in the database, false otherwise
     * TODO: rewrite this function to check the real database
     */
    private fun dbContainsGroupCode(groupCode: String): Boolean {
        return mockPendingGames.containsKey(groupCode)
    }

    /**
     * This function checks if the group is full.
     * @param groupCode (String): The group code to check
     * @return (Boolean): True if the group is full, false otherwise
     * TODO: rewrite this function to check the real database
     */
    private fun groupIsFull(groupCode: String): Boolean {

        val pendingGame = mockPendingGames.getOrElse(groupCode) { return false }

        return groupIsFull(pendingGame)
    }

    /**
     * This function checks if the group is full.
     * @param group (PendingGame): The group to check
     * @return (Boolean): True if the group is full, false otherwise
     */
    private fun groupIsFull(group: PendingGame): Boolean {
        return group.usersRegistered.size >= group.maximumNumberOfPlayers
    }


    // ------------------- MOCKUP CODE -------------------
    // This code is only here to show how the group room activity should be called
    // It will be removed when the group room activity is created and the database is set up


    // mock DB

    private val code1 = "1234"
    private val code2 = "abcd"
    private val code3 = "123abc"
    private val code4 = "1234abc"
    private val code5 = "abc123"
    private val code6 = "abc1234"

    private val name1 = "Full group"
    private val name2 = "Joinable 1"
    private val name3 = "Joinable 2"
    private val name4 = "Joinable 3"
    private val name5 = "Private group"
    private val name6 = "Joinable 4"

    private val emptySkin = Skin(0, 0, 0)
    private val zeroStats = Stats()
    private val testUser1 = User(1, "test_user_1", "", emptySkin, zeroStats)
    private val testUser2 = User(2, "test_user_2", "", emptySkin, zeroStats)
    private val testUser3 = User(3, "test_user_3", "", emptySkin, zeroStats)
    private val testUser4 = User(4, "test_user_4", "", emptySkin, zeroStats)
    private val testUser5 = User(5, "test_user_5", "", emptySkin, zeroStats)

    private val testMinNumberPlayers = 2
    private val testMaxNumberPlayers = 5
    private val testDuration = 2.hours
    private val testInitialBalance = 100

    private val pendingGameFull = PendingGame(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name1, code1
    )
    private val pendingGameJoinable1 = PendingGame(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name2, code2
    )
    private val pendingGameJoinable2 = PendingGame(
        testUser1, GameMode.LAST_STANDING, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name3, code3
    )
    private val pendingGameJoinable3 = PendingGame(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name4, code4
    )
    private val pendingGamePrivate = PendingGame(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name5, code5, true
    )

    private val pendingGameJoinable4 = PendingGame(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name6, code6
    )

    private val mockPendingGames :HashMap<String, PendingGame> = hashMapOf(
        code1 to pendingGameFull,
        code2 to pendingGameJoinable1,
        code3 to pendingGameJoinable2,
        code4 to pendingGameJoinable3,
        code5 to pendingGamePrivate
    )

    init {
        pendingGameFull.addUser(testUser2)
        pendingGameFull.addUser(testUser3)
        pendingGameFull.addUser(testUser4)
        pendingGameFull.addUser(testUser5)

        pendingGameJoinable1.addUser(testUser2)
        pendingGameJoinable1.addUser(testUser3)

        pendingGameJoinable2.addUser(testUser2)
        pendingGameJoinable2.addUser(testUser3)
        pendingGameJoinable2.addUser(testUser4)

        pendingGamePrivate.addUser(testUser2)
        pendingGamePrivate.addUser(testUser3)
        pendingGamePrivate.addUser(testUser4)
    }

}



