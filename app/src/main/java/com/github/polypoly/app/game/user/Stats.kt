package com.github.polypoly.app.game.user

/**
 * All the statistics about a player
 * @property accountCreation The date of the creation of the user account in Unix-based time
 * @property lastConnection The date of the last connection of the user in Unix-based time
 * @property numberOfGames The number of games played by the user
 * @property numberOfWins The number of games won by the user (rank 1)
 * @property kilometersTraveled The number of kilometers traveled by the user during the games
 */
data class Stats(
    val accountCreation: Long = 0,
    val lastConnection: Long = 0,
    val numberOfGames: Int = 0,
    val numberOfWins: Int = 0,
    val kilometersTraveled: Int = 0,
)
