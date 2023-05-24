package com.github.polypoly.app.base.menu.lobby

import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.network.StorableObject
import com.github.polypoly.app.network.getValues
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.utils.global.Settings.Companion.DB_GAME_LOBBIES_PATH
import java.util.concurrent.CompletableFuture

/**
 * Represent a game lobby where [User]s can join and wait for the game to start,
 * and where the [admin] can decide some [rules] about the [Game]
 * @property admin The admin of the lobby who have created the [GameLobby]
 * @property rules The rules of the future [Game]
 * @property name The name of the [GameLobby]
 * @property code The (secret) code of the [GameLobby]
 * @property private If the [GameLobby] is private or not
 * @property started If the [Game] has started or not
 */
data class GameLobby(
    val admin: User = User(),
    val rules: GameParameters = GameParameters(),
    val name: String = "defaultName",
    val code: String = "defaultCode",
    val private: Boolean = false,
    var started: Boolean = false
): StorableObject<GameLobbyDB>(GameLobbyDB::class, DB_GAME_LOBBIES_PATH, code) {

    private val currentUsersRegistered: ArrayList<User> = ArrayList()
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
     * TODO: only add if user is in DB
     *
     * Add a user to the lobby, only if the user is in the DB
     * @param user the user to add
     * @throws IllegalStateException if the game is already full
     * @throws IllegalArgumentException if the user is already registered
     */
    fun addUser(user: User) {
        if (currentUsersRegistered.size >= rules.maximumNumberOfPlayers)
            throw IllegalStateException("The game is already full")
        if (currentUsersRegistered.any{ u -> u.id == user.id})
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
        return currentUsersRegistered.size in rules.minimumNumberOfPlayers..rules.maximumNumberOfPlayers
    }

    /**
     * Remove a user from the lobby
     * @param withId the id of the user to remove
     * @throws IllegalArgumentException if no user with the given id is not in the lobby
     */
    fun removeUser(withId: String) {
        for (i in 0 until currentUsersRegistered.size) {
            if (currentUsersRegistered[i].id == withId) {
                currentUsersRegistered.removeAt(i)
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
        started = true
        val game = Game.launchFromPendingGame(this)
        GameRepository.game = game
        return game
    }

    // ====================================================================== STORABLE

    // TODO: add tests for converters
    override fun toDBObject(): GameLobbyDB {
        return GameLobbyDB(
            code,
            name,
            private,
            rules,
            currentUsersRegistered.map { user -> user.id },
            admin.id,
            started
        )
    }

    override fun toLocalObject(dbObject: GameLobbyDB): CompletableFuture<StorableObject<GameLobbyDB>> {
        return remoteDB.getValues<User>(dbObject.userIds).thenApply { users ->
            val lobby = GameLobby(
                users.first { user -> user.id == dbObject.adminId },
                dbObject.parameters,
                dbObject.name,
                dbObject.code,
                dbObject.private,
                dbObject.started
            )
            lobby.addUsers(users.filter { user -> user.id != dbObject.adminId })
            lobby
        }
    }
}

// TODO: add tests for data class
data class GameLobbyDB(
    val code: String = "",
    val name: String = "",
    val private: Boolean = false,
    val parameters: GameParameters = GameParameters(),
    val userIds: List<String> = listOf(""),
    val adminId: String = "",
    val started: Boolean = false
) {
    init {
        if(!userIds.contains(adminId)) {
            throw IllegalArgumentException("Admin id must be in user ids")
        }
    }
}