package com.github.polypoly.app.commons

abstract class LoggedInTest(
    clearRemoteStorage: Boolean,
    fillWithFakeData: Boolean)
    :PolyPolyTest(clearRemoteStorage, fillWithFakeData) {

    protected val userLoggedIn = TEST_USER_0

    // TODO: effectively log in the user before any test
}