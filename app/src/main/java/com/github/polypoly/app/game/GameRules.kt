package com.github.polypoly.app.game

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
    val gameMode: GameMode,
    val minimumNumberOfPlayers: Int,
    val maximumNumberOfPlayers: Int,
    val roundDuration: Duration,
    val maxRound: Int? = null,
    val gameMap: List<Zone>,
    val initialPlayerBalance: Int,
) {

    init {
        if (minimumNumberOfPlayers <= 1)
            throw java.lang.IllegalArgumentException("At least 2 players are needed for a game (provided $minimumNumberOfPlayers)")
        if (maximumNumberOfPlayers < minimumNumberOfPlayers)
            throw java.lang.IllegalArgumentException("Maximum number of players $maximumNumberOfPlayers must be greater than the minimum number $minimumNumberOfPlayers")
        if (roundDuration.isNegative() || roundDuration.isInfinite())
            throw java.lang.IllegalArgumentException("Invalid game duration$roundDuration")
    }
}