package com.github.polypoly.app.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.R
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.ui.menu.MenuComposable
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.Shapes

/**
 * The heads-up display with [Player] and [Game] stats that is displayed on top of the map
 *
 * @param playerData the [Player] data of the user
 * @param otherPlayersData the [Player] data of the other players
 * @param round the current round
 * @param location the current location
 */
@Composable
fun Hud(playerData: Player, otherPlayersData: List<Player>, round: Int, location: String) {
    HudPlayer(playerData)
    HudGameAndOtherPlayers(otherPlayersData, round)
    HudLocation(location)
    HudGameMenu()
}

/**
 * The HUD for the current nearby location (a text at the top of the screen)
 *
 * @param location the current location
 *
 * @see Hud
 */
@Composable
fun HudLocation(location: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Padding.medium)
    ) {
        Text(
            text = location,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * The HUD for the [Player] stats, displaying basic information such as their balance,
 * with a button showing complete information on click
 *
 * @param playerData the [Player] data of the user
 *
 * @see Hud
 */
@Composable
fun HudPlayer(playerData: Player) {
    var openPlayerInfo by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(Padding.medium)
                .background(MaterialTheme.colors.background, shape = Shapes.medium)
                .align(Alignment.BottomEnd)
        ) {
            Row(Modifier.padding(Padding.medium)) {
                HudButton(
                    name = "playerInfoButton",
                    onClick = { openPlayerInfo = true },
                    icon_id = R.drawable.tmp_happysmile,
                    description = "See player information"
                )
                Column(Modifier.padding(Padding.medium)) {
                    HudText("playerBalance", "${playerData.getBalance()} $")
                }
            }
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
 * The HUD that shows the stats for other [Player]s and the [Game], it's a button on the top left
 * that expands and collapses a tab that contains information about the [Game] and the other
 * [Player]s
 *
 * @param otherPlayersData the [Player] data of the other players
 * @param round the current round
 *
 * @see Hud
 */
@Composable
fun HudGameAndOtherPlayers(otherPlayersData: List<Player>, round: Int) {
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
                    R.drawable.tmp_sadsmile,
                    R.drawable.tmp_happysmile
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
 * The HUD for the [Game] stats, it displays basic information such as the current round,
 * and a button shows complete information on click
 *
 * @param round the current round
 *
 * @see HudGameAndOtherPlayers
 */
@Composable
fun HudGame(round: Int) {
    var openGameInfo by remember { mutableStateOf(false) }

    Row(Modifier.padding(Padding.medium)) {
        HudButton(
            name = "gameInfoButton",
            onClick = { openGameInfo = true },
            icon_id = R.drawable.tmp_happysmile,
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
 * The HUD for the stats of other [Player]s, it displays basic information such as their balance,
 * and a button shows complete information on click
 *
 * @param playerData the [Player] data of the other player
 *
 * @see HudGameAndOtherPlayers
 */
@Composable
fun HudOtherPlayer(playerData: Player) {
    var openOtherPlayerInfo by remember { mutableStateOf(false) }
    Row(Modifier.padding(Padding.medium)) {
        HudButton(
            name = "otherPlayerInfoButton",
            onClick = { openOtherPlayerInfo = true },
            icon_id = R.drawable.tmp_happysmile,
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
 * The HUD for the [Game] menu, which is a button on the bottom left that expands and collapses
 * the menu
 *
 * @see Hud
 */
@Composable
fun HudGameMenu() {
    var openGameMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(Padding.medium)
                .align(Alignment.BottomStart)
        ) {
            Column(Modifier.padding(Padding.medium)) {
                // The game menu slides in and out when the game menu button is pressed
                AnimatedVisibility(
                    visible = openGameMenu,
                ) {
                    Surface(
                        color = MaterialTheme.colors.background,
                        shape = Shapes.medium,
                        modifier = Modifier
                            .padding(Padding.medium)
                    ) {
                        MenuComposable.RowButtons()
                    }
                }

                // The drop down button that expands and collapses the game menu
                HudButton(
                    name = "gameMenuDropDownButton",
                    onClick = { openGameMenu = !openGameMenu },
                    icon_id = R.drawable.tmp_happysmile,
                    description = "Expand or collapse the game menu"
                )
            }
        }
    }
}

/**
 * A button that is used in the HUD
 *
 * @param name the name of the button
 * @param onClick the action to perform when the button is clicked
 * @param icon_id the id of the icon to display
 * @param description the description of the button
 *
 * @see Hud
 */
@Composable
fun HudButton(name: String, onClick: () -> Unit, icon_id: Int, description: String) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .semantics { contentDescription = description }
            .testTag(name),
        shape = CircleShape,
    ) {
        Image(
            painter = painterResource(icon_id),
            contentDescription = description,
            modifier = Modifier.size(50.dp)
        )
    }
}

/**
 * A text that is used in the HUD
 *
 * @param name the name of the text
 * @param text the text to display
 *
 * @see Hud
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

/**
 * A button whose icon changes depending on a toggle
 *
 * @param name the name of the button
 * @param description the description of the button
 * @param onClick the action to perform when the button is clicked
 * @param toggle the toggle that determines which icon to display
 * @param onIcon the id of the icon to display when the toggle is true
 * @param offIcon the id of the icon to display when the toggle is false
 *
 * @see Hud
 */
@Composable
fun ToggleIconButton(
    name: String,
    description: String,
    onClick: () -> Unit,
    toggle: Boolean,
    onIcon: Int,
    offIcon: Int
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .semantics { contentDescription = description }
            .testTag(name),
        shape = CircleShape,
    )
    {
        if (toggle) {
            Image(
                painter = painterResource(onIcon),
                contentDescription = "Expand",
                modifier = Modifier.size(50.dp)
            )
        } else {
            Image(
                painter = painterResource(offIcon),
                contentDescription = "Collapse",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
