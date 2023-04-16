package com.github.polypoly.app.game.user

/**
 * All the statistics about a player
 */
data class Stats(
    /**
     * The date of the creation of the user account in Unix-based time
     */
    val accountCreation: Long = 0,

    /**
     * The date of the last connection of the user in Unix-based time
     */
    val lastConnection: Long = 0,

    /**
     * The number of games played by the user
     */
    val numberOfGames: Int = 0,

    /**
     * The number of games won by the user (rank 1)
     */
    val numberOfWins: Int = 0,

    /**
     * The number of kilometers traveled by the user during the games
     */
    val kilometersTraveled: Int = 0,
)
