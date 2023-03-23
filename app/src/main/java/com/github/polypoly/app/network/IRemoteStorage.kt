package com.github.polypoly.app.network

import com.github.polypoly.app.game.PendingGame
import com.github.polypoly.app.game.Skin
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

    fun setUserProfileWithId(userId: Long, user: User)

    fun setUserName(userId: Long, name: String): CompletableFuture<Boolean>
    fun setUserBio(userId: Long, bio: String): CompletableFuture<Boolean>
    fun setUserSkin(userId: Long, skin: Skin): CompletableFuture<Boolean>
    fun <T>setUserStat(userId: Long, statName: String, stat: T): CompletableFuture<Boolean>

    // ------------------- GROUPS -------------------//

    /**
     * fetches a group data from the database given an ID
     * @param groupId the id of the group
     */
    fun getGroupFromId(groupId: String): CompletableFuture<PendingGame>

    /**
     * adds a group to the database
     */
    fun addGroup(group: PendingGame): CompletableFuture<Boolean>

    /**
     * fetches all the groups from the database
     */
    fun getAllGroupsIds(): CompletableFuture<List<String>>

}