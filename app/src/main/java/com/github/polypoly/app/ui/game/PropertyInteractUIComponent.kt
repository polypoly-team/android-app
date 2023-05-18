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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.models.game.GameViewModel
import com.github.polypoly.app.ui.game.GameActivity.Companion.interactingWithProperty
import com.github.polypoly.app.ui.game.GameActivity.Companion.mapViewModel

/**
 * Manage the building info dialog and the bet dialog.
 */
@Composable
fun PropertyInteractUIComponent(gameViewModel: GameViewModel) {
    val showBetDialog = remember { mutableStateOf(false) }
    val playerState = gameViewModel.getPlayerState().observeAsState().value

    if (interactingWithProperty.value) PropertyInteractDialog(showBetDialog, gameViewModel)

    if (showBetDialog.value && playerState == PlayerState.BETTING) {
        BetDialog(onBuy = { valueBet ->
            onBuy(valueBet, gameViewModel)
        }, onClose = { leaveBetDialog(gameViewModel) })
    }
}

/**
 * Building Info popup dialog.
 */
@Composable
private fun PropertyInteractDialog(showBuyDialog: MutableState<Boolean>, gameViewModel: GameViewModel) {
    AlertDialog(
        onDismissRequest = { interactingWithProperty.value = false },
        modifier = Modifier.testTag("buildingInfoDialog"),
        title = {
            Row {
                val currentProperty =
                    mapViewModel.markerToLocationProperty[mapViewModel.selectedMarker]
                Text(text = currentProperty?.name ?: "Unknown")
                Spacer(modifier = Modifier.weight(0.5f))
                Text(text = "Base price: ${currentProperty?.basePrice}")
            }
        },
        text = {
            Text(text = "This is some trivia related to the building and or some info related to it.")
        },
        buttons = {
            PropertyInteractButtons(showBuyDialog, gameViewModel)
        }
    )
}

/**
 * Building Info popup dialog buttons.
 */
@Composable
private fun PropertyInteractButtons(showBuyDialog: MutableState<Boolean>, gameViewModel: GameViewModel) {
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
            onClick = { leaveBetDialog(gameViewModel) },
            modifier = Modifier.testTag("closeButton")
        ) {
            Text(text = "Close")
        }
    }
}

private fun leaveBetDialog(gameViewModel: GameViewModel) {
    interactingWithProperty.value = false
    gameViewModel.cancelBetting()
}

private fun onBuy(valueBet: Float, gameViewModel: GameViewModel) {
    leaveBetDialog(gameViewModel)
}