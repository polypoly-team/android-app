package com.github.polypoly.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.TilesOverlay
import kotlin.random.Random

class MapActivity : ComponentActivity() {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val donutPosition = GeoPoint(46.518726, 6.566613)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapView()
                    LocationLogic()
                }
            }
        }
    }

    @Composable
    fun MapView() {
        AndroidView(factory = { context ->
            // Set a custom user agent string for OsmDroid
            Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

            val mapView = initMapView(context, donutPosition)

            addMarkerTo(mapView, donutPosition, "Donut")

            val campusTileSource = CampusTileSource(0)
            val tileProvider = MapTileProviderBasic(applicationContext, campusTileSource)
            val tilesOverlay = TilesOverlay(tileProvider, applicationContext)
            mapView.overlays.add(tilesOverlay)

            val locationOverlay = initLocationOverlay(mapView)
            mapView.overlays.add(locationOverlay)

            mapView
        })
    }


    @Composable
    fun LocationLogic() {
        val mContext = LocalContext.current
        val locationClient = remember { getFusedLocationProviderClient(mContext) }

        val locationRequest = remember {
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100).build()
        }

        var lastLocation by remember { mutableStateOf(Location("")) }

        if (permissions
                .all {
                    ContextCompat.checkSelfPermission(
                        this@MapActivity,
                        it
                    ) != PackageManager.PERMISSION_GRANTED
                }
        )
            ActivityCompat.requestPermissions(this@MapActivity, permissions, 1)

        var distanceWalked by remember { mutableStateOf(0f) }
        locationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                distanceWalked += lastLocation.distanceTo(locationResult.lastLocation!!)
                lastLocation = locationResult.lastLocation!!
            }
        }, mainLooper)

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                shape = CircleShape,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-80).dp)
                    .testTag("resetButton"),
                onClick = { distanceWalked = 0f }
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
                    text = "Distance walked: ${formattedDistance(distanceWalked)}",
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

        // Set initial zoom and center point
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
        val locationOverlay = object : MyLocationNewOverlay(locationProvider, mapView) {
            override fun onLocationChanged(location: Location?, provider: IMyLocationProvider?) {
                super.onLocationChanged(location, provider)
                if (location != null)
                    updateAllDistances(mapView, GeoPoint(location))
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


    class CampusTileSource(private val floorId: Int) : OnlineTileSourceBase("EPFLCampusTileSource", 0, 18, 256, ".png", arrayOf()) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            return "https://plan-epfl-tiles${Random.nextInt(3)}.epfl.ch/1.0.0/batiments/default/20160712/$floorId/3857/${MapTileIndex.getZoom(pMapTileIndex)}/${MapTileIndex.getY(pMapTileIndex)}/${MapTileIndex.getX(pMapTileIndex)}.png"
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
