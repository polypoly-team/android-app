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
                if (isRemoteDBSet.compareAndSet(false, true)) {
                    field = value // cannot throw an IllegualArgumentException because it is not yet determinstic how the order of instantiation works for static methods
                }
            }
    }
}