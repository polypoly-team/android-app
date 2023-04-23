package com.github.polypoly.app.menu.shared_component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.user.Trophy
import com.github.polypoly.app.base.user.Trophy.Companion.allTrophies
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.ui.theme.advancedShadow
import com.github.polypoly.app.util.toDp


/**
 * The default size of trophies
 */
const val TROPHY_SIZE = 50

/**
 * The default padding between trophies
 */
const val PADDING_TROPHIES = 10

/**
 * A trophy view that the player has won or not
 * @param trophy the trophy to display
 * @param won if the trophy is won by the player
 * @param selected if the trophy is selected, i.e. is highlighted compared to a normal trophy
 * @param onClick what happens when the user click on the trophy
 * @param disable if the trophy is not clickable
 */
@Composable
fun TrophyView(trophy: Trophy, won: Boolean, selected: Boolean = false,
               onClick: () -> Unit = {}, disable: Boolean = false) {

    val trophyColor: Color = if (won) MaterialTheme.colors.primary else
        MaterialTheme.colors.onSecondary

    Box(
        modifier = Modifier
            .advancedShadow(
                alpha = if (selected && !disable) 0.3f else 0f,
                color = MaterialTheme.colors.onSecondary,
                shadowBlurRadius = PADDING_TROPHIES.dp,
                cornersRadius = TROPHY_SIZE.dp)
            .clip(CircleShape)
            .background(
                color = trophyColor)
            .background(
                color = MaterialTheme.colors.background.copy(alpha = if (selected) 0f else 0.6f))
            .size(TROPHY_SIZE.dp)
            .clickable(onClick = if(disable) {{}} else onClick)
            .testTag("Trophy${trophy.getId()}"),
        contentAlignment = Alignment.Center,
    ) {
        if (won)
            Icon(
                imageVector = trophy.getIcon(),
                contentDescription = "Trophy Icon",
                tint = MaterialTheme.colors.onPrimary)
        else
            Text(
                "?",
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.h6)
    }
}

/**
 * Display all the trophies that the player has won or can win.
 * @param callBack the function that will be call back when we selected the trophy of index "input"
 * @param maxSelected the max number of trophies the user can select
 * @param selected the current selected trophies
 * @param user the user to whom the profile belongs
 */
@Composable
fun TrophiesView(callBack: (input: Int) -> Unit, maxSelected: Int, selected: List<Int>,
                 user: User) {

    assert(selected.size <= maxSelected)

    val totalNumberOfTrophies = allTrophies.size
    // default number of trophies per row is 5 , but this number change if the layout is too
    // small to put 5 trophies per row
    var maxPerRow by remember { mutableStateOf(5) }
    var maxPerColumn = totalNumberOfTrophies/maxPerRow

    Row(
        modifier = Modifier.fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                val size = coordinates.size
                val widthInDP = size.width.toDp
                // to adapt the layout
                while (widthInDP < maxPerRow*TROPHY_SIZE + (maxPerRow-1)*PADDING_TROPHIES
                    && maxPerRow > 0) {
                    --maxPerRow
                }
                maxPerColumn = totalNumberOfTrophies/maxPerRow
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ){
        repeat(maxPerRow) { columnIdx ->
            if(columnIdx > 0) Spacer(modifier = Modifier.width(PADDING_TROPHIES.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                repeat(maxPerColumn) {rowIdx ->
                    if(rowIdx > 0) Spacer(modifier = Modifier.height(PADDING_TROPHIES.dp))
                    val idx: Int = rowIdx*maxPerRow +columnIdx
                    TrophyView(
                        trophy = allTrophies[idx],
                        won = user.hasTrophy(idx),
                        selected = selected.contains(idx),
                        onClick = { callBack(idx) })
                }
            }
        }
    }
}