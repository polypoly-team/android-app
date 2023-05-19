package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.TradeRequest
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.models.game.GameViewModel
import com.github.polypoly.app.ui.theme.Padding

/**
 * Dialog to that propose a trade to the player
 * @param playerApplicant The player that propose the trade
 * @param playerReceiver The player that receive the trade
 * @param openDialog A mutable state to open and close the dialog.
 */
@Composable
fun ProposeTradeDialog(trade: TradeRequest, openDialog: MutableState<Boolean>) {
    val openDialogChooseLocation = remember {
        mutableStateOf(false)
    }
    Surface(
        color = Color.Transparent,
    ) {
        if (openDialogChooseLocation.value) {
            LocationsDialog(
                "Choose a location to trade2",
                openDialogChooseLocation,
                trade.playerReceiver.getOwnedLocations()
            ) {
                trade.locationReceived = it
                openDialogChooseLocation.value = false
            }
        }
        if(openDialog.value) {
            AlertDialog(
                modifier = Modifier.testTag("propose_trade_dialog"),
                onDismissRequest = { openDialog.value = false },
                title = { Text(text = "Player ${trade.playerApplicant.user.name} propose you a trade !") },
                text = { Text(text = "Do you accept ?") },
                buttons = {
                    Row(
                        modifier = Modifier
                            .padding(Padding.medium)
                            .fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                openDialogChooseLocation.value = true
                                openDialog.value = false
                            }
                        ) {
                            Text(text = "Yes")
                        }
                        Spacer(modifier = Modifier.width(Padding.medium))
                        Button(onClick = {
                            openDialog.value = false
                        }) {
                            Text(text = "No")
                        }
                    }
                }
            )
        }
    }
}

/**
 * Dialog to know if the player accept the trade
 * @param trade The trade to accept
 * @param isApplicant A boolean to know if the player is the applicant or the receiver
 */
@Composable
fun AcceptTradeDialog(trade: TradeRequest, isApplicant: Boolean, gameModel: GameViewModel) {
    AlertDialog(
        modifier = Modifier.testTag("accept_trade_dialog"),
        onDismissRequest = {},
        title = { Text(text = "Do you want to trade ${trade.locationGiven?.locationProperty?.name} against" +
                " ${trade.locationReceived?.locationProperty?.name} with ${trade.playerApplicant.user.name}?") },
        text = {},
        buttons = {
            Row {
                Button(onClick = {
                    if(isApplicant) {
                        trade.currentPlayerApplicantAcceptation = true
                        gameModel.updateTradeRequest(trade)
                    } else {
                        trade.currentPlayerReceiverAcceptation = true
                        gameModel.updateTradeRequest(trade)
                    }
                }) {
                    Text(text = "Accept")
                }
                Button(onClick = {
                    if(isApplicant) {
                        trade.currentPlayerApplicantAcceptation = false
                        gameModel.updateTradeRequest(trade)
                    } else {
                        trade.currentPlayerReceiverAcceptation = false
                        gameModel.updateTradeRequest(trade)
                    }
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
        modifier = Modifier.testTag("waiting_for_the_other_player_decision_dialog"),
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
        modifier = Modifier.testTag("the_trade_is_done_dialog"),
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