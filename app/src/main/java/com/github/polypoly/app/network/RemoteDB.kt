package com.github.polypoly.app.network


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * Implementation of IRemoteStorage as a Firebase remote DB
 */
open class RemoteDB(
    db: FirebaseDatabase?,
    root: String
) : IRemoteStorage {

    val rootRef: DatabaseReference

    init {
        if(db == null)
            throw java.lang.IllegalArgumentException("Null db provided")
        rootRef = db.getReference(root)
    }

    private fun <T> setValueWithCheck(key: String, value: T, shouldExist: Boolean, error: Exception): CompletableFuture<Boolean> {
        return keyExists(key).thenCompose { exists ->
            var result = CompletableFuture<Boolean>()
            if (exists != shouldExist) {
                result.completeExceptionally(error)
            } else {
                result = setValue(key, value)
            }
            result
        }
    }

    private fun <T> getValueAndThen(
        key: String,
        onSuccess: (DataSnapshot, CompletableFuture<T>) -> Unit,
        onError: (CompletableFuture<T>) -> Unit)
    : CompletableFuture<T> {
        val valuePromise = CompletableFuture<T>()
        rootRef.child(key).get().addOnSuccessListener{ data ->
            if (!data.exists()) {
                onError(valuePromise)
            } else {
                onSuccess(data, valuePromise)
            }
        }.addOnFailureListener(valuePromise::completeExceptionally)
        return valuePromise
    }

    private fun <T> getValueAndThen(key: String, onSuccess: (DataSnapshot, CompletableFuture<T>) -> Unit): CompletableFuture<T> {
        return getValueAndThen(key, onSuccess) { valuePromise ->
            valuePromise.completeExceptionally(IllegalAccessException("No value found for key <$key>"))
        }
    }

    override fun <T : Any> getAllValues(key: String, clazz: KClass<T>): CompletableFuture<List<T>> {
        return getValueAndThen(key) { data, valuesPromise ->
            val users = ArrayList<T>()
            for (child in data.children) {
                users.add(child.getValue(clazz.java)!!)
            }
            valuesPromise.complete(users)
        }
    }

    override fun <T : Any> getValue(key: String, clazz: KClass<T>): CompletableFuture<T> {
        return getValueAndThen(key) { data, valuePromise ->
            valuePromise.complete(data.getValue(clazz.java)!!)
        }
    }

    override fun getAllKeys(key: String): CompletableFuture<List<String>> {
        return getValueAndThen(key) { data, keysPromise ->
            val keys = ArrayList<String>()
            for (child in data.children) {
                keys.add(child.key!!)
            }
            keysPromise.complete(keys)
        }
    }

    override fun keyExists(key: String): CompletableFuture<Boolean> {
        return getValueAndThen(
            key,
            { _, promise -> promise.complete(true) }, // value found so key exists
            { promise -> promise.complete(false) }  // value not found so key doesn't exist
        )
    }

    override fun <T> registerValue(key: String, value: T): CompletableFuture<Boolean> {
        return setValueWithCheck(key, value, false, IllegalAccessException("Try to register a value with an already existing key"))
    }

    override fun <T> updateValue(key: String, value: T): CompletableFuture<Boolean> {
        return setValueWithCheck(key, value, true, IllegalAccessException("Try to update a value with no existing key"))
    }

    override fun <T> setValue(key: String, value: T): CompletableFuture<Boolean> {
        val registerPromise = CompletableFuture<Boolean>()
        rootRef.child(key).setValue(value).addOnSuccessListener {
            registerPromise.complete(true)
        }.addOnFailureListener(registerPromise::completeExceptionally)
        return registerPromise
    }
}