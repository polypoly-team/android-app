package com.github.polypoly.app.utils.global

import com.github.polypoly.app.base.game.TradeRequest
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.database.IRemoteStorage
import com.github.polypoly.app.database.RemoteDB
import com.github.polypoly.app.database.getValue
import com.github.polypoly.app.database.keyExists
import com.github.polypoly.app.ui.menu.lobby.UniqueGameLobbyCodeGenerator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Instances shared across the entire app for consistency purpose. This consist in:
 * - a RemoteDB instance which is our connection to the firebase DB
 * - more to be added later
 */
class GlobalInstances {
    companion object {

        // ============================================== STORAGE
        /**
         * For the storable classes to be correctly initialized, we create dummy instances
         */
        @Suppress("UNUSED")
        val dummyInstances = listOf(GameLobby(), User(), TradeRequest())

        lateinit var remoteDB: IRemoteStorage
        var remoteDBInitialized = false
        private const val rootDB = "live"

        fun initRemoteDB() {
            if(!remoteDBInitialized) {
                val db = Firebase.database
                remoteDB = RemoteDB(db, rootDB)
            }
        }

        // ============================================== CURRENT USER
        var currentUser : User? = null

        var isSignedIn = false

        fun initCurrentUser(key: String, name: String) {
            remoteDB.keyExists<User>(key).thenAccept { exists ->
                if(exists) {
                    remoteDB.getValue<User>(key).thenAccept { currentUser = it }
                } else {
                    currentUser = User(id = key, name = name, currentUser = true)
                    remoteDB.registerValue(currentUser!!)
                }
            }
        }

        // ============================================== GAME CODE
        val uniqueCodeGenerator = UniqueGameLobbyCodeGenerator()
    }
}