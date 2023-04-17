package com.github.polypoly.app.global

import com.github.polypoly.app.network.RemoteDB
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Instances shared across the entire app for consistency purpose. This consist in:
 * - a RemoteDB instance which is our connection to the firebase DB
 * - more to be added later
 */
class GlobalInstances {
    companion object {
        lateinit var remoteDB: RemoteDB
    }
}