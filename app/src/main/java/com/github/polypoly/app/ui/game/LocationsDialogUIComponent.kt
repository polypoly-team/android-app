package com.github.polypoly.app.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.ui.theme.Padding

/**
 * Dialog to show the locations the user owns.
 * @param title The title of the dialog.
 * @param open A mutable state to open and close the dialog.
 * @param locationsOwnByPlayer The locations the current player owns.
 */
@Composable
fun LocationsDialog(title: String, open: MutableState<Boolean>, locationsOwnByPlayer: List<InGameLocation>) {
    AlertDialog(
        onDismissRequest = { open.value = false },
        title = {
            Text(text = title)
        },
        text = { LocationsDialogBody(locationsOwnByPlayer) },
        modifier = Modifier.testTag("locationsDialog"),
        buttons = {
            Button(
                onClick = ({ open.value = false }),
                modifier = Modifier
                    .testTag("locationsDialogCloseButton")
                    .padding(Padding.large),
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

/**
 * Body for the locations list dialog.
 * @param locationList The list of locations to show.
 */
@Composable
fun LocationsDialogBody(locationList: List<InGameLocation>) {
    Column {
        locationList.forEach { location ->
            LocationInList(location)
        }
    }
}

/**
 * A Location in the list.
 * @param location The location to show.
 */
@Composable
fun LocationInList(location: InGameLocation) {
    Button(
        onClick = {}
    ) {
        Row (
            modifier = Modifier.padding(Padding.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = Color(getLocationColor(location)))
            )
            Spacer(modifier = Modifier.width(Padding.medium))
            Text(text = location.locationProperty.name)
        }
    }
}

/**
 * Get the color for the location.
 * @param location The location to get the color for.
 */
fun getLocationColor(location: InGameLocation) : Int {
    return LocationPropertyRepository.getZones()
        .find { it.locationProperties.contains(location.locationProperty) }?.color ?: 0
}