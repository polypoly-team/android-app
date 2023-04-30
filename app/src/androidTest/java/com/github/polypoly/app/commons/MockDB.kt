package com.github.polypoly.app.commons

import com.github.polypoly.app.network.IRemoteStorage
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * Mock version of RemoteDB for testing purpose
 */
class MockDB: IRemoteStorage {

    private val keysHierarchy: MutableMap<String, MutableList<String>> = mutableMapOf()
    private val data: MutableMap<String, Any> = mutableMapOf()
    private val actions: MutableMap<String, (Any) -> Unit> = mutableMapOf()

    private fun cleanKey(key: String): String {
        return key.removePrefix("/").removeSuffix("/")
    }

    override fun <T : Any> getValue(key: String, clazz: KClass<T>): CompletableFuture<T> {
        val keyCleaned = cleanKey(key)
        if (!data.containsKey(keyCleaned))
            return CompletableFuture.failedFuture(IllegalAccessException("Invalid key $keyCleaned"))
        @Suppress("UNCHECKED_CAST")
        return CompletableFuture.completedFuture(data[cleanKey(key)] as T)
    }

    override fun <T : Any> getAllValues(key: String, clazz: KClass<T>): CompletableFuture<List<T>> {
        val objs = mutableListOf<T>()
        val parentKeyCleaned = cleanKey(key)
        @Suppress("UNCHECKED_CAST")
        for (child in keysHierarchy[parentKeyCleaned] ?: listOf())
            objs.add(data["$parentKeyCleaned/$child"] as T)
        return CompletableFuture.completedFuture(objs)
    }

    override fun getAllKeys(parentKey: String): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(keysHierarchy[cleanKey(parentKey)] ?: listOf())
    }

    override fun keyExists(key: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(data.containsKey(cleanKey(key)))
    }

    override fun <T> registerValue(key: String, value: T): CompletableFuture<Boolean> {
        val keyCleaned = cleanKey(key)
        if (data.containsKey(keyCleaned))
            return CompletableFuture.failedFuture(IllegalAccessException("Registering a value already registered"))
        return setValue(keyCleaned, value)
    }

    override fun <T> updateValue(key: String, value: T): CompletableFuture<Boolean> {
        val keyCleaned = cleanKey(key)
        if (!data.containsKey(keyCleaned)) {
            return CompletableFuture.failedFuture(IllegalAccessException("Update a value not already registered"))
        }
        if(actions.containsKey(key)) {
            actions[key]?.invoke(value as Any)
        }
        return setValue(keyCleaned, value)
    }

    override fun <T> setValue(key: String, value: T): CompletableFuture<Boolean> {
        val keyCleaned = cleanKey(key)
        val hierarchy = keyCleaned.split("/")
        for (index in 0 until hierarchy.size - 1) {
            val parent = hierarchy[index]
            val child = hierarchy[index + 1]
            keysHierarchy.putIfAbsent(parent, mutableListOf())
            keysHierarchy[parent]!!.add(child)
        }
        data[keyCleaned] = value as Any
        return CompletableFuture.completedFuture(true)
    }

    override fun removeValue(key: String): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> addListener(
        key: String,
        action: (newObj: T) -> Unit,
        clazz: KClass<T>
    ): CompletableFuture<Boolean> {
        val keyCleaned = cleanKey(key)
        if (!data.containsKey(keyCleaned)) {
            return CompletableFuture.failedFuture(IllegalAccessException("Adds a listener to an unregistered value"))
        }
        actions[keyCleaned] = action as (Any) -> Unit
        return CompletableFuture.completedFuture(true)
    }

    override fun removeListener(key: String): CompletableFuture<Boolean> {
        if(actions.containsKey(key)) {
            actions.remove(key)
            return CompletableFuture.completedFuture(true)
        }
        return CompletableFuture.completedFuture(false)
    }

    /**
     * Clears all value in the mock database
     */
    fun clear() {
        data.clear()
        keysHierarchy.clear()
        actions.clear()
    }
}