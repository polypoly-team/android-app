package com.github.polypoly.app.network

import com.github.polypoly.app.game.User
import java.util.concurrent.Future

interface IRemoteStorage {
    fun getUserProfileWithId(userId: Long): Future<User>

    fun setUserProfileWithId(userId: Long, user: User): Future<User>

    fun getAllUsers(): Future<List<User>>

    /**
     * Returns True if the user managed to be added
     */
    fun addUser(userId: Long): Future<Boolean>
}