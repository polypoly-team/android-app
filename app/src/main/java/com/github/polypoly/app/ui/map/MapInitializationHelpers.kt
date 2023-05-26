package com.github.polypoly.app.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import com.github.polypoly.app.R
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.game.location.PropertyLevel
import com.github.polypoly.app.viewmodels.game.GameViewModel
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
import kotlin.math.roundToInt
import kotlin.random.Random

private val INITIAL_POSITION = GeoPoint(46.518726, 6.566613)
private const val INITIAL_ZOOM = 18.0
private const val MARKER_SIDE_LENGTH = 100
private const val STAR_SIDE_LENGTH = 50
private const val TARGET_FACTOR = 1.8

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
 * @param mapViewModel GameViewModel to use for map business logic.
 * @param gameViewModel GameViewModel to use for game business logic. Null if no game is going on.
 * @return the marker that was added
 */
fun addMarkerTo(
    mapView: MapView,
    location: LocationProperty,
    zoneColor: Int,
    mapViewModel: MapViewModel,
    gameViewModel: GameViewModel?,
    propertyLevel: PropertyLevel,
    owned: Boolean,
    targetLocation: Boolean
): Marker {
    fun buildMarkerIcon(
        context: Context,
        color: Int,
        level: PropertyLevel,
        isOwned: Boolean
    ): Drawable {
        val markerIcon = BitmapFactory.decodeResource(context.resources, R.drawable.location_pin)
        val starIcon = BitmapFactory.decodeResource(context.resources, R.drawable.star_icon)

        val markerSize: Int = when (targetLocation) {
            true -> (MARKER_SIDE_LENGTH * TARGET_FACTOR).roundToInt()
            false -> MARKER_SIDE_LENGTH
        }
        val scaledMarkerBitmap =
            Bitmap.createScaledBitmap(markerIcon, markerSize, markerSize, true)
        val scaledStarBitmap =
            Bitmap.createScaledBitmap(starIcon, STAR_SIDE_LENGTH, STAR_SIDE_LENGTH, true)

        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)

        val strokeWidth = 5f // This is the width of the stroke. Adjust it as needed.
        val strokePaint = Paint().apply {
            style = Paint.Style.STROKE
            this.color = Color.BLACK
            this.strokeWidth = strokeWidth
        }

        val resultBitmap = Bitmap.createBitmap(
            scaledMarkerBitmap.width,
            scaledMarkerBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(resultBitmap)

        // Draw the marker first
        canvas.drawBitmap(scaledMarkerBitmap, 0f, 0f, paint)

        // If the building is owned, draw the black border around the marker
        if (isOwned) {
            val rect = RectF(
                strokeWidth / 2,
                strokeWidth / 2,
                scaledMarkerBitmap.width - strokeWidth / 2,
                scaledMarkerBitmap.height - strokeWidth / 2
            )
            canvas.drawRect(rect, strokePaint)
        }

        // Draw the stars on top of the marker
        for (i in 0 until level.ordinal) {
            val left =
                scaledMarkerBitmap.width / 2 - ((level.ordinal * STAR_SIDE_LENGTH) / 2) + i * STAR_SIDE_LENGTH
            val top = (scaledMarkerBitmap.height - scaledStarBitmap.height) / 2.toFloat()
            canvas.drawBitmap(scaledStarBitmap, left.toFloat(), top, null)
        }

        return BitmapDrawable(context.resources, resultBitmap)
    }


    val marker = Marker(mapView)

    marker.position = location.position()
    marker.title = location.name
    marker.isDraggable = false
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
    marker.icon = buildMarkerIcon(mapView.context, zoneColor, propertyLevel, owned)

    marker.setOnMarkerClickListener { _, _ ->
        val interactionAllowed =
            gameViewModel?.getPlayerStateData()?.value == PlayerState.INTERACTING || gameViewModel == null
        if (interactionAllowed && mapViewModel.getLocationSelected().value == null) {
            mapViewModel.selectLocation(location)
        }
        true
    }

    return marker
}

fun removeMarkerFrom(mapView: MapView, location: LocationProperty) {
    val marker = mapView.overlays.find { it is Marker && it.title == location.name } as Marker
    mapView.overlays.remove(marker)
}

/**
 * Initializes the location overlay and sets the location listener.
 * @param mapViewModel GameViewModel to use for map business logic
 * @param gameViewModel GameViewModel to use for game business logic. Null if no game is going on
 * @return the location overlay that was added
 */
fun initLocationOverlay(
    mapView: MapView,
    mapViewModel: MapViewModel,
    gameViewModel: GameViewModel?
): MyLocationNewOverlay {
    val locationProvider = GpsMyLocationProvider(mapView.context)
    var lastLocation = Location("")

    val onClosestLocationFound = { closestLocation: LocationProperty? ->
        if (closestLocation != null)
            mapViewModel.setInteractableLocation(closestLocation)
        else
            mapViewModel.setInteractableLocation(null)
    }

    val locationOverlay = object : MyLocationNewOverlay(locationProvider, mapView) {
        override fun onLocationChanged(location: Location?, provider: IMyLocationProvider?) {
            super.onLocationChanged(location, provider)
            gameViewModel?.computeClosestLocation(GeoPoint(location))
                ?.thenApply(onClosestLocationFound)
            mapViewModel.addDistanceWalked(lastLocation.distanceTo(location!!))
            lastLocation = locationProvider.lastKnownLocation
            if (mapViewModel.currentPlayer != null
                && gameViewModel?.getPlayerStateData()?.value == PlayerState.MOVING
                && mapViewModel.interactableProperty.value == mapViewModel.goingToLocationPropertyData.value
            ) {
                gameViewModel.locationReached()
                mapViewModel.goToLocation(null)
            }
        }
    }

    locationOverlay.enableMyLocation()
    locationOverlay.enableFollowLocation()
    locationOverlay.runOnFirstFix {
        gameViewModel?.computeClosestLocation(GeoPoint(locationOverlay.myLocation))
            ?.thenApply(onClosestLocationFound)
        mapView.post {
            mapView.controller.animateTo(locationOverlay.myLocation)
        }
        mapViewModel.resetDistanceWalked()
    }

    return locationOverlay
}