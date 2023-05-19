package com.github.polypoly.app.ui.game

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.game.service.TaxService
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.models.game.GameViewModel
import com.github.polypoly.app.ui.map.MapUI
import com.github.polypoly.app.ui.map.MapViewModel
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.utils.Constants
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import timber.log.Timber

/**
 * Activity for displaying the map used in the game.
 */
class GameActivity : ComponentActivity() {
    private val gameModel: GameViewModel by viewModels { GameViewModel.Factory }
    private lateinit var taxService: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameActivityContent() }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the tax service if the game mode is landlord.
        if (GameRepository.game?.rules?.gameMode == GameMode.LANDLORD) {
            stopTaxService()
        }
    }

    @Composable
    fun GameActivityContent() {
        val player = gameModel.getPlayerData().observeAsState().value
        mapViewModel.currentPlayer = player
        val game = gameModel.getGameData().observeAsState().value
        val gameTurn = gameModel.getRoundTurnData().observeAsState().value
        val gameEnded = gameModel.getGameFinishedData().observeAsState().value

        if (game != null && gameTurn != null && gameEnded != null) {
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
                        BackgroundLocationPermissionHandler { startTaxService() }
                    }
                    MapUI.MapView(mapViewModel, interactingWithProperty)
                    PropertyInteractUIComponent()
                    if (player?.playerState!!.value == PlayerState.ROLLING_DICE) {
                        RollDiceDialog()
                        RollDiceButton()
                    }
                    NextTurnButton(gameEnded)
                    DistanceWalkedUIComponents()
                    Hud(
                        player,
                        game.players,
                        gameTurn,
                        mapViewModel.interactableProperty.value?.name ?: ""
                    )
                    GameEndedLabel(gameEnded)
                }
            }
        }
    }

    @Composable
    fun NextTurnButton(gameEnded: Boolean) {
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

    @Composable
    fun GameEndedLabel(gameEnded: Boolean) {
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

        // flag to show the building info dialog
        val interactingWithProperty = mutableStateOf(false)

        /**
         * Updates the distance of all markers and returns the closest one.
         *
         * @return the closest location or null if there are no locations close enough to the player
         */
        fun updateAllDistancesAndFindClosest(
            mapView: MapView,
            myLocation: GeoPoint
        ): LocationProperty? {
            fun markersOf(mapView: MapView): List<Marker> {
                return mapView.overlays.filterIsInstance<Marker>()
            }

            var closestLocationProperty = null as LocationProperty?
            for (marker in markersOf(mapView)) {
                val markerLocation = mapViewModel.markerToLocationProperty[marker]!!
                if (closestLocationProperty == null ||
                    myLocation.distanceToAsDouble(markerLocation.position())
                    < myLocation.distanceToAsDouble(closestLocationProperty.position())
                ) {
                    closestLocationProperty = markerLocation
                }
            }
            if (myLocation.distanceToAsDouble(closestLocationProperty!!.position()) > Constants.MAX_INTERACT_DISTANCE)
                closestLocationProperty = null

            return closestLocationProperty
        }
    }

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

    private fun startTaxService() {
        taxService = Intent(this, TaxService::class.java)
        startForegroundService(taxService)
    }

    private fun stopTaxService() {
        stopService(taxService)
    }

}
