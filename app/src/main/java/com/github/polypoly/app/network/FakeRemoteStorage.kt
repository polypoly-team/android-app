package com.github.polypoly.app.network

import com.github.polypoly.app.base.*
import com.github.polypoly.app.base.game.rules_and_lobby.kotlin.GameLobby
import com.github.polypoly.app.base.game.rules_and_lobby.kotlin.GameMode
import com.github.polypoly.app.base.game.rules_and_lobby.kotlin.GameRules
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.hours
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.global.Settings.Companion.DB_GAME_LOBIES_PATH
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
import kotlin.reflect.KClass

/**
 * A fake remote storage to test the functionalities without the database
 * Temporary until complete removal to connect with the real DB
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
    private val testDuration = 2
    private val testMaxRound = 10
    private val testInitialBalance = 100

    private val gameLobbyFull = GameLobby(
        testUser1, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, null, emptyList(), testInitialBalance), "Full gameLobby", "1234"
    )
    private val gameLobbyJoinable1 = GameLobby(
        testUser1, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, null, emptyList(), testInitialBalance), "Joinable 1", "abcd"
    )
    private val gameLobbyJoinable2 = GameLobby(
        testUser1, GameRules(GameMode.LAST_STANDING, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, testMaxRound, emptyList(), testInitialBalance), "Joinable 2", "123abc"
    )
    private val gameLobbyJoinable3 = GameLobby(
        testUser1, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, null, emptyList(), testInitialBalance), "Joinable 3", "1234abc"
    )
    private val gameLobbyPrivate = GameLobby(
        testUser1, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, null, emptyList(), testInitialBalance), "Private gameLobby", "abc123", true
    )

    private val gameLobbyJoinable4 = GameLobby(
        testUser1, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, null, emptyList(), testInitialBalance), "Joinable 4", "abc1234"
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

    private val datas = mutableMapOf<String, Any>()

    init {
        gameLobbyFull.addUsers(listOf(testUser2, testUser3, testUser4, testUser5))
        gameLobbyJoinable1.addUsers(listOf(testUser2, testUser3))
        gameLobbyJoinable2.addUsers(listOf(testUser2, testUser3, testUser4))
        gameLobbyJoinable3.addUsers(listOf(testUser2, testUser3, testUser4))

        registerValue(DB_GAME_LOBIES_PATH + gameLobbyFull.code, gameLobbyFull)
        registerValue(DB_GAME_LOBIES_PATH + gameLobbyJoinable1.code, gameLobbyJoinable1)
        registerValue(DB_GAME_LOBIES_PATH + gameLobbyJoinable2.code, gameLobbyJoinable2)
        registerValue(DB_GAME_LOBIES_PATH + gameLobbyJoinable3.code, gameLobbyJoinable3)
        registerValue(DB_GAME_LOBIES_PATH + gameLobbyJoinable4.code, gameLobbyJoinable4)
        registerValue(DB_GAME_LOBIES_PATH + gameLobbyPrivate.code, gameLobbyPrivate)

        registerValue(DB_USERS_PROFILES_PATH + "0", user)
        registerValue(DB_USERS_PROFILES_PATH + user.id, user)
        registerValue(DB_USERS_PROFILES_PATH + testUser1.id, testUser1)
        registerValue(DB_USERS_PROFILES_PATH + testUser2.id, testUser2)
        registerValue(DB_USERS_PROFILES_PATH + testUser3.id, testUser3)
        registerValue(DB_USERS_PROFILES_PATH + testUser4.id, testUser4)
        registerValue(DB_USERS_PROFILES_PATH + testUser5.id, testUser5)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getAllValues(key: String, clazz: KClass<T>): CompletableFuture<List<T>> {
        if (!datas.containsKey(key))
            return CompletableFuture.completedFuture(listOf())
        return CompletableFuture.completedFuture(listOf(datas[key] as T))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getValue(key: String, clazz: KClass<T>): CompletableFuture<T> {
        return CompletableFuture.completedFuture(datas[key] as T)
    }

    override fun getAllKeys(parentKey: String): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(listOf())
    }

    override fun keyExists(key: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(datas.keys.contains(key))
    }

    override fun <T> registerValue(key: String, value: T): CompletableFuture<Boolean> {
        if (value == null)
            throw java.lang.IllegalArgumentException("null value")
        datas[key] = value
        return CompletableFuture.completedFuture(true)
    }

    override fun <T> updateValue(key: String, value: T): CompletableFuture<Boolean> {
        if (value == null)
            throw java.lang.IllegalArgumentException("null value")
        datas[key] = value
        return CompletableFuture.completedFuture(true)
    }

    override fun <T> setValue(key: String, value: T): CompletableFuture<Boolean> {
        if (value == null)
            throw java.lang.IllegalArgumentException("null value")
        datas[key] = value
        return CompletableFuture.completedFuture(true)
    }

    companion object {
        val instance: FakeRemoteStorage = FakeRemoteStorage()
    }
}