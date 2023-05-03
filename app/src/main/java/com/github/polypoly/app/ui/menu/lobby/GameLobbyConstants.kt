package com.github.polypoly.app.ui.menu.lobby

import androidx.compose.ui.unit.dp

/**
 * Constants used in the app
 */
class GameLobbyConstants {
    companion object {

        /**
         * Constants for the lobby code generator
         */
        const val GAME_LOBBY_CODE_LENGTH = 5
        const val GAME_LOBBY_CHARACTERS = "1234567890"

        /**
         * Constants for game lobby settings
         */
        const val GAME_LOBBY_MAX_NAME_LENGTH = 15
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
            "5 min" to 5, "10 min" to 10, "15 min" to 15, "20 min" to 20, "25 min" to 25, "30 min" to 30,
            "1 hour" to 60, "2 hours" to 120, "3 hours" to 180, "4 hours" to 240, "5 hours" to 300, "10 hours" to 600,
            "15 hours" to 900, "20 hours" to 1200, "1 day" to 1440, "2 days" to 2880, "3 days" to 4320, "4 days" to 5760,
            "5 days" to 7200, "6 days" to 8640, "7 days" to 10080, "8 days" to 11520, "9 days" to 12960, "10 days" to 14400
        )

        /**
         * Constants for game lobby menu UI
         */
        val GAME_LOBBY_MENU_PICKER_WIDTH = 65.dp


    }

    enum class DurationType(){
        HOUR, DAY, MINUTE;
    }

    /**
     * Enum for the round durations
     */
    enum class RoundDurations(private val value: Int, private val durationType: DurationType) {
        FIVE_MINUTES(5, DurationType.MINUTE),
        TEN_MINUTES(10, DurationType.MINUTE),
        FIFTEEN_MINUTES(15, DurationType.MINUTE),
        TWENTY_MINUTES(20, DurationType.MINUTE),
        TWENTY_FIVE_MINUTES(25, DurationType.MINUTE),
        THIRTY_MINUTES(30, DurationType.MINUTE),
        ONE_HOUR(1, DurationType.HOUR),
        TWO_HOURS(2, DurationType.HOUR),
        THREE_HOURS(3, DurationType.HOUR),
        FOUR_HOURS(4, DurationType.HOUR),
        FIVE_HOURS(5, DurationType.HOUR),
        TEN_HOURS(10, DurationType.HOUR),
        FIFTEEN_HOURS(15, DurationType.HOUR),
        TWENTY_HOURS(20, DurationType.HOUR),
        ONE_DAY(1, DurationType.DAY),
        TWO_DAYS(2, DurationType.DAY),
        THREE_DAYS(3, DurationType.DAY),
        FOUR_DAYS(4, DurationType.DAY),
        FIVE_DAYS(5, DurationType.DAY),
        SIX_DAYS(6, DurationType.DAY),
        SEVEN_DAYS(7, DurationType.DAY),
        EIGHT_DAYS(8, DurationType.DAY),
        NINE_DAYS(9, DurationType.DAY),
        TEN_DAYS(10, DurationType.DAY);

        /**
         * Converts the duration to a string
         */
        override fun toString(): String {
            return when (durationType) {
                DurationType.MINUTE -> "$value min"
                DurationType.HOUR -> "$value hour${if (value > 1) "s" else ""}"
                DurationType.DAY -> "$value day${if (value > 1) "s" else ""}"
            }
        }

        /**
         * Converts the duration to minutes
         */
        fun toMinutes(): Int {
            return when (durationType) {
                DurationType.MINUTE -> value
                DurationType.HOUR -> value * 60
                DurationType.DAY -> value * 60 * 24
            }
        }

        companion object{

            /**
             * default value for the round duration
             */
            fun getDefaultValue(): RoundDurations{
                return ONE_DAY
            }
        }

    }


}
