package com.github.polypoly.app.network

import com.github.polypoly.app.game.*
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.global.Settings.Companion.DB_GAME_LOBBIES_PATH
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours

@RunWith(JUnit4::class)
class RemoteDBTest {
    companion object {
        val TIMEOUT_DURATION = 1L
    }

    private val underlyingDB: FirebaseDatabase
    private val rootName = "test"
    private val rootRef: DatabaseReference
    private val usersRootRef: DatabaseReference
    private val gameLobbiesRootRef: DatabaseReference

    private val testUser1 = User(1234L,"John", "Hi!", Skin(1, 1, 1), Stats())
    private val testUser2 = User(12345L,"Harry", "Ha!", Skin(1, 1, 1), Stats())
    private val testUser3 = User(123456L,"James", "Hey!", Skin(1, 1, 1), Stats())
    private val testUser4 = User(1234567L,"Henri", "Ohh!", Skin(1, 1, 1), Stats())
    private val allTestUsers = listOf(testUser1, testUser2, testUser3, testUser4)

    private val testGameLobby1 = GameLobby(testUser1, GameMode.RICHEST_PLAYER, 2, 5,
        2.hours, listOf(), 100, "testGameLobby1", "abcd")
    private val testGameLobby2 = GameLobby(testUser2, GameMode.LAST_STANDING, 2, 5,
        2.hours, listOf(), 100, "testGameLobby2", "abcde")
    private val testGameLobby3 = GameLobby(testUser3, GameMode.RICHEST_PLAYER, 2, 5,
        2.hours, listOf(), 100, "testGameLobby3", "abcdef")
    private val testGameLobby4 = GameLobby(testUser4, GameMode.LAST_STANDING, 2, 5,
        2.hours, listOf(), 100, "testGameLobby4", "abcdefg")
    private val allGameLobbies = listOf(testGameLobby1, testGameLobby2, testGameLobby3, testGameLobby4)

    init {
        val db = Firebase.database
        try {
            db.setPersistenceEnabled(false)
        } catch(_: java.lang.Exception) { }
        remoteDB = RemoteDB(db, rootName)
        underlyingDB = remoteDB.getUnderlyingDB()

        rootRef = underlyingDB.getReference(rootName)
        usersRootRef = rootRef.child(DB_USERS_PROFILES_PATH)
        gameLobbiesRootRef = rootRef.child(DB_GAME_LOBBIES_PATH)
    }

