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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.github.polypoly.app.game.PlayerGlobalData
import com.github.polypoly.app.map.LocationRepository.getZones
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

    private val markerToLocation = mutableMapOf<Marker, com.github.polypoly.app.game.Location>()

    // flag to show the building info dialog
    val showDialog = mutableStateOf(false)
    lateinit var currentMarker: Marker

    // flag to show the roll dice dialog
    val showRollDiceDialog = mutableStateOf(false)

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
                    RollDiceDialog()
                    Hud(
                        PlayerGlobalData(false, 420),
                        listOf(PlayerGlobalData(false, 32), PlayerGlobalData(false, 56)),
                        16
                    )
                    RollDiceButton()
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

            val mapView = initMapView(context, INITIAL_POSITION)

            for (zone in getZones())
                for (location in zone.locations) {
                    val marker = addMarkerTo(mapView, location.position, location.name, zone.color)
                    markerToLocation[marker] = location
                }

            val currentLocationOverlay = initLocationOverlay(mapView)
            mapView.overlays.add(currentLocationOverlay)
            this.mapView = mapView
            mapView
        }, modifier = Modifier.testTag("map"))
    }

    /**
     * Button for rolling the dice.
     */
    @Composable
    fun RollDiceButton() {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-80).dp)
                    .testTag("rollDiceButton"),
                onClick = {
                    // Toast.makeText(this@MapActivity, rollDice(), Toast.LENGTH_SHORT).show()
                    showRollDiceDialog.value = true
                },
                shape = CircleShape

            ) {
                Icon(Icons.Filled.Casino, contentDescription = "Roll Dice")
            }
        }
    }

    /**
     * Dice roll dialog, shows the result of 3 dice rolls in a column.
     */
    @Composable
    fun RollDiceDialog() {
        if (showRollDiceDialog.value) {
            Dialog(onDismissRequest = { showRollDiceDialog.value = false }) {
                AlertDialog(
                    onDismissRequest = { showRollDiceDialog.value = false },
                    title = { Text("Dice Roll") },
                    text = {
                        Column {
                            val rollDice = rollDiceLocations()
                            // 3 buttons, containing the name of a random location
                            for (i in 0..2)
                                Button(onClick = { }) {
                                    Text(rollDice[i].name)
                                }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showRollDiceDialog.value = false }) {
                            Text("Quit")
                        }
                    }
                )
            }
        }
    }

    /**
     * Rolls the dice and returns the location that corresponds to the sum of 2 dice rolls, 3 times
     * ensuring that the player does not visit the same location twice.
     */
    private fun rollDiceLocations(): List<com.github.polypoly.app.game.Location> {
        val locationsNotToVisitName = mutableListOf(mapViewModel.closeLocation.value?.name)

        val locationsToVisit = mutableListOf<com.github.polypoly.app.game.Location>()
        for (i in 1..3) {
            val diceRollsSum = IntArray(2) { (1..6).random() }.sum() - 2
            val closestLocations = markerToLocation.entries
                .filter { !locationsNotToVisitName.contains(it.value.name) }
                .sortedBy { it.key.position.distanceToAsDouble(mapViewModel.closeLocation.value!!.position) }
                .take(11)

            locationsToVisit.add(closestLocations[diceRollsSum].value)
            locationsNotToVisitName.add(closestLocations[diceRollsSum].value.name)
        }
        return locationsToVisit
    }

    /**
     * Displays the distance walked and a button to reset it.
     */
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

    /**
     * Manage the building info dialog and the bet dialog.
     */
    @Composable
    fun BuildingInfoUIComponent() {
        val showBetDialog = remember { mutableStateOf(false) }
        if (showDialog.value) BuildingInfoDialog(showBetDialog)

        if (showBetDialog.value) {
            BetDialog(onBuy = { amount ->
                showBetDialog.value =
                    false // TODO: Handle the buy action with the entered amount here
            }, onClose = {
                showBetDialog.value = false
            })
        }
    }

    /**
     * Building Info popup dialog.
     */
    @Composable
    private fun BuildingInfoDialog(showBuyDialog: MutableState<Boolean>) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            modifier = Modifier.testTag("buildingInfoDialog"),
            title = {
                Row {
                    Text(text = markerToLocation[currentMarker]?.name ?: "Unknown")
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text(text = "Base price: ${markerToLocation[currentMarker]?.basePrice}")
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

    /**
     * Building Info popup dialog buttons.
     */
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

    /**
     * Bet popup dialog
     */
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

    /**
     * Body for the bet dialog.
     */
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

    /**
     * The buttons that are shown in the bet dialog.
     */
    @Composable
    private fun BetDialogButtons(
        onBuy: (Float) -> Unit,
        onClose: () -> Unit,
        inputPrice: MutableState<String>,
        showError: MutableState<Boolean>
    ) {
        val minBet = markerToLocation[currentMarker]?.basePrice!!
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

    /**
     * Initializes the map view with the given start position.
     */
    private fun initMapView(context: Context, startPosition: GeoPoint): MapView {
        val mapView = MapView(context)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.controller.setZoom(INITIAL_ZOOM)
        mapView.controller.setCenter(startPosition)
        val campusTileSource = CampusTileSource(0)
        val tileProvider = MapTileProviderBasic(context, campusTileSource)
        val tilesOverlay = TilesOverlay(tileProvider, context)
        mapView.overlays.add(tilesOverlay)
        return mapView
    }

    /**
     * Adds a marker to the given map view.
     */
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

    /**
     * Builds a marker icon with the given color.
     */
    private fun buildMarkerIcon(context: Context, color: Int): Drawable {
        val markerIcon = decodeResource(context.resources, R.drawable.location_pin)
        val scaledBitmap = createScaledBitmap(
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

    /**
     * Initializes the location overlay and sets the location listener.
     */
    private fun initLocationOverlay(mapView: MapView): MyLocationNewOverlay {
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
            }
        }

        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        locationOverlay.runOnFirstFix {
            runOnUiThread {
                mapViewModel.setCloseLocation(
                    updateAllDistancesAndFindClosest(mapView, GeoPoint(locationOverlay.myLocation))
                )
                mapView.controller.animateTo(locationOverlay.myLocation)
                mapViewModel.resetDistanceWalked()
            }
        }
        return locationOverlay
    }

    /**
     * Returns all markers on the map.
     */
    private fun markersOf(mapView: MapView): List<Marker> {
        return mapView.overlays.filterIsInstance<Marker>()
    }

    /**
     * Updates the distance of all markers and returns the closest one.
     *
     * @return the closest location or null if there are no locations close enough to the player
     */
    private fun updateAllDistancesAndFindClosest(
        mapView: MapView,
        myLocation: GeoPoint
    ): com.github.polypoly.app.game.Location? {
        fun updateDistance(marker: Marker, myLocation: GeoPoint) {
            val distance = myLocation.distanceToAsDouble(marker.position).toFloat()
            marker.snippet = "Distance: ${formattedDistance(distance)}"
        }

        var closestLocation = null as com.github.polypoly.app.game.Location?
        for (marker in markersOf(mapView)) {
            updateDistance(marker, myLocation)
            val markerLocation = markerToLocation[marker]!!
            if (closestLocation == null ||
                myLocation.distanceToAsDouble(markerLocation.position)
                < myLocation.distanceToAsDouble(closestLocation.position)
            ) {
                closestLocation = markerLocation
            }
        }
        if (myLocation.distanceToAsDouble(closestLocation!!.position) > MAX_CLOSE_LOCATION_DISTANCE)
            closestLocation = null

        return closestLocation
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
        HudLocation(location = mapViewModel.closeLocation.value?.name ?: "")
    }

    /**
     * The HUD for the current nearby location (a text at the top of the screen)
     */
    @Composable
    fun HudLocation(location: String) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.medium)
        ) {
            Text(
                text = location,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
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
    fun ToggleIconButton(
        name: String,
        description: String,
        onClick: () -> Unit,
        toggle: Boolean,
        onIcon: Int,
        offIcon: Int
    ) {
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

    companion object {
        private val INITIAL_POSITION = GeoPoint(46.518726, 6.566613)
        private const val INITIAL_ZOOM = 18.0
        private const val MARKER_SIDE_LENGTH = 100

        //used to determine if the player is close enough to a location to interact with it
        private const val MAX_CLOSE_LOCATION_DISTANCE = 10.0 // meters
    }
}
