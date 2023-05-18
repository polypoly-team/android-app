package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.InGameLocation

/**
 * Dialog to that propose a trade to the player
 * @param playerApplicant The player that propose the trade
 * @param openDialog A mutable state to open and close the dialog.
 * @param openDialogChooseLocation A mutable state to open and close the dialog to choose the location
 */
@Composable
fun ProposeTradeDialog(playerApplicant: Player, openDialog: MutableState<Boolean>,
                       openDialogChooseLocation: MutableState<Boolean>) {
    AlertDialog(
        onDismissRequest = { openDialog.value = false },
        title = { Text(text = "Player ${playerApplicant.user.name} propose you a trade !") },
        text = { Text(text = "Do you accept ?") },
        buttons = {
            Row {
                Button(onClick = {
                    openDialogChooseLocation.value = true
                }) {
                    Text(text = "Yes")
                }
                Button(onClick = {
                    openDialog.value = false
                }) {
                    Text(text = "No")
                }
            }
        }
    )
}

/**
 * Dialog to know if the player accept the trade
 * @param playerApplicant The player that propose the trade
 * @param openDialog A mutable state to open and close the dialog.
 * @param locationGiven The location given by the player
 * @param locationReceived The location received by the player
 * @param currentPlayerAcceptation A mutable state to know if the current player accept the trade
 */
@Composable
fun AcceptTradeDialog(playerApplicant: Player, openDialog: MutableState<Boolean>,
                      locationGiven: InGameLocation, locationReceived: InGameLocation,
                        currentPlayerAcceptation: MutableState<Boolean?>) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Do you want to trade ${locationGiven.locationProperty.name} against" +
                " ${locationReceived.locationProperty.name} with ${playerApplicant.user.name}?") },
        text = {},
        buttons = {
            Row {
                Button(onClick = {
                    currentPlayerAcceptation.value = true
                    openDialog.value = false
                }) {
                    Text(text = "Accept")
                }
                Button(onClick = {
                    currentPlayerAcceptation.value = false
                    openDialog.value = false
                }) {
                    Text(text = "Refuse")
                }
            }
        }
    )
}

/**
 * Dialog to inform the player that he/she has to wait for the other player decision
 */
@Composable
fun WaitingForTheOtherPlayerDecisionDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Waiting for the other player decision ... ") },
        text = {},
        buttons = {}
    )
}

/**
 * Dialog to inform the player that the trade is done
 * @param result The result of the trade (true if the trade is successful, false otherwise)
 * @param openDialog A mutable state to open and close the dialog.
 */
@Composable
fun TheTradeIsDoneDialog(result: Boolean, openDialog: MutableState<Boolean>) {
    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        title = {
            if (result)
                Text(text = "The trade is done !")
            else
                Text(text = "One Player has cancel the trade !")
        },
        text = {},
        buttons = {
            Button(onClick = {
                openDialog.value = false
            }) {
                Text(text = "Ok")
            }
        }
    )
}