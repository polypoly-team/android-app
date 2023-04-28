package com.github.polypoly.app.base.menu

import com.github.polypoly.app.base.user.User

/**
 * A class that represent a past [Game] played by [User]s
 * @property users The id of the [User]s that played the game
 * @property usersRank The rank of the [User]s in the [Game] by id
 * @property date The date when the [Game] began in Unix-based time
 * @property duration The duration of the [Game] in seconds
 */
data class PastGame(
    private val users: List<User> = listOf(),
    private val usersRank: Map<Long, Int> = mapOf(),
    val date: Long = 0,
    val duration: Long = 0,
) {

    /**
     * Return if the [User] has won the [Game]
     * @param userId the id of the [User]
     * @return true if the user has won the [Game], false otherwise
     */
    fun hasWon(userId: Long): Boolean {
        return usersRank[userId] == 1
    }

    /**
     * get the [User] id of the winner
     * @return the [User] id of the winner
     */
    fun getWinnerId(): Long {
        return usersRank.filterValues { it == 1 }.keys.first()
    }

    /**
     * get the [User]s that played the [PastGame]
     * @return a [List] of the [User]s that played the [PastGame]
     */
    fun getUsers(): List<User> {
        return users
    }

    /**
     * get the rank of the [User]
     * @param userId the id of the [User]
     * @return the rank of the [User]
     * @throws IllegalArgumentException if the [User] is not in the [Game]
     */
    fun getRank(userId: Long): Int {
        return usersRank[userId] ?: throw IllegalArgumentException("User not in game")
    }

    /**
     * get the date when the [Game] ended
     * @return the date when the game ended in Unix-based time
     */
    fun getEndGameDate(): Long {
        return date + duration
    }
}