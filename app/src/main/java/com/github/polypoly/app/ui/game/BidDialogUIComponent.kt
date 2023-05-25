package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.location.LocationBid
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.viewmodels.game.GameViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Bid popup dialog
 * @param onBuy lambda to execute when a valid bid is set
 * @param onClose lambda to execute when the bid is canceled
 * @param locationOnBid location to bid for
 */
@Composable
fun BidDialog(onBuy: (Float) -> Unit, onClose: () -> Unit, locationOnBid: LocationProperty) {
    val inputPrice = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(text = "Enter your bid")
        },
        modifier = Modifier.testTag("bid_dialog"),
        text = {
            BidDialogBody(
                inputPrice = inputPrice,
                showError = showError
            )
        },
        buttons = {
            BidDialogButtons(
                locationBid = locationOnBid,
                onBid = onBuy,
                onClose = onClose,
                inputPrice = inputPrice,
                showError = showError
            )
        }
    )
}

/**
 * Body for the bid dialog.
 * @param inputPrice the price input by the user
 * @param showError whether to show the error message
 */
@Composable
private fun BidDialogBody(
    inputPrice: MutableState<String>,
    showError: MutableState<Boolean>
) {
    Column {
        TextField(
            value = inputPrice.value,
            onValueChange = { newValue -> inputPrice.value = newValue },
            placeholder = { Text(text = "Enter amount") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            textStyle = typography.body1,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("bid_input")
        )
        if (showError.value) {
            Text(
                text = "You cannot bid less than the base price!",
                color = colors.error,
                style = typography.caption,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .testTag("bid_error_message")
            )
        }
    }
}

/**
 * The buttons that are shown in the bid dialog.
 * @param locationBid location to bid for
 * @param onBid lambda to execute when a valid bid is set
 * @param onClose lambda to execute when the bid is canceled
 * @param inputPrice the price input by the user
 * @param showError whether to show the error message
 */
@Composable
private fun BidDialogButtons(
    locationBid: LocationProperty,
    onBid: (Float) -> Unit,
    onClose: () -> Unit,
    inputPrice: MutableState<String>,
    showError: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = SpaceEvenly
    ) {
        Button(
            onClick = {
                val amount = inputPrice.value.toFloatOrNull()
                if (amount != null && amount >= locationBid.basePrice) {
                    onBid(amount)
                } else {
                    showError.value = true
                }
            }
        ) {
            Text(
                text = "Confirm",
                modifier = Modifier.testTag("confirm_bid_button")
            )
        }

        Button(
            onClick = onClose
        ) {
            Text(
                text = "Close",
                modifier = Modifier.testTag("close_bid_button")
            )
        }
    }
}

/**
 * Creates a popup that indicates a bid was successful
 * @param gameViewModel view model of the associated game
 * @param duration duration of the popup before automatic dismiss
 */
@Composable
fun SuccessfulBidNotification(gameViewModel: GameViewModel, duration: Duration) {
    val successfulBid = gameViewModel.successfulBidData.observeAsState().value
    val currentRoundTurn = gameViewModel.getRoundTurnData().observeAsState().value

    val notificationTurn = remember { mutableStateOf(-1) }
    val showCurrentTurn = remember { mutableStateOf(false) }

    if (successfulBid != null && currentRoundTurn != null) {
        if (!showCurrentTurn.value && notificationTurn.value < currentRoundTurn) {
            notificationTurn.value = currentRoundTurn
            showCurrentTurn.value = true

            LaunchedEffect(currentRoundTurn) {
                delay(duration.toLong(DurationUnit.MILLISECONDS))
                showCurrentTurn.value = false
            }
        }

        if (showCurrentTurn.value) {
            SuccessfulBidAlert(
                bid =  successfulBid,
                onClose = { showCurrentTurn.value = false }
            )
        }
    }
}

@Composable
private fun SuccessfulBidAlert(bid: LocationBid, onClose: () -> Unit) {
    AlertDialog(
        modifier = Modifier.testTag("successful_bid_alert"),
        onDismissRequest = onClose,
        title = {
            Text("Congratulations, your bid was successful !!")
        },
        text = {
            Text("You bought ${bid.location.name} for ${bid.amount} !!")
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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
