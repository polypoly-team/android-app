package com.github.polypoly.app.network

import com.github.polypoly.app.game.Skin
import com.github.polypoly.app.game.Stats
import com.github.polypoly.app.game.User
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
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

    private var underlyingDB: FirebaseDatabase

    private val testUser = User(1234L,"John", "Hi!", Skin(1, 1, 1), Stats())

    init {
        val db = Firebase.database
        try {
            db.setPersistenceEnabled(false)
        } catch(_: java.lang.Exception) { }
        remoteDB = RemoteDB(db)
        underlyingDB = remoteDB.getUnderlyingDB()
    }

    @Test
    fun userCanBeRetrievedFromId() {
        val setTimeout = CompletableFuture<Boolean>()
        val usersRoot = underlyingDB.getReference(DB_USERS_PROFILES_PATH)
        usersRoot.child(testUser.id.toString())
            .setValue(testUser)
            .addOnSuccessListener {
            setTimeout.complete(true)
        }.addOnFailureListener(setTimeout::completeExceptionally)
        setTimeout.get(5, TimeUnit.SECONDS)

        val userFound = remoteDB.getUserWithId(testUser.id).get(TIMEOUT_DURATION, TimeUnit.SECONDS)

        assertEquals(testUser.id, userFound.id)
        assertEquals(testUser.name, userFound.name)
        assertEquals(testUser.bio, userFound.bio)
        assertEquals(testUser.skin, userFound.skin)
        assertEquals(testUser.stats, userFound.stats)
    }

    @Test
    fun gettingUserOfInvalidIdFails() {
        val invalidId = -1L
        val failFuture = remoteDB.getUserWithId(invalidId)
        failFuture.handle { _, exception ->
            assertTrue(exception != null)
            assertTrue(exception is IllegalAccessError)
        }
        assertThrows(ExecutionException::class.java) { failFuture.get(5, TimeUnit.SECONDS) }
    }
}