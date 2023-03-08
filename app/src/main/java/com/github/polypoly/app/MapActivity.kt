package com.github.polypoly.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.polypoly.app.ui.theme.PolypolyTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

class MapActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PolypolyTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapView()
                }
            }
        }
    }

    @Composable
    fun MapView() {
        AndroidView(factory = { context ->
            // Set a custom user agent string for OsmDroid
            Configuration.getInstance().userAgentValue = "polypoly"

            val mapView = MapView(context)
            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            mapView.setMultiTouchControls(true)
            mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            // Set initial zoom and center point
            mapView.controller.setZoom(17.5)
            mapView.controller.setCenter(GeoPoint(46.518956, 6.566513))
            mapView
        })
    }

    @Preview
    @Composable
    fun MapViewPreview() {
        PolypolyTheme {
            Surface (
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                MapView()
            }
        }
    }
}
