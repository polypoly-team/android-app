package com.github.polypoly.app.utils.global
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.network.IRemoteStorage
import com.github.polypoly.app.network.RemoteDB
import com.github.polypoly.app.ui.menu.lobby.UniqueGameLobbyCodeGenerator
import com.google.firebase.auth.FirebaseUser
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
        val dummyInstances = listOf(GameLobby(), User())

        lateinit var remoteDB: IRemoteStorage
        var remoteDBInitialized = false
        private const val rootDB = "live"

        fun initRemoteDB() {
            if(!remoteDBInitialized) {
                val db = Firebase.database
                remoteDB = RemoteDB(db, rootDB)
            }
        }

        var currentUser : User = User(7, "fake_user", "I am fake until google db is fully setup", Skin.default(),
            Stats(0, 0, 0, 0, 0), listOf(), mutableListOf()
        )
        var currentFBUser : FirebaseUser? = null
        var isSignedIn = false

        val uniqueCodeGenerator = UniqueGameLobbyCodeGenerator()
    }
}