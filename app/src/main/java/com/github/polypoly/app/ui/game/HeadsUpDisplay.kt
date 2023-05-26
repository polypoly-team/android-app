package com.github.polypoly.app.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.ui.map.MapViewModel
import com.github.polypoly.app.ui.menu.MenuComposable
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.Shapes
import com.github.polypoly.app.utils.Constants.Companion.NOTIFICATION_DURATION
import com.github.polypoly.app.viewmodels.game.GameViewModel

/**
 * The heads-up display with player and game stats that is displayed on top of the map
 * @param playerData current player of the game
 * @param gameViewModel GameViewModel to use for game business logic
 * @param mapViewModel GameViewModel to use for map business logic
 * @param otherPlayersData other players in the game
 * @param round current round of the game
 * @param location current location of the player
 */
@Composable
fun Hud(
    playerData: Player,
    gameViewModel: GameViewModel,
    mapViewModel: MapViewModel,
    otherPlayersData: List<Player>,
    location: String,
    gameModel: GameViewModel
) {
    val playerState = gameViewModel.getPlayerStateData().observeAsState().value
    val goingToLocation = mapViewModel.goingToLocationPropertyData.value?.name ?: "EPFL"

    SuccessfulBidNotification(gameViewModel, NOTIFICATION_DURATION)
    TaxToPayNotification(gameViewModel, mapViewModel)
    HudLocation(location, testTag = "interactable_location_text")
    if (playerState == PlayerState.MOVING) {
        HudLocation(
            goingToLocation,
            DpOffset(0.dp, 140.dp),
            "going_to_location_text",
            "Going to"
        )
    } else if (playerState == PlayerState.TURN_FINISHED) {
        TurnFinishedNotification()
    }
    HudPlayer(playerData, gameModel)
    HudOtherPlayersAndGame(otherPlayersData, gameModel)
    HudGameMenu()
    MilestoneEventConsumer(milestonesToDisplay = mapViewModel.newMilestonesToDisplay)
}

/**
 * The HUD for the current nearby location (a text at the top of the screen)
 * @param location current location of the player
 * @param offset offset of the text
 * @param testTag test tag for the text in the Hud
 * @param headerText text to display before the location
 */
@Composable
fun HudLocation(
    location: String, 
    offset: DpOffset = DpOffset(0.dp, 10.dp), 
    testTag: String,
    headerText: String = "Current location"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Padding.medium)
    ) {
        Box(
            modifier = Modifier.align(TopCenter),
            contentAlignment = Center
        ) {
            if (location.isNotEmpty())
                Text(
                    text = headerText,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(offset.x, offset.y)
                        .background(MaterialTheme.colors.background, shape = Shapes.medium)
                        .padding(Padding.medium),
                    style = MaterialTheme.typography.h6
                )
            Text(
                text = location,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .testTag(testTag)
                    .offset(offset.x, offset.y + 50.dp)
                    .background(MaterialTheme.colors.background, shape = Shapes.medium)
                    .padding(Padding.medium),
                style = MaterialTheme.typography.h4,
            )
        }
    }
}

/**
 * The HUD for the player stats, it displays basic information such as their balance,
 * and a button shows complete information on click
 * @param playerData current player of the game
 */
@Composable
fun HudPlayer(playerData: Player, gameModel: GameViewModel) {
    var openPlayerInfo by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(BottomEnd)
                .testTag("hud_player"),
            contentAlignment = Center
        ) {
            MoneyHudText("playerBalance", "${playerData.getBalance()} $ ")
            HudButton(
                testTag = "player_info_button",
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
            val playerOwnedLocations = gameModel.locationsOwnedData.observeAsState().value ?: listOf()
            Column(Modifier.padding(Padding.medium)) {
                StatsHeader(textContent = "${playerData.user.name}'s stats")
                StatsCategory(textContent = "Balance")
                StatsData(textContent = "${playerData.getBalance()} $")
                StatsCategory(textContent = "Owned locations")
                playerOwnedLocations.forEach {
                    StatsData(textContents = listOf(
                        it.locationProperty.name,
                        "${it.level}",
                        "${it.currentTax()}$ tax")
                    )
                }
            }
        }
    }
}

/**
 * The HUD that shows the stats for other players and the game, it's a button on the top left
 * that expands and collapses a tab that contains information about the game and the other
 * players
 * @param otherPlayersData other players in the game
 * @param round current round of the game
 * @param gameModel GameViewModel associated with the game
 */
