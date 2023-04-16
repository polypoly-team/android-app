package com.github.polypoly.app.game

import com.github.polypoly.app.game.user.User

/**
 * A class that represent a past game played by users
 */
data class PastGame(

    /**
     * The id of the users that played the game
     */
    private val users: List<User> = listOf(),

    /**
     * The rank of the users in the game by id
     */
    private val usersRank: Map<Long, Int> = mapOf(),

    /**
     * The date when the game began in Unix-based time
     */
    val date: Long = 0,

    /**
     * The duration of the game in seconds
     */
    val duration: Long = 0,
) {

    /**
     * Return if the user has won the game
     * @param userId the id of the user
     * @return true if the user has won the game, false otherwise
     */
    fun hasWin(userId: Long): Boolean {
        return usersRank[userId] == 1
    }

    /**
     * get the user id of the winner
     * @return the user id of the winner
     */
    fun getWinnerId(): Long {
        return usersRank.filterValues { it == 1 }.keys.first()
    }

    /**
     * get the users that played the game
     * @return a list of the users that played the game
     */
    fun getUsers(): List<User> {
        return users
    }

    /**
     * get the rank of the user
     * @param userId the id of the user
     * @return the rank of the user or 0 if the user is not in the game
     */
    fun getRank(userId: Long): Int {
        return usersRank[userId] ?: 0
    }

    /**
     * get the date when the game ended
     * @return the date when the game ended in Unix-based time
     */
    fun getEndGameDate(): Long {
        return date + duration
    }
}