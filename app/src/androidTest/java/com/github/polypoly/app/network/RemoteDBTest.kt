package com.github.polypoly.app.network

import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDBInitialized
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException
import kotlin.reflect.KClass

@RunWith(JUnit4::class)
class RemoteDBTest: PolyPolyTest(false, false) {

    private val rootTests = "test-hugo"
    var dbRootRef: DatabaseReference
    private val testDB: RemoteDB

    init {
        val db = Firebase.database
        try {
            db.setPersistenceEnabled(false)
        } catch (_: Exception) {}
        testDB = RemoteDB(db, rootTests)
        remoteDB = testDB
        remoteDBInitialized = true

        dbRootRef = db.reference.child(rootTests)
    }

    override fun _prepareTest() {
        clearRealDB()
        remoteDB = testDB
    }

    private fun clearRealDB() {
        val timeout = CompletableFuture<Boolean>()
        dbRootRef.removeValue()
            .addOnSuccessListener {
                timeout.complete(true)
            }.addOnFailureListener(timeout::completeExceptionally)
        timeout.get(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }



    /*@Test
    fun dataCanBeRetrievedFromKey() {
        val data = TEST_USER_1
        addDataToDB(data)
        val dataFound = remoteDB.getValue<User>(TEST_USER_1.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(data, dataFound)
    }

    @Test
    fun gettingDataWithInvalidKeyFails() {
        val invalidKey = "invalid_key"
        val failFuture = remoteDB.getValue<User>(invalidKey)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is NoSuchElementException)
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
    }*/

    // ========================================================================== SETTERS

    /*@Test
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

    @Test
    fun unregisteredDataCantBeRemoved() {
        val key = "some_key"
        assertFalse(
            remoteDB.removeValue(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun registeredDataCanBeRemoved() {
        val key = "some_key"
        val data = TEST_USER_1
        remoteDB.registerValue(key, data).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(
            remoteDB.removeValue(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun registeredDataIsRemoved() {
        val key = "some_key"
        val data = TEST_USER_1
        remoteDB.registerValue(key, data).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.removeValue(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertFalse(
            remoteDB.keyExists(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }*/

    // ========================================================================== LISTENERS
    /*@Test
    fun addingOnChangeListenerToUnregisteredDataFails() {
        val key = "some_key"
        val action = { _: User -> }
        assertFalse(
            remoteDB.addOnChangeListener(key, "tag", action, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun addingOnChangeListenerToRegisteredDataWorks() {
        val key = "some_key"
        val action = { _: User -> }

        val data = TEST_USER_1
        addDataToDB(data, key)

        assertTrue(
            remoteDB.addOnChangeListener(key, "tag", action, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun onChangeListenerIsExecutedAtUpdate() {
        val key = "some_key"
        var num = 0
        val action = { _: User -> num += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_2
        addDataToDB(data1, key)

        remoteDB.addOnChangeListener(key, "tag", action, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num >= 2)
    }

    @Test
    fun onChangeListenerIsExecutedWithNewData() {
        val key = "some_key"
        val userList = mutableListOf<User>()
        val action = { user: User ->
            val ignore = userList.add(user)
        }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_2
        addDataToDB(data1, key)

        remoteDB.addOnChangeListener(key, "tag", action, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(userList.any { user -> user.id == data2.id })
    }

    @Test
    fun oldOnChangeListenerIsNotExecutedAtOverwrite() {
        val key = "some_key"
        var num1 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_2
        addDataToDB(data1, key)

        remoteDB.addOnChangeListener(key, "tag", action1, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(key, "tag", action2, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 <= 1)
    }

    @Test
    fun newOnChangeListenerIsExecutedAtOverwrite() {
        val key = "some_key"
        var num1 = 0
        var num2 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> num2 += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_2
        addDataToDB(data1, key)

        remoteDB.addOnChangeListener(key, "tag", action1, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(key, "tag", action2, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num2 >= 3)
    }

    @Test
    fun onChangeListenersWithDifferentTagsAreExecutedAtUpdate() {
        val key = "some_key"
        var num1 = 0
        var num2 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> num2 += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_2
        addDataToDB(data1, key)

        remoteDB.addOnChangeListener(key, "tag1", action1, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(key, "tag2", action2, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 >= 3)
        assertTrue(num2 >= 3)
    }

    @Test
    fun onChangeListenerIsNotExecutedAtRest() {
        val key = "some_key"
        var num = 0
        val action = { _: User -> num += 1 }

        val data = TEST_USER_1
        addDataToDB(data, key)

        remoteDB.addOnChangeListener(key, "tag", action, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(num, 0)
    }

    @Test
    fun deletingOnChangeListenerFromUnregisteredDataFails() {
        val key = "some_key"
        assertFalse(
            remoteDB.deleteOnChangeListener(key, "tag").get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun deletingOnChangeListenerFromRegisteredDataWorks() {
        val key = "some_key"

        val data = TEST_USER_1
        addDataToDB(data, key)

        assertTrue(
            remoteDB.deleteOnChangeListener(key, "tag").get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun onChangeListenerIsNotExecutedAfterDeletion() {
        val key = "some_key"
        var num = 0
        val action = { _: User -> num += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_2
        addDataToDB(data1, key)

        remoteDB.addOnChangeListener(key, "tag", action, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.deleteOnChangeListener(key, "tag").get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num <= 1)
    }

    @Test
    fun onChangeListenerIsNotExecutedAfterRemoveValue() {
        val key = "some_key"
        var num = 0
        val action = { _: User -> num += 1 }

        val data = TEST_USER_1
        addDataToDB(data, key)

        remoteDB.addOnChangeListener(key, "tag", action, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.removeValue(key)
        assertTrue(num <= 1)
    }

    @Test
    fun onChangeListenerIsExecutedAfterDeletingAnotherTag() {
        val key = "some_key"
        var num1 = 0
        var num2 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> num2 += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_2
        addDataToDB(data1, key)

        remoteDB.addOnChangeListener(key, "tag1", action1, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(key, "tag2", action2, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.deleteOnChangeListener(key, "tag2").get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 >= 3)
    }

    @Test
    fun deletingAllOnChangeListenersFromUnregisteredDataFails() {
        val key = "some_key"
        assertFalse(
            remoteDB.deleteAllOnChangeListeners(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun deletingAllOnChangeListenersFromRegisteredDataWorks() {
        val key = "some_key"

        val data = TEST_USER_1
        addDataToDB(data, key)

        assertTrue(
            remoteDB.deleteAllOnChangeListeners(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun allOnChangeListenersDoNotExecuteAfterDeletingAll() {
        val key = "some_key"
        var num1 = 0
        var num2 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> num2 += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_2
        addDataToDB(data1, key)

        remoteDB.addOnChangeListener(key, "tag1", action1, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(key, "tag2", action2, User::class).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.deleteAllOnChangeListeners(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(key, data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 <= 1)
        assertTrue(num1 <= 1)
    }*/

}