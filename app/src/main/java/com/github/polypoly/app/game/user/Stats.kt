package com.github.polypoly.app.game.user

import java.time.LocalDateTime

/**
 * All the statistics about a player
 */
data class Stats(
    val accountCreation: LocalDateTime,
    val lastConnection: LocalDateTime,
    val numberOfGames: Int,
    val numberOfWins: Int,
    val kilometersTraveled: Int,
)
