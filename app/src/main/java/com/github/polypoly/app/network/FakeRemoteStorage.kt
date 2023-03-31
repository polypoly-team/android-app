package com.github.polypoly.app.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.polypoly.app.game.GameLobby
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
        stats = Stats(0, 0, 67)
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

    override fun getGameLobbyWithCode(code: String): Future<GameLobby> {
        TODO("Not yet implemented")
    }

    override fun getAllGameLobbies(): Future<List<GameLobby>> {
        TODO("Not yet implemented")
    }

    override fun getAllGameLobbiesCodes(): Future<List<String>> {
        TODO("Not yet implemented")
    }

    override fun registerGameLobby(gameLobby: GameLobby): Future<Boolean> {
        TODO("Not yet implemented")
    }

    override fun updateGameLobby(gameLobby: GameLobby): Future<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllUsersIds(): CompletableFuture<List<Long>> {
        TODO("Not yet implemented")
    }

    companion object {
        val instance: FakeRemoteStorage = FakeRemoteStorage()
    }
}