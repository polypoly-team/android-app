package com.github.polypoly.app.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.compose.runtime.MutableState
import com.github.polypoly.app.R
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.ui.game.GameActivity.Companion.updateAllDistancesAndFindClosest
import com.github.polypoly.app.ui.game.PlayerState
import net.bytebuddy.dynamic.scaffold.TypeInitializer.None
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory.DEFAULT_TILE_SOURCE
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

private val INITIAL_POSITION = GeoPoint(46.518726, 6.566613)
private const val INITIAL_ZOOM = 18.0
private const val MARKER_SIDE_LENGTH = 100

/**
 * Tile source for EPFL campus map.
 * @param floorId the floor of the map to display
 */
class CampusTileSource(private val floorId: Int) :
    OnlineTileSourceBase("EPFLCampusTileSource", 0, 18, 256, ".png", arrayOf()) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        // The EPFL campus map is served from 3 different servers, so we randomly choose one
        val epflCampusServerCount = 3
        return "https://plan-epfl-tiles${Random.nextInt(epflCampusServerCount)}.epfl.ch/1.0.0/batiments/default/20160712/$floorId/3857/${
            MapTileIndex.getZoom(
                pMapTileIndex
            )
        }/${MapTileIndex.getY(pMapTileIndex)}/${MapTileIndex.getX(pMapTileIndex)}.png"
    }
}

/**
 * Initializes the map view with the given start position.
 * @param context the current context
 */
fun initMapView(context: Context): MapView {
    val mapView = MapView(context)
    mapView.setTileSource(DEFAULT_TILE_SOURCE)
    mapView.setMultiTouchControls(true)
    mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
    mapView.controller.setZoom(INITIAL_ZOOM)
    mapView.controller.setCenter(INITIAL_POSITION)
    val campusTileSource = CampusTileSource(0)
    val tileProvider = MapTileProviderBasic(context, campusTileSource)
    val tilesOverlay = TilesOverlay(tileProvider, context)
    mapView.overlays.add(tilesOverlay)
    return mapView
}

/**
 * Adds a marker to the given map view.
 * @param mapView the map view to add the marker to
 * @param position the position of the marker
 * @param title the title of the marker
 * @param zoneColor the color of the marker
 * @param mapViewModel the view model of the map
 * @return the marker that was added
 */
fun addMarkerTo(mapView: MapView, position: GeoPoint, title: String, zoneColor: Int,
                mapViewModel: MapViewModel, interactingWithProperty: MutableState<Boolean>
): Marker {
    fun buildMarkerIcon(context: Context, color: Int): Drawable {
        val markerIcon = BitmapFactory.decodeResource(context.resources, R.drawable.location_pin)
        val scaledBitmap = Bitmap.createScaledBitmap(
            markerIcon,
            MARKER_SIDE_LENGTH,
            MARKER_SIDE_LENGTH, true
        )
        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        val canvas = Canvas(scaledBitmap)
        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)
        return BitmapDrawable(context.resources, scaledBitmap)
    }

    val marker = Marker(mapView)
    marker.position = position
    marker.title = title
    marker.isDraggable = false
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
    marker.icon = buildMarkerIcon(mapView.context, zoneColor)
    marker.setOnMarkerClickListener { _, _ ->
        if (mapViewModel.currentPlayer?.playerState?.value == PlayerState.INTERACTING) {
            mapViewModel.selectedMarker = marker
            interactingWithProperty.value = true
            mapViewModel.currentPlayer?.playerState?.value = PlayerState.BETTING
        }
        true
    }
    mapView.overlays.add(marker)
    return marker
}

/**
 * Initializes the location overlay and sets the location listener.
 * @param mapView the map view to add the location overlay to
 * @param mapViewModel the view model of the map
 * @return the location overlay that was added
 */
fun initLocationOverlay(mapView: MapView, mapViewModel: MapViewModel): MyLocationNewOverlay {
    val locationProvider = GpsMyLocationProvider(mapView.context)
    var lastLocation = Location("")

    val locationOverlay = object : MyLocationNewOverlay(locationProvider, mapView) {
        override fun onLocationChanged(location: Location?, provider: IMyLocationProvider?) {
            super.onLocationChanged(location, provider)
            mapViewModel.setCloseLocation(
                updateAllDistancesAndFindClosest(mapView, GeoPoint(location))
            )
            mapViewModel.addDistanceWalked(lastLocation.distanceTo(location!!))
            lastLocation = locationProvider.lastKnownLocation
            if (mapViewModel.currentPlayer != null
                && mapViewModel.currentPlayer!!.playerState.value == PlayerState.MOVING
                && mapViewModel.interactableProperty.value == mapViewModel.goingToLocationProperty) {
                mapViewModel.currentPlayer!!.playerState.value = PlayerState.INTERACTING
                mapViewModel.goingToLocationProperty = null
            }
        }
    }

    locationOverlay.enableMyLocation()
    locationOverlay.enableFollowLocation()
    locationOverlay.runOnFirstFix {
        mapViewModel.setCloseLocation(
            updateAllDistancesAndFindClosest(mapView, GeoPoint(locationOverlay.myLocation))
        )
        mapView.post {
            mapView.controller.animateTo(locationOverlay.myLocation)
        }
        mapViewModel.resetDistanceWalked()
    }
    return locationOverlay
}
