package com.github.polypoly.app.network

import com.github.polypoly.app.game.user.Skin
import com.github.polypoly.app.game.user.Stats
import com.github.polypoly.app.game.user.User
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class RemoteDBTest {
    companion object {
        const val TIMEOUT_DURATION = 1L

        val ZERO_STATS = Stats(0, 0, 0, 0, 0)
        val NO_SKIN = Skin(0,0,0)

        val TEST_USER_1 = User(1234L,"John", "Hi!", Skin(1, 1, 1),
            ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_2 = User(12345L,"Harry", "Ha!", Skin(1, 1, 1),
            ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_3 = User(123456L,"James", "Hey!", Skin(1, 1, 1),
            ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_4 = User(1234567L,"Henri", "Ohh!", Skin(1, 1, 1),
            ZERO_STATS, listOf(), mutableListOf())

        val TEST_ALL_USERS = listOf(TEST_USER_1, TEST_USER_2, TEST_USER_3, TEST_USER_4)
    }

    @Suppress("JoinDeclarationAndAssignment")
    private val rootRef: DatabaseReference
    private val remoteStorageRootName = "test"

    init {
        val db = Firebase.database
        try {
            db.setPersistenceEnabled(false)
            remoteDB = RemoteDB(db, remoteStorageRootName)
        } catch(_: java.lang.Exception) { }
        rootRef = remoteDB.rootRef
    }

    private fun <T> addDataToDB(data: List<T>, keys: List<String>) {
        val timeouts = List(data.size) {CompletableFuture<Boolean>()}

        for (i in data.indices) {
            val user = data[i]
            rootRef.child(keys[i])
                .setValue(user)
                .addOnSuccessListener {
                    timeouts[i].complete(true)
                }.addOnFailureListener(timeouts[i]::completeExceptionally)
        }

        timeouts.map{ timeout -> timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)}
    }

    private fun addUsersToDB(users: List<User>, root: String = "") = addDataToDB(users, users.map{user ->  root + user.id})

    @Before
    fun removeAllDataFromDB() {
        val timeout = CompletableFuture<Boolean>()
        rootRef.removeValue()
            .addOnSuccessListener {
                timeout.complete(true)
            }.addOnFailureListener(timeout::completeExceptionally)
        timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }

    @Test
    fun dataCanBeRetrievedFromKey() {
        addUsersToDB(listOf(TEST_USER_1))
        val userFound = remoteDB.getValue<User>(TEST_USER_1.id.toString()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(TEST_USER_1, userFound)
    }

    @Test
    fun gettingDataWithInvalidKeyFails() {
        val invalidKey = "invalid"
        val failFuture = remoteDB.getValue<User>(invalidKey)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessException)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun allDataWithSameKeyCanBeRetrievedAtOnce() {
        val sharedKey = "all_users/"
        addUsersToDB(TEST_ALL_USERS, sharedKey)
        val allUsersFound = remoteDB.getAllValues<User>(sharedKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(TEST_ALL_USERS.sortedBy(User::id), allUsersFound.sortedBy(User::id))
    }

    @Test
    fun allKeysCanBeRetrievedAtOnce() {
        val rootKey = "root_key/"
        val allUsersKeys = TEST_ALL_USERS.map{user -> user.id.toString()}

        addUsersToDB(TEST_ALL_USERS, rootKey)
        val allUsersKeysFound = remoteDB.getAllKeys(rootKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(allUsersKeys.sorted(), allUsersKeysFound.sorted())
    }

    @Test
    fun newDataCanBeRegistered() {
        assertTrue(
            remoteDB.registerValue(TEST_USER_1.id.toString(), TEST_USER_1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun registeringAnAlreadyRegisteredDataFails() {
        addUsersToDB(listOf(TEST_USER_1))
        val failFuture = remoteDB.registerValue(TEST_USER_1.id.toString(), TEST_USER_1)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun dataCanBeUpdatedAfterRegistration() {
        val userUpdated = User(TEST_USER_1.id, "Cool_name", "I updated my bio!", NO_SKIN,
            ZERO_STATS, listOf(), mutableListOf())
        addUsersToDB(listOf(TEST_USER_1))

        assertTrue(remoteDB.updateValue(userUpdated.id.toString(), userUpdated).get(TIMEOUT_DURATION, TimeUnit.SECONDS))

        val userUpdatedFound = remoteDB.getValue<User>(userUpdated.id.toString()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(userUpdated, userUpdatedFound)
    }

    @Test
    fun unergisteredDataCannotBeUpdated() {
        val failFuture = remoteDB.updateValue(TEST_USER_1.id.toString(), TEST_USER_1)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }
}