    fun addUsersToDB(users: List<User>) {
        val timeouts = List(users.size) {CompletableFuture<Boolean>()}

        for (i in users.indices) {
            val user = users[i]
            usersRootRef.child(user.id.toString())
                .setValue(user)
                .addOnSuccessListener {
                    timeouts[i].complete(true)
                }.addOnFailureListener(timeouts[i]::completeExceptionally)
        }

        timeouts.map{ timeout -> timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)}
    }

    fun addGameLobbiesToDB(lobbies: List<GameLobby>) {
        val timeouts = List(lobbies.size) {CompletableFuture<Boolean>()}

        for (i in lobbies.indices) {
            val lobby = lobbies[i]
            gameLobbiesRootRef.child(lobby.code)
                .setValue(lobby)
                .addOnSuccessListener {
                    timeouts[i].complete(true)
                }.addOnFailureListener(timeouts[i]::completeExceptionally)
        }

        timeouts.map{ timeout -> timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)}
    }

    fun removeAllChildrenFromDB(parent: DatabaseReference) {
        val timeout = CompletableFuture<Boolean>()
        parent.removeValue()
            .addOnSuccessListener {
                timeout.complete(true)
            }.addOnFailureListener(timeout::completeExceptionally)
        timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }

    @Test
    fun userCanBeRetrievedFromId() {
        addUsersToDB(listOf(testUser1))
        val userFound = remoteDB.getUserWithId(testUser1.id).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(testUser1, userFound)
    }

    @Test
    fun gettingUserOfInvalidIdFails() {
        val invalidId = -1L
        val failFuture = remoteDB.getUserWithId(invalidId)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun allUsersCanBeRetrievedAtOnce() {
        removeAllChildrenFromDB(usersRootRef)
        addUsersToDB(allTestUsers)

        val allUsersFound = remoteDB.getAllUsers().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(allTestUsers.size, allUsersFound.size)
        for (user in allTestUsers) {
            assertTrue(allUsersFound.contains(user))
        }
    }

    @Test
    fun allUsersIdsCanBeRetrievedAtOnce() {
        removeAllChildrenFromDB(usersRootRef)
        addUsersToDB(allTestUsers)

        val allUsersIds = allTestUsers.map(User::id)
        val allUsersIdsFound = remoteDB.getAllUsersIds().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(allUsersIds.sorted(), allUsersIdsFound.sorted())
    }

    @Test
    fun newUserCanBeRegistered() {
        removeAllChildrenFromDB(usersRootRef)
        assertTrue(remoteDB.registerUser(testUser1).get(TIMEOUT_DURATION, TimeUnit.SECONDS))
    }

    @Test
    fun registeringAnAlreadyRegisteredUserFails() {
        addUsersToDB(listOf(testUser1))
        val failFuture = remoteDB.registerUser(testUser1)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun userCanBeUpdatedAfterRegistration() {
        val userUpdated = User(testUser1.id, "Cool_name", "I updated my bio!", Skin(), Stats())
        addUsersToDB(listOf(testUser1))

        assertTrue(remoteDB.updateUser(userUpdated).get(TIMEOUT_DURATION, TimeUnit.SECONDS))

        val userUpdatedFound = remoteDB.getUserWithId(userUpdated.id).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(userUpdated, userUpdatedFound)
    }

    @Test
    fun unregisteredUserCannotBeUpdated() {
        removeAllChildrenFromDB(usersRootRef)
        val failFuture = remoteDB.updateUser(testUser1)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun gameLobbyCanBeRetrievedFromCode() {
        addGameLobbiesToDB(listOf(testGameLobby1))
        val gameLobbyFound = remoteDB.getGameLobbyWithCode(testGameLobby1.code).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(testGameLobby1, gameLobbyFound)
    }

    @Test
    fun gettingGameLobbyOfInvalidCodeFails() {
        val invalidCode = "I do not exist"
        val failFuture = remoteDB.getGameLobbyWithCode(invalidCode)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun allGameLobbiesCanBeRetrievedAtOnce() {
        removeAllChildrenFromDB(gameLobbiesRootRef)
        addGameLobbiesToDB(allGameLobbies)

        val allGameLobbiesFound = remoteDB.getAllGameLobbies().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(allGameLobbies.size, allGameLobbiesFound.size)
        for (gameLobby in allGameLobbies) {
            assertTrue(allGameLobbiesFound.contains(gameLobby))
        }
    }

    @Test
    fun allGameLobbyCodesCanBeRetrievedAtOnce() {
        removeAllChildrenFromDB(gameLobbiesRootRef)
        addGameLobbiesToDB(allGameLobbies)

        val allGameLobbiesCodes = allGameLobbies.map(GameLobby::code)
        val allGameLobbiesCodesFound = remoteDB.getAllGameLobbiesCodes().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(allGameLobbiesCodes.sorted(), allGameLobbiesCodesFound.sorted())
    }

    @Test
    fun newGameLobbyCanBeRegistered() {
        removeAllChildrenFromDB(gameLobbiesRootRef)
        assertTrue(remoteDB.registerGameLobby(testGameLobby1).get(TIMEOUT_DURATION, TimeUnit.SECONDS))
    }

    @Test
    fun registeringAnAlreadyRegisteredGameLobbyFails() {
        addGameLobbiesToDB(listOf(testGameLobby1))
        val failFuture = remoteDB.registerGameLobby(testGameLobby1)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun gameLobbiesCanBeUpdatedAfterRegistration() {
        val gameLobbyUpdated = GameLobby(testUser1, GameMode.LAST_STANDING, 2, 5,
            2.hours, listOf(), 100, "updated_testGameLobby1", "abcd")
        addGameLobbiesToDB(listOf(testGameLobby1))

        assertTrue(remoteDB.updateGameLobby(gameLobbyUpdated).get(TIMEOUT_DURATION, TimeUnit.SECONDS))

        val gameLobbyUpdatedFound = remoteDB.getGameLobbyWithCode(testGameLobby1.code).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(gameLobbyUpdated, gameLobbyUpdatedFound)
    }

    @Test
    fun unregisteredGameLobbyCannotBeUpdated() {
        removeAllChildrenFromDB(gameLobbiesRootRef)
        val failFuture = remoteDB.updateGameLobby(testGameLobby1)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }
}