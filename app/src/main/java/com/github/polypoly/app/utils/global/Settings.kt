package com.github.polypoly.app.utils.global

/**
 * Global settings shared across the entire app for consistency purpose
 */
class Settings {
    companion object {
        ////////////////////// Game-related constants

        const val NUMBER_OF_LOCATIONS_ROLLED = 3

        ////////////////////// DB-related constants
        const val DB_USERS_PROFILES_PATH = "users/"

        const val DB_GAME_LOBBIES_PATH = "lobbies/"

        const val DB_GAMES_PATH = "games/"

        const val DB_TRADE_REQUESTS_PATH = "trade_requests/"
    }
}