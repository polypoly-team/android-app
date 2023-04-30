package com.github.polypoly.app.network.storable

import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

abstract class StorableObject<T : Any>
    (private val path: String, private val key: String, private val clazz: KClass<T>) {

    fun getByKey(key: String): CompletableFuture<StorableObject<T>> {
        return remoteDB.getValue(path + key, clazz).thenCompose { obj ->
            val result = CompletableFuture<StorableObject<T>>()
            result.complete(toLocalObject(obj))
            result
        }
    }

    fun register(): CompletableFuture<Boolean> {
        return remoteDB.registerValue(path + key, toDBObject())
    }

    fun update(): CompletableFuture<Boolean> {
        return remoteDB.updateValue(path + key, toDBObject())
    }

    fun remove(): CompletableFuture<Boolean> {
        return remoteDB.removeElement(path + key)
    }

    fun onChange(action: (newObj: T) -> Unit) {
        remoteDB.addListener(path + key, object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                action(data.getValue(clazz.java)!!)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    abstract fun toDBObject() : T
    abstract fun toLocalObject(dbObject: T) : StorableObject<T>
}