package com.github.polypoly.app.global

import com.github.polypoly.app.network.RemoteDB
import java.util.concurrent.atomic.AtomicBoolean

class GlobalInstances {
    companion object {
        private val isRemoteDBSet: AtomicBoolean = AtomicBoolean(false)
        var remoteDB: RemoteDB = RemoteDB.InvalidRemoteDB
            get() {
                if (!isRemoteDBSet.get()) {
                    throw java.lang.IllegalStateException("RemoteDB is not yet setup")
                }
                return field
            }
            set(value) {
                if (!isRemoteDBSet.compareAndSet(false, true)) {
//                    throw java.lang.IllegalStateException("RemoteDB is already setup")
                }
                field = value
            }
    }
}