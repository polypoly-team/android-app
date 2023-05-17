package com.github.polypoly.app.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.ui.game.GameActivity.Companion.mapViewModel
import com.github.polypoly.app.ui.menu.MenuComposable
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.Shapes

/**
 * The heads-up display with player and game stats that is displayed on top of the map
 */
@Composable
fun Hud(playerData: Player, otherPlayersData: List<Player>, round: Int, location: String) {
    Column(modifier = Modifier.testTag("hud")) {
        HudPlayer(playerData)
        HudOtherPlayersAndGame(otherPlayersData, round)
        HudLocation(location, testTag = "interactable_location_text")
        if (playerData.playerState.value == PlayerState.MOVING)
            HudLocation(mapViewModel.goingToLocationProperty!!.name, DpOffset(0.dp, 80.dp), "going_to_location_text")
        HudGameMenu()
    }
}

/**
 * The HUD for the current nearby location (a text at the top of the screen)
 */
@Composable
fun HudLocation(location: String, offset: DpOffset = DpOffset(0.dp, 10.dp), testTag: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Padding.medium)
    ) {
        if (location.isNotEmpty())
            Text(
                text = location,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .testTag(testTag)
                    .offset(offset.x, offset.y)
                    .background(MaterialTheme.colors.background, shape = Shapes.medium)
                    .border(
                        1.dp,
                        MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                        shape = Shapes.medium
                    )
                    .padding(Padding.medium),
                style = MaterialTheme.typography.h4
            )
    }
}

/**
 * The HUD for the player stats, it displays basic information such as their balance,
 * and a button shows complete information on click
 */
@Composable
fun HudPlayer(playerData: Player) {
    var openPlayerInfo by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.align(BottomEnd),
            contentAlignment = Center
        ) {
            MoneyHudText("playerBalance", "${playerData.getBalance()} $")
            HudButton(
                name = "playerInfoButton",
                onClick = { openPlayerInfo = true },
                icon = Icons.Filled.Person,
                description = "See player information"
            )
        }
    }

    if (openPlayerInfo) {
        Dialog(
            onDismissRequest = { openPlayerInfo = false },
        ) {
            Surface(
                color = MaterialTheme.colors.background,
                shape = Shapes.medium,
                modifier = Modifier
                    .padding(Padding.medium)
                    .fillMaxWidth()
            ) {
                // TODO: Add information about the player
                Text(text = "Player info")
            }
        }
    }
}

/**
 * The HUD that shows the stats for other players and the game, it's a button on the top left
 * that expands and collapses a tab that contains information about the game and the other
 * players
 */
