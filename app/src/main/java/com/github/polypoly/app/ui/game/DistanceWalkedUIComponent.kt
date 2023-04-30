package com.github.polypoly.app.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.game.GameActivity.Companion.formattedDistance
import com.github.polypoly.app.ui.game.GameActivity.Companion.gameViewModel

/**
 * Displays the distance walked and a button to reset it.
 */
@Composable
fun DistanceWalkedUIComponents() {
    Box(modifier = Modifier.fillMaxWidth().offset(y = 80.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(White)
                .border(1.dp, Black)
                .align(TopCenter)
        ) {
            Text(
                text = "Distance walked: ${formattedDistance(gameViewModel.distanceWalked.value)}",
                color = Black,
                modifier = Modifier
                    .padding(8.dp)
                    .testTag("distanceWalked")
            )
        }
    }
}