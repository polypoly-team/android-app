package com.github.polypoly.app.network

import com.github.polypoly.app.network.storable.StorableObject

class StorableObjectTest {
}


// ================================================================== MOCK USER

const val DB_MOCK_USER_PATH = "test_users/"

class TestUser(private val key: String): StorableObject<TestUserDB>(DB_MOCK_USER_PATH, key, TestUserDB::class) {

    override fun toDBObject(): TestUserDB {
        return TestUserDB(key, "bigflo", 30)
    }

    override fun toLocalObject(dbObject: TestUserDB): StorableObject<TestUserDB> {
        return TestUser(dbObject.key)
    }
}

data class TestUserDB(val key: String, val name: String, val age: Int)