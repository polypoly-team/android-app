package com.github.polypoly.app.commons

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.game.GameLobby
import com.github.polypoly.app.game.GameMode
import com.github.polypoly.app.game.user.Skin
import com.github.polypoly.app.game.user.Stats
import com.github.polypoly.app.game.user.User
import com.github.polypoly.app.global.GlobalInstances
import com.github.polypoly.app.network.RemoteDB
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit

@RunWith(AndroidJUnit4::class)
abstract class PolyPolyTest(
    val clearRemoteStorage: Boolean, //> clear remote storage at the beginning of every test
    val fillWithFakeData: Boolean //> fill remote storage with fake data at the beginning of every test
) {
    companion object {
        // Global tests constants
        const val TIMEOUT_DURATION = 5L

        // Ensures only one global initialization even for multithreaded testing
        var globalInitCompleted = false
        var initLock = ReentrantLock(true)

        // Miscellaneous test data
        val ZERO_STATS = Stats(0, 0, 0, 0, 0)
        val NO_SKIN = Skin(0,0,0)

        val TEST_USER_0 = User(12,"John", "Hi!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_1 = User(12,"Carter", "Not me!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_2 = User(123,"Harry", "Ha!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_3 = User(1234,"James", "Hey!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_4 = User(12345,"Henri", "Ohh!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_5 = User(123456, "test_user_5", "", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val ALL_TEST_USERS = listOf(TEST_USER_0, TEST_USER_1, TEST_USER_2, TEST_USER_3, TEST_USER_4, TEST_USER_5)

        val TEST_GAME_LOBBY_FULL = GameLobby(
            TEST_USER_1, GameMode.RICHEST_PLAYER, 2, 6,
            60, emptyList(), 100, "Full gameLobby", "1234"
        )
        val TEST_GAME_LOBBY_PRIVATE = GameLobby(
            TEST_USER_1, GameMode.RICHEST_PLAYER, 4, 6,
            360, emptyList(), 300, "Private gameLobby", "abc123", true
        )
        val TEST_GAME_LOBBY_AVAILABLE_1 = GameLobby(
            TEST_USER_1, GameMode.LAST_STANDING, 3, 8,
            600, emptyList(), 1000, "Joinable 1", "abcd"
        )
        val TEST_GAME_LOBBY_AVAILABLE_2 = GameLobby(
            TEST_USER_2, GameMode.RICHEST_PLAYER, 10, 25,
            3600, emptyList(), 2000, "Joinable 2", "123abc"
        )
        val TEST_GAME_LOBBY_AVAILABLE_3 = GameLobby(
            TEST_USER_3, GameMode.RICHEST_PLAYER, 7, 77,
            720, emptyList(), 3000, "Joinable 3", "1234abc"
        )
        val TEST_GAME_LOBBY_AVAILABLE_4 = GameLobby(
            TEST_USER_4, GameMode.RICHEST_PLAYER, 2, 4,
            1080, emptyList(), 4000, "Joinable 4", "abc1234"
        )

        val ALL_TEST_GAME_LOBBIES = listOf(TEST_GAME_LOBBY_FULL, TEST_GAME_LOBBY_PRIVATE, TEST_GAME_LOBBY_AVAILABLE_1,
        TEST_GAME_LOBBY_AVAILABLE_2, TEST_GAME_LOBBY_AVAILABLE_3, TEST_GAME_LOBBY_AVAILABLE_4)

        init {
            val db = Firebase.database
            db.setPersistenceEnabled(false)
            GlobalInstances.remoteDB = RemoteDB(db, "test")

            TEST_GAME_LOBBY_FULL.addUsers(listOf(TEST_USER_2, TEST_USER_3, TEST_USER_4, TEST_USER_5))
            TEST_GAME_LOBBY_PRIVATE.addUsers(listOf(TEST_USER_2))
            TEST_GAME_LOBBY_AVAILABLE_1.addUsers(listOf(TEST_USER_2, TEST_USER_3))
            TEST_GAME_LOBBY_AVAILABLE_2.addUsers(listOf(TEST_USER_1, TEST_USER_4))
            TEST_GAME_LOBBY_AVAILABLE_3.addUsers(listOf(TEST_USER_1, TEST_USER_2, TEST_USER_4))
        }
    }

    private val dbRootRef: DatabaseReference = GlobalInstances.remoteDB.rootRef

    private fun <T> requestAddDataToDB(data: List<T>, keys: List<String>): List<CompletableFuture<Boolean>> {
        val timeouts = List(data.size) {CompletableFuture<Boolean>()}
        for (i in data.indices) {
            val user = data[i]
            dbRootRef.child(keys[i])
                .setValue(user)
                .addOnSuccessListener {
                    timeouts[i].complete(true)
                }.addOnFailureListener(timeouts[i]::completeExceptionally)
        }
        return timeouts
    }

    fun <T> addDataToDB(data: List<T>, keys: List<String>) {
        requestAddDataToDB(data, keys).map{ timeout -> timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)}
    }

    fun addUsersToDB(users: List<User>, root: String = "") = addDataToDB(users, users.map{user ->  root + user.id})

    fun addUserToDB(users: User, root: String = "") = addUsersToDB(listOf(users), root)

    fun addGameLobbiesToDB(gameLobby: List<GameLobby>, root: String = "") = addDataToDB(gameLobby, gameLobby.map(GameLobby::code))

    fun addGameLobbyToDB(gameLobby: GameLobby, root: String = "") = addGameLobbiesToDB(listOf(gameLobby), root)

    @Before
    fun prepareTest() {
        if (clearRemoteStorage) {
            clearTestDB()
        }
        if (fillWithFakeData) {
            fillWithFakeData()
        }
    }

    private fun clearTestDB() {
        val timeout = CompletableFuture<Boolean>()
        dbRootRef.removeValue()
            .addOnSuccessListener {
                timeout.complete(true)
            }.addOnFailureListener(timeout::completeExceptionally)
        timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }

    fun fillWithFakeData() {
        val allRequests = mutableListOf<CompletableFuture<Boolean>>()
        allRequests.addAll(
            requestAddDataToDB(ALL_TEST_USERS, ALL_TEST_USERS.map{user -> user.id.toString()})
        )
        allRequests.addAll(
            requestAddDataToDB(ALL_TEST_GAME_LOBBIES, ALL_TEST_GAME_LOBBIES.map(GameLobby::code))
        )
        allRequests.map{promise -> promise.get(TIMEOUT_DURATION, TimeUnit.SECONDS)}
    }
}