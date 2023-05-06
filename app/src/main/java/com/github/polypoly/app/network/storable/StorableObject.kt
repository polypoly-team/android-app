package com.github.polypoly.app.network.storable

import kotlin.reflect.KClass

/**
 * Every object that can be stored in the DB has a "local" and a "DB" version.
 * This superclass implements all the queries necessary to get and set values in the DB.
 *
 * This class takes a generic type T as argument, corresponding to the data class that is
 * going to be stored in the DB.
 */
abstract class StorableObject<T : Any> (DBPath: String) {

    init {
        initClassCompanion(DBPath, this::class.toString(), ::toLocalObject)
    }

    companion object {
        private val paths = mutableMapOf<String, String>()
        private val converters = mutableMapOf<String, (Any) -> StorableObject<*>>()

        @JvmStatic
        private fun <T: Any> initClassCompanion(
            DBPath: String,
            className: String,
            convertToDB: (T) -> StorableObject<T>
        ) {
            paths[className] = DBPath
            @Suppress("UNCHECKED_CAST")
            converters[className] = convertToDB as (Any) -> StorableObject<*>
        }

        fun <T: StorableObject<*>> getPath(clazz: KClass<T>): String {
            return paths[clazz.toString()] ?:
            throw NoSuchElementException("The DB path for this class doesn't exist")
        }

        inline fun <reified T: StorableObject<*>> getPath(): String {
            return getPath(T::class)
        }

        fun <T: StorableObject<*>> convertToLocal(clazz: KClass<T>, obj: Any): T {
            val converter = converters[clazz.toString()] ?:
            throw NoSuchElementException("The DB converter for this class doesn't exist")

            @Suppress("UNCHECKED_CAST")
            return converter(obj) as T
        }

        inline fun <reified T: StorableObject<*>> convertToLocal(obj: Any): T {
            return convertToLocal(T::class, obj)
        }
    }

    // ================================================================== CONVERTERS
    /**
     * Converts this instance to its DB version
     * @return the DB version
     */
    protected abstract fun toDBObject(): T

    /**
     * Converts the given DB version to its local version
     * @param dbObject: the object to convert
     * @return the local version
     *
     * @note /!\ STATIC /!\ The returned value shouldn't depend on the current object instance
     */
    protected abstract fun toLocalObject(dbObject: T): StorableObject<T>


}

