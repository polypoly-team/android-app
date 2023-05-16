package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.ui.game.GameActivity.Companion.mapViewModel

// flag to show the roll dice dialog
val showRollDiceDialog = mutableStateOf(false)

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
            onClick = {
                if (mapViewModel.interactableProperty.value != null)
                    showRollDiceDialog.value = true
            },
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
fun RollDiceDialog() {
    if (showRollDiceDialog.value) {
        Dialog(onDismissRequest = { showRollDiceDialog.value = false }) {
            AlertDialog(
                onDismissRequest = { showRollDiceDialog.value = false },
                title = { Text("Dice Roll") },
                text = {
                    Column {
                        val rollDice = rollDiceLocations()
                        for (i in 0..2)
                            Button(onClick = {
                                showRollDiceDialog.value = false
                                mapViewModel.currentPlayer?.playerState?.value = PlayerState.MOVING
                                mapViewModel.goingToLocationProperty = rollDice[i]
                            }) {
                                Text(rollDice[i].name)
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

/**
 * Rolls the dice and returns the location that corresponds to the sum of 2 dice rolls, 3 times
 * ensuring that the player does not visit the same location twice.
 */
private fun rollDiceLocations(): List<LocationProperty> {
    val locationsNotToVisitName = mutableListOf(mapViewModel.interactableProperty.value?.name)

    val locationsToVisit = mutableListOf<LocationProperty>()
    for (i in 1..3) {
        val diceRollsSum = IntArray(2) { (1..6).random() }.sum() - 2
        val closestLocations = mapViewModel.markerToLocationProperty.entries
            .filter { !locationsNotToVisitName.contains(it.value.name) }
            .sortedBy { it.key.position.distanceToAsDouble(mapViewModel.interactableProperty.value!!.position()) }
            .take(11)

        locationsToVisit.add(closestLocations[diceRollsSum].value)
        locationsNotToVisitName.add(closestLocations[diceRollsSum].value.name)
    }
    return locationsToVisit
}