package com.github.polypoly.app.ui.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.TradeRequest
import com.github.polypoly.app.models.game.GameViewModel
import com.github.polypoly.app.ui.map.MapUI
import com.github.polypoly.app.ui.map.MapViewModel
import com.github.polypoly.app.ui.theme.PolypolyTheme

/**
 * Activity for displaying the map used in the game.
 */
class GameActivity : ComponentActivity() {

    val gameModel: GameViewModel by viewModels { GameViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameActivityContent() }
    }

    /**
     * Component for the entire game activity content
     */
    @Composable
    fun GameActivityContent() {
        val player = gameModel.getPlayerData().observeAsState().value
        mapViewModel.currentPlayer = player
        val game = gameModel.getGameData().observeAsState().value
        val gameTurn = gameModel.getRoundTurnData().observeAsState().value
        val gameEnded = gameModel.getGameFinishedData().observeAsState().value
        val trade = gameModel.getTradeRequestData().observeAsState().value

        if (player != null && game != null && gameTurn != null && gameEnded != null) {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapUI.MapView(mapViewModel, gameModel)
                    PropertyInteractUIComponent(gameModel, mapViewModel)
                    DiceRollUI(gameModel, mapViewModel)
                    NextTurnButton(gameEnded)
                    DistanceWalkedUIComponents(mapViewModel)
                    Hud(
                        player,
                        gameModel,
                        mapViewModel,
                        game.players,
                        gameTurn,
                        mapViewModel.interactableProperty.value?.name ?: "EPFL"
                    )
                    GameEndedLabel(gameEnded)

                    // pop-ups for trades:
                    DialogsForTrade(trade, player)
                }
            }
        }
    }

    @Composable
    private fun NextTurnButton(gameEnded: Boolean) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-30).dp)
                    .testTag("next_turn_button"),
                onClick = {
                    if (!gameEnded) {
                        gameModel.nextTurn()
                    }
                },
                shape = CircleShape
            ) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Next turn")
            }
        }
    }

    /**
     * This function is used to display the pop-ups for the trades
     * @param trade the trade request
     * @param player the current player
     */
    @Composable
    fun DialogsForTrade(trade: TradeRequest?, player: Player) {
        // Pop up when a player ask for a trade
        if (trade != null && trade.playerReceiver.user.id == player.user.id
            && trade.locationReceived == null) {
            val tradeDialog = remember {
                mutableStateOf(true)
            }
            ProposeTradeDialog(trade, tradeDialog, gameModel)
        }

        // Pop up when the player has to accept or refuse a trade
        if(trade?.locationReceived != null
            && ((trade.playerReceiver.user.id == player.user.id && trade.currentPlayerReceiverAcceptation == null) ||
                    (trade.playerApplicant.user.id == player.user.id && trade.currentPlayerApplicantAcceptation == null))) {
            AcceptTradeDialog(trade, trade.playerApplicant.user.id == player.user.id, gameModel)
        }

        //Pop up when the player has to wait for the other player decision
        if(trade?.locationReceived != null
            && ((trade.playerReceiver.user.id == player.user.id && trade.currentPlayerReceiverAcceptation != null
                    && trade.currentPlayerApplicantAcceptation == null) ||
                    (trade.playerApplicant.user.id == player.user.id && trade.currentPlayerApplicantAcceptation != null
                            && trade.currentPlayerReceiverAcceptation == null))) {
            WaitingForTheOtherPlayerDecisionDialog()
        }

        //Pop up when the trade is cancelled
        if(trade != null
            && (trade.currentPlayerApplicantAcceptation == false || trade.currentPlayerReceiverAcceptation == false)) {
            val tradeDialog = remember {
                mutableStateOf(true)
            }
            if(tradeDialog.value) {
                TheTradeIsDoneDialog(false, tradeDialog, gameModel, trade)
            }
        }

        //Pop up when the trade is accepted
        if(trade != null
            && (trade.currentPlayerApplicantAcceptation == true && trade.currentPlayerReceiverAcceptation == true)) {
            val tradeDialog = remember {
                mutableStateOf(true)
            }
            if(tradeDialog.value) {
                TheTradeIsDoneDialog(true, tradeDialog, gameModel, trade)
            }
        }
    }

    @Composable
    private fun GameEndedLabel(gameEnded: Boolean) {
        if (gameEnded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.7f))
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "The game ended !!",
                    fontWeight = FontWeight(1000)
                )
            }
        }
    }

    companion object {
        val mapViewModel: MapViewModel = MapViewModel()
    }
}
