package com.github.polypoly.app.network.storable

import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * Every object that can be stored in the DB has a "local" and a "DB" version.
 * This superclass implements all the queries necessary to get and set values in the DB.
 *
 * This class takes a generic type T as argument, corresponding to the data class that is
 * going to be stored in the DB.
 */
abstract class StorableObject<T : Any>
    (private val path: String, private val key: String, private val clazz: KClass<T>) {

    /**
     * Gets the local version of the stored DB object
     * @param key: The key of the object in DB
     * @return A future with the local version object
     *
     * @note The returned value doesn't depend on the current object instance
     */
    fun getByKey(key: String): CompletableFuture<StorableObject<T>> {
        return remoteDB.getValue(path + key, clazz).thenCompose { obj ->
            val result = CompletableFuture<StorableObject<T>>()
            result.complete(toLocalObject(obj))
            result
        }
    }

    /**
     * Registers this instance (DB version) in the DB
     * @return A future with true iff the object was successfully stored
     */
    fun register(): CompletableFuture<Boolean> {
        return remoteDB.registerValue(path + key, toDBObject())
    }

    /**
     * Updates this instance (DB version) in the DB
     * @return A future with true iff the object was successfully updated
     */
    fun update(): CompletableFuture<Boolean> {
        return remoteDB.updateValue(path + key, toDBObject())
    }

    /**
     * Removes this instance (DB version) from the DB
     * @return A future with true iff the object was successfully removed
     */
    fun remove(): CompletableFuture<Boolean> {
        return remoteDB.removeElement(path + key)
    }

    /**
     * Executes a given action whenever the DB object corresponding to this instance is
     * modified.
     * @param action: Action to be executed when the DB object changes
     * @return A future with true iff the action was successfully added
     */
    fun onChange(action: (newObj: T) -> Unit): CompletableFuture<Boolean> {
        return remoteDB.addListener(path + key, object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                action(data.getValue(clazz.java)!!)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ================================================== ABSTRACT METHODS

    /**
     * Converts this instance to its DB version
     * @return the DB version
     */
    protected abstract fun toDBObject() : T

    /**
     * Converts the given DB version to its local version
     * @param dbObject: the object to convert
     * @return the local version
     *
     * @note The returned value doesn't depend on the current object instance
     */
    protected abstract fun toLocalObject(dbObject: T) : StorableObject<T>
}