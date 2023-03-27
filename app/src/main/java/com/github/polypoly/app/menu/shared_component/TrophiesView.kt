package com.github.polypoly.app.menu.shared_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.game.user.Trophy
import com.github.polypoly.app.game.user.allTrophies
import com.github.polypoly.app.ui.theme.advancedShadow

/**
 * A trophy view that the player has won or not
 * @param trophy the trophy to display
 * @param won if the trophy is won by the player
 * @param selected if the trophy is selected, i.e. is highlighted compared to a normal trophy
 */
@Composable
fun TrophyView(trophy: Trophy, won: Boolean, selected: Boolean) {

    val trophyColor: Color = if (won) MaterialTheme.colors.primary else
        MaterialTheme.colors.onSecondary

    Box(
        modifier = Modifier
            .advancedShadow(
                alpha = if (selected) 0.3f else 0f,
                color = MaterialTheme.colors.onSecondary,
                shadowBlurRadius = 8.dp,
                cornersRadius = 50.dp
            )
            .clip(CircleShape)
            .background(
                color = trophyColor
            )
            .background(
                color = Color.White.copy(alpha = if (selected) 0f else 0.2f)
            )
            .size(50.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (won)
            Icon(
                imageVector = trophy.getIcon(),
                contentDescription = "Person Icon",
                tint = MaterialTheme.colors.onPrimary
            )
        else
            Text(
                "?",
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.h6
            )
    }
}

/**
 * Display all the trophies that the player has won or can win.
 * @param callBack the function that will be call back when we selected the trophy of index "input"
 * @param maxSelected the max number of trophies the user can select
 */
@Composable
fun TrophiesView(callBack: (input: Int) -> Unit, maxSelected: Int) {

    val maxPerRow = 5
    val maxPerColumn = 3

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ){
        repeat(maxPerRow) { columnIdx ->
            if(columnIdx > 0) Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                repeat(maxPerColumn) {rowIdx ->
                    if(rowIdx > 0) Spacer(modifier = Modifier.height(10.dp))
                    val idx: Int = rowIdx*maxPerRow +columnIdx
                    TrophyView(trophy = allTrophies[idx], won = idx%4 == 0, selected = idx==4)
                }
            }
        }
    }
}