package com.github.polypoly.app.game

import com.github.polypoly.app.game.user.User

data class GameLobby(
    val admin: User = User(),
    val gameMode: GameMode = GameMode.LAST_STANDING,
    val minimumNumberOfPlayers: Int = 2,
    val maximumNumberOfPlayers: Int = Int.MAX_VALUE,
    val roundDuration: Long = 0, //> unix timestamp encoding
    val gameMap: List<Zone> = listOf(),
    val initialPlayerBalance: Int = 0,
    val name: String = "default-lobby-instance",
    val code: String = "default-lobby-code",
    val private: Boolean = false
) {

    val usersRegistered: ArrayList<User> = ArrayList()

    init {
        if (minimumNumberOfPlayers <= 1)
            throw java.lang.IllegalArgumentException("At least 2 players are needed for a game (provided $minimumNumberOfPlayers)")
        if (maximumNumberOfPlayers < minimumNumberOfPlayers)
            throw java.lang.IllegalArgumentException("Maximum number of players $maximumNumberOfPlayers must be greater than the minimum number $minimumNumberOfPlayers")
        if (roundDuration < 0)
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
        if (usersRegistered.size >= maximumNumberOfPlayers)
            throw IllegalStateException("The game is already full")
        if (usersRegistered.any{ u -> u.id == user.id})
            throw java.lang.IllegalArgumentException("User $user is already registered")
        usersRegistered.add(user)
    }

    fun addUsers(users: List<User>) {
        for (user in users)
            addUser(user)
    }

    fun canStart(): Boolean {
        return usersRegistered.size in minimumNumberOfPlayers..maximumNumberOfPlayers
    }

    fun removeUser(withId: Long) {
        for (i in 0 until usersRegistered.size) {
            if (usersRegistered[i].id == withId) {
                usersRegistered.removeAt(i)
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