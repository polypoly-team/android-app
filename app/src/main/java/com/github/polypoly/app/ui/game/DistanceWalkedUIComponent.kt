package com.github.polypoly.app.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.map.MapViewModel
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.UIElements

/**
 * Displays the distance walked and a button to reset it.
 * @param mapViewModel GameViewModel to use for map business logic
 */
@Composable
fun DistanceWalkedUIComponents(mapViewModel: MapViewModel) {
    fun formattedDistance(distance: Float): String {
        return if (distance < 1000) "${"%.1f".format(distance)} m"
        else "${"%.1f".format(distance / 1000)} km"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = 20.dp)
            .padding(Padding.medium),
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(UIElements.smallIconSize)
                )
                .align(TopEnd)
                .testTag("distance_walked_row")
        ) {
            Icon(
                Icons.Filled.DirectionsWalk,
                contentDescription = "Distance Walked",
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .padding(Padding.medium)
                    .align(CenterVertically)
            )
            Text(
                text = formattedDistance(mapViewModel.distanceWalked.value),
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .padding(Padding.medium)
                    .testTag("distance_walked")
            )
        }
    }
}