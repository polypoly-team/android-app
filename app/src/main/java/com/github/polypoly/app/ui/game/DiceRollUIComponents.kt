package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.viewmodels.game.GameViewModel
import com.github.polypoly.app.ui.map.MapViewModel

// flag to show the roll dice dialog
val showRollDiceDialog = mutableStateOf(false)

/**
 * UI for dice button and dialog
 * @param gameViewModel GameViewModel to use for game business logic
 * @param mapViewModel GameViewModel to use for map business logic
 */
@Composable
fun DiceRollUI(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    val playerState = gameViewModel.getPlayerStateData().observeAsState().value

    if (playerState == PlayerState.ROLLING_DICE) {
        RollDiceButton()
        RollDiceDialog(gameViewModel, mapViewModel)
    }
}

/**
 * Button for rolling the dice.
 */
@Composable
fun RollDiceButton() {
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
 * @param gameViewModel GameViewModel to use for game business logic
 * @param mapViewModel GameViewModel to use for map business logic
 */
@Composable
fun RollDiceDialog(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    if (showRollDiceDialog.value) {
        var locationsRolled by remember { mutableStateOf(listOf<LocationProperty>()) }

        gameViewModel.rollDiceLocations(mapViewModel.getLocationSelected().value).thenAccept { rolled ->
            locationsRolled = rolled
        }

        Dialog(onDismissRequest = {}) {
            Surface(shape = MaterialTheme.shapes.medium, elevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Pick a location to go to !",
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                    )
                    locationsRolled.forEach { location ->
                        Button(
                            onClick = {
                                showRollDiceDialog.value = false
                                mapViewModel.goingToLocationProperty = location
                                gameViewModel.diceRolled()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(location.name)
                        }
                    }
                }
            }
        }
    }
}
