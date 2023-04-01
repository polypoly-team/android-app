package com.github.polypoly.app.map

import android.content.Context
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory.decodeResource
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.BuildConfig
import com.github.polypoly.app.R
import com.github.polypoly.app.game.Localization
import com.github.polypoly.app.game.PlayerGlobalData
import com.github.polypoly.app.map.LocalizationRepository.getZones
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.Shapes
import com.github.polypoly.app.utils.Padding
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

/**
 * Activity for displaying the map used in the game.
 */
class MapActivity : ComponentActivity() {

    // Not public for testing purposes
    val mapViewModel: MapViewModel = MapViewModel()
    private val initialPosition = GeoPoint(46.518726, 6.566613)
    private val initialZoom = 18.0
    private val markerSideLength = 100

    private val markerToLocalization = mutableMapOf<Marker, Localization>()

    // flag to show the dialog
    val showDialog = mutableStateOf(false)
    lateinit var currentMarker: Marker

    // store the map view for testing purposes
    lateinit var mapView: MapView private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapView()
                    BuildingInfoUIComponent()
                    Hud(PlayerGlobalData(false, 420), listOf(PlayerGlobalData(false, 32), PlayerGlobalData(false, 56)),16)
                }
            }
        }
    }

    /**
     * Initialize the map view that sits beneath the UI components.
     */
    @Composable
    fun MapView() {
        AndroidView(factory = { context ->
            Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

            val mapView = initMapView(context, initialPosition)

            for (zone in getZones())
                zone.localizations
                    .forEach {
                        markerToLocalization[addMarkerTo(
                            mapView,
                            it.position,
                            it.name,
                            zone.color
                        )] = it
                    }

            val currentLocationOverlay = initLocationOverlay(mapView)
            mapView.overlays.add(currentLocationOverlay)
            this.mapView = mapView
            mapView
        }, modifier = Modifier.testTag("map"))
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
                    modifier = Modifier
                        .padding(8.dp)
                        .testTag("distanceWalked")
                )
            }
        }
    }

    @Composable
    fun BuildingInfoUIComponent() {
        val showBuyDialog = remember { mutableStateOf(false) }
        if (showDialog.value) BuildingInfoDialog(showBuyDialog)

        if (showBuyDialog.value) {
            BetDialog(onBuy = { amount ->
                showBuyDialog.value = false // TODO: Handle the buy action with the entered amount here
            }, onClose = {
                showBuyDialog.value = false
            })
        }
    }

    @Composable
    private fun BuildingInfoDialog(showBuyDialog: MutableState<Boolean>) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            modifier = Modifier.testTag("buildingInfoDialog"),
            title = {
                Row {
                    Text(text = markerToLocalization[currentMarker]?.name ?: "Unknown")
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text(text = "Base price: ${markerToLocalization[currentMarker]?.basePrice}")
                }
            },
            text = {
                Text(text = "This is some trivia related to the building and or some info related to it.")
            },
            buttons = {
                BuildingInfoButtons(showBuyDialog)
            }
        )
    }

    @Composable
    private fun BuildingInfoButtons(showBuyDialog: MutableState<Boolean>) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showBuyDialog.value = true },
                modifier = Modifier.testTag("betButton")
            ) {
                Text(text = "Bet")
            }
            Button(
                onClick = { showDialog.value = false },
                modifier = Modifier.testTag("closeButton")
            ) {
                Text(text = "Close")
            }
        }
    }

    @Composable
    fun BetDialog(onBuy: (Float) -> Unit, onClose: () -> Unit) {
        val inputPrice = remember { mutableStateOf("") }
        val showError = remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = onClose,
            title = {
                Text(text = "Enter your bet")
            },
            modifier = Modifier.testTag("betDialog"),
            text = {
                BetDialogBody(
                    inputPrice = inputPrice,
                    showError = showError
                )
            },
            buttons = {
                BetDialogButtons(
                    onBuy = onBuy,
                    onClose = onClose,
                    inputPrice = inputPrice,
                    showError = showError
                )
            }
        )
    }

    @Composable
    private fun BetDialogBody(
        inputPrice: MutableState<String>,
        showError: MutableState<Boolean>
    ) {
        Column {
            TextField(
                value = inputPrice.value,
                onValueChange = { newValue -> inputPrice.value = newValue },
                placeholder = { Text(text = "Enter amount") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.body1,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("betInput")
            )
            if (showError.value) {
                Text(
                    text = "You cannot bet less than the base price!",
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .testTag("betErrorMessage")
                )
            }
        }
    }

    @Composable
    private fun BetDialogButtons(
        onBuy: (Float) -> Unit,
        onClose: () -> Unit,
        inputPrice: MutableState<String>,
        showError: MutableState<Boolean>
    ) {
        val minBet = markerToLocalization[currentMarker]?.basePrice!!
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    val amount = inputPrice.value.toFloatOrNull()
                    if (amount != null && amount >= minBet) {
                        onBuy(amount)
                    } else {
                        showError.value = true
                    }
                }
            ) {
                Text(
                    text = "Confirm",
                    modifier = Modifier.testTag("confirmBetButton")
                )
            }

            Button(
                onClick = onClose
            ) {
                Text(
                    text = "Close",
                    modifier = Modifier.testTag("closeBetButton")
                )
            }
        }
    }

    private fun initMapView(context: Context, startPosition: GeoPoint): MapView {
        val mapView = MapView(context)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.controller.setZoom(initialZoom)
        mapView.controller.setCenter(startPosition)
        val campusTileSource = CampusTileSource( 0)
        val tileProvider = MapTileProviderBasic(context, campusTileSource)
        val tilesOverlay = TilesOverlay(tileProvider, context)
        mapView.overlays.add(tilesOverlay)
        return mapView
    }

    private fun addMarkerTo(
        mapView: MapView,
        position: GeoPoint,
        title: String,
        zoneColor: Int
    ): Marker {
        val marker = Marker(mapView)
        marker.position = position
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.title = title
        marker.isDraggable = false
        marker.icon = buildMarkerIcon(mapView.context, zoneColor)
        marker.setOnMarkerClickListener { _, _ ->
            currentMarker = marker
            showDialog.value = true
            true
        }
        mapView.overlays.add(marker)
        return marker
    }

    private fun buildMarkerIcon(context: Context, color: Int): Drawable {
        val markerIcon = decodeResource(context.resources, R.drawable.location_pin)
        val scaledBitmap = createScaledBitmap(markerIcon, markerSideLength, markerSideLength, true)
        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        val canvas = Canvas(scaledBitmap)
        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)
        return BitmapDrawable(context.resources, scaledBitmap)
    }

    private fun initLocationOverlay(mapView: MapView): MyLocationNewOverlay {
        val locationProvider = GpsMyLocationProvider(mapView.context)
        var lastLocation = Location("")

        val locationOverlay = object : MyLocationNewOverlay(locationProvider, mapView) {
            override fun onLocationChanged(location: Location?, provider: IMyLocationProvider?) {
                super.onLocationChanged(location, provider)
                updateAllDistances(mapView, GeoPoint(location))
                mapViewModel.addDistanceWalked(lastLocation.distanceTo(location!!))
                lastLocation = locationProvider.lastKnownLocation
            }
        }

        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        locationOverlay.runOnFirstFix {
            runOnUiThread {
                updateAllDistances(mapView, locationOverlay.myLocation)
                mapView.controller.animateTo(locationOverlay.myLocation)
                mapViewModel.resetDistanceWalked()
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
     * The heads-up display with player and game stats that is displayed on top of the map
     */
    @Composable
    fun Hud(playerData: PlayerGlobalData, otherPlayersData: List<PlayerGlobalData>, round: Int) {
        HudPlayer(playerData)
        HudOtherPlayersAndGame(otherPlayersData, round)
    }

    /**
     * The HUD for the player stats
     */
    @Composable
    fun HudPlayer(playerData: PlayerGlobalData) {
        var openPlayerInfo by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .padding(Padding.medium)
                    .background(MaterialTheme.colors.background, shape = Shapes.medium)
                    .align(Alignment.BottomStart)
            ) {
                Row(Modifier.padding(Padding.medium)) {
                    HudButton(
                        name = "playerInfoButton",
                        onClick = { openPlayerInfo = true },
                        icon_id = R.drawable.tmp_happysmile,
                        description = "See player information"
                    )
                    Column(Modifier.padding(Padding.medium)) {
                        HudText("playerBalance", "${playerData.balance} $")
                    }
                }
            }
        }

        if (openPlayerInfo) {
            Dialog(
                onDismissRequest = { openPlayerInfo = false },
            ) {
                Surface(
                    color = MaterialTheme.colors.background,
                    shape = Shapes.medium,
                    modifier = Modifier
                        .padding(Padding.medium)
                        .fillMaxWidth()
                ) {
                    // TODO: Add information about the player
                    Text(text = "Player info")
                }
            }
        }
    }

    /**
     * The HUD that shows the stats for other players and the game
     */
    @Composable
    fun HudOtherPlayersAndGame(otherPlayersData: List<PlayerGlobalData>, round: Int) {
        var isExpanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .padding(Padding.medium)
                    .align(Alignment.TopStart)
            ) {
                Column(Modifier.padding(Padding.medium)) {
                    // A drop down button that expands and collapses the stats for other players and the game
                    ToggleIconButton(
                        "dropDownButton",
                        "Expand or collapse the stats for other players and the game",
                        { isExpanded = !isExpanded },
                        isExpanded,
                        R.drawable.tmp_happysmile,
                        R.drawable.tmp_happysmile
                    )

                    // The stats for other players and the game slide in and out when the drop down button is pressed
                    AnimatedVisibility(
                        visible = isExpanded,
                    ) {
                        Surface(
                            color = MaterialTheme.colors.background,
                            shape = Shapes.medium,
                            modifier = Modifier
                                .padding(Padding.medium)
                        ) {
                            Column(Modifier.padding(Padding.medium)) {
                                HudGame(round)
                                otherPlayersData.forEach {
                                    HudOtherPlayer(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * The HUD for the game stats
     */
    @Composable
    fun HudGame(round: Int) {
        var openGameInfo by remember { mutableStateOf(false) }

        Row(Modifier.padding(Padding.medium)) {
            HudButton(
                name = "gameInfoButton",
                onClick = { openGameInfo = true },
                icon_id = R.drawable.tmp_happysmile,
                description = "See game information"
            )
            Column(Modifier.padding(Padding.medium)) {
                HudText("gameRound", text = "Round $round")
            }
        }

        if (openGameInfo) {
            Dialog(
                onDismissRequest = { openGameInfo = false },
            ) {
                Surface(
                    color = MaterialTheme.colors.background,
                    shape = Shapes.medium,
                    modifier = Modifier
                        .padding(Padding.medium)
                        .fillMaxWidth()
                ) {
                    // TODO: Add information about the game
                    Text(text = "Game info")
                }
            }
        }
    }

    /**
     * The HUD for the stats of other players
     */
    @Composable
    fun HudOtherPlayer(playerData: PlayerGlobalData) {
        var openOtherPlayerInfo by remember { mutableStateOf(false) }
        Row(Modifier.padding(Padding.medium)) {
            HudButton(
                name = "otherPlayerInfoButton",
                onClick = { openOtherPlayerInfo = true },
                icon_id = R.drawable.tmp_happysmile,
                description = "See other player information"
            )
            Column(Modifier.padding(Padding.medium)) {
                HudText("playerBalance", "${playerData.balance} $")
            }
        }

        if (openOtherPlayerInfo) {
            Dialog(
                onDismissRequest = { openOtherPlayerInfo = false },
            ) {
                Surface(
                    color = MaterialTheme.colors.background,
                    shape = Shapes.medium,
                    modifier = Modifier
                        .padding(Padding.medium)
                        .fillMaxWidth()
                ) {
                    // TODO: Add information about other players
                    Text(text = "Other player info")
                }
            }
        }
    }

    /**
     * A button that is used in the HUD
     */
    @Composable
    fun HudButton(name: String, onClick: () -> Unit, icon_id: Int, description: String) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .semantics { contentDescription = description }
                .testTag(name),
            shape = CircleShape,
        ) {
            Image(
                painter = painterResource(icon_id),
                contentDescription = description,
                modifier = Modifier.size(50.dp)
            )
        }
    }

    /**
     * A text that is used in the HUD
     */
    @Composable
    fun HudText(name: String, text: String) {
        Text(
            text = text,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(Padding.small)
                .testTag(name)
        )
    }

    /**
     * A button whose icon changes depending on a toggle
     */
    @Composable
    fun ToggleIconButton(name: String, description: String, onClick: () -> Unit, toggle: Boolean, onIcon: Int, offIcon: Int) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .semantics { contentDescription = description }
                .testTag(name),
            shape = CircleShape,
        )
        {
            if (toggle) {
                Image(
                    painter = painterResource(onIcon),
                    contentDescription = "Expand",
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Image(
                    painter = painterResource(offIcon),
                    contentDescription = "Collapse",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }

    // ============================== PREVIEW ==============================
    @Preview
    @Composable
    fun MapViewPreview() {
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                MapView()
                DistanceWalkedUIComponents()
                BuildingInfoUIComponent()
            }
        }
    }
}
