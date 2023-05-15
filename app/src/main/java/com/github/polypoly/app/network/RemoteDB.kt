package com.github.polypoly.app.network


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
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

    @Deprecated("Prefer to use getDataAndThen")
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

    /**
     * First gets the [DataSnapshot] associated to the given [absoluteKey] and then composes it
     * with the corresponding given future (onSuccess if the data exists, onFailure else)
     * @return a future composed with [onSuccess] or [onFailure]
     */
    private fun <T> getDataAndThen(
        absoluteKey: String,
        onSuccess: (DataSnapshot) -> CompletableFuture<T>,
        onFailure: CompletableFuture<T>
    ): CompletableFuture<T> {
        val waitingFuture = CompletableFuture<CompletableFuture<T>>()

        rootRef.child(absoluteKey).get().addOnSuccessListener { data ->
            if(!data.exists()) {
                waitingFuture.complete(onFailure)
            } else {
                waitingFuture.complete(onSuccess(data))
            }
        }.addOnFailureListener(waitingFuture::completeExceptionally)

        return waitingFuture.get()
    }

    /**
     * @return a future with true iff the given [absoluteKey] is in the DB
     */
    private fun absoluteKeyExists(absoluteKey: String): CompletableFuture<Boolean> {
        return getDataAndThen(
            absoluteKey,
            { CompletableFuture.completedFuture(true) },
            CompletableFuture.completedFuture(false)
        )
    }

    /**
     * Gets the reference of a given key and if it exists, executes the given action with that reference
     * @return a future with true if the action was successfully executed, false else or if no value was found
     */
    private fun getRefAndThen(absoluteKey: String, action: (DatabaseReference) -> Unit): CompletableFuture<Boolean> {
        return getDataAndThen(
            absoluteKey,
            { data ->
                action(data.ref)
                CompletableFuture.completedFuture(true)
            },
            CompletableFuture.completedFuture(false)
        )
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
        val failedFuture = CompletableFuture<T>()
        failedFuture.completeExceptionally(NoSuchElementException("No value found for key <$key>"))

        return getDataAndThen(
            StorableObject.getPath(clazz) + key,
            { data -> convertDataToObject(data, clazz) },
            failedFuture
        )
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
        return getDataAndThen(
            StorableObject.getPath(clazz),
            { data ->
                val keys = ArrayList<String>()
                for (child in data.children) {
                    keys.add(child.key!!)
                }
                CompletableFuture.completedFuture(keys)
            },
            CompletableFuture.completedFuture(listOf()) // empty list if no parent key
        )
    }

    override fun <T : StorableObject<*>> keyExists(key: String, clazz: KClass<T>): CompletableFuture<Boolean> {
        return absoluteKeyExists(StorableObject.getPath(clazz) + key)
    }

    // ========================================================================== SETTERS
    override fun <T : StorableObject<*>> registerValue(value: T): CompletableFuture<Boolean> {
        return setValueWithCheck(value, false, IllegalAccessException("Try to register a value with an already existing key"))
    }

    override fun <T : StorableObject<*>> updateValue(value: T): CompletableFuture<Boolean> {
        return setValueWithCheck(value, true, NoSuchElementException("Try to update a value with no existing key"))
    }

    override fun <T : StorableObject<*>> setValue(value: T): CompletableFuture<Boolean> {
        val registerPromise = CompletableFuture<Boolean>()
        rootRef.child(value.getAbsoluteKey()).setValue(value.toDBObject()).addOnSuccessListener {
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
                        convertDataToObject(data, clazz).thenApply { obj -> action(obj) }
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
        value: T,
        shouldExist: Boolean,
        error: Exception
    ): CompletableFuture<Boolean> {
        return absoluteKeyExists(value.getAbsoluteKey()).thenCompose { exists ->
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