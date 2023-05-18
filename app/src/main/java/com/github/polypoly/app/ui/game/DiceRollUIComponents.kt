package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.models.game.GameViewModel
import com.github.polypoly.app.ui.map.MapViewModel

// flag to show the roll dice dialog
val showRollDiceDialog = mutableStateOf(false)

@Composable
fun DiceRollUI(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    val playerState = gameViewModel.getPlayerState().observeAsState().value

    if (playerState == PlayerState.ROLLING_DICE) {
        RollDiceButton(mapViewModel)
        RollDiceDialog(gameViewModel, mapViewModel)
    }
}

/**
 * Button for rolling the dice.
 */
@Composable
fun RollDiceButton(mapViewModel: MapViewModel) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .size(80.dp)
                .align(BottomCenter)
                .offset(y = (-80).dp)
                .testTag("roll_dice_button"),
            onClick = { showRollDiceDialog.value = true },
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Casino, contentDescription = "Roll Dice")
        }
    }
}

/**
 * Dice roll dialog, shows the result of 3 dice rolls in a column.
 */
@Composable
fun RollDiceDialog(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    if (showRollDiceDialog.value) {
        var locationsRolled by remember { mutableStateOf(listOf<LocationProperty>()) }

        gameViewModel.rollDiceLocations(mapViewModel.getLocationSelected().value).thenAccept { rolled ->
            locationsRolled = rolled
        }

        Dialog(onDismissRequest = { showRollDiceDialog.value = false }) {
            AlertDialog(
                onDismissRequest = { showRollDiceDialog.value = false },
                title = { Text("Dice Roll") },
                text = {
                    Column {
                        for (location in locationsRolled) {
                            Button(onClick = {
                                showRollDiceDialog.value = false
                                mapViewModel.goingToLocationProperty = location
                                gameViewModel.diceRolled()
                            }) {
                                Text(location.name)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showRollDiceDialog.value = false }) {
                        Text("Quit")
                    }
                }
            )
        }
    }
}