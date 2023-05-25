package com.github.polypoly.app.ui.map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.github.polypoly.app.BuildConfig
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.base.game.location.PropertyLevel
import com.github.polypoly.app.models.game.GameViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

object MapUI {
    /**
     * Initialize the map view that sits beneath the UI components.
     * @param mapViewModel The view model for the map.
     */
    @Composable
    fun MapView(mapViewModel: MapViewModel, gameViewModel: GameViewModel?) {
        val locationsOwned = gameViewModel?.locationsOwnedData?.observeAsState()?.value ?: listOf()
        AndroidView(
            factory = { context ->
                createMapView(context, mapViewModel, gameViewModel, locationsOwned)
            }, modifier = Modifier.testTag("map")
        )
    }

    private fun createMapView(
        context: Context,
        mapViewModel: MapViewModel,
        gameViewModel: GameViewModel?,
        locationsOwned: List<InGameLocation>
    ): MapView {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        val mapView = initMapView(context)
        if (gameViewModel != null) {
            val player = gameViewModel.getPlayerData().value
            for (location in gameViewModel.getGameData().value?.allLocations ?: listOf()){
                val zone = LocationPropertyRepository.getZones().find { it.locationProperties.contains(location) }
                val owned = locationsOwned.find { inGame -> inGame.locationProperty == location }
                addMarkerTo(
                    mapView,
                    location,
                    zone!!.color,
                    mapViewModel,
                    gameViewModel,
                    owned?.level ?: PropertyLevel.LEVEL_0,
                    owned?.owner == player
                )
            }
        } else {
            for (zone in LocationPropertyRepository.getZones()) {
                for (location in zone.locationProperties)
                    addMarkerTo(
                        mapView,
                        location,
                        zone.color,
                        mapViewModel,
                        gameViewModel,
                        PropertyLevel.LEVEL_0
                    )
            }
        }

        val currentLocationOverlay = initLocationOverlay(mapView, mapViewModel, gameViewModel)
        mapView.overlays.add(currentLocationOverlay)
        return mapView
    }
}