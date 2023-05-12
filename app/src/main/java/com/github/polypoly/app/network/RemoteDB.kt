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

    // ====================================================================================
    // ============================================================================ HELPERS
    // ====================================================================================

    private fun <T> getValueAndThen(
        absoluteKey: String,
        onSuccess: (DataSnapshot, CompletableFuture<T>) -> Unit,
        onError: (CompletableFuture<T>) -> Unit
    ) : CompletableFuture<T> {
        val valuePromise = CompletableFuture<T>()
        rootRef.child(absoluteKey).get().addOnSuccessListener{ data ->
            if (!data.exists()) {
                onError(valuePromise)
            } else {
                onSuccess(data, valuePromise)
            }
        }.addOnFailureListener(valuePromise::completeExceptionally)
        return valuePromise
    }

    private fun absoluteKeyExists(absoluteKey: String): CompletableFuture<Boolean> {
        return getValueAndThen(
            absoluteKey,
            { _, promise -> promise.complete(true) }, // value found so key exists
            { promise -> promise.complete(false) }  // value not found so key doesn't exist
        )
    }

    /**
     * Gets the reference of a given key and if it exists, executes the given action with that reference
     * @return a future with true if the action was successfully executed, false else or if no value was found
     */
    private fun getRefAndThen(key: String, action: (DatabaseReference) -> Unit): CompletableFuture<Boolean> {
        return absoluteKeyExists(key).thenCompose { exists ->
            if(exists) {
                action(rootRef.child(key))
            }
            CompletableFuture.completedFuture(exists)
        }
    }

    /**
     * Converts a data snapshot to a given subclass of [StorableObject].
     * The data contained in data is of type U, the returned object is of type T = StorableObject<U>
     * @return the converted object
     */
    private fun <T : StorableObject<*>> convertDataToObject(data: DataSnapshot, clazz: KClass<T>): CompletableFuture<T> {
        val dbObj = data.getValue(StorableObject.getDBClass(clazz).java)!!
        return StorableObject.convertToLocal(clazz, dbObj)
    }


    // ====================================================================================
    // ========================================================================== OVERRIDES
    // ====================================================================================

    // ========================================================================== GETTERS
    override fun <T : StorableObject<*>> getValue(key: String, clazz: KClass<T>): CompletableFuture<T> {
        return getValueAndThen(
            StorableObject.getPath(clazz) + key,
            { data, valuePromise ->
                valuePromise.thenCompose { convertDataToObject(data, clazz) }
            },
            { valuePromise ->
                valuePromise.completeExceptionally(NoSuchElementException("No value found for key <$key>"))
            })
    }

    override fun <T : StorableObject<*>> getValues(keys: List<String>, clazz: KClass<T>): CompletableFuture<List<T>> {
        val returnFuture = CompletableFuture<List<T>>()
        keys.forEach { key ->
            returnFuture.thenCompose {
                getValue(key, clazz) }
        }
        CompletableFuture.
        return returnFuture
    }

    override fun <T : StorableObject<*>> getAllValues(clazz: KClass<T>): CompletableFuture<List<T>> {
        return getValueAndThen(
            StorableObject.getPath(clazz),
            { data, valuesPromise ->
                val objects = ArrayList<T>()
                for (child in data.children) {
                    objects.add(convertDataToObject(child, clazz))
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
        return absoluteKeyExists(StorableObject.getPath(clazz))
    }

    // ========================================================================== SETTERS
    override fun <T : StorableObject<*>> registerValue(value: T): CompletableFuture<Boolean> {
        return setValueWithCheck(value.key, value, false, IllegalAccessException("Try to register a value with an already existing key"))
    }

    override fun <T : StorableObject<*>> updateValue(value: T): CompletableFuture<Boolean> {
        return setValueWithCheck(value.key, value, true, NoSuchElementException("Try to update a value with no existing key"))
    }

    override fun <T : StorableObject<*>> setValue(value: T): CompletableFuture<Boolean> {
        val registerPromise = CompletableFuture<Boolean>()
        rootRef.child(value.key).setValue(value.toDBObject()).addOnSuccessListener {
            registerPromise.complete(true)
        }.addOnFailureListener(registerPromise::completeExceptionally)
        return registerPromise
    }

    /**
     * @note we maybe could improve this as it will check the key existence twice
     */
    override fun <T : StorableObject<*>> removeValue(key: String, clazz: KClass<T>): CompletableFuture<Boolean> {
        return deleteAllOnChangeListeners(key, clazz).thenCompose { result ->
            if(result) {
                getRefAndThen(StorableObject.getPath(clazz) + key) { ref ->
                    ref.setValue(null)
                }
            } else {
                CompletableFuture.completedFuture(false)
            }
        }
    }

    // ========================================================================== LISTENERS

    private val changeListeners = mutableMapOf<Pair<String, String>, ValueEventListener>()

    override fun <T : StorableObject<*>> addOnChangeListener(
        key: String,
        tag: String,
        action: (newObj: T) -> Unit,
        clazz: KClass<T>
    ): CompletableFuture<Boolean> {
        val absoluteKey = StorableObject.getPath(clazz) + key
        return getRefAndThen(absoluteKey) { ref ->
            val previousListener = changeListeners.remove(Pair(absoluteKey, tag))
            if(previousListener != null) {
                ref.removeEventListener(previousListener)
            }
            val listener = object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    if(data.exists()) {
                        action(convertDataToObject(data, clazz))
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            }
            changeListeners[Pair(absoluteKey, tag)] = listener
            ref.addValueEventListener(listener)
        }
    }

    override fun <T : StorableObject<*>> deleteOnChangeListener(
        key: String,
        tag: String,
        clazz: KClass<T>
    ): CompletableFuture<Boolean>{
        val absoluteKey = StorableObject.getPath(clazz) + key
        return getRefAndThen(absoluteKey) { ref ->
            val previousListener = changeListeners.remove(Pair(absoluteKey, tag))
            if(previousListener != null) {
                ref.removeEventListener(previousListener)
            }
        }
    }

    override fun <T : StorableObject<*>> deleteAllOnChangeListeners(key: String, clazz: KClass<T>): CompletableFuture<Boolean> {
        val absoluteKey = StorableObject.getPath(clazz) + key
        return getRefAndThen(absoluteKey) { ref ->
            changeListeners
                .filterKeys { pair -> pair.first == absoluteKey }
                .keys
                .forEach { pair ->
                    val previousListener = changeListeners.remove(pair)
                    if(previousListener != null) {
                        ref.removeEventListener(previousListener)
                    }
                }
            }
    }

    // ========================================================================== HELPERS
    private fun <T : StorableObject<*>> setValueWithCheck(
        absoluteKey: String,
        value: T,
        shouldExist: Boolean,
        error: Exception
    ): CompletableFuture<Boolean> {
        return absoluteKeyExists(absoluteKey).thenCompose { exists ->
            var result = CompletableFuture<Boolean>()
            if (exists != shouldExist) {
                result.completeExceptionally(error)
            } else {
                result = setValue(value)
            }
            result
        }
    }

}