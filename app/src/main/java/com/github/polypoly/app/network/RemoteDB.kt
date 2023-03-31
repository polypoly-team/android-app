package com.github.polypoly.app.network

import com.github.polypoly.app.game.GameLobby
import com.github.polypoly.app.game.User
import com.github.polypoly.app.global.Settings.Companion.DB_GAME_LOBBIES_PATH
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

open class RemoteDB(
    private val db: FirebaseDatabase?,
    private val root: String
) : IRemoteStorage {

    private lateinit var rootRef: DatabaseReference
    private lateinit var usersRootRef: DatabaseReference
    private lateinit var gameLobbiesRootRef: DatabaseReference

    init {
        if (db != null) {
            rootRef = db.getReference(root)
            usersRootRef = rootRef.child(DB_USERS_PROFILES_PATH)
            gameLobbiesRootRef = rootRef.child(DB_GAME_LOBBIES_PATH)
        }
    }

    private inline fun <reified T>getAllChildren(parentRef: DatabaseReference): CompletableFuture<List<T>> {
        val childrenPromise = CompletableFuture<List<T>>()

        parentRef.get().addOnSuccessListener{ parentData ->
            val users = ArrayList<T>()
            for (child in parentData.children) {
                users.add(child.getValue<T>()!!)
            }
            childrenPromise.complete(users)
        }.addOnFailureListener(childrenPromise::completeExceptionally)

        return childrenPromise
    }

    private inline fun <reified T>getData(dataRef: DatabaseReference, key: String, error: Throwable): CompletableFuture<T> {
        val future = CompletableFuture<T>()

        dataRef.child(key).get().addOnSuccessListener { data ->
            if (data.value == null) {
                future.completeExceptionally(error)
            } else {
                future.complete(data.getValue<T>())
            }
        }.addOnFailureListener(future::completeExceptionally)

        return future
    }

    private fun getAllKeys(dataRef: DatabaseReference): CompletableFuture<List<String>> {
        val keysPromise = CompletableFuture<List<String>>()

        usersRootRef.get().addOnSuccessListener{ data ->
            val keys = ArrayList<String>()
            for (child in data.children) {
                keys.add(child.key!!)
            }
            keysPromise.complete(keys)
        }.addOnFailureListener(keysPromise::completeExceptionally)

        return keysPromise
    }

    private inline fun <reified T>registerData(dataRef: DatabaseReference, key: String, data: T, error: Throwable): CompletableFuture<Boolean> {
        return getAllKeys(dataRef).thenCompose { allKeys ->
            val registerPromise = CompletableFuture<Boolean>()

            if (allKeys.contains(key)) {
                registerPromise.completeExceptionally(error)
            } else {
                dataRef.setValue(data).addOnSuccessListener {
                    registerPromise.complete(true)
                }.addOnFailureListener(registerPromise::completeExceptionally)
            }

            registerPromise
        }
    }

    private inline fun <reified T>updateData(dataRef: DatabaseReference, key: String, data: T, error: Throwable): CompletableFuture<Boolean> {
        return getAllKeys(dataRef).thenCompose { allKeys ->
            val updatePromise = CompletableFuture<Boolean>()

            if (!allKeys.contains(key)) {
                updatePromise.completeExceptionally(error)
            } else {
                dataRef.updateChildren(mapOf(
                    key to data
                )).addOnSuccessListener {
                    updatePromise.complete(true)
                }.addOnFailureListener(updatePromise::completeExceptionally)
            }

            updatePromise
        }
    }

    override fun getUserWithId(userId: Long): CompletableFuture<User> {
        return getData(usersRootRef, userId.toString(), IllegalAccessError("No user with id $userId"))
    }

    override fun getAllUsers(): CompletableFuture<List<User>> {
        return getAllChildren(usersRootRef)
    }

    override fun getAllUsersIds(): CompletableFuture<List<Long>> {
        return getAllKeys(usersRootRef).thenApply { keys -> keys.map(String::toLong) }
    }

    override fun registerUser(user: User): CompletableFuture<Boolean> {
        return registerData(usersRootRef, user.id.toString(), user, IllegalAccessError("You cannot register two times a single account"))
    }

    override fun updateUser(user: User): CompletableFuture<Boolean> {
        return updateData(usersRootRef, user.id.toString(), user, IllegalAccessError("Tries to update a user not registered yet"))
    }

    override fun getGameLobbyWithCode(code: String): CompletableFuture<GameLobby> {
        return getData(gameLobbiesRootRef, code, IllegalAccessError("No game lobby found for code $code"))
    }

    override fun getAllGameLobbies(): CompletableFuture<List<GameLobby>> {
        return getAllChildren(gameLobbiesRootRef)
    }

    override fun getAllGameLobbiesCodes(): CompletableFuture<List<String>> {
        return getAllKeys(gameLobbiesRootRef)
    }

    override fun registerGameLobby(gameLobby: GameLobby): CompletableFuture<Boolean> {
        return registerData(gameLobbiesRootRef, gameLobby.code, gameLobby,
            IllegalAccessError("You cannot create two game lobbies with the same code"))
    }

    override fun updateGameLobby(gameLobby: GameLobby): CompletableFuture<Boolean> {
        return updateData(gameLobbiesRootRef, gameLobby.code, gameLobby,
            IllegalAccessError("This game lobby doesn't exist yet so it cannot be updated"))
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