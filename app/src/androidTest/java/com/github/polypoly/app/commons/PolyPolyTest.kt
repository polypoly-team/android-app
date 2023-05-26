package com.github.polypoly.app.commons

import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.base.game.location.LocationPropertyRepository.getZones
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.database.StorableObject
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.isSignedIn
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDBInitialized
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.absoluteValue
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
abstract class PolyPolyTest(
    private val clearRemoteStorage: Boolean, //> clear remote storage at the beginning of every test
    private val fillWithFakeData: Boolean, //> fill remote storage with fake data at the beginning of every test
    private val signFakeUserIn: Boolean = false //> sign a fake user in at the beginning of every test
) {

    // ======================================================================= COMPANION OBJECT
    companion object {
        // Global tests constants
        const val TIMEOUT_DURATION = 15L

        // Ensures only one global initialization even for multithreaded testing
        var globalInitCompleted = false
        var initLock = ReentrantLock(true)

        // Miscellaneous test data
        val ZERO_STATS = Stats(0, 0, 0, 0, 0)
        val NO_SKIN = Skin(0,0,0)

        val TEST_USER_0 = User(
            id = "0",
            name = "John",
            bio = "Hi, this is my bio :)",
            skin = Skin(0,0,0),
            stats = Stats(0, 0, 67, 28, 14),
            trophiesWon = listOf(0, 4, 8, 11, 12, 14),
            trophiesDisplay = mutableListOf(0, 4)
        )
        val TEST_USER_1 = User("12","Carter", "Not me!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_1_BIS = User("12","Carter", "IT IS me!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_2 = User("123","Harry", "Ha!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_2_BIS = User("123","Harry", "Kachow!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_3 = User("1234","James", "Hey!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_4 = User("12345","Henri", "Ohh!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_5 = User("123456", "test_user_5", "", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_NOT_IN_LOBBY = User("8888", "BIGFLO", "& CARLITO", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val ALL_TEST_USERS = listOf(TEST_USER_0, TEST_USER_1, TEST_USER_2, TEST_USER_3, TEST_USER_4, TEST_USER_5, TEST_USER_NOT_IN_LOBBY)

        val TEST_GAME_LOBBY_FULL = GameLobby(
            TEST_USER_0, GameParameters(GameMode.RICHEST_PLAYER, 4, 6,
            60, 20, getZones(), 1000), "Full gameLobby", "11111"
        )
        val TEST_GAME_LOBBY_PRIVATE = GameLobby(
            TEST_USER_1, GameParameters(GameMode.RICHEST_PLAYER, 2, 6,
            360, 20, getZones(), 300), "Private gameLobby", "22222", true
        )
        val TEST_GAME_LOBBY_AVAILABLE_1 = GameLobby(
            TEST_USER_1, GameParameters(GameMode.LAST_STANDING, 2, 8,
            600, null, getZones(), 1000), "Joinable 1", "33333"
        )
        val TEST_GAME_LOBBY_AVAILABLE_2 = GameLobby(
            TEST_USER_2, GameParameters(GameMode.RICHEST_PLAYER, 5, 7,
            4320, 20, getZones(), 2000), "Joinable 2", "44444"
        )
        val TEST_GAME_LOBBY_AVAILABLE_3 = GameLobby(
            TEST_USER_3, GameParameters(GameMode.RICHEST_PLAYER, 7, 8,
            900, 20, getZones(), 3000), "Joinable 3", "55555"
        )
        val TEST_GAME_LOBBY_AVAILABLE_4 = GameLobby(
            TEST_USER_4, GameParameters(GameMode.RICHEST_PLAYER, 2, 4,
            7200, 20, getZones(), 4000), "Joinable 4", "66666"
        )
        val TEST_GAME_LOBBY_AVAILABLE_5 = GameLobby(
            TEST_USER_5, GameParameters(GameMode.LANDLORD, 2, 4,
                600, 20, getZones(), 4000), "Joinable 5", "lobbyabc12345",
            private = true
        )

        val testPlayer1 = Player(TEST_USER_1, 100)
        val testPlayer2 = Player(TEST_USER_2, 200)
        val testPlayer3 = Player(TEST_USER_3, 100)
        val testPlayer4 = Player(TEST_USER_4, 0, 4)
        val testPlayer5 = Player(TEST_USER_5, 0, 5)
        val testPlayer6 = Player(TEST_USER_0, 0, 5)
        val gameRulesDefault = GameParameters(GameMode.RICHEST_PLAYER, 3, 7,
            2, 10, LocationPropertyRepository.getZones(), 200)

        val ALL_TEST_GAME_LOBBIES = listOf(TEST_GAME_LOBBY_FULL, TEST_GAME_LOBBY_PRIVATE, TEST_GAME_LOBBY_AVAILABLE_1,
        TEST_GAME_LOBBY_AVAILABLE_2, TEST_GAME_LOBBY_AVAILABLE_3, TEST_GAME_LOBBY_AVAILABLE_4, TEST_GAME_LOBBY_AVAILABLE_5)

        private val mockDB = MockDB()

        /**
         * Every code here is executed once during the runtime
         */
        init {
            if (!remoteDBInitialized) {
                remoteDB = mockDB
                remoteDBInitialized = true
            }

            FirebaseAuth.getInstance().signOut()

            TEST_GAME_LOBBY_FULL.addUsers(listOf(TEST_USER_1, TEST_USER_2, TEST_USER_3, TEST_USER_4, TEST_USER_5))
            TEST_GAME_LOBBY_PRIVATE.addUsers(listOf(TEST_USER_2))
            TEST_GAME_LOBBY_AVAILABLE_1.addUsers(listOf(TEST_USER_2, TEST_USER_3))
            TEST_GAME_LOBBY_AVAILABLE_2.addUsers(listOf(TEST_USER_1, TEST_USER_4))
            TEST_GAME_LOBBY_AVAILABLE_3.addUsers(listOf(TEST_USER_1, TEST_USER_2, TEST_USER_4))
            TEST_GAME_LOBBY_AVAILABLE_5.addUsers(listOf(TEST_USER_1, TEST_USER_2))
        }
    }


    // ======================================================================= TEST PREPARATION
    init {
        remoteDB = mockDB
        isSignedIn = signFakeUserIn
        if(signFakeUserIn) {
            currentUser = TEST_USER_NOT_IN_LOBBY
        }
    }

    @Before
    fun prepareTest() {
        remoteDB = mockDB
        if (clearRemoteStorage) {
            mockDB.clear()
        }
        if(signFakeUserIn) {
            currentUser = TEST_USER_NOT_IN_LOBBY
        }
        if (fillWithFakeData) {
            fillWithFakeData()
        }
        _prepareTest()
    }

    /**
     * Function always called after the preparation of the test is completed
     */
    open fun _prepareTest() {}

    @After
    fun cleanUp() {}


    // ======================================================================= DB DATA HELPERS
    private fun <T : StorableObject<*>> requestAddDataToDB(data: List<T>): List<CompletableFuture<Boolean>> {
        val timeouts = mutableListOf<CompletableFuture<Boolean>>()
        for (i in data.indices) {
            timeouts.add(remoteDB.setValue(data[i]))
        }
        return timeouts
    }

    fun <T : StorableObject<*>> addDataToDB(data: List<T>) {
        requestAddDataToDB(data).map{ timeout -> timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)}
    }

    fun <T: StorableObject<*>> addDataToDB(data: T) = addDataToDB(listOf(data))

    fun addUsersToDB(users: List<User>) = addDataToDB(users)

    fun addUserToDB(users: User) = addUsersToDB(listOf(users))

    fun addGameLobbiesToDB(gameLobby: List<GameLobby>) = addDataToDB(gameLobby)

    fun addGameLobbyToDB(gameLobby: GameLobby) = addGameLobbiesToDB(listOf(gameLobby))

    fun fillWithFakeData() {
        val allRequests = mutableListOf<CompletableFuture<Boolean>>()
        allRequests.addAll(requestAddDataToDB(ALL_TEST_USERS))
        allRequests.addAll(requestAddDataToDB(ALL_TEST_GAME_LOBBIES))
        allRequests.map{promise -> promise.get(TIMEOUT_DURATION, TimeUnit.SECONDS)}
    }

    // ======================================================================= TEST HELPERS
    /**
     * Observes a live data until it updates its value and returns the new value
     * @param liveData data to observe
     * @return T The new value found after the data update
     */
    fun <T> waitForDataUpdate(liveData: LiveData<T>): T {
        val promise: CompletableFuture<T> = CompletableFuture()
        val observer: (T) -> Unit = { value: T -> promise.complete(value) }

        val scope = CoroutineScope(Dispatchers.Main + Job())

        scope.launch { liveData.observeForever(observer) }

        val result = promise.get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        scope.launch { liveData.removeObserver(observer) }

        return result
    }

    /**
     * Picks a random location among the locations provided
     * @param amongLocations list of locations to pick from
     * @return a random location in the list
     */
    fun getRandomLocation(amongLocations: List<LocationProperty> = getZones().flatMap { zone -> zone.locationProperties },
                          excluding: List<LocationProperty> = listOf()): LocationProperty {
        val availableLocations = amongLocations.minus(excluding.toSet())
        return availableLocations[Random.nextInt().absoluteValue % availableLocations.size]
    }

    /**
     * Executes the given lambda in the main thread. This is intended for assignations to MutableLiveData
     * (generally in ViewModel classes) that have this constraint.
     * @param action Lambda to execute in the main thread
     * @return a future that completes once the lambda is completed
     */
    fun execInMainThread(action: () -> Unit): CompletableFuture<Boolean> {
        val scope = CoroutineScope(Dispatchers.Main + Job())
        val future = CompletableFuture<Boolean>()
        scope.launch {
            try {
                action()
                future.complete(true)
            } catch (e: Throwable) {
                future.completeExceptionally(e)
            }
        }
        return future
    }

    /**
     * Waits for an update of the UI to start
     */
    fun waitForUIToUpdate() {
        runBlocking { delay(500) }
    }
}