package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
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
import com.github.polypoly.app.viewmodels.game.GameViewModel
import com.github.polypoly.app.ui.map.MapViewModel
import com.github.polypoly.app.ui.theme.Padding
import kotlin.math.floor

/**
 * Manage the building info dialog and the bid dialog.
 * @param gameViewModel GameViewModel to use for game business logic
 * @param mapViewModel GameViewModel to use for map business logic
 */
@Composable
fun PropertyInteractUIComponent(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    val playerState = gameViewModel.getPlayerStateData().observeAsState().value
    val locationSelected = mapViewModel.getLocationSelected().observeAsState().value ?: return

    PropertyInteractDialog(locationSelected, gameViewModel, mapViewModel)

    if (playerState == PlayerState.BIDDING) {
        BidDialog(
            onBuy = { valueBid ->
                gameViewModel.bidForLocation(locationSelected, floor(valueBid).toInt()).thenApply {
                    leaveInteractionDialog(gameViewModel, mapViewModel)
                }
            },
            onClose = { leaveInteractionDialog(gameViewModel, mapViewModel) },
            locationOnBid = locationSelected
        )
    }
}

/**
 * Building Info popup dialog.
 * @param locationSelected location selected by the player
 * @param gameViewModel GameViewModel to use for game business logic
 * @param mapViewModel GameViewModel to use for map business logic
 */
@Composable
private fun PropertyInteractDialog(locationSelected: LocationProperty, gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    AlertDialog(
        onDismissRequest = { leaveInteractionDialog(gameViewModel, mapViewModel) },
        modifier = Modifier.testTag("building_info_dialog"),
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
 * @param gameViewModel GameViewModel to use for game business logic
 * @param mapViewModel GameViewModel to use for map business logic
 */
@Composable
private fun PropertyInteractButtons(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Padding.medium),
        horizontalArrangement = SpaceEvenly
    ) {
        Button(
            onClick = { gameViewModel.bidOnLocationSelected(mapViewModel.getLocationSelected().value ?: LocationProperty()) },
            modifier = Modifier.testTag("bid_button")
        ) {
            Text(text = "Bid")
        }
        Button(
            onClick = { leaveInteractionDialog(gameViewModel, mapViewModel) },
            modifier = Modifier.testTag("close_button")
        ) {
            Text(text = "Close")
        }
    }
}

private fun leaveInteractionDialog(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    if (gameViewModel.getPlayerStateData().value == PlayerState.BIDDING) {
        gameViewModel.cancelBidding()
    }
    mapViewModel.selectLocation(null)
}

@Composable
fun TaxToPayNotification(gameViewModel: GameViewModel, mapViewModel: MapViewModel) {
    val locationForTax = gameViewModel.taxToPay.observeAsState().value

    val onClose = {
        gameViewModel.endInteraction()
        leaveInteractionDialog(gameViewModel, mapViewModel)
    }

    if (locationForTax != null) {
        AlertDialog(
            modifier = Modifier.testTag("tax_alert"),
            onDismissRequest = onClose,
            title = {
                Text("You have to pay a tax !")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Padding.medium),
                ) {
                    Text("Unfortunately, ${locationForTax.locationProperty.name} is already owned by ${locationForTax.owner?.user?.name ?: "unknown"}...")
                    Text("You will have to pay ${locationForTax.currentTax()}$ as a tax !")
                }
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Padding.medium),
                    horizontalArrangement = SpaceEvenly
                ) {
                    Button(
                        onClick = { onClose() },
                    )
                    {
                        Text("close")
                    }
                }
            }
        )
    }
}