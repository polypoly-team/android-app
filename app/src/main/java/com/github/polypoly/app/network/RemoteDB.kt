package com.github.polypoly.app.network


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        onError: (CompletableFuture<T>) -> Unit
    ) : CompletableFuture<T> {
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

    override fun <T : Any> getValue(key: String, clazz: KClass<T>): CompletableFuture<T> {
        return getValueAndThen(key, { data, valuePromise ->
            valuePromise.complete(data.getValue(clazz.java)!!)
        }, { valuePromise ->
            valuePromise.completeExceptionally(NoSuchElementException("No value found for key <$key>"))
        })
    }

    override fun <T : Any> getAllValues(key: String, clazz: KClass<T>): CompletableFuture<List<T>> {
        return getValueAndThen(key, { data, valuesPromise ->
            val users = ArrayList<T>()
            for (child in data.children) {
                users.add(child.getValue(clazz.java)!!)
            }
            valuesPromise.complete(users)
        }, { promise -> promise.complete(listOf()) })
    }

    override fun getAllKeys(parentKey: String): CompletableFuture<List<String>> {
        return getValueAndThen(
            parentKey,
            { data, keysPromise ->
                val keys = ArrayList<String>()
                for (child in data.children) {
                    keys.add(child.key!!)
                }
                keysPromise.complete(keys)
            },
            { promise -> promise.complete(listOf()) }) // empty list if no parent key
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
        return setValueWithCheck(key, value, true, NoSuchElementException("Try to update a value with no existing key"))
    }

    override fun <T> setValue(key: String, value: T): CompletableFuture<Boolean> {
        val registerPromise = CompletableFuture<Boolean>()
        rootRef.child(key).setValue(value).addOnSuccessListener {
            registerPromise.complete(true)
        }.addOnFailureListener(registerPromise::completeExceptionally)
        return registerPromise
    }

    override fun removeValue(key: String): CompletableFuture<Boolean> {
        TODO("Not implemented yet") // Hint: set value to null
    }

    // ========================================================================== LISTENERS

    override fun <T : Any> addChangeListener(
        key: String,
        action: (newObj: T) -> Unit,
        clazz: KClass<T>
    ): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteChangeListener(key: String): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun addRemoveListener(key: String, action: Unit): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteRemoveListener(key: String): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    // ========================================================================== LISTENING



}