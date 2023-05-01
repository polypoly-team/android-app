package com.github.polypoly.app.ui.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.ui.game.GameActivity
import com.github.polypoly.app.ui.theme.Padding
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

    /**
     * The content of the map to visit
     */
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
                BuildingDescription(currentProperty)
            },
            buttons = {
                CloseButton()
            }
        )
    }

    /**
     * Building description, positive point and negative point.
     * @param currentProperty the building we want to show the description
     */
    @Composable
    private fun BuildingDescription(currentProperty: LocationProperty?) {
        Column {
            if(currentProperty?.description != null && currentProperty.description != "") {
                Text(text = currentProperty.description)
            } else {
                Text(text ="No Info about this building")
            }
            Spacer(modifier = Modifier.height(Padding.medium))
            if(currentProperty?.positivePoint != null && currentProperty.positivePoint != "") {
                Text(text = "Positive point: ", color = MaterialTheme.colors.primary)
                Text(text = currentProperty.positivePoint)
                Spacer(modifier = Modifier.height(Padding.medium))
            }
            if(currentProperty?.negativePoint != null && currentProperty.negativePoint != "") {
                Text(text = "Negative point: ", color = MaterialTheme.colors.primary)
                Text(text = currentProperty.negativePoint)
            }
        }
    }

    /**
     * Button which close the building info dialog
     */
    @Composable
    private fun CloseButton() {
        Button(
            onClick = {
                GameActivity.interactingWithProperty.value = false },
            modifier = Modifier
                .testTag("close_building_description_dialog")
                .padding(Padding.large),
        ) {
            Text(text = "Close")
        }
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