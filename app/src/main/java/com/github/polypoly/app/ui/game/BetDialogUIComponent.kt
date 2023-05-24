package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.location.LocationProperty

/**
 * Bet popup dialog
 * @param onBuy lambda to execute when a valid bet is set
 * @param onClose lambda to execute when the bet is canceled
 * @param locationOnBet location to bid for
 */
@Composable
fun BetDialog(onBuy: (Float) -> Unit, onClose: () -> Unit, locationOnBet: LocationProperty) {
    val inputPrice = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(text = "Enter your bet")
        },
        modifier = Modifier.testTag("betDialog"),
        text = {
            BetDialogBody(
                inputPrice = inputPrice,
                showError = showError
            )
        },
        buttons = {
            BetDialogButtons(
                locationOnBet = locationOnBet,
                onBuy = onBuy,
                onClose = onClose,
                inputPrice = inputPrice,
                showError = showError
            )
        }
    )
}

/**
 * Body for the bet dialog.
 */
@Composable
private fun BetDialogBody(
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
                .testTag("betInput")
        )
        if (showError.value) {
            Text(
                text = "You cannot bet less than the base price!",
                color = colors.error,
                style = typography.caption,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .testTag("betErrorMessage")
            )
        }
    }
}

/**
 * The buttons that are shown in the bet dialog.
 */
@Composable
private fun BetDialogButtons(
    locationOnBet: LocationProperty,
    onBuy: (Float) -> Unit,
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
                if (amount != null && amount >= locationOnBet.basePrice) {
                    onBuy(amount)
                } else {
                    showError.value = true
                }
            }
        ) {
            Text(
                text = "Confirm",
                modifier = Modifier.testTag("confirmBetButton")
            )
        }

        Button(
            onClick = onClose
        ) {
            Text(
                text = "Close",
                modifier = Modifier.testTag("closeBetButton")
            )
        }
    }
}
