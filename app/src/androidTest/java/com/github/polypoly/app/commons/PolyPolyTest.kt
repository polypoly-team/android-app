package com.github.polypoly.app.commons

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.LocationRepository
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentFBUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.isSignedIn
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDBInitialized
import com.github.polypoly.app.utils.global.Settings.Companion.DB_GAME_LOBBIES_PATH
import com.github.polypoly.app.utils.global.Settings.Companion.DB_USERS_PROFILES_PATH
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

@RunWith(AndroidJUnit4::class)
abstract class PolyPolyTest(
    private val clearRemoteStorage: Boolean, //> clear remote storage at the beginning of every test
    private val fillWithFakeData: Boolean, //> fill remote storage with fake data at the beginning of every test
    val signFakeUserIn: Boolean = false //> sign a fake user in at the beginning of every test
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

        val CURRENT_USER = User(1000,"test_current_user", "I am a fake current user!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_0 = User(
            id = 0,
            name = "John",
            bio = "Hi, this is my bio :)",
            skin = Skin(0,0,0),
            stats = Stats(0, 0, 67, 28, 14),
            trophiesWon = listOf(0, 4, 8, 11, 12, 14),
            trophiesDisplay = mutableListOf(0, 4)
        )
        val TEST_USER_1 = User(12,"Carter", "Not me!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_2 = User(123,"Harry", "Ha!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_3 = User(1234,"James", "Hey!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_4 = User(12345,"Henri", "Ohh!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_5 = User(123456, "test_user_5", "", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val ALL_TEST_USERS = listOf(TEST_USER_0, TEST_USER_1, TEST_USER_2, TEST_USER_3, TEST_USER_4, TEST_USER_5)

        val TEST_GAME_LOBBY_FULL = GameLobby(
            TEST_USER_0, GameParameters(GameMode.RICHEST_PLAYER, 2, 6,
            60, 20, emptyList(), 100), "Full gameLobby", "lobby1234"
        )
        val TEST_GAME_LOBBY_PRIVATE = GameLobby(
            TEST_USER_1, GameParameters(GameMode.RICHEST_PLAYER, 4, 6,
            360, 20, emptyList(), 300), "Private gameLobby", "lobbyabc123", true
        )
        val TEST_GAME_LOBBY_AVAILABLE_1 = GameLobby(
            TEST_USER_1, GameParameters(GameMode.LAST_STANDING, 3, 8,
            600, null, emptyList(), 1000), "Joinable 1", "lobbyabcd"
        )
        val TEST_GAME_LOBBY_AVAILABLE_2 = GameLobby(
            TEST_USER_2, GameParameters(GameMode.RICHEST_PLAYER, 10, 25,
            3600, 20, emptyList(), 2000), "Joinable 2", "lobby123abc"
        )
        val TEST_GAME_LOBBY_AVAILABLE_3 = GameLobby(
            TEST_USER_3, GameParameters(GameMode.RICHEST_PLAYER, 7, 77,
            720, 20, emptyList(), 3000), "Joinable 3", "lobby1234abc"
        )
        val TEST_GAME_LOBBY_AVAILABLE_4 = GameLobby(
            TEST_USER_4, GameParameters(GameMode.RICHEST_PLAYER, 2, 4,
            1080, 20, emptyList(), 4000), "Joinable 4", "lobbyabc1234"
        )

        val testPlayer1 = Player(TEST_USER_1, 100, listOf())
        val testPlayer2 = Player(TEST_USER_2, 200, listOf())
        val testPlayer3 = Player(TEST_USER_3, 100, listOf())
        val testPlayer4 = Player(TEST_USER_4, 0, listOf(), 4)
        val testPlayer5 = Player(TEST_USER_5, 0, listOf(), 5)
        val testPlayer6 = Player(TEST_USER_0, 0, listOf(), 5)
        val gameRulesDefault = GameParameters(GameMode.RICHEST_PLAYER, 3, 7,
            2, 10, LocationRepository.getZones(), 200)

        val ALL_TEST_GAME_LOBBIES = listOf(TEST_GAME_LOBBY_FULL, TEST_GAME_LOBBY_PRIVATE, TEST_GAME_LOBBY_AVAILABLE_1,
        TEST_GAME_LOBBY_AVAILABLE_2, TEST_GAME_LOBBY_AVAILABLE_3, TEST_GAME_LOBBY_AVAILABLE_4)

        private val mockDB = MockDB()

        init {
            if (!remoteDBInitialized) {
                remoteDB = mockDB
                remoteDBInitialized = true
            }

            FirebaseAuth.getInstance().signOut()
            currentFBUser = null
            isSignedIn = false

            TEST_GAME_LOBBY_FULL.addUsers(listOf(TEST_USER_1, TEST_USER_2, TEST_USER_3, TEST_USER_4, TEST_USER_5))
            TEST_GAME_LOBBY_PRIVATE.addUsers(listOf(TEST_USER_2))
            TEST_GAME_LOBBY_AVAILABLE_1.addUsers(listOf(TEST_USER_2, TEST_USER_3))
            TEST_GAME_LOBBY_AVAILABLE_2.addUsers(listOf(TEST_USER_1, TEST_USER_4))
            TEST_GAME_LOBBY_AVAILABLE_3.addUsers(listOf(TEST_USER_1, TEST_USER_2, TEST_USER_4))
        }
    }

    private fun <T> requestAddDataToDB(data: List<T>, keys: List<String>, root: String): List<CompletableFuture<Boolean>> {
        val timeouts = mutableListOf<CompletableFuture<Boolean>>()
        for (i in data.indices) {
            timeouts.add(remoteDB.setValue(root + keys[i], data[i]))
        }
        return timeouts
    }

    fun <T> addDataToDB(data: List<T>, keys: List<String>, root: String = "") {
        requestAddDataToDB(data, keys, root).map{ timeout -> timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)}
    }

    fun <T> addDataToDB(data: T, key: String) {
        addDataToDB(listOf(data), listOf(key))
    }

    fun addUsersToDB(users: List<User>) = addDataToDB(users,
        users.map{user ->  user.id.toString()}, DB_USERS_PROFILES_PATH)

    fun addUserToDB(users: User) = addUsersToDB(listOf(users))

    fun addGameLobbiesToDB(gameLobby: List<GameLobby>) = addDataToDB(gameLobby,
        gameLobby.map(GameLobby::code), DB_GAME_LOBBIES_PATH)

    fun addGameLobbyToDB(gameLobby: GameLobby) = addGameLobbiesToDB(listOf(gameLobby))

    /**
     * Function always called after the preparation of the test is completed
     */
    open fun _prepareTest() {}

    @Before
    fun prepareTest() {
        if (clearRemoteStorage) {
            clearMockDB()
        }
        if (fillWithFakeData) {
            fillWithFakeData()
        }
        _prepareTest()
    }
    @After
    fun cleanUp() {
        currentFBUser = null
        isSignedIn = false
    }

    private fun clearMockDB() {
        mockDB.clear()
    }

    fun fillWithFakeData() {
        val allRequests = mutableListOf<CompletableFuture<Boolean>>()
        allRequests.addAll(
            requestAddDataToDB(ALL_TEST_USERS, ALL_TEST_USERS.map{user -> user.id.toString()}, DB_USERS_PROFILES_PATH)
        )
        allRequests.addAll(
            requestAddDataToDB(ALL_TEST_GAME_LOBBIES, ALL_TEST_GAME_LOBBIES.map(GameLobby::code), DB_GAME_LOBBIES_PATH)
        )
        allRequests.map{promise -> promise.get(TIMEOUT_DURATION, TimeUnit.SECONDS)}
    }
}