package com.github.polypoly.app.base

import com.github.polypoly.app.base.game.rules_and_lobby.GameLobby
import com.github.polypoly.app.base.game.rules_and_lobby.GameMode
import com.github.polypoly.app.base.game.rules_and_lobby.GameRules
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@RunWith(JUnit4::class)
class GameLobbyTest {

    private val emptySkin = Skin(0, 0, 0)
    private val zeroStats = Stats(0, 0, 0, 0, 0)
    private val testUser = User(42042042, "test_user", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testMinNumberPlayers = 3
    private val testMaxNumberPlayers = 7
    private val testDuration = 2.hours
    private val testInitialBalance = 100
    private val testName = "Unit Test Game"
    private val testCode = "007"

    @Test
    fun pendingGameCanStartWithEnoughPlayers() {
        val gameLobby = GameLobby(
            testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, null, emptyList(), testInitialBalance), testName, testCode
        )
        for (n in 1L until testMinNumberPlayers)
            gameLobby.addUser(User( n, "test-$n", "", emptySkin, zeroStats, listOf(), mutableListOf()))
        for (n in testMinNumberPlayers + 1..testMaxNumberPlayers) {
            gameLobby.addUser(User(n.toLong(), "test-$n", "", emptySkin, zeroStats, listOf(), mutableListOf()))
            assertTrue(gameLobby.canStart())
        }
    }

    @Test
    fun pendingGameCannotStartWithoutCorrectNumberOfPlayers() {
        val gameLobby = GameLobby(
            testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, null, emptyList(), testInitialBalance), testName, testCode
        )
        for (n in 1L until testMinNumberPlayers) {
            assertFalse(gameLobby.canStart())
            gameLobby.addUser(User(n, "test-$n", "", emptySkin, zeroStats, listOf(), mutableListOf()))
        }
        for (n in testMinNumberPlayers until testMaxNumberPlayers) {
            gameLobby.addUser(User(n.toLong(), "test-$n", "", emptySkin, zeroStats, listOf(), mutableListOf()))
        }
        for (n in 0L..10L)
            assertThrows(IllegalStateException::class.java) {
                gameLobby.addUser(User((testMaxNumberPlayers.toLong()) + n, "test-$n", "", emptySkin, zeroStats, listOf(), mutableListOf()))
            }
    }

    @Test
    fun pendingGameDoesntAcceptInvalidDurations() {
        val infinitRoundDuration = Duration.INFINITE
        val negativeRoundDuration = (-2).hours

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val gameLobby = GameLobby(testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                infinitRoundDuration, null, emptyList(), testInitialBalance), testName, testCode)
        }

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val gameLobby = GameLobby(testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                negativeRoundDuration, null, emptyList(), testInitialBalance), testName, testCode)
        }
    }

    @Test
    fun pendingGameDoesntAcceptInvalidGameNames() {
        val emptyName = ""
        val blancName = "    \n  \t"

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val gameLobby = GameLobby(testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, null, emptyList(), testInitialBalance), emptyName, testCode)
        }

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val gameLobby = GameLobby(testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, null, emptyList(), testInitialBalance), emptyName, testCode)
        }
    }

    @Test
    fun pendingGameDoesntAcceptInvalidGameCodes() {
        val emptyCode = ""
        val blancCode = "    \n  \t"

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val gameLobby = GameLobby(testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, null, emptyList(), testInitialBalance), testName, emptyCode)
        }

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val gameLobby = GameLobby(testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, null, emptyList(), testInitialBalance), testName, blancCode)
        }
    }

    @Test
    fun canAddUsersToPendingGame() {
        val gameLobby = GameLobby(testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, null, emptyList(), testInitialBalance), testName, testCode)

        val u1 = User(42042043, "test_user1", "", emptySkin, zeroStats, listOf(), mutableListOf())
        val u2 = User(42042044, "test_user2", "", emptySkin, zeroStats, listOf(), mutableListOf())
        val u3 = User(42042045, "test_user3", "", emptySkin, zeroStats, listOf(), mutableListOf())
        val u4 = User(42042046, "test_user4", "", emptySkin, zeroStats, listOf(), mutableListOf())

        gameLobby.addUser(u1)
        gameLobby.addUser(u2)
        gameLobby.addUser(u3)
        gameLobby.addUser(u4)

        val users = gameLobby.usersRegistered
        assertTrue(users.any {
            it.id == u1.id
        })
        assertTrue(users.any {
            it.id == u2.id
        })
        assertTrue(users.any {
            it.id == u3.id
        })
        assertTrue(users.any {
            it.id == u4.id
        })
    }

    @Test
    fun canRemoveUsersToPendingGame() {
        val gameLobby = GameLobby(testUser, GameRules(GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, null, emptyList(), testInitialBalance), testName, testCode)

        val u1 = User(42042043, "test_user1", "", emptySkin, zeroStats, listOf(), mutableListOf())
        val u2 = User(42042044, "test_user2", "", emptySkin, zeroStats, listOf(), mutableListOf())
        val u3 = User(42042045, "test_user3", "", emptySkin, zeroStats, listOf(), mutableListOf())
        val u4 = User(42042046, "test_user4", "", emptySkin, zeroStats, listOf(), mutableListOf())

        gameLobby.addUser(u1)
        gameLobby.addUser(u2)
        gameLobby.addUser(u3)
        gameLobby.addUser(u4)

        gameLobby.removeUser(u1.id)
        gameLobby.removeUser(u3.id)

        val users = gameLobby.usersRegistered
        assertFalse(users.any {
            it.id == u1.id
        })
        assertTrue(users.any {
            it.id == u2.id
        })
        assertFalse(users.any {
            it.id == u3.id
        })
        assertTrue(users.any {
            it.id == u4.id
        })
    }

    @Test
    fun pendingGameCanStartAGameOnDemand() {
        val gameLobby = GameLobby(
            testUser, GameRules(GameMode.RICHEST_PLAYER, 2, testMaxNumberPlayers,
                testDuration, null, emptyList(), testInitialBalance), testName, testCode
        )
        gameLobby.addUser(User(42042050, "test_user1", "", emptySkin, zeroStats, listOf(), mutableListOf()))

        val game = gameLobby.start()
        val usersRegistered = gameLobby.usersRegistered

        val user = usersRegistered.find {
            it.id == testUser.id
        }
        assertEquals(game.admin.id, gameLobby.admin.id)
        assertEquals(game.rules.gameMode, gameLobby.rules.gameMode)
    }
}