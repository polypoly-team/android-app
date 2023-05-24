package com.github.polypoly.app.database

import com.github.polypoly.app.commons.PolyPolyTest
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

@RunWith(JUnit4::class)
class RemoteDBTest: PolyPolyTest(false, false) {

    private val rootTests = "test-hugo"
    private var dbRootRef: DatabaseReference
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


    // ==================================================================================
    // ========================================================================== GETTERS
    // ==================================================================================

    // ========================================================================== GET VALUE

    @Test
    fun dataCanBeRetrievedFromKey() {
        val data = TEST_USER_1
        addDataToDB(data)
        val dataFound = remoteDB.getValue<User>(data.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
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

    // ========================================================================== GET VALUES

    @Test
    fun getValuesFailsWithAtLeastOneInvalidKey() {
        val invalidKey = "invalid_key"
        val data = TEST_USER_1
        addDataToDB(data)

        val failedFuture = remoteDB.getValues<User>(listOf(data.key, invalidKey))
        failedFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is NoSuchElementException)
        }
        assertThrows(ExecutionException::class.java) { failedFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun getValuesWithEmptyListReturnsEmptyList() {
        val dataFound = remoteDB.getValues<User>(listOf()).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(dataFound.isEmpty())
    }

    @Test
    fun getValuesReturnAllRequestedValues() {
        val data1 = TEST_USER_1
        val data2 = TEST_USER_2
        addDataToDB(data1)
        addDataToDB(data2)

        val dataFound = remoteDB.getValues<User>(listOf(data1.key, data2.key)).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(dataFound, listOf(data1, data2))
    }

    // ========================================================================== GET ALL VALUES

    @Test
    fun allDataWithSameKeyCanBeRetrievedAtOnce() {
        val allData = ALL_TEST_USERS
        addDataToDB(allData)
        val allDataFound = remoteDB.getAllValues<User>().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(allData.sortedBy(User::id), allDataFound.sortedBy(User::id))
    }

    @Test
    fun gettingAllDataWithNoDataReturnsEmptyList() {
        val dataFound = remoteDB.getAllValues<User>().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(dataFound.isEmpty())
    }

    // ========================================================================== GET ALL KEYS

    @Test
    fun allKeysCanBeRetrievedAtOnce() {
        val allData = ALL_TEST_USERS
        val allDataKeys = ALL_TEST_USERS.map { user -> user.key }
        addDataToDB(allData)

        val allUsersKeysFound = remoteDB.getAllKeys<User>().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(allDataKeys.sorted(), allUsersKeysFound.sorted())
    }

    @Test
    fun gettingAllKeysWithNoDataReturnsEmptyList() {
        val keysFound = remoteDB.getAllKeys<User>().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(keysFound.isEmpty())
    }

    // ========================================================================== KEY EXISTS

    @Test
    fun existingKeyCanBeIdentified() {
        val value = TEST_USER_1
        addDataToDB(value)
        assertTrue(
            remoteDB.keyExists<User>(value.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun nonExistingKeyCanBeIdentified() {
        val nonExistingKey = "I_do_not_exist"
        assertFalse(
            remoteDB.keyExists<User>(nonExistingKey).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    // ==================================================================================
    // ========================================================================== SETTERS
    // ==================================================================================

    // ========================================================================== REGISTER VALUE

    @Test
    fun unregisteredDataCanBeRegistered() {
        val data = TEST_USER_1
        assertTrue(remoteDB.registerValue(data).get(TIMEOUT_DURATION, TimeUnit.SECONDS))
    }

    @Test
    fun unregisteredDataIsWellRegistered() {
        val data = TEST_USER_1
        remoteDB.registerValue(data).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        val dataFound = remoteDB.getValue<User>(data.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(data, dataFound)
    }

    @Test
    fun registeringAnAlreadyRegisteredDataFails() {
        val data = TEST_USER_1

        addDataToDB(data)
        val failFuture = remoteDB.registerValue(data)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }

        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    // ========================================================================== UPDATE VALUE

    @Test
    fun dataCanBeUpdatedAfterRegistration() {
        val data = TEST_USER_1
        val dataUpdated = TEST_USER_1_BIS

        addDataToDB(data)
        assertTrue(remoteDB.updateValue(dataUpdated).get(TIMEOUT_DURATION, TimeUnit.SECONDS))
    }

    @Test
    fun dataIsUpdatedAfterRegistration() {
        val data = TEST_USER_1
        val dataUpdated = TEST_USER_1_BIS

        addDataToDB(data)
        remoteDB.updateValue(dataUpdated).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        val dataUpdatedFound = remoteDB.getValue<User>(data.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(dataUpdated, dataUpdatedFound)
    }

    @Test
    fun updatingNonExistingDataFails() {
        val data = TEST_USER_1
        val failFuture = remoteDB.updateValue(data)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is NoSuchElementException)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    // ========================================================================== SET VALUE

    @Test
    fun dataCanBeSet() {
        val data = TEST_USER_1
        assertTrue(
            remoteDB.registerValue(data).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
        val dataFound = remoteDB.getValue<User>(data.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(data, dataFound)
    }

    // ========================================================================== REMOVE VALUE

    @Test
    fun unregisteredDataCantBeRemoved() {
        val key = "some_key"
        assertFalse(remoteDB.removeValue<User>(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS))
    }

    @Test
    fun registeredDataCanBeRemoved() {
        val data = TEST_USER_1
        remoteDB.registerValue(data).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(remoteDB.removeValue<User>(data.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS))
    }

    @Test
    fun registeredDataIsRemoved() {
        val data = TEST_USER_1
        remoteDB.registerValue(data).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.removeValue<User>(data.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertFalse(remoteDB.keyExists<User>(data.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS))
    }

    // ====================================================================================
    // ========================================================================== LISTENERS
    // ====================================================================================

    // ========================================================================== ADD ON CHANGE (key)

    @Test
    fun addingOnChangeListenerToUnregisteredDataFails() {
        val key = "some_key"
        val action = { _: User -> }
        assertFalse(
            remoteDB.addOnChangeListener(key, "tag", action).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun addingOnChangeListenerToRegisteredDataWorks() {
        val action = { _: User -> }

        val data = TEST_USER_1
        addDataToDB(data)

        assertTrue(
            remoteDB.addOnChangeListener(data.key, "tag", action).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun onChangeListenerIsExecutedAtUpdate() {
        var num = 0
        val action = { _: User -> num += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_1_BIS
        addDataToDB(data1)

        remoteDB.addOnChangeListener(data1.key, "tag", action).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num >= 2)
    }

    @Test
    fun onChangeListenerIsExecutedWithNewData() {
        val userList = mutableListOf<User>()
        val action = { user: User ->
            val ignore = userList.add(user)
        }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_1_BIS
        addDataToDB(data1)

        remoteDB.addOnChangeListener(data1.key, "tag", action).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(userList.any { user -> user.id == data2.id })
    }

    @Test
    fun oldOnChangeListenerIsNotExecutedAtOverwrite() {
        var num1 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_1_BIS
        addDataToDB(data1)

        remoteDB.addOnChangeListener(data1.key, "tag", action1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(data1.key, "tag", action2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 <= 1)
    }

    @Test
    fun newOnChangeListenerIsExecutedAtOverwrite() {
        var num1 = 0
        var num2 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> num2 += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_1_BIS
        addDataToDB(data1)

        remoteDB.addOnChangeListener(data1.key, "tag", action1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(data1.key, "tag", action2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num2 >= 3)
    }

    @Test
    fun onChangeListenersWithDifferentTagsAreExecutedAtUpdate() {
        var num1 = 0
        var num2 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> num2 += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_1_BIS
        addDataToDB(data1)

        remoteDB.addOnChangeListener(data1.key, "tag1", action1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(data1.key, "tag2", action2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 >= 3)
        assertTrue(num2 >= 3)
    }

    @Test
    fun onChangeListenerIsNotExecutedAtRest() {
        var num = 0
        val action = { _: User -> num += 1 }

        val data = TEST_USER_1
        addDataToDB(data)

        remoteDB.addOnChangeListener(data.key, "tag", action).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(num, 0)
    }

    // ========================================================================== DELETE ON CHANGE

    @Test
    fun deletingOnChangeListenerFromUnregisteredDataFails() {
        val key = "some_key"
        assertFalse(
            remoteDB.deleteOnChangeListener<User>(key, "tag").get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun deletingOnChangeListenerFromRegisteredDataWorks() {
        val data = TEST_USER_1
        addDataToDB(data)

        assertTrue(
            remoteDB.deleteOnChangeListener<User>(data.key, "tag").get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun onChangeListenerIsNotExecutedAfterDeletion() {
        var num = 0
        val action = { _: User -> num += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_1_BIS
        addDataToDB(data1)

        remoteDB.addOnChangeListener(data1.key, "tag", action).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.deleteOnChangeListener<User>(data1.key, "tag").get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num <= 1)
    }

    @Test
    fun onChangeListenerIsNotExecutedAfterRemoveValue() {
        var num = 0
        val action = { _: User -> num += 1 }

        val data = TEST_USER_1
        addDataToDB(data)

        remoteDB.addOnChangeListener(data.key, "tag", action).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.removeValue<User>(data.key)
        assertTrue(num <= 1)
    }

    @Test
    fun onChangeListenerIsExecutedAfterDeletingAnotherTag() {
        var num1 = 0
        var num2 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> num2 += 1 }

        val data1 = TEST_USER_1
        val data2 = TEST_USER_1_BIS
        addDataToDB(data1)

        remoteDB.addOnChangeListener(data1.key, "tag1", action1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(data1.key, "tag2", action2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.deleteOnChangeListener<User>(data1.key, "tag2").get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 >= 3)
    }

    @Test
    fun onChangeListenerIsExecutedForDifferentKeysWithDifferentTags() {
        var num1 = 0
        var num2 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> num2 += 1 }

        val data1 = TEST_USER_1
        val data1Updated = TEST_USER_1_BIS
        addDataToDB(data1)

        val data2 = TEST_USER_2
        val data2Updated = TEST_USER_2_BIS
        addDataToDB(data2)

        remoteDB.addOnChangeListener(data1.key, "tag1", action1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(data2.key, "tag2", action2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1Updated).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2Updated).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 >= 2)
        assertTrue(num2 >= 2)
    }

    @Test
    fun onChangeListenerIsExecutedForDifferentKeysWithSameTags() {
        var num1 = 0
        var num2 = 0
        val action1 = { _: User -> num1 += 1 }
        val action2 = { _: User -> num2 += 1 }

        val data1 = TEST_USER_1
        val data1Updated = TEST_USER_1_BIS
        addDataToDB(data1)

        val data2 = TEST_USER_2
        val data2Updated = TEST_USER_2_BIS
        addDataToDB(data2)

        remoteDB.addOnChangeListener(data1.key, "tag", action1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(data2.key, "tag", action2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1Updated).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2Updated).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 >= 2)
        assertTrue(num2 >= 2)
    }

    // ========================================================================== DELETE ALL ON CHANGE

    @Test
    fun deletingAllOnChangeListenersFromUnregisteredDataFails() {
        val key = "some_key"
        assertFalse(
            remoteDB.deleteAllOnChangeListeners<User>(key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        )
    }

    @Test
    fun deletingAllOnChangeListenersFromRegisteredDataWorks() {
        val data = TEST_USER_1
        addDataToDB(data)

        assertTrue(
            remoteDB.deleteAllOnChangeListeners<User>(data.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
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
        val data2 = TEST_USER_1_BIS
        addDataToDB(data1)

        remoteDB.addOnChangeListener(data1.key, "tag1", action1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.addOnChangeListener(data1.key, "tag2", action2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.deleteAllOnChangeListeners<User>(data1.key).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data1).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        remoteDB.updateValue(data2).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(num1 <= 1)
        assertTrue(num1 <= 1)
    }

}