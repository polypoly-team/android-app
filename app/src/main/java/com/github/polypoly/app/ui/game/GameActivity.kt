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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.polypoly.app.BuildConfig
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.game.location.LocationPropertyRepository.getZones
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.ui.theme.PolypolyTheme
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

    // mock current Player
    private val currentPlayer = Player(
        user = User(
            id = 4572,
            name = "User test 1",
            bio = "",
            skin = Skin.default(),
            stats = Stats(0, 0, 0, 0, 0),
            trophiesWon = listOf(),
            trophiesDisplay = mutableListOf(),
        ),
        balance = 420,
        ownedLocations = listOf(),
        roundLost = null,
    )

    // mock List of Players
    private val players = listOf(
        Player(
            user = User(
                id = 4573,
                name = "User test 2",
                bio = "",
                skin = Skin.default(),
                stats = Stats(0, 0, 0, 0, 0),
                trophiesWon = listOf(),
                trophiesDisplay = mutableListOf(),
            ),
            balance = 32,
            ownedLocations = listOf(),
            roundLost = null,
        ), Player(
            user = User(
                id = 4574,
                name = "User test 3",
                bio = "",
                skin = Skin.default(),
                stats = Stats(0, 0, 0, 0, 0),
                trophiesWon = listOf(),
                trophiesDisplay = mutableListOf(),
            ),
            balance = 56,
            ownedLocations = listOf(),
            roundLost = null,
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
                        currentPlayer,
                        players,
                        16,
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

    // ============================== PREVIEW ==============================
    @Preview
    @Composable
    fun MapViewPreview() {
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
                    currentPlayer, players,
                    16,
                    gameViewModel.interactableProperty.value?.name ?: ""
                )
            }
        }
    }

    companion object {
        val gameViewModel: GameViewModel = GameViewModel()

        // flag to show the building info dialog
        val interactingWithProperty = mutableStateOf(false)

        //used to determine if the player is close enough to a location to interact with it
        private const val MAX_INTERACT_DISTANCE = 10.0 // meters

        fun formattedDistance(distance: Float): String {
            return if (distance < 1000) "${"%.1f".format(distance)}m"
            else "${"%.1f".format(distance / 1000)}km"
        }

        /**
         * Updates the distance of all markers and returns the closest one.
         *
         * @return the closest location or null if there are no locations close enough to the player
         */
        fun updateAllDistancesAndFindClosest(
            mapView: MapView,
            myLocation: GeoPoint
        ): LocationProperty? {
            fun updateDistance(marker: Marker, myLocation: GeoPoint) {
                val distance = myLocation.distanceToAsDouble(marker.position).toFloat()
                marker.snippet = "Distance: ${formattedDistance(distance)}"
            }

            fun markersOf(mapView: MapView): List<Marker> {
                return mapView.overlays.filterIsInstance<Marker>()
            }

            var closestLocationProperty = null as LocationProperty?
            for (marker in markersOf(mapView)) {
                updateDistance(marker, myLocation)
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
