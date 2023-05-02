package com.github.polypoly.app.ui.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.github.polypoly.app.BuildConfig
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.game.location.LocationPropertyRepository.getZones
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.utils.global.GlobalInstances
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * Activity for displaying the map used in the game.
 */
class GameActivity : ComponentActivity() {
    // store the map view for testing purposes
    lateinit var mapView: MapView private set

    val game = Game.launchFromPendingGame(
        GameLobby(
            admin = GlobalInstances.currentUser
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapView()
                    PropertyInteractUIComponent()
                    RollDiceDialog()
                    RollDiceButton()
                    DistanceWalkedUIComponents()
                    Hud(
                        game.getPlayer(GlobalInstances.currentUser.id)!!,
                        game.players,
                        game.currentRound,
                        gameViewModel.interactableProperty.value?.name ?: ""
                    )
                }
            }
        }
    }

    /**
     * Initialize the map view that sits beneath the UI components.
     */
    @Composable
    fun MapView() {
        AndroidView(factory = { context ->
            Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
            val mapView = initMapView(context)
            for (zone in getZones())
                for (location in zone.locationProperties) {
                    val marker =
                        addMarkerTo(mapView, location.position(), location.name, zone.color)
                    gameViewModel.markerToLocationProperty[marker] = location
                }

            val currentLocationOverlay = initLocationOverlay(mapView)
            mapView.overlays.add(currentLocationOverlay)
            this.mapView = mapView
            mapView
        }, modifier = Modifier.testTag("map"))
    }

    companion object {
        val gameViewModel: GameViewModel = GameViewModel()

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
                val markerLocation = gameViewModel.markerToLocationProperty[marker]!!
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
