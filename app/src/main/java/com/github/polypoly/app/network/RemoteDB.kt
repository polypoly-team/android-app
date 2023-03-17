package com.github.polypoly.app.network

import android.util.Log
import com.github.polypoly.app.game.Skin
import com.github.polypoly.app.game.User
import com.github.polypoly.app.global.Settings.Companion.DB_ALL_USERS_ID_PATH
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
import com.github.polypoly.app.global.Settings.Companion.DB_USER_BIO_DIRECTORY
import com.github.polypoly.app.global.Settings.Companion.DB_USER_NAME_DIRECTORY
import com.github.polypoly.app.global.Settings.Companion.DB_USER_SKIN_DIRECTORY
import com.github.polypoly.app.global.Settings.Companion.DB_USER_STATS_DIRECTORY
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import java.util.concurrent.CompletableFuture

open class RemoteDB(
    private val db: FirebaseDatabase?
) : IRemoteStorage {

    private lateinit var rootRef: DatabaseReference
    private lateinit var usersProfilesRef: DatabaseReference

    init {
        if (db != null) {
            rootRef = db.getReference()
            usersProfilesRef = db.getReference(DB_USERS_PROFILES_PATH)
        }
    }

    override fun getUserProfileWithId(userId: Long): CompletableFuture<User> {
        val future = CompletableFuture<User>()
        usersProfilesRef.child(userId.toString()).get().addOnSuccessListener {
            future.complete(it.getValue<User>())
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        future.complete(User())
        return future
    }

    override fun getAllUsersIds(): CompletableFuture<List<Long>> {
        val future: CompletableFuture<List<Long>> = CompletableFuture<List<Long>>()
        rootRef.child(DB_ALL_USERS_ID_PATH).get().addOnSuccessListener {
            future.complete(it.getValue<List<Long>>())
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getAllUsers(): CompletableFuture<List<User>> {
        val future = CompletableFuture<List<User>>()
        val usersFound = ArrayList<User>()

        val allIds = getAllUsersIds()
        allIds.whenComplete { ids, error ->
            if (allIds.isCompletedExceptionally) {
                future.completeExceptionally(error)
            } else {
                for (id in ids) {
                    usersProfilesRef.child(id.toString()).get().addOnSuccessListener {
                        usersFound.add(it.value as User)
                        if (usersFound.size == ids.size)
                            future.complete(usersFound)
                    }.addOnFailureListener {
                        future.completeExceptionally(it)
                    }
                }
            }
        }

        return future
    }

    override fun addUser(userId: Long, user: User): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val id = userId.toString()

        val ids = listOf<Long>() // TODO fetch on database
        if (ids.contains(userId)) {
                future.completeExceptionally(throw IllegalAccessError("You cannot register two times a single account"))
            } else {
                val extendedIds = ArrayList(ids)
                extendedIds.add(userId)
                usersProfilesRef.child("$userId").setValue(user).addOnSuccessListener {
                    rootRef.child("$DB_ALL_USERS_ID_PATH/$userId").setValue(extendedIds).addOnSuccessListener {
                    } .addOnFailureListener {future.completeExceptionally(it)}
                }.addOnFailureListener{future.completeExceptionally(it)}
            }

        return future
    }

    private fun <T>setUserData(userId: Long, dataName: String, data: T): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val userProfileRef = usersProfilesRef.child(userId.toString())
        userProfileRef.get().addOnSuccessListener {
            userProfileRef.child(dataName).setValue(data).addOnSuccessListener {
                future.complete(true)
            }.addOnFailureListener {
                future.completeExceptionally(it)
            }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun setUserName(userId: Long, name: String): CompletableFuture<Boolean> {
        return setUserData(userId, DB_USER_NAME_DIRECTORY, name)
    }

    override fun setUserBio(userId: Long, bio: String): CompletableFuture<Boolean> {
        return setUserData(userId, DB_USER_BIO_DIRECTORY, bio)
    }

    override fun setUserSkin(userId: Long, skin: Skin): CompletableFuture<Boolean> {
        return setUserData(userId, DB_USER_SKIN_DIRECTORY, skin)
    }

    override fun <T>setUserStat(userId: Long, statName: String, stat: T): CompletableFuture<Boolean> {
        return setUserData(userId, "$DB_USER_STATS_DIRECTORY/$statName", stat)
    }

    companion object InvalidRemoteDB: RemoteDB(null) {
        override fun getUserProfileWithId(userId: Long): CompletableFuture<User> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun getAllUsers(): CompletableFuture<List<User>> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun addUser(userId: Long, user: User): CompletableFuture<Boolean> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun setUserName(userId: Long, name: String): CompletableFuture<Boolean> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun setUserBio(userId: Long, bio: String): CompletableFuture<Boolean> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun setUserSkin(userId: Long, skin: Skin): CompletableFuture<Boolean> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun <T>setUserStat(userId: Long, statName: String, stat: T): CompletableFuture<Boolean> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }
    }
}