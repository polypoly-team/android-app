package com.github.polypoly.app.network

import com.google.firebase.database.ValueEventListener
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
     * @param key: key of the value to set
     * @return A promise holding true iff the value was successfully removed
     */
    fun removeValue(key: String): CompletableFuture<Boolean>

    /**
     * Adds an event listener to the data with the given key
     * @param key: key of the data to listen
     * @param action: the action to execute on data change
     * @return A promise holding true iff the listener was successfully set
     */
    fun <T : Any>addListener(key: String, action: (newObj: T) -> Unit, clazz: KClass<T>): CompletableFuture<Boolean>

    /**
     * Removes (if any) the listener associated to data corresponding to the given key
     * @param key: key of the data to stop listening
     * @return A promise holding true iff the listener was successfully removed
     */
    fun removeListener(key: String): CompletableFuture<Boolean>
}

/**
 * Extension function of IRemoteStorage::getAllValues to enable prettier call of the function
 */
inline fun <reified T : Any> IRemoteStorage.getAllValues(key: String) = getAllValues(key, T::class)

/**
 * Extension function of IRemoteStorage::getAllValues to enable prettier call of the function
 */
inline fun <reified T : Any> IRemoteStorage.getValue(key: String) = getValue(key, T::class)
