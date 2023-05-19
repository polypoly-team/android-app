package com.github.polypoly.app.ui.game

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.models.game.GameViewModel
import com.github.polypoly.app.ui.map.MapUI
import com.github.polypoly.app.ui.map.MapViewModel
import com.github.polypoly.app.ui.theme.PolypolyTheme
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * Activity for displaying the map used in the game.
 */
class GameActivity : ComponentActivity() {

    private val gameModel: GameViewModel by viewModels { GameViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameActivityContent() }
    }


    @Composable
    fun GameActivityContent() {
        val player = gameModel.getPlayerData().observeAsState().value
        mapViewModel.currentPlayer = player
        val game = gameModel.getGameData().observeAsState().value
        val gameTurn = gameModel.getRoundTurnData().observeAsState().value
        val gameEnded = gameModel.getGameFinishedData().observeAsState().value
        val trade = gameModel.getTradeRequestData().observeAsState().value

        if (game != null && gameTurn != null && gameEnded != null) {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
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
                        mapViewModel.interactableProperty.value?.name ?: "",
                        gameModel
                    )
                    GameEndedLabel(gameEnded)
                    if (trade != null) {
                    }
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

    // =================================== PREVIEW ==============
    @Preview(
        name = "Light Mode"
    )
    @Preview(
        name = "Dark Mode",
        uiMode = Configuration.UI_MODE_NIGHT_YES
    )
    @Composable
    fun GameActivityPreview() {
        setContent { GameActivityContent() }
    }

    companion object {
        val mapViewModel: MapViewModel = MapViewModel()

        // flag to show the building info dialog
        val interactingWithProperty = mutableStateOf(false)

        //used to determine if the player is close enough to a location to interact with it
        private const val MAX_INTERACT_DISTANCE = 10.0 // meters

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
            if (myLocation.distanceToAsDouble(closestLocationProperty!!.position()) > MAX_INTERACT_DISTANCE)
                closestLocationProperty = null

            return closestLocationProperty
        }
    }
}
