package com.github.polypoly.app.network

import com.github.polypoly.app.network.storable.StorableObject
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * Interface for a remote key-value store kind of storage
 */
interface IRemoteStorage {

    /**
     * Retrieve the single value associated to the given key
     * @param key: key of the value asked
     * @param clazz: class used to construct the value retrieved
     * @return A promise with the value found
     */
    fun <T : Any>getValue(key: String, clazz: KClass<T>): CompletableFuture<T>

    /**
     * Retrieve all values associated to the given key
     * @param key: key of the values asked
     * @param clazz: class used to construct the valued retrieved
     * @return A promise with the values found
     */
    fun <T : Any>getAllValues(key: String, clazz: KClass<T>): CompletableFuture<List<T>>

    /**
     * Retrieve all child keys of a certain key
     * @param parentKey: parent key
     * @return A promise with the keys found
     */
    fun getAllKeys(parentKey: String): CompletableFuture<List<String>>

    /**
     * Check if the given key exists within the storage
     * @param key: key to check
     * @return A promise holding true iff the key exists
     */
    fun keyExists(key: String): CompletableFuture<Boolean>

    /**
     * Register a new value in the storage
     * @param key: key of the value to register
     * @param value: value to register
     * @return A promise holding true iff the value was successfully registered
     * @throws IllegalAccessException if the key already exists in the storage
     */
    fun <T>registerValue(key: String, value: T): CompletableFuture<Boolean>

    /**
     * Update an already existing value in the storage
     * @param key: key of the value to update
     * @param value: value to update
     * @return A promise holding true iff the value was successfully updated
     * @throws IllegalAccessException if the key doesn't exist yet in the storage
     */
    fun <T>updateValue(key: String, value: T): CompletableFuture<Boolean>

    /**
     * Set a value in the storage. It doesn't take into account whether the key already exists or not.
     * @param key: key of the value to set
     * @param value: value to set
     * @return A promise holding true iff the value was successfully set
     */
    fun <T>setValue(key: String, value: T): CompletableFuture<Boolean>

    /**
     * Removes the element with the given key
     * @param key: key of the value to delete
     * @return A promise holding true if a value was successfully removed, false if no value existed
     */
    fun removeValue(key: String): CompletableFuture<Boolean>

    // ========================================================================== LISTENERS
    /**
     * Adds an event listener to the data with the given key. If another action with the same tag
     * was attached to this node, replaces it
     * @param key: key of the data to listen
     * @param tag: a tag for the corresponding listener
     * @param action: the action to execute on data change
     * @return A promise holding true if the listener was successfully set, false if no such value exists
     *
     * @note The number of times the [action] will take place is not deterministic, as minor DB changes
     * may trigger it, so don't use actions that use the number of times [action] is called
     */
    fun <T : Any>addOnChangeListener(key: String, tag: String, action: (newObj: T) -> Unit, clazz: KClass<T>): CompletableFuture<Boolean>

    /**
     * Removes (if any) the listener with [tag] associated to data corresponding to the given key
     * @param key: key of the data to stop listening
     * @param tag: the name tag of the listener
     * @return A promise holding true if the listener was successfully removed, false if no such value exists
     *
     * @note Calling this may call the change listener one last time
     */
    fun deleteOnChangeListener(key: String, tag: String): CompletableFuture<Boolean>

    /**
     * Removes (if any) all the listeners attached to the data corresponding to the given key
     */
    fun deleteAllOnChangeListeners(key: String): CompletableFuture<Boolean>

}

/**
 * Extension function of IRemoteStorage::getAllValues to enable prettier call of the function
 */
inline fun <reified T : Any> IRemoteStorage.getAllValues(key: String) = getAllValues(key, T::class)

/**
 * Extension function of IRemoteStorage::getAllValues to enable prettier call of the function
 */
inline fun <reified T : Any> IRemoteStorage.getValue(key: String) = getValue(key, T::class)
