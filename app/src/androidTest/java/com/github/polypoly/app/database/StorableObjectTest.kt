package com.github.polypoly.app.database

import com.github.polypoly.app.commons.PolyPolyTest
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CompletableFuture

class StorableObjectTest: PolyPolyTest(true, false) {

    // Values that are often used in tests
    private val TEST_PERSON_1 = TestPerson("1", "Sylvain", 1930)
    private val TEST_PERSON_2 = TestPerson("2", "bigflo", 2001)
    private val TEST_PERSON_3 = TestPerson("3", "Truck", 1988)

    private val TEST_PERSON_DB_1 = TestPersonDB("1", "Sylvain", 2023-TEST_PERSON_1.birth)
    private val TEST_PERSON_DB_2 = TestPersonDB("2", "bigflo", 2023-TEST_PERSON_2.birth)
    private val TEST_PERSON_DB_3 = TestPersonDB("3", "Truck", 2023-TEST_PERSON_3.birth)

    @Test
    fun nonInstantiatedClassAccessFails() {
        assertThrows(NoSuchElementException::class.java) { StorableObject.getPath(DummySubclass2::class) }
    }

    @Test
    fun dBPathsAreWellStored() {
        DummySubclass1()
        assertTrue(DB_TEST_PERSON_PATH == StorableObject.getPath(TestPerson::class))
        assertTrue(DB_DUMMY_PATH_1 == StorableObject.getPath(DummySubclass1::class))
    }

    @Test
    fun getPathWithReifiedWorks() {
        DummySubclass1()
        assertTrue(DB_TEST_PERSON_PATH == StorableObject.getPath<TestPerson>())
        assertTrue(DB_DUMMY_PATH_1 == StorableObject.getPath<DummySubclass1>())
    }

    @Test
    fun convertersAreWellStored() {
        assertTrue(TEST_PERSON_1.equalsPerson(StorableObject.convertToLocal(TestPerson::class, TEST_PERSON_DB_1).get()))
    }

    @Test
    fun convertWithReifiedWorks() {
        assertTrue(TEST_PERSON_1.equalsPerson(StorableObject.convertToLocal<TestPerson>(TEST_PERSON_DB_1).get()))
    }

    @Test
    fun convertAnObjectWithWrongClassFails() {
        assertThrows(IllegalArgumentException::class.java) { StorableObject.convertToLocal<TestPerson>(DummySubclass1()).get() }
    }

    @Test
    fun dbClassesAreWellStored() {
        DummySubclass1()
        assertTrue(TestPersonDB::class.toString() == StorableObject.getDBClass(TestPerson::class).toString())
        assertTrue(TestPersonDB::class.toString() == StorableObject.getDBClass(DummySubclass1::class).toString())
    }

    @Test
    fun getDBClassWithReifiedWorks() {
        DummySubclass1()
        assertTrue(TestPersonDB::class.toString() == StorableObject.getDBClass<TestPerson>().toString())
        assertTrue(TestPersonDB::class.toString() == StorableObject.getDBClass<DummySubclass1>().toString())
    }

    @Test
    fun getAbsoluteKeyWorks() {
        assertTrue(TEST_PERSON_1.getAbsoluteKey() == DB_TEST_PERSON_PATH + TEST_PERSON_1.key)
    }

}


// ================================================================== TEST PERSON

const val DB_TEST_PERSON_PATH = "test_people/"

class TestPerson(
    private val id: String = "",
    val name: String = "",
    val birth: Int = 2003
): StorableObject<TestPersonDB>(TestPersonDB::class, DB_TEST_PERSON_PATH, id) {

    fun equalsPerson(other: TestPerson): Boolean {
        return id == other.id && name == other.name && birth == other.birth
    }

    override fun toDBObject(): TestPersonDB {
        return TestPersonDB(id, name, 2023 - birth)
    }

    override fun toLocalObject(dbObject: TestPersonDB): CompletableFuture<StorableObject<TestPersonDB>> {
        val person = TestPerson(dbObject.key, dbObject.name, 2023 - dbObject.age)
        return CompletableFuture.completedFuture(person)
    }
}

data class TestPersonDB(val key: String = "", val name: String = "", val age: Int = 0)


// ================================================================== DUMMY SUBCLASSES

const val DB_DUMMY_PATH_1 = "dummy_1/"
const val DB_DUMMY_PATH_2 = "dummy_2/"

class DummySubclass1(id: String = ""): StorableObject<TestPersonDB>(TestPersonDB::class, DB_DUMMY_PATH_1, id) {
    override fun toDBObject(): TestPersonDB { TODO("Not yet implemented") }
    override fun toLocalObject(dbObject: TestPersonDB): CompletableFuture<StorableObject<TestPersonDB>> { TODO("Not yet implemented") }
}

class DummySubclass2(id: String = ""): StorableObject<TestPersonDB>(TestPersonDB::class, DB_DUMMY_PATH_2, id) {
    override fun toDBObject(): TestPersonDB { TODO("Not yet implemented") }
    override fun toLocalObject(dbObject: TestPersonDB): CompletableFuture<StorableObject<TestPersonDB>> { TODO("Not yet implemented") }
}