package com.github.polypoly.app.network

import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.base.game.rules_and_lobby.kotlin.GameLobby
import com.github.polypoly.app.base.user.User
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
        val key = "some_key"
        val data = TEST_USER_1
        addDataToDB(data, key)
        val dataFound = remoteDB.getValue<User>(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(data, dataFound)
    }

    @Test
    fun gettingDataWithInvalidKeyFails() {
        val invalidKey = "invalid_key"
        val failFuture = remoteDB.getValue<User>(invalidKey)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessException)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun allDataWithSameKeyCanBeRetrievedAtOnce() {
        val sharedKey = "some_parent_key/"
        val allData = ALL_TEST_USERS
        val allKeys = ALL_TEST_USERS.map { user -> user.id.toString() }

        addDataToDB(allData, allKeys, sharedKey)
        val allDataFound = remoteDB.getAllValues<User>(sharedKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(allData.sortedBy(User::id), allDataFound.sortedBy(User::id))
    }

    @Test
    fun gettingAllDataWithInvalidKeyReturnsEmptyList() {
        val invalidKey = "invalid_key"
        val dataFound = remoteDB.getAllValues<User>(invalidKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(dataFound.isEmpty())
    }

    @Test
    fun allKeysCanBeRetrievedAtOnce() {
        val sharedKey = "some_root_key/"
        val allData = ALL_TEST_USERS
        val allDataKeys = ALL_TEST_USERS.indices.map(Int::toString)
        addDataToDB(allData, allDataKeys, sharedKey)

        val allUsersKeysFound = remoteDB.getAllKeys(sharedKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(allDataKeys.sorted(), allUsersKeysFound.sorted())
    }

    @Test
    fun gettingAllKeysWithInvalidKeyReturnsEmptyList() {
        val invalidKey = "invalid_key"
        val keysFound = remoteDB.getAllKeys(invalidKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(keysFound.isEmpty())
    }

    @Test
    fun existingKeyCanBeIdentified() {
        val existingKey = "I_exist"
        val value = TEST_USER_1
        addDataToDB(value, existingKey)
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
        val data = TEST_USER_1
        val key = "some_key"
        assertTrue(
            remoteDB.registerValue(key, data).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
        val dataFound = remoteDB.getValue<User>(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(data, dataFound)
    }

    @Test
    fun registeringAnAlreadyRegisteredDataFails() {
        val key = "some_key"
        val data = TEST_USER_1
        val dataBis = TEST_USER_2

        addDataToDB(data, key)
        val failFuture = remoteDB.registerValue(key, dataBis)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }

        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun dataCanBeUpdatedAfterRegistration() {
        val key = "some_key"
        val data = TEST_USER_1
        val dataUpdated = TEST_USER_2

        addDataToDB(data, key)

        assertTrue(
            remoteDB.updateValue(key, dataUpdated).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )

        val dataUpdatedFound = remoteDB.getValue<User>(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(dataUpdated, dataUpdatedFound)
    }

    @Test
    fun updatingNonExistingDataFails() {
        val key = "some_key"
        val data = TEST_USER_1
        val failFuture = remoteDB.updateValue(key, data)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun dataCanBeSet() {
        val key = "some_key"
        val data = TEST_USER_1
        assertTrue(
            remoteDB.registerValue(key, data).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
        val dataFound = remoteDB.getValue<User>(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(data, dataFound)
    }
}