@Composable
fun HudOtherPlayersAndGame(otherPlayersData: List<Player>, round: Int) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(Padding.medium)
                .align(Alignment.TopStart)
        ) {
            Column(Modifier.padding(Padding.medium)) {
                // A drop down button that expands and collapses the stats for other players and
                // the game
                ToggleIconButton(
                    "otherPlayersAndGameDropDownButton",
                    "Expand or collapse the stats for other players and the game",
                    { isExpanded = !isExpanded },
                    isExpanded,
                    Icons.Filled.ArrowDropUp,
                    Icons.Filled.Info
                )

                // The stats for other players and the game slide in and out when the drop down
                // button is pressed
                AnimatedVisibility(
                    visible = isExpanded,
                ) {
                    Surface(
                        color = MaterialTheme.colors.background,
                        shape = Shapes.medium,
                        modifier = Modifier
                            .padding(Padding.medium)
                    ) {
                        Column(Modifier.padding(Padding.medium)) {
                            HudGame(round)
                            otherPlayersData.forEach {
                                HudOtherPlayer(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * The HUD for the game stats, it displays basic information such as the current round,
 * and a button shows complete information on click
 */
@Composable
fun HudGame(round: Int) {
    var openGameInfo by remember { mutableStateOf(false) }

    Row(Modifier.padding(Padding.medium)) {
        HudButton(
            name = "gameInfoButton",
            onClick = { openGameInfo = true },
            icon = Icons.Filled.Info,
            description = "See game information"
        )
        Column(Modifier.padding(Padding.medium)) {
            HudText("gameRound", text = "Round $round")
        }
    }

    if (openGameInfo) {
        Dialog(
            onDismissRequest = { openGameInfo = false },
        ) {
            Surface(
                color = MaterialTheme.colors.background,
                shape = Shapes.medium,
                modifier = Modifier
                    .padding(Padding.medium)
                    .fillMaxWidth()
            ) {
                // TODO: Add information about the game
                Text(text = "Game info")
            }
        }
    }
}

/**
 * The HUD for the stats of other players, it displays basic information such as their balance,
 * and a button shows complete information on click
 */
@Composable
fun HudOtherPlayer(playerData: Player) {
    var openOtherPlayerInfo by remember { mutableStateOf(false) }
    Row(Modifier.padding(Padding.medium)) {
        HudButton(
            name = "otherPlayerInfoButton",
            onClick = { openOtherPlayerInfo = true },
            icon = Icons.Filled.Person,
            description = "See other player information"
        )
        Column(Modifier.padding(Padding.medium)) {
            HudText("playerBalance", "${playerData.getBalance()} $")
        }
    }

    if (openOtherPlayerInfo) {
        Dialog(
            onDismissRequest = { openOtherPlayerInfo = false },
        ) {
            Surface(
                color = MaterialTheme.colors.background,
                shape = Shapes.medium,
                modifier = Modifier
                    .padding(Padding.medium)
                    .fillMaxWidth()
            ) {
                // TODO: Add information about other players
                Text(text = "Other player info")
            }
        }
    }
}

/**
 * The HUD for the game menu, which is a button on the bottom left that expands and collapses
 * the menu
 */
@Composable
fun HudGameMenu() {
    var openGameMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            Column(modifier = Modifier.padding(10.dp, 0.dp)) {
                // The game menu slides in and out when the game menu button is pressed
                AnimatedVisibility(
                    visible = openGameMenu,
                ) {
                    Surface(
                        color = MaterialTheme.colors.background,
                        shape = Shapes.medium,
                        modifier = Modifier.padding(Padding.medium)
                    ) {
                        MenuComposable.RowButtons()
                    }
                }

                // The drop down button that expands and collapses the game menu
                HudButton(
                    name = "gameMenuDropDownButton",
                    onClick = { openGameMenu = !openGameMenu },
                    icon = Icons.Filled.Menu,
                    description = "Expand or collapse the game menu"
                )
            }
        }
    }
}

/**
 * A button that is used in the HUD
 */
@Composable
fun HudButton(name: String, onClick: () -> Unit, icon: ImageVector, description: String) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .semantics { contentDescription = description }
            .testTag(name)
            .padding(bottom = Padding.large),
        shape = CircleShape,
    ) {
        Icon(
            icon, contentDescription = null,
            modifier = Modifier
                .padding(Padding.small)
                .size(30.dp)
        )
    }
}

/**
 * A text that is used in the HUD
 */
@Composable
fun HudText(name: String, text: String) {
    Text(
        text = text,
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.h6,
        modifier = Modifier
            .padding(Padding.small)
            .testTag(name)
    )
}

@Composable
fun MoneyHudText(name: String, text: String) {
    Box(modifier = Modifier.offset(x = (-50).dp, y = -(7).dp)) {
        Text(
            text = text,
            modifier = Modifier
                .testTag(name)
                .background(MaterialTheme.colors.background, shape = Shapes.medium)
                .width(100.dp)
                .padding(Padding.medium),
            style = MaterialTheme.typography.h6,
        )
    }
}

/**
 * A button whose icon changes depending on a toggle
 */
@Composable
fun ToggleIconButton(
    name: String,
    description: String,
    onClick: () -> Unit,
    toggle: Boolean,
    onIcon: ImageVector,
    offIcon: ImageVector
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .semantics { contentDescription = description }
            .testTag(name),
        shape = CircleShape,
    )
    {
        if (toggle)
            Icon(onIcon, contentDescription = null, modifier = Modifier.size(20.dp))
        else
            Icon(offIcon, contentDescription = null, modifier = Modifier.size(50.dp))
    }
}

