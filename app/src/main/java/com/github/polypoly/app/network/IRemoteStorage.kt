package com.github.polypoly.app.network

import com.github.polypoly.app.base.game.rules_and_lobby.GameLobby
import com.github.polypoly.app.base.user.User
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

interface IRemoteStorage {
    /**
     * Retries the profile of the user with the given ID
     * @param userId ID of the user requested
     * @return a promise holding the user requested
     */
    fun getUserWithId(userId: Long): CompletableFuture<User>

    /**
     * Retries all the users currently existing
     * @return a promise holding the list of users requested
     */
    fun getAllUsers(): CompletableFuture<List<User>>

    /**
     * Retries all the user ids currently existing
     * @return a promise holding the list of all user ids
     */
    fun getAllUsersIds(): CompletableFuture<List<Long>>

    /**
     * Adds a new user
     * @return a promise holding whether the insertion succeeded or not
     */
    fun registerUser(user: User): CompletableFuture<Boolean>

    /**
     * Updates the user stored
     * @return a promise holding whether the update succeeded or not
     */
    fun updateUser(user: User): CompletableFuture<Boolean>

    /**
     * Retries the game lobby with the given ID
     * @param userId ID of the game lobby requested
     * @return a promise holding the game lobby requested
     */
    fun getGameLobbyWithCode(code: String): Future<GameLobby>

    /**
     * Retries all game lobbies existing
     * @return a promise holding the game lobby requested
     */
    fun getAllGameLobbies(): Future<List<GameLobby>>

    /**
     * Retries all game lobbies ids existing
     * @return a promise holding the game lobby requested
     */
    fun getAllGameLobbiesCodes(): Future<List<String>>

    /**
     * Registers a new game lobby
     * @param gameLobby game lobby to register
     * @return a promise holding whether the registration succeeded or not
     */
    fun registerGameLobby(gameLobby: GameLobby): Future<Boolean>

    /**
     * Updates an existing game lobby
     * @param gameLobby game lobby to update
     * @return a promise holding whether the update succeeded or not
     */
    fun updateGameLobby(gameLobby: GameLobby): Future<Boolean>
}

enum class StorageType {
    FIREBASE,
    TEST
}