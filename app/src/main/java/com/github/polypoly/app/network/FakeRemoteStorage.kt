package com.github.polypoly.app.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.polypoly.app.game.Skin
import com.github.polypoly.app.game.Stats
import com.github.polypoly.app.game.User
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * A fake remote storage to test the functionalities without the database
 */
class FakeRemoteStorage : IRemoteStorage {
    @RequiresApi(Build.VERSION_CODES.O)
    var user: User = User(
        id = 1,
        name = "Tamara",
        bio = "J'ai besoin de beaucoup beaucoup beaucoup de sommeil",
        skin = Skin(0,0,0),
        stats = Stats(LocalDateTime.MIN, LocalDateTime.MAX, 67)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getUserProfileWithId(userId: Long): CompletableFuture<User> {
        return CompletableFuture.completedFuture(user)
    }

    override fun getAllUsersIds(): CompletableFuture<List<Long>> {
        throw UnsupportedOperationException("Not implemented yet")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setUserProfileWithId(userId: Long, user: User) {
        this.user = user
    }

    override fun setUserName(userId: Long, name: String): CompletableFuture<Boolean> {
        throw UnsupportedOperationException("Not implemented yet")
    }

    override fun setUserBio(userId: Long, bio: String): CompletableFuture<Boolean> {
        throw UnsupportedOperationException("Not implemented yet")
    }

    override fun setUserSkin(userId: Long, skin: Skin): CompletableFuture<Boolean> {
        throw UnsupportedOperationException("Not implemented yet")
    }

    override fun <T> setUserStat(
        userId: Long,
        statName: String,
        stat: T
    ): CompletableFuture<Boolean> {
        throw UnsupportedOperationException("Not implemented yet")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllUsers(): CompletableFuture<List<User>> {
        return CompletableFuture.completedFuture(listOf<User>(user))
    }

    override fun addUser(userId: Long, user: User): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(false)
    }

    companion object {
        val instance: FakeRemoteStorage = FakeRemoteStorage()
    }
}