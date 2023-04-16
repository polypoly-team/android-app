package com.github.polypoly.app

import com.github.polypoly.app.global.GlobalInstances
import com.github.polypoly.app.network.RemoteDB
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

open class AppTest {
    private val remoteStorageRootName = "test"

    init {
        val db = Firebase.database
        try {
            db.setPersistenceEnabled(false)
            GlobalInstances.remoteDB = RemoteDB(db, remoteStorageRootName)
        } catch(_: java.lang.Exception) { }
    }
}