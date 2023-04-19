package com.github.polypoly.app.global

import com.github.polypoly.app.network.RemoteDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Instances shared across the entire app for consistency purpose. This consist in:
 * - a RemoteDB instance which is our connection to the firebase DB
 * - more to be added later
 */
class GlobalInstances {
    companion object {
        lateinit var remoteDB: RemoteDB

        var currentUser : FirebaseUser? = null
        var isSignedIn = currentUser != null
    }
}