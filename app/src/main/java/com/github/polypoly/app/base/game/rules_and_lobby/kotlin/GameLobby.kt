package com.github.polypoly.app.base.game.rules_and_lobby.kotlin

import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.global.Settings.Companion.DB_GAME_LOBBIES_PATH
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH

/**
 * Represent a game lobby where [User]s can join and wait for the game to start,
 * and where the [admin] can decide some [rules] about the [Game]
 * @property admin The admin of the lobby who have created the [GameLobby]
 * @property rules The rules of the future [Game]
 * @property name The name of the [GameLobby]
 * @property code The (secret) code of the [GameLobby]
 * @property private If the [GameLobby] is private or not
 */
class GameLobby(
    val admin: User = User(),
    val rules: GameRules = GameRules(),
    val name: String = "default",
    val code: String = "0",
    val private: Boolean = false,
) {

    private val currentUsersRegistered: ArrayList<User> = ArrayList()
    private var isInDB: Boolean = false
    val usersRegistered: List<User> get() = currentUsersRegistered.toList()

    init {
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
     * Call it whenever the lobby is ready to be in the DB
     */
    fun openLobby() {
        remoteDB.registerValue(DB_GAME_LOBBIES_PATH + code, this)
        isInDB = true
    }

    /**
     * Add a user to the lobby, only if the user is in the DB
     * @param user the user to add
     * @throws IllegalStateException if the game is already full
     * @throws IllegalArgumentException if the user is already registered
     */
    fun addUser(user: User) {
        if (currentUsersRegistered.size >= rules.maximumNumberOfPlayers)
            throw IllegalStateException("The game is already full")
        if (currentUsersRegistered.any{u -> u.id == user.id})
            throw java.lang.IllegalArgumentException("User $user is already registered")
        remoteDB.keyExists(DB_USERS_PROFILES_PATH + user.id).thenAccept { exists ->
            if(exists) {
                currentUsersRegistered.add(user)
                update()
            } else {
                throw java.lang.IllegalArgumentException("User id ${user.id} doesn't exist")
            }
        }
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
        return currentUsersRegistered.size in rules.minimumNumberOfPlayers..rules.maximumNumberOfPlayers
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
                update()
                return
            }
        }
        throw java.lang.IllegalArgumentException("No user found with id $withId")
    }

    /**
     * Starts the game and deletes the lobby from the DB
     * @return the game that has been started
     * @throws IllegalStateException if the game is not ready to start yet
     */
    fun start(): Game {
        if (!canStart()) {
            throw java.lang.IllegalStateException("Try to start a game not ready to start yet")
        }
        end()
        return Game.launchFromPendingGame(this)
    }

    /**
     * Deletes the lobby from the DB
     */
    fun end() {/*TODO*/}

    /**
     * Updates the lobby in the DB with the current information
     */
    private fun update() {
        if(isInDB) {
            remoteDB.updateValue(DB_GAME_LOBBIES_PATH + code, this)
        }
    }
}