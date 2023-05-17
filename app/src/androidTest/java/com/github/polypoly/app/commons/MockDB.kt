package com.github.polypoly.app.commons

import com.github.polypoly.app.network.IRemoteStorage
import com.github.polypoly.app.network.StorableObject
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * Mock version of RemoteDB for testing purpose
 */
class MockDB: IRemoteStorage {

    private val keysHierarchy: MutableMap<String, MutableList<String>> = mutableMapOf()
    private val data: MutableMap<String, Any> = mutableMapOf()

    private val listeners: MutableMap<String, MutableList<Pair<String, (Any) -> Unit>>> = mutableMapOf()

    private fun cleanKey(key: String): String {
        return key.removePrefix("/").removeSuffix("/")
    }

    // ====================================================================================
    // ========================================================================== OVERRIDES
    // ====================================================================================

    // ========================================================================== GETTERS

    override fun <T : StorableObject<*>> getValue(
        key: String,
        clazz: KClass<T>
    ): CompletableFuture<T> {
        val keyCleaned = cleanKey(StorableObject.getPath(clazz) + key)
        if (!data.containsKey(keyCleaned)) {
            val failedFuture = CompletableFuture<T>()
            failedFuture.completeExceptionally(NoSuchElementException("Invalid key $keyCleaned"))
            return failedFuture
        }
        return StorableObject.convertToLocal(clazz, data[keyCleaned]!!)
    }

    override fun <T : StorableObject<*>> getValues(keys: List<String>, clazz: KClass<T>): CompletableFuture<List<T>> {
        val futures = keys.map { key -> getValue(key, clazz) }
        return CompletableFuture.allOf(*futures.toTypedArray()).thenApply {
            futures.map { it.join() }
        }
    }

    override fun <T : StorableObject<*>> getAllValues(clazz: KClass<T>): CompletableFuture<List<T>> {
        return getAllKeys(clazz).thenCompose { keys -> getValues(keys, clazz) }
    }

    override fun <T : StorableObject<*>> getAllKeys(clazz: KClass<T>): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(keysHierarchy[cleanKey(StorableObject.getPath(clazz))] ?: listOf())
    }

    override fun <T : StorableObject<*>> keyExists(
        key: String,
        clazz: KClass<T>
    ): CompletableFuture<Boolean> {
        val keyCleaned = cleanKey(StorableObject.getPath(clazz) + key)
        return CompletableFuture.completedFuture(data.containsKey(keyCleaned))
    }

    // ========================================================================== SETTERS

    override fun <T : StorableObject<*>> registerValue(value: T): CompletableFuture<Boolean> {
        val keyCleaned = cleanKey(value.getAbsoluteKey())
        if (data.containsKey(keyCleaned)) {
            return CompletableFuture.failedFuture(IllegalAccessException("Registering a value already registered"))
        }
        return setValue(value)
    }

    override fun <T : StorableObject<*>> updateValue(value: T): CompletableFuture<Boolean> {
        val keyCleaned = cleanKey(value.getAbsoluteKey())
        if (!data.containsKey(keyCleaned)) {
            return CompletableFuture.failedFuture(NoSuchElementException("Update a value not already registered"))
        }
        return setValue(value)
    }

    override fun <T : StorableObject<*>> setValue(value: T): CompletableFuture<Boolean> {
        val keyCleaned = cleanKey(value.getAbsoluteKey())
        val hierarchy = keyCleaned.split("/")
        for (index in 0 until hierarchy.size - 1) {
            val parent = hierarchy[index]
            val child = hierarchy[index + 1]
            keysHierarchy.putIfAbsent(parent, mutableListOf())
            keysHierarchy[parent]!!.add(child)
        }
        data[keyCleaned] = value.toDBObject()

        for (listener in listeners[keyCleaned] ?: listOf()) {
            listener.second(value)
        }

        return CompletableFuture.completedFuture(true)
    }

    override fun <T : StorableObject<*>> removeValue(key: String, clazz: KClass<T>): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    // ========================================================================== LISTENERS

    override fun <T : StorableObject<*>> addOnChangeListener(key: String, tag: String, action: (newObj: T) -> Unit, clazz: KClass<T>): CompletableFuture<Boolean> {
        val listenersFound = listeners[key] ?: mutableListOf()
        @Suppress("UNCHECKED_CAST")
        listenersFound.add(Pair(tag, action as (Any) -> Unit))
        listeners[key] = listenersFound
        return CompletableFuture.completedFuture(true)
    }

    override fun <T : StorableObject<*>> deleteOnChangeListener(key: String, tag: String, clazz: KClass<T>): CompletableFuture<Boolean> {
        listeners[key]?.removeIf { listener -> listener.first == tag }
        return CompletableFuture.completedFuture(true)
    }

    override fun <T : StorableObject<*>> deleteAllOnChangeListeners(key: String, clazz: KClass<T>): CompletableFuture<Boolean> {
        listeners.remove(key)
        return CompletableFuture.completedFuture(true)
    }

    override fun <T : StorableObject<*>> addOnRootChangeListener(
        tag: String,
        action: (newObjects: List<T>) -> Unit,
        clazz: KClass<T>
    ): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T : StorableObject<*>> deleteOnRootChangeListener(
        tag: String,
        clazz: KClass<T>
    ): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T : StorableObject<*>> deleteAllOnRootChangeListeners(clazz: KClass<T>): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }


    /**
     * Clears all value in the mock database
     */
    fun clear() {
        data.clear()
        keysHierarchy.clear()
    }
}