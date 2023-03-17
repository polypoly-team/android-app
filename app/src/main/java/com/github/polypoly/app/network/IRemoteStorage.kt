package com.github.polypoly.app.network

import com.github.polypoly.app.game.Skin
import com.github.polypoly.app.game.Stats
import com.github.polypoly.app.game.User
import java.util.concurrent.CompletableFuture

interface IRemoteStorage {
//    fun getUserProfileWithId(userId: Long): CompletableFuture<User>
    fun getUserProfileWithId(userId: Long): CompletableFuture<User>

    fun getAllUsersIds(): CompletableFuture<List<Long>>

    fun getAllUsers(): CompletableFuture<List<User>>

    /**
     * Returns True if the user managed to be added
     */
    fun addUser(userId: Long, user: User): CompletableFuture<Boolean>

    fun setUserName(userId: Long, name: String): CompletableFuture<Boolean>
    fun setUserBio(userId: Long, bio: String): CompletableFuture<Boolean>
    fun setUserSkin(userId: Long, skin: Skin): CompletableFuture<Boolean>
    fun <T>setUserStat(userId: Long, statName: String, stat: T): CompletableFuture<Boolean>
}