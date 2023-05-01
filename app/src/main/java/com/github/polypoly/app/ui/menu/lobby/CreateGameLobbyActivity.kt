package com.github.polypoly.app.ui.menu.lobby

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.ui.menu.MenuActivity
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_INITIAL_BALANCE_DEFAULT
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_INITIAL_BALANCE_STEP
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_MAX_INITIAL_BALANCE
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_MAX_PLAYERS
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_MAX_ROUNDS
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_MENU_PICKER_WIDTH
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_MIN_PLAYERS
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_MIN_ROUNDS
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_PRIVATE_DEFAULT
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_ROUNDS_DEFAULT
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_ROUNDS_DURATIONS
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_ROUND_DURATION_DEFAULT
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.uniqueCodeGenerator
import com.github.polypoly.app.utils.Constants.Companion.GAME_LOBBY_MIN_INITIAL_BALANCE as GAME_LOBBY_MIN_INITIAL_BALANCE1

class CreateGameLobbyActivity :  MenuActivity("Create a game") {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MenuContent{
                CreateGameLobbyContent()
            }
        }
    }

    @Composable
    fun CreateGameLobbyContent() {

        PolypolyTheme {
            GameSettingsMenu()
        }
    }

    /**
     * The menu for creating a game
     */
    @Composable
    fun GameSettingsMenu() {
        val mContext = LocalContext.current

        val gameName = remember { mutableStateOf("") }
        val isPrivateGame = remember { mutableStateOf(GAME_LOBBY_PRIVATE_DEFAULT) }
        val minNumPlayers = remember { mutableStateOf(GAME_LOBBY_MIN_PLAYERS) }
        val maxNumPlayers = remember { mutableStateOf(GAME_LOBBY_MAX_PLAYERS) }
        val numRounds = remember { mutableStateOf(GAME_LOBBY_ROUNDS_DEFAULT) }
        val roundDuration = remember { mutableStateOf(GAME_LOBBY_ROUND_DURATION_DEFAULT) }
        val gameMode = remember { mutableStateOf(GameMode.RICHEST_PLAYER) }
        val initialPlayerBalance = remember { mutableStateOf(GAME_LOBBY_INITIAL_BALANCE_DEFAULT) }
        val gameCode = remember {uniqueCodeGenerator.generateUniqueCode()}
        
        val createGameEnabled : Boolean = gameName.value.isNotBlank()
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 25.dp),
        ) {
            OutlinedTextField(
                value = gameName.value,
                onValueChange = { newText : String ->
                    gameName.value =
                        if (newText.matches(Regex("[a-zA-Z\\d]*")) && newText.length <= 10)
                            newText
                        else gameName.value
                },
                singleLine = true,
                label = { Text("Choose a game name") },
                modifier = Modifier.fillMaxWidth(),
                colors = UIElements.outlineTextFieldColors()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Private game : ", modifier = Modifier.weight(1f))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    Checkbox(
                        modifier = Modifier.offset(x = 12.dp),
                        checked = isPrivateGame.value,
                        onCheckedChange = {isPrivateGame.value = !isPrivateGame.value},
                        colors = UIElements.checkboxColors()
                    )
                }
            }


            NumberPickerField(
                title = "Min number of players : ",
                value = minNumPlayers.value,
                onValueChange = {
                    minNumPlayers.value = if (it <= maxNumPlayers.value) it
                    else maxNumPlayers.value
                },
                minValue = GAME_LOBBY_MIN_PLAYERS,
                maxValue = maxNumPlayers.value
            )

            NumberPickerField(
                title = "Max number of players : ",
                value = maxNumPlayers.value,
                onValueChange = { if (it >= minNumPlayers.value) maxNumPlayers.value = it },
                minValue = minNumPlayers.value,
                maxValue = GAME_LOBBY_MAX_PLAYERS
            )

            NumberPickerField(
                title = "Number of rounds : ",
                value = numRounds.value,
                onValueChange = { numRounds.value = it },
                minValue = GAME_LOBBY_MIN_ROUNDS,
                maxValue = GAME_LOBBY_MAX_ROUNDS
            )

            ListPickerField(
                title = "Round duration : ",
                value = roundDuration.value,
                onValueChange = { roundDuration.value = it },
                items = GAME_LOBBY_ROUNDS_DURATIONS.keys.toList()
            )

            ListPickerField(
                title = "Game mode : ",
                value = gameMode.value,
                onValueChange = { gameMode.value = it },
                items = GameMode.values().toList()
            )

            NumberPickerField(
                title = "Initial player balance: ",
                value = initialPlayerBalance.value,
                onValueChange = { initialPlayerBalance.value = it },
                minValue = GAME_LOBBY_MIN_INITIAL_BALANCE1,
                maxValue = GAME_LOBBY_MAX_INITIAL_BALANCE,
                step = GAME_LOBBY_INITIAL_BALANCE_STEP
            )

            Divider(
                modifier = Modifier.padding(vertical = 25.dp).fillMaxWidth(0.7f).align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = androidx.compose.ui.graphics.Color.Black
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Game code is: $gameCode", Modifier.padding(bottom = 20.dp))
                GameButton(
                    onClick = { createGame(mContext, gameName.value, isPrivateGame.value, minNumPlayers.value, maxNumPlayers.value, numRounds.value, roundDuration.value, gameMode.value, initialPlayerBalance.value, gameCode) },
                    text = "Create game",
                    enabled = createGameEnabled,
                )
            }

        }
    }

    /**
     * A picker field that displays numbers within a bounded range, using arrows to increment or decrement the value
     * @param title The title of the field
     * @param value The current value of the field
     * @param onValueChange The callback to be called when the value of the field changes
     * @param minValue The minimum value of the field
     * @param maxValue The maximum value of the field
     */
    @Composable
    private fun NumberPickerField(
        title: String,
        value: Int,
        onValueChange: (Int) -> Unit,
        minValue: Int,
        maxValue: Int,
        isEnabled: Boolean = true,
        step: Int = 1,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, modifier = Modifier.weight(1f))
            ArrowButton(
                onClick = { if (value > minValue) onValueChange(value - step) },
                enabled = isEnabled && value > minValue,
                leftArrow = true
            )
            Text(
                text = value.toString(),
                modifier = Modifier
                    .width(GAME_LOBBY_MENU_PICKER_WIDTH)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            ArrowButton(
                onClick = { if (value < maxValue) onValueChange(value + step) },
                enabled = isEnabled && value < maxValue
            )
        }
    }

    /**
     * A picker field that displays a list of items and allows the user to iterate through them with arrows
     * @param title The title of the field
     * @param value The current value of the field
     * @param onValueChange The callback to be called when the value of the field changes
     * @param items The list of items to be displayed
     * @param isEnabled Whether the field is enabled or not
     * @param width The width of the field
     */
    @Composable
    private fun <T> ListPickerField(
        title: String,
        value: T,
        onValueChange: (T) -> Unit,
        items: List<T>,
        isEnabled: Boolean = true,
        width : Dp = GAME_LOBBY_MENU_PICKER_WIDTH
    ) {
        val currentIndex = items.indexOf(value)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, modifier = Modifier.weight(1f))
            ArrowButton(
                onClick = {
                    if (currentIndex > 0) {
                        onValueChange(items[currentIndex - 1])
                    }
                },
                enabled = isEnabled && currentIndex > 0,
                leftArrow = true
            )
            Text(
                text = value.toString(),
                modifier = Modifier.width(width).wrapContentWidth(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            ArrowButton(
                onClick = {
                    if (currentIndex < items.lastIndex) {
                        onValueChange(items[currentIndex + 1])
                    }
                },
                enabled = isEnabled && currentIndex < items.lastIndex,
            )
        }
    }


    /**
     * A button that displays an arrow
     */
    @Composable
    private fun ArrowButton(onClick: () -> Unit, enabled: Boolean, leftArrow : Boolean = false) {
        IconButton(onClick = onClick, enabled = enabled) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Arrow",
                modifier = Modifier.size(24.dp).rotate(if (leftArrow) 180f else 0f)
            )
        }
    }

    /**
     *  Creates a game lobby, registers it in DB and navigates to the game lobby screen
     */
    private fun createGame(mContext : Context, name: String, isPrivate: Boolean, minPlayers: Int, maxPlayers: Int, numRounds: Int, roundDuration: String, gameMode: GameMode, initialPlayerBalance: Int, gameCode: String) {

        val rules = GameParameters(
            gameMode = gameMode,
            minimumNumberOfPlayers = minPlayers,
            maximumNumberOfPlayers = maxPlayers,
            roundDuration = GAME_LOBBY_ROUNDS_DURATIONS[roundDuration]!!,
            maxRound = numRounds,
            initialPlayerBalance = initialPlayerBalance
        )
        val lobby = GameLobby(currentUser, rules, name, gameCode, isPrivate)

        //TODO : create game in database and navigate to game screen
        val gameLobbyIntent = Intent(mContext, GameLobbyActivity::class.java)
        gameLobbyIntent.putExtra("lobby_code", "1234")
        startActivity(gameLobbyIntent)
        finish()
    }

    /**
     * a button
     */
    @Composable
    private fun GameButton(onClick: () -> Unit, text: String, enabled: Boolean = true) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .width(200.dp)
                .height(70.dp),
            enabled = enabled
        ) {
            Text(text = text)
        }
    }
}