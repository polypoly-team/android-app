package com.github.polypoly.app.ui.game

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
import com.github.polypoly.app.R
import com.github.polypoly.app.ui.game.GameActivity.Companion.gameViewModel
import com.github.polypoly.app.ui.game.GameActivity.Companion.interactingWithProperty
import com.github.polypoly.app.ui.game.GameActivity.Companion.updateAllDistancesAndFindClosest
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
 */
fun addMarkerTo(mapView: MapView, position: GeoPoint, title: String, zoneColor: Int): Marker {
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
        gameViewModel.selectedMarker = marker
        interactingWithProperty.value = true
        true
    }
    mapView.overlays.add(marker)
    return marker
}

/**
 * Initializes the location overlay and sets the location listener.
 */
fun initLocationOverlay(mapView: MapView): MyLocationNewOverlay {
    val locationProvider = GpsMyLocationProvider(mapView.context)
    var lastLocation = Location("")

    val locationOverlay = object : MyLocationNewOverlay(locationProvider, mapView) {
        override fun onLocationChanged(location: Location?, provider: IMyLocationProvider?) {
            super.onLocationChanged(location, provider)
            gameViewModel.setCloseLocation(
                updateAllDistancesAndFindClosest(mapView, GeoPoint(location))
            )
            gameViewModel.addDistanceWalked(lastLocation.distanceTo(location!!))
            lastLocation = locationProvider.lastKnownLocation
        }
    }

    locationOverlay.enableMyLocation()
    locationOverlay.enableFollowLocation()
    locationOverlay.runOnFirstFix {
        gameViewModel.setCloseLocation(
            updateAllDistancesAndFindClosest(mapView, GeoPoint(locationOverlay.myLocation))
        )
        mapView.post {
            mapView.controller.animateTo(locationOverlay.myLocation)
        }
        gameViewModel.resetDistanceWalked()
    }
    return locationOverlay
}