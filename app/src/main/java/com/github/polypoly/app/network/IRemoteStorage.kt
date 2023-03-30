package com.github.polypoly.app.network

import com.github.polypoly.app.game.user.User
import java.util.concurrent.Future

interface IRemoteStorage {
    fun getUserProfileWithId(userId: Long): Future<User>

    fun setUserProfileWithId(userId: Long, user: User)

    fun getAllUsers(): Future<List<User>>

    /**
     * Returns True if the user managed to be added
     */
    fun addUser(userId: Long): Future<Boolean>
}

enum class StorageType {
    FIREBASE,
    TEST
}