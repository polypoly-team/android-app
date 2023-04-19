package com.github.polypoly.app.base.game.rules_and_lobby.kotlin

import com.github.polypoly.app.base.game.location.Zone
import com.github.polypoly.app.map.LocationRepository
import java.io.Serializable
import kotlin.time.Duration

/**
 * A class that represent the rules of a [Game]
 * @property gameMode The game mode of the future game
 * @property minimumNumberOfPlayers The minimum number of players needed to start the game
 * @property maximumNumberOfPlayers The maximum number of players that can join the lobby
 * @property roundDuration The duration of a round in the
 * @property maxRound The maximum number of round before the game end. This settings is available
 * only in RICHEST_PLAYER mode. Is null if an other game mode is selected.
 * @property gameMap The map of the game whit the different zones available
 * @property initialPlayerBalance The initial balance of money of the players
 */
data class GameRules (
    val gameMode: GameMode = GameMode.LAST_STANDING,
    val minimumNumberOfPlayers: Int = 2,
    val maximumNumberOfPlayers: Int = 2,
    val roundDuration: Int = 1,
    val maxRound: Int? = null,
    val gameMap: List<Zone> = LocationRepository.getZones(),
    val initialPlayerBalance: Int = 0,
) {

    private val maxRoundHours = 24

    init {
        if (minimumNumberOfPlayers <= 1)
            throw java.lang.IllegalArgumentException("At least 2 players are needed for a game (provided $minimumNumberOfPlayers)")
        if (maximumNumberOfPlayers < minimumNumberOfPlayers)
            throw java.lang.IllegalArgumentException("Maximum number of players $maximumNumberOfPlayers must be greater than the minimum number $minimumNumberOfPlayers")
        if (roundDuration <= 0 || roundDuration >= maxRoundHours)
            throw java.lang.IllegalArgumentException("Invalid game duration $roundDuration")
    }
}