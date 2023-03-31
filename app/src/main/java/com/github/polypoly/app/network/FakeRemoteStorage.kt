package com.github.polypoly.app.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.polypoly.app.game.*
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import kotlin.time.Duration.Companion.hours

/**
 * A fake remote storage to test the functionalities with compatibility for later use of the DB
 */
class FakeRemoteStorage : IRemoteStorage {

    private val emptySkin = Skin(0, 0, 0)
    private val zeroStats = Stats()
    private val testUser1 = User(2, "test_user_1", "", emptySkin, zeroStats)
    private val testUser2 = User(3, "test_user_2", "", emptySkin, zeroStats)
    private val testUser3 = User(4, "test_user_3", "", emptySkin, zeroStats)
    private val testUser4 = User(5, "test_user_4", "", emptySkin, zeroStats)
    private val testUser5 = User(6, "test_user_5", "", emptySkin, zeroStats)

    private val testMinNumberPlayers = 2
    private val testMaxNumberPlayers = 5
    private val testDuration = 2.hours
    private val testInitialBalance = 100

    private val gameLobbyFull = GameLobby(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, "Full gameLobby", "1234"
    )
    private val gameLobbyJoinable1 = GameLobby(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, "Joinable 1", "abcd"
    )
    private val gameLobbyJoinable2 = GameLobby(
        testUser1, GameMode.LAST_STANDING, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, "Joinable 2", "123abc"
    )
    private val gameLobbyJoinable3 = GameLobby(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, "Joinable 3", "1234abc"
    )
    private val gameLobbyPrivate = GameLobby(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, "Private gameLobby", "abc123", true
    )

    private val gameLobbyJoinable4 = GameLobby(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, "Joinable 4", "abc1234"
    )

    private val mockGameLobbies :HashMap<String, GameLobby> = hashMapOf(
        gameLobbyFull.code to gameLobbyFull,
        gameLobbyJoinable1.code to gameLobbyJoinable1,
        gameLobbyJoinable2.code to gameLobbyJoinable2,
        gameLobbyJoinable3.code to gameLobbyJoinable3,
        gameLobbyPrivate.code to gameLobbyPrivate,
        gameLobbyJoinable4.code to gameLobbyJoinable4
    )

    var user: User = User(
        id = 1,
        name = "Tamara",
        bio = "J'ai besoin de beaucoup beaucoup beaucoup de sommeil",
        skin = Skin(0,0,0),
        stats = Stats(LocalDateTime.MIN, LocalDateTime.MAX, 67)
    )

    init {
        gameLobbyFull.addUsers(listOf(testUser2, testUser3, testUser4, testUser5))

        gameLobbyJoinable1.addUsers(listOf(testUser2, testUser3))

        gameLobbyJoinable2.addUsers(listOf(testUser2, testUser3, testUser4))

        gameLobbyJoinable3.addUsers(listOf(testUser2, testUser3, testUser4))
    }

    override fun getUserWithId(userId: Long): CompletableFuture<User> {
        return CompletableFuture.completedFuture(user)
    }

    override fun updateUser(user: User): CompletableFuture<Boolean> {
        this.user = user
        return CompletableFuture.completedFuture(true)
    }

    override fun getAllUsers(): CompletableFuture<List<User>> {
        return CompletableFuture.completedFuture(listOf<User>(user))
    }

    override fun registerUser(user: User): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(false)
    }

    override fun getGameLobbyWithCode(code: String): Future<GameLobby> {
        return CompletableFuture.completedFuture(mockGameLobbies[code])
    }

    override fun getAllGameLobbies(): Future<List<GameLobby>> {
        return CompletableFuture.completedFuture(mockGameLobbies.values.toList())
    }

    override fun getAllGameLobbiesCodes(): Future<List<String>> {
        return CompletableFuture.completedFuture(mockGameLobbies.keys.toList())
    }

    override fun registerGameLobby(gameLobby: GameLobby): Future<Boolean> {
        mockGameLobbies[gameLobby.code] = gameLobby
        return CompletableFuture.completedFuture(true)
    }

    override fun updateGameLobby(gameLobby: GameLobby): Future<Boolean> {
        mockGameLobbies[gameLobby.code] = gameLobby
        return CompletableFuture.completedFuture(true)
    }

    companion object {
        val instance: FakeRemoteStorage = FakeRemoteStorage()
    }
}