package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.TradeRequest
import com.github.polypoly.app.models.game.GameViewModel
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.Shapes

/**
 * Dialog to that propose a trade to the player
 * @param trade The trade to propose
 * @param openDialog A mutable state to open and close the dialog.
 * @param gameModel The game view model
 */
@Composable
fun ProposeTradeDialog(trade: TradeRequest, openDialog: MutableState<Boolean>, gameModel: GameViewModel) {
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
                gameModel.updateTradeRequest(trade)
                openDialogChooseLocation.value = false
            }
        }
        if(openDialog.value) {
            PropositionTradeDialog(trade, openDialog, gameModel, openDialogChooseLocation)
        }
    }
}

/**
 * Dialog to know if the player accept the trade
 * @param trade The trade to accept
 * @param openDialog A mutable state to open and close the dialog.
 * @param gameModel The game view model
 * @param openDialogChooseLocation A mutable state to open and close the dialog to choose a location
 */
@Composable
fun PropositionTradeDialog(trade: TradeRequest, openDialog: MutableState<Boolean>, gameModel: GameViewModel,
                           openDialogChooseLocation: MutableState<Boolean>){
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
                        // test if you can accept the trade, i.e. if you have a location to trade
                        if(trade.playerReceiver.getOwnedLocations().isNotEmpty()) {
                            openDialogChooseLocation.value = true
                        } else {
                            trade.currentPlayerReceiverAcceptation = false
                            gameModel.updateTradeRequest(trade)
                        }
                        openDialog.value = false
                    }
                ) {
                    Text(text = "Yes")
                }
                Spacer(modifier = Modifier.width(Padding.medium))
                Button(onClick = {
                    trade.currentPlayerReceiverAcceptation = false
                    gameModel.updateTradeRequest(trade)
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
            Row (
                modifier = Modifier
                    .padding(Padding.medium)
                    .fillMaxWidth()
            ) {
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
                Spacer(modifier = Modifier.width(Padding.medium))
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
 * @param gameModel The game model
 * @param trade The trade that has been done
 */
@Composable
fun TheTradeIsDoneDialog(result: Boolean, openDialog: MutableState<Boolean>?, gameModel: GameViewModel,
                         trade: TradeRequest) {
    AlertDialog(
        modifier = Modifier.testTag("the_trade_is_done_dialog"),
        onDismissRequest = {
            openDialog?.value = false
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
                openDialog?.value = false
                gameModel.closeTradeRequest(trade)
            }) {
                Text(text = "Ok")
            }
        }
    )
}

/**
 * Dialog to ask the player if he/she wants to trade with the other player
 * @param openOtherPlayerInfo A mutable state to open and close the dialog.
 * @param openLocationsDialog A mutable state to open and close the dialog to choose a location
 */
@Composable
fun AskingForATrade(openOtherPlayerInfo: MutableState<Boolean>, openLocationsDialog: MutableState<Boolean>,
                player: Player
) {
    Dialog(
        onDismissRequest = { openOtherPlayerInfo.value = false },
    ) {
        Surface(
            color = MaterialTheme.colors.background,
            shape = Shapes.medium,
            modifier = Modifier
                .padding(Padding.medium)
                .fillMaxWidth()
                .testTag("asking_for_a_trade_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(Padding.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Do you want to trade with this player?")
                Spacer(modifier = Modifier.height(Padding.medium))
                Button(
                    onClick = {
                        if(player.getOwnedLocations().isNotEmpty()) {
                            openLocationsDialog.value = true
                        }
                        openOtherPlayerInfo.value = false
                    },
                    modifier = Modifier.testTag("trade_button")
                ) {
                    Text(text = "Trade")
                }
            }
        }
    }
}