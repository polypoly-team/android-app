package com.github.polypoly.app.base.menu.lobby

import com.github.polypoly.app.base.game.location.Zone
import com.github.polypoly.app.base.game.location.LocationRepository

/**
 * A class that represent the parameters of a [Game]
 * @property gameMode The game mode of the future game
 * @property minimumNumberOfPlayers The minimum number of players needed to start the game
 * @property maximumNumberOfPlayers The maximum number of players that can join the lobby
 * @property roundDuration The duration of a round in the
 * @property maxRound The maximum number of round before the game end. This settings is available
 * only in RICHEST_PLAYER mode. Is null if an other game mode is selected.
 * @property gameMap The map of the game whit the different zones available
 * @property initialPlayerBalance The initial balance of money of the players
 */
data class GameParameters (
    val gameMode: GameMode = GameMode.RICHEST_PLAYER,
    val minimumNumberOfPlayers: Int = 3,
    val maximumNumberOfPlayers: Int = 7,
    val roundDuration: Int = 360,
    val maxRound: Int? = null,
    val gameMap: List<Zone> = LocationRepository.getZones(),
    val initialPlayerBalance: Int = 500,
) {

    // TODO: define real value as this is useless
    private val maxRoundHours = Int.MAX_VALUE

    init {
        if (minimumNumberOfPlayers <= 1)
            throw java.lang.IllegalArgumentException("At least 2 players are needed for a game (provided $minimumNumberOfPlayers)")
        if (maximumNumberOfPlayers < minimumNumberOfPlayers)
            throw java.lang.IllegalArgumentException("Maximum number of players $maximumNumberOfPlayers must be greater than the minimum number $minimumNumberOfPlayers")
        if (roundDuration <= 0 || roundDuration >= maxRoundHours)
            throw java.lang.IllegalArgumentException("Invalid game duration$roundDuration")
    }
}