package com.github.polypoly.app.commons

import com.github.polypoly.app.network.IRemoteStorage
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

class MockDB: IRemoteStorage {

    private val keysHierarchy: MutableMap<String, MutableList<String>> = mutableMapOf()
    private val data: MutableMap<String, Any> = mutableMapOf()

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
        if (!data.containsKey(keyCleaned))
            return CompletableFuture.failedFuture(IllegalAccessException("Update a value not already registered"))
        return setValue(keyCleaned, value)
    }

    override fun <T> setValue(key: String, value: T): CompletableFuture<Boolean> {
        val keyCleaned = cleanKey(key)
        val hierarchy = keyCleaned.split("/")
        for (index in 0 until hierarchy.size - 1) {
            val parent = hierarchy[index]
            val child = hierarchy[index + 1]
            if (!keysHierarchy.containsKey(parent)) {
                keysHierarchy[parent] = mutableListOf()
            }
            keysHierarchy[parent]!!.add(child)
        }
        data[keyCleaned] = value as Any
        return CompletableFuture.completedFuture(true)
    }

    fun clear() {
        data.clear()
        keysHierarchy.clear()
    }
}