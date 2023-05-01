package com.github.polypoly.app.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.github.polypoly.app.BuildConfig
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

object MapUI {

    // store the map view for testing purposes
    lateinit var mapView: MapView private set

    /**
     * Initialize the map view that sits beneath the UI components.
     * @param mapViewModel The view model for the map.
     */
    @Composable
    fun MapView(mapViewModel: MapViewModel) {
        AndroidView(
            factory = { context ->
                Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
                val mapView = initMapView(context)
                for (zone in LocationPropertyRepository.getZones())
                    for (location in zone.locationProperties) {
                        val marker =
                            addMarkerTo(mapView, location.position(), location.name, zone.color,
                                mapViewModel)
                        mapViewModel.markerToLocationProperty[marker] = location
                    }
                val currentLocationOverlay = initLocationOverlay(mapView, mapViewModel)
                mapView.overlays.add(currentLocationOverlay)
                this.mapView = mapView
                mapView
            }, modifier = Modifier.testTag("map"))
    }
}