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

    // ================================================================== GETTERS
    /**
     * Gets the local version of the stored DB object
     * @param key: The key of the object in DB
     * @return A future with the local version object
     *
     * @note /!\ STATIC /!\ The returned value doesn't depend on the current object instance
     */
    fun get(key: String): CompletableFuture<StorableObject<T>> {
        return remoteDB.getValue(path + key, clazz).thenCompose { obj ->
            val result = CompletableFuture<StorableObject<T>>()
            result.complete(toLocalObject(obj))
            result
        }
    }

    /**
     * @note /!\ STATIC /!\ The returned value doesn't depend on the current object instance
     */
    fun getAll(): CompletableFuture<List<StorableObject<T>>> {
        return remoteDB.getAllValues(path, clazz).thenCompose { objList ->
            val result = CompletableFuture<List<StorableObject<T>>>()
            result.complete(objList.map { obj -> toLocalObject(obj) })
            result
        }
    }

    /**
     * @note /!\ STATIC /!\ The returned value doesn't depend on the current object instance
     */
    fun exists(key: String): CompletableFuture<Boolean> {
        return remoteDB.keyExists(key)
    }

    // ================================================================== SETTERS
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
        return remoteDB.removeValue(path + key)
    }

    // ================================================================== LISTENERS
    /**
     * Executes a given action whenever the DB object corresponding to this instance is
     * modified.
     * @param action: Action to be executed when the DB object changes
     * @return A future with true iff the action was successfully added
     */
    fun onChange(action: (newObj: T) -> Unit): CompletableFuture<Boolean> {
        return remoteDB.addChangeListener(path + key, action, clazz)
    }

    /**
     * Removes (if any) the action to be executed when this object is modified in DB
     * @return A promise holding false iff the action was not removed (if no action was found, holds true)
     */
    fun offChange(): CompletableFuture<Boolean> {
        return remoteDB.deleteChangeListener(path + key)
    }

    /**
     * Executes a given action whenever the DB object corresponding to this instance is
     * removed.
     * @param action: Action to be executed when the DB object is removed
     * @return A future with true iff the action was successfully added
     */
    fun onRemove(action: () -> Unit): CompletableFuture<Boolean> {
        TODO("implement if necessary")
    }

    /**
     * Removes (if any) the action to be executed when this object is removed in DB
     * @return A promise holding false iff the action was not removed (if no action was found, holds true)
     */
    fun offRemove(): CompletableFuture<Boolean> {
        TODO("implement if necessary")
    }

    // ================================================================== CONVERTERS
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
     * @note /!\ STATIC /!\ The returned value doesn't depend on the current object instance
     */
    protected abstract fun toLocalObject(dbObject: T) : StorableObject<T>
}