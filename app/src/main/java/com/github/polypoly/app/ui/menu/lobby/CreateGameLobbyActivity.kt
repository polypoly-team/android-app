package com.github.polypoly.app.ui.menu.lobby

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.R
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.ui.menu.MenuActivity
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_INITIAL_BALANCE_DEFAULT
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_INITIAL_BALANCE_STEP
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_MAX_INITIAL_BALANCE
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_MAX_NAME_LENGTH
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_MAX_PLAYERS
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_MAX_ROUNDS
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_MENU_PICKER_WIDTH
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_MIN_INITIAL_BALANCE
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_MIN_PLAYERS
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_MIN_ROUNDS
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_PRIVATE_DEFAULT
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants.Companion.GAME_LOBBY_ROUNDS_DEFAULT
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements
import com.github.polypoly.app.ui.theme.UIElements.BigButton
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.uniqueCodeGenerator
import java.util.concurrent.CompletableFuture

class CreateGameLobbyActivity :  MenuActivity("Create a game") {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MenuContent{
                CreateGameLobbyContent()
            }
        }
    }

    private val futureGameCode = uniqueCodeGenerator.generateUniqueGameLobbyCode()

    @Composable
    fun CreateGameLobbyContent() {

        PolypolyTheme {
            GameSettingsMenu()
        }
    }

    /**
     * The menu for creating a game
     */
    @Composable
    fun GameSettingsMenu() {
        val mContext = LocalContext.current

        var gameName by remember { mutableStateOf("") }
        var isPrivateGame by remember { mutableStateOf(GAME_LOBBY_PRIVATE_DEFAULT) }
        var minNumPlayers by remember { mutableStateOf(GAME_LOBBY_MIN_PLAYERS) }
        var maxNumPlayers by remember { mutableStateOf(GAME_LOBBY_MAX_PLAYERS) }
        var numRounds by remember { mutableStateOf(GAME_LOBBY_ROUNDS_DEFAULT) }
        var roundDuration by remember { mutableStateOf(GameLobbyConstants.RoundDurations.getDefaultValue()) }
        var gameMode by remember { mutableStateOf(GameMode.RICHEST_PLAYER) }
        var initialPlayerBalance by remember { mutableStateOf(GAME_LOBBY_INITIAL_BALANCE_DEFAULT) }

        val createGameEnabled : Boolean = gameName.isNotBlank()

        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 25.dp)
                .testTag("create_game_menu"),
        ) {
            OutlinedTextField(
                value = gameName,
                onValueChange = { newText : String ->
                    gameName =
                        if (newText.matches(Regex("[a-zA-Z\\d]*")) && newText.length <= GAME_LOBBY_MAX_NAME_LENGTH)
                            newText
                        else gameName
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                singleLine = true,
                label = { Text(getString(R.string.create_game_lobby_choose_game_name)) },
                modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.CenterHorizontally).testTag("game_name_text_field"),
                colors = UIElements.outlineTextFieldColors()
            )

            Row(
                modifier = Modifier.fillMaxWidth().testTag("private_game_row"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(getString(R.string.create_game_lobby_private_game), modifier = Modifier.weight(1f).testTag("private_game_text"))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    Checkbox(
                        modifier = Modifier.offset(x = 14.dp)
                            .testTag("private_game_checkbox"),
                        checked = isPrivateGame,
                        onCheckedChange = {isPrivateGame = !isPrivateGame},
                        colors = UIElements.checkboxColors()
                    )
                }
            }


            NumberPickerField(
                title = getString(R.string.create_game_lobby_min_num_players),
                value = minNumPlayers,
                onValueChange = {
                    minNumPlayers = if (it <= maxNumPlayers) it
                    else maxNumPlayers
                },
                minValue = GAME_LOBBY_MIN_PLAYERS,
                maxValue = maxNumPlayers
            )

            NumberPickerField(
                title = getString(R.string.create_game_lobby_max_num_players),
                value = maxNumPlayers,
                onValueChange = { if (it >= minNumPlayers) maxNumPlayers = it },
                minValue = minNumPlayers,
                maxValue = GAME_LOBBY_MAX_PLAYERS
            )

            NumberPickerField(
                title = getString(R.string.create_game_lobby_num_rounds),
                value = numRounds,
                onValueChange = { numRounds = it },
                minValue = GAME_LOBBY_MIN_ROUNDS,
                maxValue = GAME_LOBBY_MAX_ROUNDS
            )

            ListPickerField(
                title = getString(R.string.create_game_lobby_round_duration),
                value = roundDuration,
                onValueChange = { roundDuration = it },
                items = GameLobbyConstants.RoundDurations.values().toList()
            )

            ListPickerField(
                title = getString(R.string.create_game_lobby_game_mode),
                value = gameMode,
                onValueChange = { gameMode = it },
                items = GameMode.values().toList()
            )

            NumberPickerField(
                title = getString(R.string.create_game_lobby_initial_balance),
                value = initialPlayerBalance,
                onValueChange = { initialPlayerBalance = it },
                minValue = GAME_LOBBY_MIN_INITIAL_BALANCE,
                maxValue = GAME_LOBBY_MAX_INITIAL_BALANCE,
                step = GAME_LOBBY_INITIAL_BALANCE_STEP
            )

            Divider(
                modifier = Modifier.padding(vertical = Padding.large).fillMaxWidth(0.7f).align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = androidx.compose.ui.graphics.Color.Black
            )

            Column(
                modifier = Modifier.fillMaxSize()
                    .testTag("create_game_lobby_column"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getString(R.string.create_game_lobby_game_code, futureGameCode.getNow("")), // FIXME: always displays a blank code
                    Modifier.padding(bottom = 20.dp),
                )
                BigButton(
                    onClick = {
                        val rules = GameParameters(
                            gameMode = gameMode,
                            minimumNumberOfPlayers = minNumPlayers,
                            maximumNumberOfPlayers = maxNumPlayers,
                            roundDuration = roundDuration.toMinutes(),
                            maxRound = numRounds,
                            initialPlayerBalance = initialPlayerBalance
                        )
                        futureGameCode.thenApply { code ->
                            createGameLobby(mContext, rules, gameName, isPrivateGame, code)
                        }
                    },
                    text = getString(R.string.create_game_lobby_create_game),
                    enabled = createGameEnabled,
                    testTag = "create_game_lobby_button"
                )
            }

        }
    }

    /**
     * A picker field that displays numbers within a bounded range, using arrows to increment or decrement the value
     * @param title The title of the field
     * @param value The current value of the field
     * @param onValueChange The callback to be called when the value of the field changes
     * @param minValue The minimum value of the field
     * @param maxValue The maximum value of the field
     */
    @Composable
    private fun NumberPickerField(
        title: String,
        value: Int,
        onValueChange: (Int) -> Unit,
        minValue: Int,
        maxValue: Int,
        isEnabled: Boolean = true,
        step: Int = 1,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().testTag("${title}number_picker_row"),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, modifier = Modifier.weight(1f))
            ArrowButton(
                onClick = { if (value > minValue) onValueChange(value - step) },
                enabled = isEnabled && value > minValue,
                leftArrow = true,
                title = title
            )
            Text(
                text = value.toString(),
                modifier = Modifier
                    .width(GAME_LOBBY_MENU_PICKER_WIDTH)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .testTag("${title}number_picker_field"),
                textAlign = TextAlign.Center
            )
            ArrowButton(
                onClick = { if (value < maxValue) onValueChange(value + step) },
                enabled = isEnabled && value < maxValue,
                title = title
            )
        }
    }

    /**
     * A picker field that displays a list of items and allows the user to iterate through them with arrows
     * @param title The title of the field
     * @param value The current value of the field
     * @param onValueChange The callback to be called when the value of the field changes
     * @param items The list of items to be displayed
     * @param isEnabled Whether the field is enabled or not
     * @param width The width of the field
     */
    @Composable
    private fun <T> ListPickerField(
        title: String,
        value: T,
        onValueChange: (T) -> Unit,
        items: List<T>,
        isEnabled: Boolean = true,
        width : Dp = GAME_LOBBY_MENU_PICKER_WIDTH
    ) {
        val currentIndex = items.indexOf(value)

        Row(
            modifier = Modifier.fillMaxWidth().testTag("${title}list_picker_row"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, modifier = Modifier.weight(1f))
            ArrowButton(
                onClick = {
                    if (currentIndex > 0) {
                        onValueChange(items[currentIndex - 1])
                    }
                },
                enabled = isEnabled && currentIndex > 0,
                leftArrow = true,
                title = title
            )
            Text(
                text = value.toString(),
                modifier = Modifier.width(width).wrapContentWidth(Alignment.CenterHorizontally)
                    .testTag("${title}list_picker_field"),
                textAlign = TextAlign.Center
            )
            ArrowButton(
                onClick = {
                    if (currentIndex < items.lastIndex) {
                        onValueChange(items[currentIndex + 1])
                    }
                },
                enabled = isEnabled && currentIndex < items.lastIndex,
                title = title
            )
        }
    }


    /**
     * A button that displays an arrow
     */
    @Composable
    private fun ArrowButton(onClick: () -> Unit, enabled: Boolean, leftArrow : Boolean = false, title : String = "") {
        IconButton(onClick = onClick, enabled = enabled, modifier = Modifier.testTag(title + (if(leftArrow) "left_" else "right_") +"arrow")) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = (if(leftArrow) "left_" else "right_") +"arrow",
                modifier = Modifier.size(24.dp).rotate(if (leftArrow) 180f else 0f)
            )
        }
    }

    /**
     *  Creates a game lobby, registers it in DB and navigates to the game lobby screen
     */
    private fun createGameLobby(mContext : Context, rules: GameParameters, name: String, isPrivate: Boolean, gameCode: String) {

        val lobby = GameLobby(currentUser!!, rules, name, gameCode, isPrivate)

        //TODO : create game in database and navigate to game lobby screen
        val gameLobbyIntent = Intent(mContext, GameLobbyActivity::class.java)
        gameLobbyIntent.putExtra("lobby_code", "1234")
        startActivity(gameLobbyIntent)
        finish()
    }
}