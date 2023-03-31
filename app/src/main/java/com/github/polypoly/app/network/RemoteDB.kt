package com.github.polypoly.app.network

import com.github.polypoly.app.game.user.User
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import java.util.concurrent.CompletableFuture

/**
 * Implementation of IRemoteStorage as a Firebase remote DB
 */
open class RemoteDB(
    private val db: FirebaseDatabase?,
    private val root: String
) : IRemoteStorage {

    private lateinit var rootRef: DatabaseReference
    private lateinit var usersRootRef: DatabaseReference

    init {
        if (db != null) {
            rootRef = db.getReference(root)
            usersRootRef = rootRef.child(DB_USERS_PROFILES_PATH)
        }
    }

    override fun getUserWithId(userId: Long): CompletableFuture<User> {
        val future = CompletableFuture<User>()

        usersRootRef.child(userId.toString()).get().addOnSuccessListener { userRef ->
            if (userRef.value == null) {
                future.completeExceptionally(IllegalAccessError("No user with id $userId"))
            } else {
                future.complete(userRef.getValue<User>())
            }
        }.addOnFailureListener(future::completeExceptionally)

        return future
    }

    override fun getAllUsers(): CompletableFuture<List<User>> {
        val usersPromise = CompletableFuture<List<User>>()

        usersRootRef.get().addOnSuccessListener{ usersRef ->
            val users = ArrayList<User>()
            for (child in usersRef.children) {
                users.add(child.getValue<User>()!!)
            }
            usersPromise.complete(users)
        }.addOnFailureListener(usersPromise::completeExceptionally)

        return usersPromise
    }

    override fun getAllUsersIds(): CompletableFuture<List<Long>> {
        val usersIdsPromise = CompletableFuture<List<Long>>()

        usersRootRef.get().addOnSuccessListener{ usersRef ->
            val ids = ArrayList<Long>()
            for (child in usersRef.children) {
                ids.add(child.key?.toLong() ?: -1)
            }
            usersIdsPromise.complete(ids)
        }.addOnFailureListener(usersIdsPromise::completeExceptionally)

        return usersIdsPromise
    }

    override fun registerUser(user: User): CompletableFuture<Boolean> {
        return getAllUsersIds().thenCompose { allIds ->
            val registerPromise = CompletableFuture<Boolean>()

            if (allIds.contains(user.id)) {
                registerPromise.completeExceptionally(
                    IllegalAccessError("You cannot register two times a single account"))
            } else {
                usersRootRef.child(user.id.toString()).setValue(user).addOnSuccessListener {
                    registerPromise.complete(true)
                }.addOnFailureListener(registerPromise::completeExceptionally)
            }

            registerPromise
        }
    }

    override fun updateUser(user: User): CompletableFuture<Boolean> {
        return getAllUsersIds().thenCompose { allIds ->
            val registerPromise = CompletableFuture<Boolean>()

            if (!allIds.contains(user.id)) {
                registerPromise.completeExceptionally(
                    IllegalAccessError("Tries to update a user not registered yet"))
            } else {
                usersRootRef.updateChildren(mapOf(
                    user.id.toString() to user
                )).addOnSuccessListener {
                    registerPromise.complete(true)
                }.addOnFailureListener(registerPromise::completeExceptionally)
            }

            registerPromise
        }
    }

    fun getUnderlyingDB(): FirebaseDatabase{
        return db!!
    }

    companion object InvalidRemoteDB: RemoteDB(null, "") {
        override fun getUserWithId(userId: Long): CompletableFuture<User> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun getAllUsers(): CompletableFuture<List<User>> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun getAllUsersIds(): CompletableFuture<List<Long>> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun registerUser(user: User): CompletableFuture<Boolean> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun updateUser(user: User): CompletableFuture<Boolean> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }
    }
}