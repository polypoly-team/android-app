package com.github.polypoly.app.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
fun LocationsDialog(title: String, open: MutableState<Boolean>, locationsOwnByPlayer: List<InGameLocation>,
    onClick: (InGameLocation) -> Unit) {
    Dialog (
        onDismissRequest = { open.value = false },
    ) {
        Surface(
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .testTag("locations_list_dialog")
                .fillMaxWidth(0.95f)
        ) {
            LocationsDialogBody(locationList = locationsOwnByPlayer, title, open, onClick)
        }
    }
}

/**
 * Body for the locations list dialog.
 * @param locationList The list of locations to show.
 */
@Composable
fun LocationsDialogBody(locationList: List<InGameLocation>, title: String, open: MutableState<Boolean>,
    onClick: (InGameLocation) -> Unit) {
    Column {
        Text(text = title,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(Padding.medium)
        )
        Spacer(modifier = Modifier.height(Padding.medium))
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxHeight(0.85f)
        ) {
            LazyColumn (
                modifier = Modifier.scrollable(
                    state = rememberScrollState(),
                    orientation = Orientation.Vertical,
                    enabled = true
                )
            ) {
                items(items = locationList) { item ->
                    LocationInList(item) { onClick(item) }
                }
            }
        }
        Spacer(modifier = Modifier.height(Padding.medium))
        Button(
            onClick = ({ open.value = false }),
            modifier = Modifier
                .testTag("locationsDialogCloseButton")
                .padding(Padding.large),
        ) {
            Text(text = "Cancel")
        }
    }
}

/**
 * A Location in the list.
 * @param location The location to show.
 */
@Composable
fun LocationInList(location: InGameLocation, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent
        )
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
            Text(text = location.locationProperty.name, color = MaterialTheme.colors.onPrimary)
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