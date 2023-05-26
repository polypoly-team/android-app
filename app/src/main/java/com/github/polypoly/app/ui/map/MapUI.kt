package com.github.polypoly.app.ui.map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.github.polypoly.app.BuildConfig
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationProperty
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
        val locationsOwned = gameViewModel?.locationsOwnedData?.observeAsState()?.value ?: listOf()
        val goingToLocation = mapViewModel.goingToLocationPropertyData.observeAsState().value

        var oldLocations = remember { listOf<InGameLocation>() }
        var oldGoingToLocation: LocationProperty? = remember { null }

        AndroidView(
            factory = { context ->
                val mapView = createMapView(context)

                addGameMapOverlay(mapView, mapViewModel, gameViewModel, locationsOwned, goingToLocation)

                val locationOverlay = initLocationOverlay(mapView, mapViewModel, gameViewModel)
                mapView.overlays.add(locationOverlay)

                mapView
            }, modifier = Modifier.testTag("map"), update = { mapView ->
                if (oldGoingToLocation != goingToLocation || locationsOwned != oldLocations) {
                    addGameMapOverlay(mapView, mapViewModel, gameViewModel, locationsOwned, goingToLocation)
                    oldGoingToLocation = goingToLocation
                    oldLocations = locationsOwned
                }
            }
        )
    }

    private fun createMapView(context: Context): MapView {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        return initMapView(context)
    }

    private fun addGameMapOverlay(
        mapView: MapView,
        mapViewModel: MapViewModel,
        gameViewModel: GameViewModel?,
        locationsOwned: List<InGameLocation>,
        goingToLocation: LocationProperty?)
    {
        val allLocations = when(gameViewModel == null) {
            true -> LocationPropertyRepository.getZones().flatMap { zone -> zone.locationProperties }
            false -> gameViewModel.getGameData().value?.getLocations() ?: listOf()
        }

        var markerId = 0
        for (location in allLocations) {
            val zone = LocationPropertyRepository.getZones().find { it.locationProperties.contains(location) }
            val owned = locationsOwned.find { inGame -> inGame.locationProperty == location }
            val isOwned = owned?.owner != null && owned.owner == gameViewModel?.getPlayerData()?.value

            val marker = addMarkerTo(
                mapView,
                location,
                zone!!.color,
                mapViewModel,
                gameViewModel,
                owned?.level ?: PropertyLevel.LEVEL_0,
                isOwned,
                location.name == goingToLocation?.name
            )

            if (mapView.overlays.size > markerId) {
                mapView.overlays[markerId] = marker
            } else {
                mapView.overlays.add(marker)
            }
            markerId += 1
        }
    }
}