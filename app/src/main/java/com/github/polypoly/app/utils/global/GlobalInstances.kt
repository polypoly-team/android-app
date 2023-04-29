package com.github.polypoly.app.utils.global

import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.network.IRemoteStorage
import com.google.firebase.auth.FirebaseUser

/**
 * Instances shared across the entire app for consistency purpose. This consist in:
 * - a RemoteDB instance which is our connection to the firebase DB
 * - more to be added later
 */
class GlobalInstances {
    companion object {
        lateinit var remoteDB: IRemoteStorage
        var remoteDBInitialized = false

        var currentUser : User = User(7, "fake_user", "I am fake until google db is fully setup", Skin(0, 0, 0),
            Stats(0, 0, 0, 0, 0), listOf(), mutableListOf()
        )
        var currentFBUser : FirebaseUser? = null
        var isSignedIn = false
    }
}