package com.github.polypoly.app.network

import com.github.polypoly.app.base.game.rules_and_lobby.GameLobby
import com.github.polypoly.app.base.game.rules_and_lobby.GameMode
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import kotlin.time.Duration.Companion.hours
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User

/**
 * A fake remote storage to test the functionalities without the database
 */
class FakeRemoteStorage : IRemoteStorage {

    private val emptySkin = Skin(0, 0, 0)
    private val zeroStats = Stats(0, 0, 0, 0, 0)
    private val trophiesWon1 = listOf(0, 4, 8, 11, 12, 14)
    private val trophiesDisplay1 = mutableListOf(0, 4)
    private val testUser1 = User(2, "test_user_1", "", emptySkin, zeroStats, trophiesWon1, trophiesDisplay1)
    private val testUser2 = User(3, "test_user_2", "", emptySkin, zeroStats, trophiesWon1, trophiesDisplay1)
    private val testUser3 = User(4, "test_user_3", "", emptySkin, zeroStats, trophiesWon1, trophiesDisplay1)
    private val testUser4 = User(5, "test_user_4", "", emptySkin, zeroStats, trophiesWon1, trophiesDisplay1)
    private val testUser5 = User(6, "test_user_5", "", emptySkin, zeroStats, trophiesWon1, trophiesDisplay1)

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
        stats = Stats(0, 0, 67, 28, 14),
        trophiesWon = listOf(0, 4, 8, 11, 12, 14),
        trophiesDisplay = mutableListOf(0, 4)
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
        return CompletableFuture.completedFuture(listOf(user))
    }

    override fun getAllUsersIds(): CompletableFuture<List<Long>> {
        return getAllUsers().thenApply { users -> users.map(User::id) }
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