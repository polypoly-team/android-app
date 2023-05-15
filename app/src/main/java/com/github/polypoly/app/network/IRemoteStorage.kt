package com.github.polypoly.app.network

import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * Interface for a remote key-value store kind of storage
 *
 * @note that in all the interface we talk about storing objects as subclass" of [StorableObject] instances,
 * however this is not entirely true, as if we have a subclass of StorableObject<T>, the real class of the
 * object in the DB is T.
 * Please check [StorableObject] for more documentation.
 */
interface IRemoteStorage {

    // ========================================================================== GETTERS

    /**
     * Retrieves the [T] instance associated to the given key
     * @param key: key of the value asked
     * @param clazz: corresponding [StorableObject] subclass
     * @return A future with the value found
     *
     * @throws NoSuchElementException (in the future) if the key isn't in the DB
     */
    fun <T : StorableObject<*>> getValue(key: String, clazz: KClass<T>): CompletableFuture<T>

    /**
     * Retrieves the [T] instances associated to the given keys
     * @param keys: keys of the values asked
     * @param clazz: corresponding [StorableObject] subclass
     * @return A future with the values found
     *
     * @throws NoSuchElementException (in the future) if one of the keys isn't in the DB
     */
    fun <T : StorableObject<*>> getValues(keys: List<String>, clazz: KClass<T>): CompletableFuture<List<T>>

    /**
     * Retrieves all values associated to a given [StorableObject] subclass
     * @param clazz: corresponding [StorableObject] subclass
     * @return A future with the values found (empty is no value was found)
     */
    fun <T : StorableObject<*>> getAllValues(clazz: KClass<T>): CompletableFuture<List<T>>

    /**
     * Retrieves all values associated to a given [StorableObject] subclass
     * @param clazz: corresponding [StorableObject] subclass
     * @return A future with the keys found (empty is no key was found)
     */
    fun <T : StorableObject<*>> getAllKeys(clazz: KClass<T>): CompletableFuture<List<String>>

    /**
     * Checks if an object of class [T] with the given key exists within the storage
     * @param key: key to check
     * @param clazz: corresponding [StorableObject] subclass
     * @return A future holding true iff the key exists
     */
    fun <T : StorableObject<*>> keyExists(key: String, clazz: KClass<T>): CompletableFuture<Boolean>



    // ========================================================================== SETTERS


    /**
     * Registers a new value in the storage
     * @param value: value to register
     * @return A future holding true iff the value was successfully registered
     *
     * @throws IllegalAccessException (in the future) if the key is already in the DB
     */
    fun <T : StorableObject<*>> registerValue(value: T): CompletableFuture<Boolean>

    /**
     * Updates an already existing value in the storage
     * @param value: value to update
     * @return A future holding true iff the value was successfully updated
     *
     * @throws NoSuchElementException (in the future) if the key doesn't exist yet in the storage
     */
    fun <T : StorableObject<*>> updateValue(value: T): CompletableFuture<Boolean>

    /**
     * Sets a value in the storage. It doesn't take into account whether the key already exists or not.
     * @param value: value to set
     * @return A future holding true iff the value was successfully set
     */
    fun <T : StorableObject<*>> setValue(value: T): CompletableFuture<Boolean>

    /**
     * Removes the element with the given key
     * @param key: key of the value to delete
     * @param clazz: corresponding [StorableObject] subclass
     * @return A future holding true if a value was successfully removed, false if no value existed
     */
    fun <T : StorableObject<*>> removeValue(key: String, clazz: KClass<T>): CompletableFuture<Boolean>




    // ========================================================================== LISTENERS

    /**
     * Adds an event listener to the data with the given key. If another action with the same tag
     * was attached to this node, replaces it
     * @param key: key of the data to listen
     * @param tag: a tag for the corresponding listener
     * @param action: the action to execute on data change
     * @param clazz: corresponding [StorableObject] subclass
     * @return A future holding true if the listener was successfully set, false if no such value exists
     *
     * @note The number of times the [action] will take place is not deterministic, as minor DB changes
     * may trigger it, so don't use actions that use the number of times [action] is called
     */
    fun <T : StorableObject<*>> addOnChangeListener(key: String, tag: String, action: (newObj: T) -> Unit, clazz: KClass<T>): CompletableFuture<Boolean>

