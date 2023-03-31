package com.github.polypoly.app.network

import com.github.polypoly.app.game.Skin
import com.github.polypoly.app.game.Stats
import com.github.polypoly.app.game.User
import com.github.polypoly.app.global.GlobalInstances
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
    fun userProfileCanBeRetrievedFromId() {
        val setTimeout = CompletableFuture<Boolean>()
        val usersRoot = underlyingDB.getReference(DB_USERS_PROFILES_PATH)
        usersRoot.child(testUser.id.toString())
            .setValue(testUser)
            .addOnSuccessListener {
            setTimeout.complete(true)
        }.addOnFailureListener(setTimeout::completeExceptionally)
        setTimeout.get(5, TimeUnit.SECONDS)

        val userFound = remoteDB.getUserWithId(testUser.id).get(5, TimeUnit.SECONDS)

        assertEquals(testUser.id, userFound.id)
        assertEquals(testUser.name, userFound.name)
        assertEquals(testUser.bio, userFound.bio)
        assertEquals(testUser.skin, userFound.skin)
        assertEquals(testUser.stats, userFound.stats)
    }
}