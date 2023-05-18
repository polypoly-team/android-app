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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.R
import com.github.polypoly.app.ui.game.GameActivity.Companion.mapViewModel

/**
 * Bet popup dialog
 */
@Composable
fun BetDialog(onBuy: (Float) -> Unit, onClose: () -> Unit) {
    val inputPrice = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(text = stringResource(R.string.bet_dialog_prompt))
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
            placeholder = { Text(text = stringResource(R.string.bet_dialog_text_field_placeholder)) },
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
                text = stringResource(R.string.bet_dialog_error),
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
    onBuy: (Float) -> Unit,
    onClose: () -> Unit,
    inputPrice: MutableState<String>,
    showError: MutableState<Boolean>
) {
    val minBet = mapViewModel.markerToLocationProperty[mapViewModel.selectedMarker]?.basePrice!!
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = SpaceEvenly
    ) {
        Button(
            onClick = {
                val amount = inputPrice.value.toFloatOrNull()
                if (amount != null && amount >= minBet) {
                    onBuy(amount)
                } else {
                    showError.value = true
                }
            }
        ) {
            Text(
                text = stringResource(R.string.bet_dialog_confirm),
                modifier = Modifier.testTag("confirmBetButton")
            )
        }

        Button(
            onClick = onClose
        ) {
            Text(
                text = stringResource(R.string.dialog_close),
                modifier = Modifier.testTag("closeBetButton")
            )
        }
    }
}
