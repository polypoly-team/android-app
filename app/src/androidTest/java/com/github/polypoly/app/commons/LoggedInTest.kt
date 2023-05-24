package com.github.polypoly.app.commons

import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser

abstract class LoggedInTest(
    clearRemoteStorage: Boolean,
    fillWithFakeData: Boolean)
    : PolyPolyTest(clearRemoteStorage, fillWithFakeData, true) {

    protected val userLoggedIn = currentUser!!

    // TODO: effectively log in the user before any test
}