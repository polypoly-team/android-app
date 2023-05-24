package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.network.StorableObject
import org.junit.Assert.assertTrue
import org.junit.Test

class UserTest {

    // Values that are often used in tests
    val USER_TEST = User("15")

    @Test
    fun convertingToDBObjectWorks() {
        assertTrue(USER_TEST.toDBObject() == USER_TEST)
    }

    @Test
    fun convertingToLocalObjectWorks() {
        assertTrue(StorableObject.convertToLocal<User>(USER_TEST).get() == USER_TEST)
    }
}