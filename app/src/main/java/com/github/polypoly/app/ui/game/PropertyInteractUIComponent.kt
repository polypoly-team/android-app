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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.models.game.GameViewModel
import com.github.polypoly.app.ui.map.MapViewModel

/**
 * Manage the building info dialog and the bet dialog.
 */
@Composable
fun PropertyInteractUIComponent(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    val playerState = gameViewModel.getPlayerState().observeAsState().value
    val locationSelected = mapViewModel.getLocationSelected().observeAsState().value ?: return

    PropertyInteractDialog(locationSelected, gameViewModel, mapViewModel)

    if (playerState == PlayerState.BETTING) {
        BetDialog(
            onBuy = { valueBet ->
                onBuy(valueBet, gameViewModel)
                leaveInteractionDialog(gameViewModel, mapViewModel)
            },
            onClose = { leaveInteractionDialog(gameViewModel, mapViewModel) },
            locationOnBet = locationSelected
        )
    }
}

/**
 * Building Info popup dialog.
 */
@Composable
private fun PropertyInteractDialog(locationSelected: LocationProperty, gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    AlertDialog(
        onDismissRequest = { leaveInteractionDialog(gameViewModel, mapViewModel) },
        modifier = Modifier.testTag("buildingInfoDialog"),
        title = {
            Row {
                Text(text = locationSelected.name)
                Spacer(modifier = Modifier.weight(0.5f))
                Text(text = "Base price: ${locationSelected.basePrice}")
            }
        },
        text = {
            Text(text = "This is some trivia related to the building and or some info related to it.")
        },
        buttons = {
            PropertyInteractButtons(gameViewModel, mapViewModel)
        }
    )
}

/**
 * Building Info popup dialog buttons.
 */
@Composable
private fun PropertyInteractButtons(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = SpaceEvenly
    ) {
        Button(
            onClick = { gameViewModel.startBetting() },
            modifier = Modifier.testTag("betButton")
        ) {
            Text(text = "Bet")
        }
        Button(
            onClick = { leaveInteractionDialog(gameViewModel, mapViewModel) },
            modifier = Modifier.testTag("closeButton")
        ) {
            Text(text = "Close")
        }
    }
}

private fun leaveInteractionDialog(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    gameViewModel.cancelBetting()
    mapViewModel.selectLocation(null)
}

private fun onBuy(valueBet: Float, gameViewModel: GameViewModel) {
    // TODO: call gameViewModel's buy logic
}