package com.github.polypoly.app.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.game.GameActivity.Companion.gameViewModel

/**
 * Displays the distance walked and a button to reset it.
 */
@Composable
fun DistanceWalkedUIComponents() {
    fun formattedDistance(distance: Float): String {
        return if (distance < 1000) "${"%.1f".format(distance)} m"
        else "${"%.1f".format(distance / 1000)} km"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = 30.dp)
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
                .align(TopEnd)
                .testTag("distanceWalkedRow")
        ) {
            Icon(
                Icons.Filled.DirectionsWalk,
                contentDescription = "Distance Walked",
                tint = White,
                modifier = Modifier
                    .padding(8.dp)
                    .align(CenterVertically)
            )
            Text(
                text = formattedDistance(gameViewModel.distanceWalked.value),
                color = White,
                modifier = Modifier
                    .padding(8.dp)
                    .testTag("distanceWalked")
            )
        }
    }
}