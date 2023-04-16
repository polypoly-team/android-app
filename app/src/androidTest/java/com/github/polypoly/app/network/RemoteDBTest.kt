package com.github.polypoly.app.network

import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
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

@RunWith(JUnit4::class)
class RemoteDBTest {
    companion object {
        val TIMEOUT_DURATION = 5L
    }

    private val underlyingDB: FirebaseDatabase
    private val rootName = "test"
    private val rootRef: DatabaseReference
    private val usersRootRef: DatabaseReference

    private val zeroStats = Stats(0, 0, 0, 0, 0)

    private val testUser1 = User(1234L,"John", "Hi!", Skin(1, 1, 1),
        zeroStats, listOf(), mutableListOf())
    private val testUser2 = User(12345L,"Harry", "Ha!", Skin(1, 1, 1),
        zeroStats, listOf(), mutableListOf())
    private val testUser3 = User(123456L,"James", "Hey!", Skin(1, 1, 1),
        zeroStats, listOf(), mutableListOf())
    private val testUser4 = User(1234567L,"Henri", "Ohh!", Skin(1, 1, 1),
        zeroStats, listOf(), mutableListOf())
    private val allTestUsers = listOf(testUser1, testUser2, testUser3, testUser4)

    init {
        val db = Firebase.database
        try {
            db.setPersistenceEnabled(false)
        } catch(_: java.lang.Exception) { }
        remoteDB = RemoteDB(db, rootName)
        underlyingDB = remoteDB.getUnderlyingDB()

        rootRef = underlyingDB.getReference(rootName)
        usersRootRef = rootRef.child(DB_USERS_PROFILES_PATH)
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

    fun removeAllUsersFromDB() {
        val timeout = CompletableFuture<Boolean>()
        usersRootRef.removeValue()
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
        removeAllUsersFromDB()
        addUsersToDB(allTestUsers)

        val allUsersFound = remoteDB.getAllUsers().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(allTestUsers.size, allUsersFound.size)
        for (user in allTestUsers) {
            assertTrue(allUsersFound.contains(user))
        }
    }

    @Test
    fun allUsersIdsCanBeRetrievedAtOnce() {
        removeAllUsersFromDB()
        addUsersToDB(allTestUsers)

        val allUsersIds = allTestUsers.map(User::id)
        val allUsersIdsFound = remoteDB.getAllUsersIds().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(allUsersIds.sorted(), allUsersIdsFound.sorted())
    }

    @Test
    fun newUserCanBeRegistered() {
        removeAllUsersFromDB()
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
        val userUpdated = User(testUser1.id, "Cool_name", "I updated my bio!", Skin(0,0,0),
            zeroStats, listOf(), mutableListOf())
        addUsersToDB(listOf(testUser1))

        assertTrue(remoteDB.updateUser(userUpdated).get(TIMEOUT_DURATION, TimeUnit.SECONDS))

        val userUpdatedFound = remoteDB.getUserWithId(userUpdated.id).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(userUpdated, userUpdatedFound)
    }

    @Test
    fun unergisteredUserCannotBeUpdated() {
        removeAllUsersFromDB()
        val failFuture = remoteDB.updateUser(testUser1)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }
}