package com.github.polypoly.app.network

import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.network.storable.StorableObject
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class StorableObjectTest: PolyPolyTest(true, false) {

    // Values that often used in tests
    private val GLOBAL_KEY = DB_MOCK_USER_PATH + "some_key"
    private val LOCAL_KEY = "some_key"

    private val TEST_PERSON_1 = TestPerson("1", "Sylvain", 1930)
    private val TEST_PERSON_2 = TestPerson("2", "bigflo", 2001)
    private val TEST_PERSON_3 = TestPerson("3", "Truck", 1988)

    private val TEST_PERSON_DB_1 = TestPersonDB("1", "Sylvain", 2023-TEST_PERSON_1.birth)
    private val TEST_PERSON_DB_2 = TestPersonDB("2", "bigflo", 2023-TEST_PERSON_2.birth)
    private val TEST_PERSON_DB_3 = TestPersonDB("3", "Truck", 2023-TEST_PERSON_3.birth)

    @Test
    fun getUnregisteredDataFails() {
        val failedFuture = TestPerson().get(LOCAL_KEY)
        failedFuture.handle { _, e ->
            assertTrue(e != null)
            assertTrue(e is NoSuchElementException)
        }
        assertThrows(ExecutionException::class.java) { failedFuture.get(TIMEOUT_DURATION, TimeUnit.SECONDS) }
    }

    @Test
    fun getRegisteredDataWorks() {
        addDataToDB(TEST_PERSON_DB_1, GLOBAL_KEY)
        TestPerson().get(LOCAL_KEY).get(TIMEOUT_DURATION, TimeUnit.SECONDS)
    }

    @Test
    fun getRegisteredDataHasCorrectValue() {
        addDataToDB(TEST_PERSON_DB_1, DB_MOCK_USER_PATH+TEST_PERSON_1.id)
        addDataToDB(TEST_PERSON_DB_2, DB_MOCK_USER_PATH+TEST_PERSON_2.id)
        val testPerson1 = TestPerson().get(TEST_PERSON_1.id).get(TIMEOUT_DURATION, TimeUnit.SECONDS) as TestPerson
        val testPerson2 = TestPerson().get(TEST_PERSON_2.id).get(TIMEOUT_DURATION, TimeUnit.SECONDS) as TestPerson
        assertTrue(testPerson1.birth == TEST_PERSON_1.birth)
        assertTrue(testPerson2.birth == TEST_PERSON_2.birth)
    }

    @Test
    fun getAllHoldsEmptyListWhenNoData() {
        val list = TestPerson().getAll().get(TIMEOUT_DURATION, TimeUnit.SECONDS)
        assertTrue(list.isEmpty())
    }

    @Test
    fun getAllHoldsAllRegisteredData() {
        addDataToDB(TEST_PERSON_DB_1, DB_MOCK_USER_PATH+TEST_PERSON_1.id)
        addDataToDB(TEST_PERSON_DB_2, DB_MOCK_USER_PATH+TEST_PERSON_2.id)
        addDataToDB(TEST_PERSON_DB_3, DB_MOCK_USER_PATH+TEST_PERSON_3.id)
        val list = TestPerson().getAll().get(TIMEOUT_DURATION, TimeUnit.SECONDS) as List<TestPerson>
        val expectedList = listOf(TEST_PERSON_1, TEST_PERSON_2, TEST_PERSON_3)
        list.forEach { person ->
            assertTrue(
                expectedList.any { expectedPerson ->
                    expectedPerson.id == person.id &&
                    expectedPerson.name == person.name &&
                    expectedPerson.birth == person.birth
                }
            )
        }
    }

}


// ================================================================== MOCK USER

const val DB_MOCK_USER_PATH = "test_people/"

class TestPerson(
    val id: String = "",
    val name: String = "",
    val birth: Int = 2003
): StorableObject<TestPersonDB>(DB_MOCK_USER_PATH, id, TestPersonDB::class) {

    override fun toDBObject(): TestPersonDB {
        return TestPersonDB(id, name, 2023 - birth)
    }

    override fun toLocalObject(dbObject: TestPersonDB): StorableObject<TestPersonDB> {
        return TestPerson(dbObject.key, dbObject.name, 2023 - dbObject.age)
    }
}

data class TestPersonDB(val key: String = "", val name: String = "", val age: Int = 0)