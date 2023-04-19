package com.github.polypoly.app.network

import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.game.GameLobby
import com.github.polypoly.app.game.user.User
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@RunWith(JUnit4::class)
class RemoteDBTest: PolyPolyTest(true, false) {

    private fun <T : Any> classCanBeStoredInDB(clazz: KClass<T>) {
        val noArgsKey = "no-args-obj"
        val noArgsConstructor = clazz.java.getDeclaredConstructor()
        val noArgsInstance = noArgsConstructor.newInstance()
        addDataToDB(listOf(noArgsInstance), listOf(noArgsKey))
        assertEquals(
            noArgsInstance,
            remoteDB.getValue(noArgsKey, clazz).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun gameLobbyCanBeStoredInDB() {
        classCanBeStoredInDB(GameLobby::class)
    }

    @Test
    fun dataCanBeRetrievedFromKey() {
        addUserToDB(TEST_USER_1)
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
        addUsersToDB(ALL_TEST_USERS, sharedKey)
        val allUsersFound = remoteDB.getAllValues<User>(sharedKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(ALL_TEST_USERS.sortedBy(User::id), allUsersFound.sortedBy(User::id))
    }

    @Test
    fun gettingAllDataWithInvalidKeyReturnsEmptyList() {
        val invalidKey = "invalid"
        val dataFound = remoteDB.getAllValues<User>(invalidKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(dataFound.isEmpty())
    }

    @Test
    fun allKeysCanBeRetrievedAtOnce() {
        val rootKey = "root_key/"
        val allUsersKeys = ALL_TEST_USERS.map{ user -> user.id.toString()}

        addUsersToDB(ALL_TEST_USERS, rootKey)
        val allUsersKeysFound = remoteDB.getAllKeys(rootKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(allUsersKeys.sorted(), allUsersKeysFound.sorted())
    }

    @Test
    fun gettingAllKeysWithInvalidKeyReturnsEmptyList() {
        val invalidKey = "invalid"
        val keysFound = remoteDB.getAllKeys(invalidKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(keysFound.isEmpty())
    }

    @Test
    fun existingKeyCanBeIdentified() {
        val existingKey = "I_exist"
        addDataToDB(listOf(TEST_USER_1), listOf(existingKey))
        assertTrue(
            remoteDB.keyExists(existingKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun nonExistingKeyCanBeIdentified() {
        val nonExistingKey = "I_do_not_exist"
        assertFalse(
            remoteDB.keyExists(nonExistingKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun newDataCanBeRegistered() {
        assertTrue(
            remoteDB.registerValue(TEST_USER_1.id.toString(), TEST_USER_1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
        val userFound = remoteDB.getValue<User>(TEST_USER_1.id.toString()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(TEST_USER_1, userFound)
    }

    @Test
    fun registeringAnAlreadyRegisteredDataFails() {
        addUserToDB(TEST_USER_1)
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
        addUserToDB(TEST_USER_1)

        assertTrue(
            remoteDB.updateValue(userUpdated.id.toString(), userUpdated).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )

        val userUpdatedFound = remoteDB.getValue<User>(userUpdated.id.toString()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(userUpdated, userUpdatedFound)
    }

    @Test
    fun updatingNonExistingDataFails() {
        val failFuture = remoteDB.updateValue(TEST_USER_1.id.toString(), TEST_USER_1)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun dataCanBeSet() {
        assertTrue(
            remoteDB.registerValue(TEST_USER_1.id.toString(), TEST_USER_1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
        val userFound = remoteDB.getValue<User>(TEST_USER_1.id.toString()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(TEST_USER_1, userFound)
    }
}