package com.github.polypoly.app.network

import com.github.polypoly.app.game.User
import java.util.concurrent.CompletableFuture

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
     * Adds a new user
     * @return a promise holding whether the insertion succeeded or not
     */
    fun registerUser(user: User): CompletableFuture<Boolean>

    /**
     * Updates the user stored
     * @return a promise holding whether the update succeeded or not
     */
    fun updateUser(user: User): CompletableFuture<Boolean>
}