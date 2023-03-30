package com.github.polypoly.app.network

import android.util.Log
import com.github.polypoly.app.game.GameLobby
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
import java.util.concurrent.Future

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

    override fun getUserWithId(userId: Long): CompletableFuture<User> {
        val future = CompletableFuture<User>()
        usersProfilesRef.child(userId.toString()).get().addOnSuccessListener {
            val user = it.getValue<User>()
            future.complete(user)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
    }

    override fun getAllUsers(): CompletableFuture<List<User>> {
        val future = CompletableFuture<List<User>>()
        val usersFound = ArrayList<User>()

        val allIds = CompletableFuture.completedFuture(listOf(0, 1, 2)) // TODO: fetch real ids
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

    override fun registerUser(user: User): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val userId = user.id

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

    override fun updateUser(user: User): CompletableFuture<Boolean> {
        throw UnsupportedOperationException("Not implemented yet")
    }

    override fun getGameLobbyWithCode(code: String): Future<GameLobby> {
        TODO("Not yet implemented")
    }

    override fun getAllGameLobbies(): Future<List<GameLobby>> {
        TODO("Not yet implemented")
    }

    override fun getAllGameLobbiesCodes(): Future<List<String>> {
        TODO("Not yet implemented")
    }

    override fun registerGameLobby(gameLobby: GameLobby): Future<Boolean> {
        TODO("Not yet implemented")
    }

    override fun updateGameLobby(gameLobby: GameLobby): Future<Boolean> {
        TODO("Not yet implemented")
    }

    fun getUnderlyingDB(): FirebaseDatabase{
        return db!!
    }

    companion object InvalidRemoteDB: RemoteDB(null) {
        override fun getUserWithId(userId: Long): CompletableFuture<User> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }

        override fun getAllUsers(): CompletableFuture<List<User>> {
            throw IllegalAccessError("This RemoteDB is invalid")
        }
    }
}