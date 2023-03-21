package com.github.polypoly.app.network

import com.github.polypoly.app.game.Skin
import com.github.polypoly.app.game.Stats
import com.github.polypoly.app.game.User
import com.github.polypoly.app.global.GlobalInstances
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
import com.github.polypoly.app.network.RemoteDB
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class RemoteDBTest {

    private lateinit var underlyingDB: DatabaseReference

    private val testUserId = 1234L

    init {
        val db = Firebase.database
        try {
            db.setPersistenceEnabled(false)
        } catch(_: java.lang.Exception) { }
        GlobalInstances.remoteDB = RemoteDB(db)
        underlyingDB = GlobalInstances.remoteDB.getUnderlyingDB().reference
    }

    @Test
    fun userProfileCanBeRetrievedFromId() {
        val user = User(testUserId,"John", "Hi!", Skin(1, 1, 1), Stats())

        val setTimeout = CompletableFuture<Boolean>()
        underlyingDB.child(DB_USERS_PROFILES_PATH).child(user.id.toString()).setValue(user).addOnSuccessListener {
            setTimeout.complete(true)
        }.addOnFailureListener { setTimeout.completeExceptionally(it) }
        setTimeout.get(5, TimeUnit.SECONDS)

        val retrieveTimeout = GlobalInstances.remoteDB.getUserProfileWithId(testUserId)
        retrieveTimeout.get(5, TimeUnit.SECONDS)
        val userFound = retrieveTimeout.get()

        assertTrue(retrieveTimeout.isDone && !retrieveTimeout.isCompletedExceptionally)
        assertEquals(user.id, userFound.id)
        assertEquals(user.name, userFound.name)
        assertEquals(user.bio, userFound.bio)
        assertEquals(user.skin, userFound.skin)
        assertEquals(user.stats, userFound.stats)
    }


}