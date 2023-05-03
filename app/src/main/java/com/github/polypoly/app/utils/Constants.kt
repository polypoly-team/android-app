package com.github.polypoly.app.utils

import androidx.compose.ui.unit.dp

/**
 * Constants used in the app
 */
class Constants {

    companion object {
        /**
         * Constants for the code generator
         */
        const val GAME_LOBBY_CODE_LENGTH = 5
        const val GAME_LOBBY_MAX_CHARACTERS = "1234567890"

        /**
         * Constants for game lobby settings
         */
        const val GAME_LOBBY_PRIVATE_DEFAULT = false
        const val GAME_LOBBY_MAX_PLAYERS = 8
        const val GAME_LOBBY_MIN_PLAYERS = 2
        const val GAME_LOBBY_MAX_ROUNDS = 30
        const val GAME_LOBBY_MIN_ROUNDS = 2
        const val GAME_LOBBY_ROUNDS_DEFAULT = 5
        const val GAME_LOBBY_MIN_INITIAL_BALANCE = 100
        const val GAME_LOBBY_MAX_INITIAL_BALANCE = 15000
        const val GAME_LOBBY_INITIAL_BALANCE_STEP = 500
        const val GAME_LOBBY_INITIAL_BALANCE_DEFAULT = 2500
        //TODO make an enum
        const val GAME_LOBBY_ROUND_DURATION_DEFAULT = "1 day"
        val GAME_LOBBY_ROUNDS_DURATIONS = mapOf(
            "5 min" to 5, "10 min" to 5, "15 min" to 5, "20 min" to 5, "25 min" to 5, "30 min" to 5,
            "1 hour" to 5, "2 hours" to 5, "3 hours" to 5, "4 hours" to 5, "5 hours" to 5, "10 hours" to 5,
            "15 hours" to 5, "20 hours" to 5, "1 day" to 5, "2 days" to 5, "3 days" to 5, "4 days" to 5,
            "5 days" to 5, "6 days" to 5, "7 days" to 5, "8 days" to 5, "9 days" to 5, "10 days" to 5
        )

        /**
         * Constants for game lobby menu UI
         */
        val GAME_LOBBY_MENU_PICKER_WIDTH = 65.dp
    }

}