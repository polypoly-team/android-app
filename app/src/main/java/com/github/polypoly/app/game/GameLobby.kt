package com.github.polypoly.app.game

import kotlin.time.Duration

class GameLobby(
    val admin: User,
    val gameMode: GameMode,
    val minimumNumberOfPlayers: Int,
    val maximumNumberOfPlayers: Int,
    val roundDuration: Duration,
    val gameMap: List<Zone>,
    val initialPlayerBalance: Int,
    val name: String,
    val code: String
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

    fun addUser(user: User) {
        if (currentUsersRegistered.size >= maximumNumberOfPlayers)
            throw IllegalStateException("The game is already full")
        if (currentUsersRegistered.any{u -> u.id == user.id})
            throw java.lang.IllegalArgumentException("User $user is already registered")
        currentUsersRegistered.add(user)
    }

    fun canStart(): Boolean {
        return currentUsersRegistered.size in minimumNumberOfPlayers..maximumNumberOfPlayers
    }

    fun removeUser(withId: Long) {
        for (i in 0 until currentUsersRegistered.size) {
            if (currentUsersRegistered[i].id == withId) {
                currentUsersRegistered.removeAt(i)
                return
            }
        }
        throw java.lang.IllegalArgumentException("No user found with id $withId")
    }

    fun start(): Game {
        if (!canStart())
            throw java.lang.IllegalStateException("Try to start a game not ready to start yet")
        return Game.launchFromPendingGame(this)
    }
}