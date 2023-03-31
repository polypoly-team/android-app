package com.github.polypoly.app.game.user

import java.time.LocalDateTime

/**
 * All stats concerning a given player.
 * This will be further developed in the future.
 */
data class Stats constructor(
    val accountCreation: Int = 0, //> TODO improve unix-representation to something better
    val lastConnection: Int = 0,
    val numberOfGames: Int = 0,
    val numberOfWins: Int = 0,
    val kilometersTraveled: Int = 0,
) {
    override fun equals(other: Any?): Boolean {
        return other is Stats &&
                accountCreation == other.accountCreation && lastConnection == other.lastConnection &&
                numberOfWins == other.numberOfWins
    }
}