    /**
     * Same as [addOnChangeListener] with key parameter but this will listen to the root of the corresponding
     * StorableObject subclass
     */
    fun <T : StorableObject<*>> addOnChangeListener(tag: String, action: (newObjects: List<T>) -> Unit, clazz: KClass<T>): CompletableFuture<Boolean>

    /**
     * Removes (if any) the listener with [tag] associated to data corresponding to the given key
     * @param key: key of the data to stop listening
     * @param tag: the name tag of the listener
     * @param clazz: corresponding [StorableObject] subclass
     * @return A future holding true if the listener was successfully removed, false if no such value exists
     *
     * @note Calling this may call the change listener one last time
     */
    fun <T : StorableObject<*>> deleteOnChangeListener(key: String, tag: String, clazz: KClass<T>): CompletableFuture<Boolean>

    /**
     * Same as [deleteOnChangeListener] with key parameter but this will delete the listener
     * listening to the root of the correspondingStorableObject subclass with the given [tag]
     */
    fun <T : StorableObject<*>> deleteOnChangeListener(tag: String, clazz: KClass<T>): CompletableFuture<Boolean>

    /**
     * Removes (if any) all the listeners attached to the data corresponding to the given key
     * @param key: key of the data to stop listening
     * @param clazz: corresponding [StorableObject] subclass
     */
    fun <T : StorableObject<*>> deleteAllOnChangeListeners(key: String, clazz: KClass<T>): CompletableFuture<Boolean>

    /**
     * Same as [deleteAllOnChangeListeners] with key parameter but this will delete all listeners
     * listening to the root of the correspondingStorableObject subclass
     */
    fun <T : StorableObject<*>> deleteAllOnChangeListeners(clazz: KClass<T>): CompletableFuture<Boolean>

}

// ========================================================================== INLINE REDEFINITIONS

/**
 * Extension functions of IRemoteStorage to enable prettier call of the functions
 */
// GETTERS
inline fun <reified T : StorableObject<*>> IRemoteStorage.getValue(key: String) = getValue(key, T::class)
inline fun <reified T : StorableObject<*>> IRemoteStorage.getValues(keys: List<String>) = getValues(keys, T::class)
inline fun <reified T : StorableObject<*>> IRemoteStorage.getAllValues() = getAllValues(T::class)
inline fun <reified T : StorableObject<*>> IRemoteStorage.getAllKeys() = getAllKeys(T::class)
inline fun <reified T : StorableObject<*>> IRemoteStorage.keyExists(key: String) = keyExists(key, T::class)

// SETTERS
inline fun <reified T : StorableObject<*>> IRemoteStorage.removeValue(key: String) = removeValue(key, T::class)

// LISTENERS
inline fun <reified T : StorableObject<*>> IRemoteStorage.addOnChangeListener(key: String, tag: String, noinline action: (newObj: T) -> Unit) = addOnChangeListener(key, tag, action, T::class)
inline fun <reified T : StorableObject<*>> IRemoteStorage.addOnChangeListener(tag: String, noinline action: (newObjects: List<T>) -> Unit) = addOnChangeListener(tag, action, T::class)
inline fun <reified T : StorableObject<*>> IRemoteStorage.deleteOnChangeListener(key: String, tag: String) = deleteOnChangeListener(key, tag, T::class)
inline fun <reified T : StorableObject<*>> IRemoteStorage.deleteOnChangeListener(tag: String) = deleteOnChangeListener(tag, T::class)
inline fun <reified T : StorableObject<*>> IRemoteStorage.deleteAllOnChangeListeners(key: String) = deleteAllOnChangeListeners(key, T::class)
inline fun <reified T : StorableObject<*>> IRemoteStorage.deleteAllOnChangeListeners() = deleteAllOnChangeListeners(T::class)
