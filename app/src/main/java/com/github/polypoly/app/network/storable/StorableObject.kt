package com.github.polypoly.app.network.storable

import kotlin.reflect.KClass

/**
 * [StorableObject] is the super class of all classes that'll be contained in the DB.
 * It implements all the abstractions needed by the remote storage to do all the queries, i.e.
 * - The [dbClass] that is the class T, stored in the DB
 * - The [dbPath] that precedes every key for a given class (e.g. "objectX/"),
 * - The converter (to local) that converts a T DB object to a local StorableObject<T>,
 * - The converter (to DB)]that converts an instance of a local StorableObject<T> to a DB T.
 *
 * It is the role of every subclass to define the path and the conversion methods.
 *
 * This class takes a generic type T as argument, corresponding to the data class that is
 * going to be stored in the DB.
 *
 * @note attribute [key] should be the primary key used to store the object in the DB
 *
 * @note for the subclass to be correctly initialized, one instance should be created before any operation
 */
abstract class StorableObject<T : Any> (dbClass: KClass<T>, dbPath: String, val key: String) {

    init {
        registerClassToCompanion(dbClass, dbPath, this::class.toString(), ::toLocalObject)
    }

    companion object {
        private val classes = mutableMapOf<String, KClass<*>>()
        private val paths = mutableMapOf<String, String>()
        private val converters = mutableMapOf<String, (Any) -> StorableObject<*>>()

        /**
         * Everytime a subclass is created, this companion object will store its path and converter
         * @param dbPath: the mentioned path
         * @param className: the name of the subclass
         * @param convertToDB: the DB to Local converter
         */
        @JvmStatic
        private fun <T: Any> registerClassToCompanion(
            dbClass: KClass<T>,
            dbPath: String,
            className: String,
            convertToDB: (T) -> StorableObject<T>
        ) {
            classes[className] = dbClass
            paths[className] = dbPath
            @Suppress("UNCHECKED_CAST")
            converters[className] = convertToDB as (Any) -> StorableObject<*>
        }

        /**
         * For the given subclass [T], returns the stored corresponding DB class
         * @param clazz: the Kotlin subclass
         *
         * TODO: test this feature
         */
        fun <T: StorableObject<*>> getDBClass(clazz: KClass<T>): KClass<T> {
            val dbClass = classes[clazz.toString()] ?:
            throw NoSuchElementException("The DB class for this class doesn't exist")

            @Suppress("UNCHECKED_CAST")
            return dbClass as KClass<T>
        }

        /**
         * Inline redefinition to avoid passing a [KClass] as parameter
         */
        inline fun <reified T: StorableObject<*>> getDBClass(): KClass<*> {
            return getDBClass(T::class)
        }

        /**
         * For the given subclass [T], returns the stored DB path
         * @param clazz: the Kotlin subclass
         */
        fun <T: StorableObject<*>> getPath(clazz: KClass<T>): String {
            return paths[clazz.toString()] ?:
            throw NoSuchElementException("The DB path for this class doesn't exist")
        }

        /**
         * Inline redefinition to avoid passing a [KClass] as parameter
         */
        inline fun <reified T: StorableObject<*>> getPath(): String {
            return getPath(T::class)
        }

        /**
         * For the given subclass [T], returns the local version of the DB [obj]
         * @param clazz: the Kotlin subclass
         */
        fun <T: StorableObject<*>> convertToLocal(clazz: KClass<T>, obj: Any): T {
            val converter = converters[clazz.toString()] ?:
            throw NoSuchElementException("The DB converter for this class doesn't exist")

            @Suppress("UNCHECKED_CAST")
            return converter(obj) as T
        }

        /**
         * Inline redefinition to avoid passing a [KClass] as parameter
         */
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

