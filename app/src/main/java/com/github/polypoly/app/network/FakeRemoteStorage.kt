package com.github.polypoly.app.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.polypoly.app.game.user.Skin
import com.github.polypoly.app.game.user.Stats
import com.github.polypoly.app.game.user.User
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
        stats = Stats(LocalDateTime.MIN, LocalDateTime.MAX, 67, 28, 14),
        trophiesWon = listOf(0, 4, 8, 11, 12, 14)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getUserProfileWithId(userId: Long): Future<User> {
        return CompletableFuture.completedFuture(user)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setUserProfileWithId(userId: Long, user: User) {
        this.user = user;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllUsers(): Future<List<User>> {
        return CompletableFuture.completedFuture(listOf<User>(user))
    }

    override fun addUser(userId: Long): Future<Boolean> {
        return CompletableFuture.completedFuture(false)
    }

    companion object {
        val instance: FakeRemoteStorage = FakeRemoteStorage()
    }
}