package com.github.polypoly.app.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.polypoly.app.game.*
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import kotlin.time.Duration.Companion.hours

/**
 * A fake remote storage to test the functionalities without the database
 */
class FakeRemoteStorage : IRemoteStorage {

    private val code1 = "1234"
    private val code2 = "abcd"
    private val code3 = "123abc"
    private val code4 = "1234abc"
    private val code5 = "abc123"
    private val code6 = "abc1234"

    private val name1 = "Full gameLobby"
    private val name2 = "Joinable 1"
    private val name3 = "Joinable 2"
    private val name4 = "Joinable 3"
    private val name5 = "Private gameLobby"
    private val name6 = "Joinable 4"

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
        testDuration, emptyList(), testInitialBalance, name1, code1
    )
    private val gameLobbyJoinable1 = GameLobby(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name2, code2
    )
    private val gameLobbyJoinable2 = GameLobby(
        testUser1, GameMode.LAST_STANDING, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name3, code3
    )
    private val gameLobbyJoinable3 = GameLobby(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name4, code4
    )
    private val gameLobbyPrivate = GameLobby(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name5, code5, true
    )

    private val gameLobbyJoinable4 = GameLobby(
        testUser1, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
        testDuration, emptyList(), testInitialBalance, name6, code6
    )

    private val mockGameLobbies :HashMap<String, GameLobby> = hashMapOf(
        code1 to gameLobbyFull,
        code2 to gameLobbyJoinable1,
        code3 to gameLobbyJoinable2,
        code4 to gameLobbyJoinable3,
        code5 to gameLobbyPrivate
    )

    var user: User = User(
        id = 1,
        name = "Tamara",
        bio = "J'ai besoin de beaucoup beaucoup beaucoup de sommeil",
        skin = Skin(0,0,0),
        stats = Stats(LocalDateTime.MIN, LocalDateTime.MAX, 67)
    )

    init {
        gameLobbyFull.addUser(testUser2)
        gameLobbyFull.addUser(testUser3)
        gameLobbyFull.addUser(testUser4)
        gameLobbyFull.addUser(testUser5)

        gameLobbyJoinable1.addUser(testUser2)
        gameLobbyJoinable1.addUser(testUser3)

        gameLobbyJoinable2.addUser(testUser2)
        gameLobbyJoinable2.addUser(testUser3)
        gameLobbyJoinable2.addUser(testUser4)

        gameLobbyPrivate.addUser(testUser2)
        gameLobbyPrivate.addUser(testUser3)
        gameLobbyPrivate.addUser(testUser4)
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