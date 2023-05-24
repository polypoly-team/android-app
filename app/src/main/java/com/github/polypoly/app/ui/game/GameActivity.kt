package com.github.polypoly.app.ui.game

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.TradeRequest
import com.github.polypoly.app.base.game.service.TaxService
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.viewmodels.game.GameViewModel
import com.github.polypoly.app.ui.map.MapUI
import com.github.polypoly.app.ui.map.MapViewModel
import com.github.polypoly.app.ui.theme.PolypolyTheme

/**
 * Activity for displaying the map used in the game.
 * @property gameModel The view model for the game
 */
class GameActivity : ComponentActivity() {
    val gameModel: GameViewModel by viewModels { GameViewModel.Factory }
    private lateinit var taxService: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameActivityContent() }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the tax service if the game mode is landlord.
        if (GameRepository.game?.rules?.gameMode == GameMode.LANDLORD) {
            // TODO : Uncomment this when the bug of BackgroundLocationPermissionHandler is fixed
            //stopTaxService()
        }
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
                    /**
                     * If the game mode is landlord, start the tax service to passively tax players
                     * if they are in a location property that is owned by another player.
                     */
                    if (GameRepository.game?.rules?.gameMode == GameMode.LANDLORD) {
                        // TODO : this code causes an error in the landlord mode that has existed for several PRs
                        //BackgroundLocationPermissionHandler { startTaxService() }
                    }
                    MapUI.MapView(mapViewModel, gameModel)
                    PropertyInteractUIComponent(gameModel, mapViewModel)
                    DiceRollUI(gameModel, mapViewModel)
                    DistanceWalkedUIComponents(mapViewModel)
                    Hud(
                        player,
                        gameModel,
                        mapViewModel,
                        game.players,
                        gameTurn,
                        mapViewModel.interactableProperty.value?.name ?: "EPFL",
                        gameModel
                    )
                    GameEndedLabel(gameEnded)

                    // pop-ups for trades:
                    DialogsForTrade(trade, player)
                }
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
        if(trade == null) return

        // Pop up when a player ask for a trade
        if (trade.isReceiver(player) && trade.locationReceived == null) {
            ProposeTradeDialog(trade, gameModel)
        }

        // Pop up when the player has to accept or refuse a trade
        if(trade.locationReceived != null
            && ((trade.isApplicant(player) && trade.currentPlayerReceiverAcceptance == null) ||
                    (trade.isReceiver(player) && trade.currentPlayerApplicantAcceptance == null))) {
            AcceptTradeDialog(trade, gameModel)
        }

        //Pop up when the player has to wait for the other player decision
        if(trade.locationReceived != null
            && ((trade.isReceiver(player) && trade.currentPlayerReceiverAcceptance != null
                    && trade.currentPlayerApplicantAcceptance == null) ||
                    (trade.isApplicant(player) && trade.currentPlayerApplicantAcceptance != null
                            && trade.currentPlayerReceiverAcceptance == null))) {
            WaitingForTheOtherPlayerDecisionDialog()
        }

        //Pop up when the trade is cancelled
        if(trade.isAccepted() == false) {
            TheTradeIsDoneDialog(false, gameModel, trade, player)
        }

        //Pop up when the trade is accepted
        if(trade.isAccepted() == true) {
            TheTradeIsDoneDialog(true, gameModel, trade, player)
        }
    }

    /**
     * A Pop-up that notify the player that the game has ended.
     * @param gameEnded true if the game has ended, false otherwise
     */
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

    /**
     * Handler for the background location permission.
     */
    @Composable
    fun BackgroundLocationPermissionHandler(callback: () -> Unit) {
        var acknowledgePermissionDenial by remember { mutableStateOf(false) }
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                callback()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Background location permission is required to passively tax players if they are in a location property that is owned by another player.",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("background_location_permission_rationale")
                    )
                    Button(
                        onClick = {
                            requestBackgroundLocationPermission(grantCallback = callback, denyCallback = {
                                acknowledgePermissionDenial = true
                            })
                        }
                    ) {
                        Text(text = "OK")
                    }
                    // TODO: Add a button to decline permission and disable taxing for everyone.
                }
            }
            else -> {
                requestBackgroundLocationPermission(grantCallback = callback, denyCallback = {
                    acknowledgePermissionDenial = true
                })
            }
        }

        // TODO: Disable taxing for everyone if one user doesn't give permission to make it fair.
        if (acknowledgePermissionDenial) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Background location permission was denied, the game will continue with taxing disabled.",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("background_location_permission_denied")
                )
                Button(
                    onClick = {
                        acknowledgePermissionDenial = false
                    }
                ) {
                    Text(text = "OK")
                }
            }
        }
    }

    /**
     * This function is used to request the background location permission
     * @param grantCallback the callback if the permission is granted
     * @param denyCallback the callback if the permission is denied
     */
    private fun requestBackgroundLocationPermission(grantCallback: () -> Unit, denyCallback: () -> Unit) {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    grantCallback()
                }
                else {
                    denyCallback()
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    /**
     * This function is used to start the tax service
     */
    private fun startTaxService() {
        taxService = Intent(this, TaxService::class.java)
        startForegroundService(taxService)
    }

    /**
     * This function is used to stop the tax service
     */
    private fun stopTaxService() {
        stopService(taxService)
    }

    companion object {
        val mapViewModel: MapViewModel = MapViewModel()
    }

}