package com.github.polypoly.app.game.user

import java.time.LocalDateTime

/**
 * All the statistics about a player
 */
data class Stats(
    val accountCreation: Long = 0, //> Unix-based time
    val lastConnection: Long = 0, //> Unix-based time
    val numberOfGames: Int = 0,
    val numberOfWins: Int = 0,
    val kilometersTraveled: Int = 0,
)
