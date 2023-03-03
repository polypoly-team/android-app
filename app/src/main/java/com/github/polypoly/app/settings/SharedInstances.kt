package com.github.polypoly.app.settings

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SharedInstances {
    companion object {
        var DB = Firebase.database
        var remoteDB = DB.reference
    }
}