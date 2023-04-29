package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.game.GameActivity.Companion.gameViewModel

// flag to show the building info dialog
val interactingWithProperty = mutableStateOf(false)

/**
 * Manage the building info dialog and the bet dialog.
 */
@Composable
fun PropertyInteractUIComponent() {
    val showBetDialog = remember { mutableStateOf(false) }
    if (interactingWithProperty.value) PropertyInteractDialog(showBetDialog)

    if (showBetDialog.value) {
        BetDialog(onBuy = {
            // TODO: Handle the buy action with the entered amount here
            showBetDialog.value = false
        }, onClose = {
            showBetDialog.value = false
        })
    }
}

/**
 * Building Info popup dialog.
 */
@Composable
private fun PropertyInteractDialog(showBuyDialog: MutableState<Boolean>) {
    AlertDialog(
        onDismissRequest = { interactingWithProperty.value = false },
        modifier = Modifier.testTag("buildingInfoDialog"),
        title = {
            Row {
                val currentProperty =
                    gameViewModel.markerToLocationProperty[gameViewModel.currentMarker]
                Text(text = currentProperty?.name ?: "Unknown")
                Spacer(modifier = Modifier.weight(0.5f))
                Text(text = "Base price: ${currentProperty?.basePrice}")
            }
        },
        text = {
            Text(text = "This is some trivia related to the building and or some info related to it.")
        },
        buttons = {
            PropertyInteractButtons(showBuyDialog)
        }
    )
}

/**
 * Building Info popup dialog buttons.
 */
@Composable
private fun PropertyInteractButtons(showBuyDialog: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = SpaceEvenly
    ) {
        Button(
            onClick = { showBuyDialog.value = true },
            modifier = Modifier.testTag("betButton")
        ) {
            Text(text = "Bet")
        }
        Button(
            onClick = { interactingWithProperty.value = false },
            modifier = Modifier.testTag("closeButton")
        ) {
            Text(text = "Close")
        }
    }
}
