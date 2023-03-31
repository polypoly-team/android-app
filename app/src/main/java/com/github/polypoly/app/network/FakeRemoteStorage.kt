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
        stats = Stats(0, 0, 67),
        trophiesWon = listOf(0, 4, 8, 11, 12, 14),
        trophiesDisplay = mutableListOf(0, 4)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getUserWithId(userId: Long): CompletableFuture<User> {
        return CompletableFuture.completedFuture(user)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun updateUser(user: User): CompletableFuture<Boolean> {
        this.user = user
        return CompletableFuture.completedFuture(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllUsers(): CompletableFuture<List<User>> {
        return CompletableFuture.completedFuture(listOf<User>(user))
    }

    override fun registerUser(user: User): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(false)
    }

    override fun getAllUsersIds(): CompletableFuture<List<Long>> {
        TODO("Not yet implemented")
    }

    companion object {
        val instance: FakeRemoteStorage = FakeRemoteStorage()
    }
}