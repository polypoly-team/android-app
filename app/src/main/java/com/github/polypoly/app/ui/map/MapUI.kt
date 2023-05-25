package com.github.polypoly.app.ui.map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.github.polypoly.app.BuildConfig
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.base.game.location.PropertyLevel
import com.github.polypoly.app.viewmodels.game.GameViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

object MapUI {
    /**
     * Initialize the map view that sits beneath the UI components.
     * @param mapViewModel The view model for the map.
     */
    @Composable
    fun MapView(mapViewModel: MapViewModel, gameViewModel: GameViewModel?) {
        val mapViewState = mapViewModel.mapViewState.value
        AndroidView(
            factory = { context ->
                if (gameViewModel != null)
                    mapViewModel.updateMapViewState(gameViewModel.getGameData().value!!.inGameLocations)
                val mapView = createMapView(context, mapViewModel, gameViewModel, mapViewState)
                mapView
            }, modifier = Modifier.testTag("map")
        )
    }

    private fun createMapView(
        context: Context,
        mapViewModel: MapViewModel,
        gameViewModel: GameViewModel?,
        mapViewState: MapViewState? = null
    ): MapView {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        val mapView = initMapView(context)
        if (mapViewState != null) {
            for (location in mapViewState.locations){
                val zone = LocationPropertyRepository.getZones().find { it.locationProperties.contains(location.locationProperty) }
                addMarkerTo(
                    mapView,
                    location.locationProperty,
                    zone!!.color,
                    mapViewModel,
                    gameViewModel,
                    location.level,
                    location.owner != null
                )
            }
        } else
            for (zone in LocationPropertyRepository.getZones())
                for (location in zone.locationProperties)
                    addMarkerTo(
                        mapView,
                        location,
                        zone.color,
                        mapViewModel,
                        gameViewModel,
                        PropertyLevel.LEVEL_0
                    )

        val currentLocationOverlay = initLocationOverlay(mapView, mapViewModel, gameViewModel)
        mapView.overlays.add(currentLocationOverlay)
        return mapView
    }
}