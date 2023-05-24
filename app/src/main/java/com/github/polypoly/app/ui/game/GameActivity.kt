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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.PlayerState
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
