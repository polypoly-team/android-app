package com.github.polypoly.app.ui.menu.lobby

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.github.polypoly.app.R
import com.github.polypoly.app.base.GameMusic
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.ui.menu.MenuActivity
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.uniqueCodeGenerator

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
     *  this function creates a menu that allows the user to chose settings for the game.
     *  The user can chose the name of the game, if the game is private, the min number of players, the max number of players, the number of rounds, the round duration, the GameMode, the initialPlayerBalance.
     *  these are displayed as a list of fields that the user can edit, with arrows on the right and on the left to iterate through the values (not for the game name).
     *  there is also a button at the end to create a lobby, that is disabled until all the fields are filled.
     *  above the button will be shown the game code, that will be used by other players to join the lobby. The game code should be "FAKE_CODE" for now
     *
     *  all the fields title should stick to the left side, all the editable fields should stick to the right side.
     *  the menu should look like this ('[]' are used to indicate a field that can be edited):
     *  [Choose a game name] (text field)
     *  Private game :          [] (checkbox. default is false)
     *  Min number of players : [] (arrow left) 2 (arrow right) (increases and decreases by 1. min value is 2 and max value is 8. default is 2. must be <= max number of players, otherwise locks value if trying to increase)
     *  Max number of players : [] (arrow left) 8 (arrow right) (increases and decreases by 1. min value is 2 and max value is 8. Must be >= min number of players. default is 8, otherwise locks the value if trying to decrease)
     *  Number of rounds :      [] (arrow left) 5 (arrow right) (increases and decreases by 1. min value is 2 and max value is 30. default is 5)
     *  Round duration :        [] (arrow left) 1 day (arrow right) (5, 10, 15, 20, 25, 30, 45, 60 minutes, then 1, 2, 3, 4, 5, 10, 15, 20 hours, then 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 days. default is 1 day. values will be prompted as so in the field, but will have numerical values in code)
     *  Game mode :             [] (arrow left) Classic (arrow right) (iterates through GameMode.values(). default GameMode.RICHEST_PLAYER)
     *  Initial player balance: [] (arrow left) 1500 (arrow right) (increases and decreases by 100. min value is 100 and max value is 10000. default is 1500)
     *
     *  Game code is: #FAKE_CODE
     *  [Create game] (button) (disabled until all the fields are filled)
     */
    @Composable
    fun GameSettingsMenu() {
        val gameName = remember { mutableStateOf("") }
        val isPrivateGame = remember { mutableStateOf(false) }
        val minNumPlayers = remember { mutableStateOf(2) }
        val maxNumPlayers = remember { mutableStateOf(8) }
        val numRounds = remember { mutableStateOf(5) }
        val roundDuration = remember { mutableStateOf("1 day") }
        val gameMode = remember { mutableStateOf(GameMode.RICHEST_PLAYER) }
        val initialPlayerBalance = remember { mutableStateOf(1500) }
        val gameCode = uniqueCodeGenerator.generateUniqueCode()
        
        val createGameEnabled : Boolean = gameName.value.isNotBlank() &&
                (minNumPlayers.value <= maxNumPlayers.value) &&
                (initialPlayerBalance.value in (100..10000))

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
                minValue = 2,
                maxValue = maxNumPlayers.value
            )

            NumberPickerField(
                title = "Max number of players : ",
                value = maxNumPlayers.value,
                onValueChange = { if (it >= minNumPlayers.value) maxNumPlayers.value = it },
                minValue = minNumPlayers.value,
                maxValue = 8
            )

            NumberPickerField(
                title = "Number of rounds : ",
                value = numRounds.value,
                onValueChange = { numRounds.value = it },
                minValue = 2,
                maxValue = 30
            )

            ListPickerField(
                title = "Round duration : ",
                value = roundDuration.value,
                onValueChange = { roundDuration.value = it },
                items = listOf(
                    "5 min", "10 min", "15 min", "20 min", "25 min", "30 min",
                    "1 hour", "2 hours", "3 hours", "4 hours", "5 hours", "10 hours",
                    "15 hours", "20 hours", "1 day", "2 days", "3 days", "4 days",
                    "5 days", "6 days", "7 days", "8 days", "9 days", "10 days"
                )
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
                minValue = 100,
                maxValue = 15000,
                step = 500
            )


            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Game code is: $gameCode" )
                GameButton(
                    onClick = { },
                    text = "Create game",
                    enabled = createGameEnabled,
                )
            }

        }
    }

    @Composable
    fun NumberPickerField(
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, modifier = Modifier.weight(1f))
            ArrowButton(
                onClick = { if (value > minValue) onValueChange(value - step) },
                enabled = isEnabled && value > minValue
            )
            Text(
                text = value.toString(),
                modifier = Modifier
                    .width(65.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            ArrowButton(
                onClick = { if (value < maxValue) onValueChange(value + step) },
                enabled = isEnabled && value < maxValue
            )
        }
    }

    @Composable
    fun <T> ListPickerField(
        title: String,
        value: T,
        onValueChange: (T) -> Unit,
        items: List<T>,
        isEnabled: Boolean = true,
        width : Dp = 65.dp
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
                enabled = isEnabled && currentIndex > 0
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
                enabled = isEnabled && currentIndex < items.lastIndex
            )
        }
    }


    @Composable
    private fun ArrowButton(onClick: () -> Unit, enabled: Boolean) {
        IconButton(onClick = onClick, enabled = enabled) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Arrow",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    /**
     * a button
     */
    @Composable
    fun GameButton(onClick: () -> Unit, text: String, enabled: Boolean = true) {
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