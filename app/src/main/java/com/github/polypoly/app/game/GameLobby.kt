package com.github.polypoly.app.game

import com.github.polypoly.app.game.user.User
import kotlin.time.Duration

/**
 * Represent a game lobby where users can join and wait for the game to start,
 * and where the admin can decide some rules about the game
 */
class GameLobby(
    /**
     * The admin of the lobby who have created the game lobby
     */
    val admin: User,

    /**
     * The game mode of the future game
     */
    val gameMode: GameMode,

    /**
     * The minimum number of players needed to start the game
     */
    val minimumNumberOfPlayers: Int,

    /**
     * The maximum number of players that can join the lobby
     */
    val maximumNumberOfPlayers: Int,

    /**
     * The duration of a round in the game
     */
    val roundDuration: Duration,

    /**
     * The map of the game whit the different zones available
     */
    val gameMap: List<Zone>,

    /**
     * The initial balance of money of the players
     */
    val initialPlayerBalance: Int,

    /**
     * The name of the lobby
     */
    val name: String,

    /**
     * The (secret) code of the lobby
     */
    val code: String,

    /**
     * If the lobby is private or not
     */
    val private: Boolean = false
) {

    private val currentUsersRegistered: ArrayList<User> = ArrayList()
    val usersRegistered: List<User> get() = currentUsersRegistered.toList()

    init {
        if (minimumNumberOfPlayers <= 1)
            throw java.lang.IllegalArgumentException("At least 2 players are needed for a game (provided $minimumNumberOfPlayers)")
        if (maximumNumberOfPlayers < minimumNumberOfPlayers)
            throw java.lang.IllegalArgumentException("Maximum number of players $maximumNumberOfPlayers must be greater than the minimum number $minimumNumberOfPlayers")
        if (roundDuration.isNegative() || roundDuration.isInfinite())
            throw java.lang.IllegalArgumentException("Invalid game duration$roundDuration")
        if (name.isEmpty())
            throw java.lang.IllegalArgumentException("Game name cannot be empty")
        if (name.isBlank())
            throw java.lang.IllegalArgumentException("Game name cannot be blank")
        if (code.isEmpty())
            throw java.lang.IllegalArgumentException("Game code cannot be empty")
        if (code.isBlank())
            throw java.lang.IllegalArgumentException("Game code cannot be blank")
        addUser(admin)
    }

    /**
     * Add a user to the lobby
     * @param user the user to add
     * @throws IllegalStateException if the game is already full
     * @throws IllegalArgumentException if the user is already registered
     */
    fun addUser(user: User) {
        if (currentUsersRegistered.size >= maximumNumberOfPlayers)
            throw IllegalStateException("The game is already full")
        if (currentUsersRegistered.any{u -> u.id == user.id})
            throw java.lang.IllegalArgumentException("User $user is already registered")
        currentUsersRegistered.add(user)
    }

    /**
     * Add a list of users to the lobby
     * @param users the list of users to add
     * @throws IllegalStateException if the game cannot all the users
     * @throws IllegalArgumentException if one of the users is already registered
     */
    fun addUsers(users: List<User>) {
        for (user in users)
            addUser(user)
    }

    /**
     * Check if the game is ready to start
     * @return true if the game is ready to start, false otherwise
     */
    fun canStart(): Boolean {
        return currentUsersRegistered.size in minimumNumberOfPlayers..maximumNumberOfPlayers
    }

    /**
     * Remove a user from the lobby
     * @param withId the id of the user to remove
     * @throws IllegalArgumentException if no user with the given id is not in the lobby
     */
    fun removeUser(withId: Long) {
        for (i in 0 until currentUsersRegistered.size) {
            if (currentUsersRegistered[i].id == withId) {
                currentUsersRegistered.removeAt(i)
                return
            }
        }
        throw java.lang.IllegalArgumentException("No user found with id $withId")
    }

    /**
     * Start the game
     * @return the game that has been started
     * @throws IllegalStateException if the game is not ready to start yet
     */
    fun start(): Game {
        if (!canStart())
            throw java.lang.IllegalStateException("Try to start a game not ready to start yet")
        return Game.launchFromPendingGame(this)
    }
}