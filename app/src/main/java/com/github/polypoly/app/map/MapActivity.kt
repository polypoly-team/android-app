package com.github.polypoly.app.map

import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.polypoly.app.BuildConfig
import com.github.polypoly.app.map.LocalizationRepository.getLocalizations
import com.github.polypoly.app.ui.theme.PolypolyTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.random.Random

class MapActivity : ComponentActivity() {

    private val mapViewModel: MapViewModel = MapViewModel()
    private val initialPosition = GeoPoint(46.518726, 6.566613)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapView()
                    DistanceWalkedUIComponents()
                }
            }
        }
    }

    @Composable
    fun MapView() {
        AndroidView(factory = { context ->
            Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

            val mapView = initMapView(context, initialPosition)

            for (localization in getLocalizations())
                addMarkerTo(mapView, localization.position, localization.name)

            val campusTileSource = CampusTileSource(Random.nextInt(2), 0)
            val tileProvider = MapTileProviderBasic(applicationContext, campusTileSource)
            val tilesOverlay = TilesOverlay(tileProvider, applicationContext)
            mapView.overlays.add(tilesOverlay)

            val locationOverlay = initLocationOverlay(mapView)
            mapView.overlays.add(locationOverlay)

            mapView
        })
    }

    @Composable
    fun DistanceWalkedUIComponents() {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                shape = CircleShape,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-80).dp)
                    .testTag("resetButton"),
                onClick = { mapViewModel.resetDistanceWalked() }
            ) {
                Text(text = "Reset")
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White)
                    .border(1.dp, Color.Black)
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = "Distance walked: ${formattedDistance(mapViewModel.distanceWalked.value)}",
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    private fun initMapView(context: Context, startPosition: GeoPoint): MapView {
        val mapView = MapView(context)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.controller.setZoom(19.5)
        mapView.controller.setCenter(startPosition)
        return mapView
    }

    private fun addMarkerTo(mapView: MapView, position: GeoPoint, title: String) {
        val marker = Marker(mapView)
        marker.position = position
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.title = title
        mapView.overlays.add(marker)
    }

    private fun initLocationOverlay(mapView: MapView): MyLocationNewOverlay {
        val locationProvider = GpsMyLocationProvider(this)
        var lastLocation = Location("")
        val locationOverlay = object : MyLocationNewOverlay(locationProvider, mapView) {
            override fun onLocationChanged(location: Location?, provider: IMyLocationProvider?) {
                super.onLocationChanged(location, provider)
                if (location != null)
                    updateAllDistances(mapView, GeoPoint(location))
                val distance = lastLocation.distanceTo(location!!)
                mapViewModel.updateDistanceWalked(distance)
                lastLocation = location
            }
        }
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()

        locationOverlay.runOnFirstFix {
            runOnUiThread {
                updateAllDistances(mapView, locationOverlay.myLocation)
                mapView.controller.setCenter(locationOverlay.myLocation)
                mapView.controller.animateTo(locationOverlay.myLocation)
            }
        }
        return locationOverlay
    }

    private fun markersOf(mapView: MapView): List<Marker> {
        return mapView.overlays.filterIsInstance<Marker>()
    }

    private fun updateAllDistances(mapView: MapView, myLocation: GeoPoint) {
        fun updateDistance(marker: Marker, myLocation: GeoPoint) {
            val distance = myLocation.distanceToAsDouble(marker.position).toFloat()
            marker.snippet = "Distance: ${formattedDistance(distance)}"
        }
        for (marker in markersOf(mapView))
            updateDistance(marker, myLocation)
    }

    private fun formattedDistance(distance: Float): String {
        return if (distance < 1000) "${"%.1f".format(distance)}m"
        else "${"%.1f".format(distance / 1000)}km"
    }


    class CampusTileSource(private val serverId: Int, private val floorId: Int) :
        OnlineTileSourceBase("EPFLCampusTileSource", 0, 18, 256, ".png", arrayOf()) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            return "https://plan-epfl-tiles$serverId.epfl.ch/1.0.0/batiments/default/20160712/$floorId/3857/${
                MapTileIndex.getZoom(
                    pMapTileIndex
                )
            }/${MapTileIndex.getY(pMapTileIndex)}/${MapTileIndex.getX(pMapTileIndex)}.png"
        }
    }

    @Preview
    @Composable
    fun MapViewPreview() {
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                MapView()
            }
        }
    }
}
