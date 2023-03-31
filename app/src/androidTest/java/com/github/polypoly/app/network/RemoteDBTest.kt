package com.github.polypoly.app.network

import com.github.polypoly.app.game.Skin
import com.github.polypoly.app.game.Stats
import com.github.polypoly.app.game.User
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

    private val testUser = User(1234L,"John", "Hi!", Skin(1, 1, 1), Stats())

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

    fun addUsersToDB(users: List<User>): CompletableFuture<Boolean> {
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

        return timeouts.reduce{ future, next ->
            if (future.isCompletedExceptionally) {
                future
            } else {
                next
            }
        }
    }

    @Test
    fun userCanBeRetrievedFromId() {
        addUsersToDB(listOf(testUser))
        val userFound = remoteDB.getUserWithId(testUser.id).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertEquals(testUser, userFound)
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
        val testUser2 = User(123456L,"Harry", "Hi!", Skin(1, 1, 1), Stats())
        val testUser3 = User(1234567L,"James", "Hey!", Skin(1, 1, 1), Stats())
        val testUser4 = User(12345678L,"Henri", "Ohh!", Skin(1, 1, 1), Stats())

        val allUsers = listOf(testUser, testUser2, testUser3, testUser4)

        addUsersToDB(allUsers)

        val allUsersFound = remoteDB.getAllUsers().get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(allUsers.size, allUsersFound.size)
        for (user in allUsers) {
            assertTrue(allUsersFound.contains(user))
        }
    }
}