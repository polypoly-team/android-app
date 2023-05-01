package com.github.polypoly.app.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.github.polypoly.app.BuildConfig
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.ui.game.addMarkerTo
import com.github.polypoly.app.ui.game.initLocationOverlay
import com.github.polypoly.app.ui.game.initMapView
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

object MapUI {
    // store the map view for testing purposes
    lateinit var mapView: MapView private set

    /**
     * Initialize the map view that sits beneath the UI components.
     */
    @Composable
    fun MapView() {
        AndroidView(
            factory = { context ->
                Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
                val mapView = initMapView(context)
                for (zone in LocationPropertyRepository.getZones())
                    for (location in zone.locationProperties) {
                        val marker =
                            addMarkerTo(mapView, location.position(), location.name, zone.color)
                        GameActivity.gameViewModel.markerToLocationProperty[marker] = location
                    }
                val currentLocationOverlay = initLocationOverlay(mapView)
                mapView.overlays.add(currentLocationOverlay)
                this.mapView = mapView
                mapView
            }, modifier = Modifier.testTag("map"))
    }
}