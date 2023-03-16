package com.github.polypoly.app.network

import com.github.polypoly.app.game.User
import java.util.concurrent.Future

interface IRemoteStorage {
    fun getUserWithId(userId: UInt): Future<User>
}