@Composable
fun HudOtherPlayersAndGame(otherPlayersData: List<Player>, gameModel: GameViewModel) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(Padding.medium)
                .align(Alignment.TopStart)
                .testTag("hud_other_players_and_game"),
        ) {
            Column(Modifier.padding(Padding.medium)) {
                // A drop down button that expands and collapses the stats for other players and
                // the game
                ToggleIconButton(
                    "other_players_and_game_hud",
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
                            HudGame(gameModel)
                            otherPlayersData.forEach {
                                HudOtherPlayer(it, gameModel)
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
fun HudGame(gameModel: GameViewModel) {
    var openGameInfo by remember { mutableStateOf(false) }

    Row(Modifier.padding(Padding.medium)) {
        HudButton(
            testTag = "game_info_button",
            onClick = { openGameInfo = true },
            icon = Icons.Filled.Info,
            description = "See game information"
        )
        Column(Modifier.padding(Padding.medium)) {
            HudText("gameRound", text = "Round ${gameModel.getRoundTurnData().observeAsState().value}")
        }
    }

    if (openGameInfo) {
        Dialog(
            onDismissRequest = { openGameInfo = false },
        ) {
            Column(Modifier.padding(Padding.medium)) {
                StatsHeader(textContent = "Game stats")
                StatsCategory(textContent = "Rules")
                StatsData(textContent = "${game?.rules?.gameMode}")
                StatsCategory(textContent = "Current round")
                StatsData(textContent = "${gameModel.getRoundTurnData().observeAsState().value}")

            }
        }
    }
}

/**
 * The HUD for the stats of other players, it displays basic information such as their balance,
 * and a button shows complete information on click
 * @param playerData The data of the player to display
 * @param gameModel The game view model
 */
@Composable
fun HudOtherPlayer(playerData: Player, gameModel: GameViewModel) {
    val game = gameModel.getGameData().observeAsState().value ?: return

    val openOtherPlayerInfo = remember { mutableStateOf(false) }

    Row(Modifier.padding(Padding.medium)) {
        HudButton(
            testTag = "other_player_hud",
            onClick = { openOtherPlayerInfo.value = true },
            icon = Icons.Filled.Person,
            description = "See other player information"
        )
        Column(Modifier.padding(Padding.medium)) {
            HudText("playerName-${playerData.user.name}", playerData.user.name)
            HudText("playerBalance", "${playerData.getBalance()} $")
        }
    }

    val openLocationsDialog =  remember{ mutableStateOf(false) }
    if (openLocationsDialog.value) {
        GameRepository.player?.let { player ->
            LocationsDialog(title = "Choose a location to trade", openLocationsDialog, game.getOwnedLocations(player) ) { location ->
                openLocationsDialog.value = false
                gameModel.createATradeRequest(playerData, location)
            } }
    }

    // LANDLORD ONLY: Asking for a trade
    if(openOtherPlayerInfo.value && game.rules.gameMode == GameMode.LANDLORD) {
        val player = GameRepository.player
        if(player != null) {
            AskingForATrade(openOtherPlayerInfo, openLocationsDialog, player, game)
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
                .testTag("hud_game_menu")
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
                    testTag = "gameMenuDropDownButton",
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
fun HudButton(testTag: String, onClick: () -> Unit, icon: ImageVector, description: String) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .semantics { contentDescription = description }
            .testTag(testTag)
            .padding(bottom = Padding.large),
        shape = CircleShape,
        elevation = ButtonDefaults.elevation(0.dp),
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
    Box(modifier = Modifier.offset(x = (-60).dp, y = -(7).dp)) {
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
    testTag: String,
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
            .testTag(testTag),
        shape = CircleShape,
        elevation = ButtonDefaults.elevation(0.dp),
    )
    {
        if (toggle)
            Icon(onIcon, contentDescription = null, modifier = Modifier.size(50.dp))
        else
            Icon(offIcon, contentDescription = null, modifier = Modifier.size(50.dp))
    }
}

@Composable
private fun StatsHeader(textContent: String) {
    CustomCardSingleText(cardColor = MaterialTheme.colors.primary, textColor = MaterialTheme.colors.onPrimary, textContent = textContent)
}

@Composable
private fun StatsCategory(textContent: String) {
    CustomCardSingleText(cardColor = MaterialTheme.colors.secondaryVariant, textColor = MaterialTheme.colors.onSecondary, textContent = textContent)
}

@Composable
private fun StatsData(textContent: String) {
    CustomCardSingleText(cardColor = MaterialTheme.colors.background, textColor = MaterialTheme.colors.onBackground, textContent = textContent)
}

@Composable
private fun StatsData(textContents: List<String>) {
    CustomCardMultipleText(cardColor = MaterialTheme.colors.background, textColor = MaterialTheme.colors.onBackground, textContents = textContents)
}

@Composable
private fun CustomCardSingleText(cardColor: Color, textColor: Color, textContent: String) {
    CustomCard(cardColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = {
                Text(
                    text = textContent,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = Padding.medium, bottom = Padding.medium),
                    color = textColor
                )
            }
        )
    }
}

@Composable
private fun CustomCardMultipleText(cardColor: Color, textColor: Color, textContents: List<String>) {
    CustomCard(cardColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = {
                for (textContent in textContents) {
                    Text(
                        text = textContent,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = Padding.large, top = Padding.medium, bottom = Padding.medium, end = Padding.large),
                        color = textColor
                    )
                }
            }
        )
    }
}

@Composable
private fun CustomCard(cardColor: Color, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            // Flat cards
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        content()
    }
}

@Composable
fun TurnFinishedNotification() {
    Box (modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .offset(y = 140.dp)){
        Text(
            text = "You finished your turn, waiting for the next one...",
            modifier = Modifier
                .testTag("turn_finished_notification")
                .background(MaterialTheme.colors.background, shape = Shapes.medium)
                .padding(Padding.medium)
                .align(TopCenter),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground
        )
    }
}
