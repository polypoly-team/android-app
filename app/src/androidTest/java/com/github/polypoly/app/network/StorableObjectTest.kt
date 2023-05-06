package com.github.polypoly.app.network

import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.network.storable.StorableObject
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
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



}


// ================================================================== MOCK USER

const val DB_MOCK_USER_PATH = "test_people/"

class TestPerson(
    val id: String = "",
    val name: String = "",
    val birth: Int = 2003
): StorableObject<TestPersonDB>(DB_MOCK_USER_PATH) {

    override fun toDBObject(): TestPersonDB {
        return TestPersonDB(id, name, 2023 - birth)
    }

    override fun toLocalObject(dbObject: TestPersonDB): StorableObject<TestPersonDB> {
        return TestPerson(dbObject.key, dbObject.name, 2023 - dbObject.age)
    }
}

data class TestPersonDB(val key: String = "", val name: String = "", val age: Int = 0)