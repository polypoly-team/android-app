package com.github.polypoly.app.network


import com.github.polypoly.app.network.storable.StorableObject
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
class RemoteDB(
    db: FirebaseDatabase?,
    root: String
) : IRemoteStorage {

    private val rootRef: DatabaseReference

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


    // ====================================================================================
    // ========================================================================== OVERRIDES
    // ====================================================================================

    // ========================================================================== GETTERS
    override fun <T : StorableObject<*>> getValue(key: String, clazz: KClass<T>): CompletableFuture<T> {
        return getValueAndThen(
            StorableObject.getPath(clazz) + key,
            { data, valuePromise ->
                val dbObj = data.getValue(StorableObject.getDBClass(clazz).java)!!
                valuePromise.complete(StorableObject.convertToLocal(clazz, dbObj))
            },
            { valuePromise ->
                valuePromise.completeExceptionally(NoSuchElementException("No value found for key <$key>"))
            })
    }

    override fun <T : StorableObject<*>> getAllValues(clazz: KClass<T>): CompletableFuture<List<T>> {
        return getValueAndThen(
            StorableObject.getPath(clazz),
            { data, valuesPromise ->
                val objects = ArrayList<T>()
                for (child in data.children) {
                    val dbObj = child.getValue(StorableObject.getDBClass(clazz).java)!!
                    objects.add(StorableObject.convertToLocal(clazz, dbObj))
                }
                valuesPromise.complete(objects)
            },
            { promise -> promise.complete(listOf()) })
    }

    override fun <T : StorableObject<*>> getAllKeys(clazz: KClass<T>): CompletableFuture<List<String>> {
        return getValueAndThen(
            StorableObject.getPath(clazz),
            { data, keysPromise ->
                val keys = ArrayList<String>()
                for (child in data.children) {
                    keys.add(child.key!!)
                }
                keysPromise.complete(keys)
            },
            { promise -> promise.complete(listOf()) }) // empty list if no parent key
    }

    override fun <T : StorableObject<*>> keyExists(key: String, clazz: KClass<T>): CompletableFuture<Boolean> {
        return getValueAndThen(
            StorableObject.getPath(clazz) + key,
            { _, promise -> promise.complete(true) }, // value found so key exists
            { promise -> promise.complete(false) }  // value not found so key doesn't exist
        )
    }

    // ========================================================================== SETTERS
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
        return deleteAllOnChangeListeners(key).thenCompose { result ->
            if(result) {
                setValue(key, null)
            } else {
                CompletableFuture.completedFuture(false)
            }

        }
    }

    // ========================================================================== LISTENERS
    /**
     * Gets the reference of a given key and if it exists, executes the given action with that reference
     * @return a promise with true if the action was successfully executed, false else or if no value was found
     */
    private fun getRefAndThen(key: String, action: (DatabaseReference) -> Unit): CompletableFuture<Boolean> {
        return keyExists(key).thenCompose { exists ->
            if(exists) {
                action(rootRef.child(key))
            }
            CompletableFuture.completedFuture(exists)
        }
    }

    private val changeListeners = mutableMapOf<Pair<String, String>, ValueEventListener>()

    override fun <T : Any>addOnChangeListener(
        key: String, tag: String, action: (newObj: T) -> Unit, clazz: KClass<T>
    ): CompletableFuture<Boolean> {

        return getRefAndThen(key) { ref ->
            val previousListener = changeListeners.remove(Pair(key, tag))
            if(previousListener != null) {
                ref.removeEventListener(previousListener)
            }
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        action(snapshot.getValue(clazz.java)!!)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            }
            changeListeners[Pair(key, tag)] = listener
            ref.addValueEventListener(listener)
        }
    }

    override fun deleteOnChangeListener(key: String, tag: String): CompletableFuture<Boolean> {
        return getRefAndThen(key) { ref ->
            val previousListener = changeListeners.remove(Pair(key, tag))
            if(previousListener != null) {
                ref.removeEventListener(previousListener)
            }
        }
    }

    override fun deleteAllOnChangeListeners(key: String): CompletableFuture<Boolean> {
        return getRefAndThen(key) { ref ->
            changeListeners
                .filterKeys { pair -> pair.first == key }
                .keys
                .forEach { pair ->
                    val previousListener = changeListeners.remove(pair)
                    if(previousListener != null) {
                        ref.removeEventListener(previousListener)
                    }
                }
            }
    }

}