package com.github.polypoly.app.ui.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.ui.theme.PolypolyTheme

/**
 * The map the user can visit when he/she is connected as a guest
 */
class VisitedMapActivity : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                VisitedMapContent()
            }
        }
    }

    @Composable
    fun VisitedMapContent() {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            MapUI.MapView()
            ShowPopup()
        }
    }

    /**
     * Manage the building info dialog.
     */
    @Composable
    private fun ShowPopup() {
        if (GameActivity.interactingWithProperty.value) {
            PopupBuildingDescription()
        }
    }

    /**
     * Building Info popup dialog with description, positive point and negative point.
     */
    @Composable
    private fun PopupBuildingDescription() {
        val currentProperty =
            GameActivity.gameViewModel.markerToLocationProperty[GameActivity.gameViewModel.selectedMarker]
        AlertDialog(
            onDismissRequest = { GameActivity.interactingWithProperty.value = false },
            modifier = Modifier.testTag("building_description_dialog"),
            title = {
                Text(text = currentProperty?.name ?: "")
            },
            text = {
                Text(text = currentProperty?.description ?: "")
            },
            buttons = {
                Button(onClick = {
                    GameActivity.interactingWithProperty.value = false }) {
                    Text(text = "Close")
                }
            }
        )
    }


    // =============== Preview =============== //

    @Preview(
        name = "Light Mode"
    )
    @Composable
    fun ProfilePreview() {
        PolypolyTheme {
            VisitedMapContent()
        }
    }
}