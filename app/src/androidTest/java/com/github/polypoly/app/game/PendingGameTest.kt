package com.github.polypoly.app.game

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@RunWith(JUnit4::class)
class PendingGameTest {

    private val emptySkin = Skin(0, 0, 0)
    private val zeroStats = Stats(LocalDateTime.MIN, LocalDateTime.MIN, 0)
    private val testUser = User(42042042, "test_user", "", emptySkin, zeroStats)
    private val testMinNumberPlayers = 3
    private val testMaxNumberPlayers = 7
    private val testDuration = 2.hours
    private val testInitialBalance = 100
    private val testName = "Unit Test Game"
    private val testCode = "007"

    @Test
    fun pendingGameCanStartWithEnoughPlayers() {
        val pendingGame = PendingGame(
            testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, emptyList(), testInitialBalance, testName, testCode
        )
        for (n in 1L until testMinNumberPlayers)
            pendingGame.addUser(User( n, "test-$n", "", emptySkin, zeroStats))
        for (n in testMinNumberPlayers + 1..testMaxNumberPlayers) {
            pendingGame.addUser(User(n.toLong(), "test-$n", "", emptySkin, zeroStats))
            assertTrue(pendingGame.canStart())
        }
    }

    @Test
    fun pendingGameCannotStartWithoutCorrectNumberOfPlayers() {
        val pendingGame = PendingGame(
            testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, emptyList(), testInitialBalance, testName, testCode
        )
        for (n in 1L until testMinNumberPlayers) {
            assertFalse(pendingGame.canStart())
            pendingGame.addUser(User(n, "test-$n", "", emptySkin, zeroStats))
        }
        for (n in testMinNumberPlayers until testMaxNumberPlayers) {
            pendingGame.addUser(User(n.toLong(), "test-$n", "", emptySkin, zeroStats))
        }
        for (n in 0L..10L)
            assertThrows(IllegalStateException::class.java) {
                pendingGame.addUser(User((testMaxNumberPlayers.toLong()) + n, "test-$n", "", emptySkin, zeroStats))
            }
    }

    @Test
    fun pendingGameDoesntAcceptInvalidDurations() {
        val infinitRoundDuration = Duration.INFINITE
        val negativeRoundDuration = (-2).hours

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val pendingGame = PendingGame(testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                infinitRoundDuration, emptyList(), testInitialBalance, testName, testCode)
        }

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val pendingGame = PendingGame(testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                negativeRoundDuration, emptyList(), testInitialBalance, testName, testCode)
        }
    }

    @Test
    fun pendingGameDoesntAcceptInvalidGameNames() {
        val emptyName = ""
        val blancName = "    \n  \t"

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val pendingGame = PendingGame(testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, emptyList(), testInitialBalance, emptyName, testCode)
        }

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val pendingGame = PendingGame(testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, emptyList(), testInitialBalance, emptyName, testCode)
        }
    }

    @Test
    fun pendingGameDoesntAcceptInvalidGameCodes() {
        val emptyCode = ""
        val blancCode = "    \n  \t"

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val pendingGame = PendingGame(testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, emptyList(), testInitialBalance, testName, emptyCode)
        }

        assertThrows(java.lang.IllegalArgumentException::class.java) {
            val pendingGame = PendingGame(testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
                testDuration, emptyList(), testInitialBalance, testName, blancCode)
        }
    }

    @Test
    fun canAddUsersToPendingGame() {
        val pendingGame = PendingGame(testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, emptyList(), testInitialBalance, testName, testCode)

        val u1 = User(42042043, "test_user1", "", emptySkin, zeroStats)
        val u2 = User(42042044, "test_user2", "", emptySkin, zeroStats)
        val u3 = User(42042045, "test_user3", "", emptySkin, zeroStats)
        val u4 = User(42042046, "test_user4", "", emptySkin, zeroStats)

        pendingGame.addUser(u1)
        pendingGame.addUser(u2)
        pendingGame.addUser(u3)
        pendingGame.addUser(u4)

        val users = pendingGame.usersRegistered
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
        val pendingGame = PendingGame(testUser, GameMode.RICHEST_PLAYER, testMinNumberPlayers, testMaxNumberPlayers,
            testDuration, emptyList(), testInitialBalance, testName, testCode)

        val u1 = User(42042043, "test_user1", "", emptySkin, zeroStats)
        val u2 = User(42042044, "test_user2", "", emptySkin, zeroStats)
        val u3 = User(42042045, "test_user3", "", emptySkin, zeroStats)
        val u4 = User(42042046, "test_user4", "", emptySkin, zeroStats)

        pendingGame.addUser(u1)
        pendingGame.addUser(u2)
        pendingGame.addUser(u3)
        pendingGame.addUser(u4)

        pendingGame.removeUser(u1.id)
        pendingGame.removeUser(u3.id)

        val users = pendingGame.usersRegistered
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
        val pendingGame = PendingGame(
            testUser, GameMode.RICHEST_PLAYER, 2, testMaxNumberPlayers,
            testDuration, emptyList(), testInitialBalance, testName, testCode
        )
        pendingGame.addUser(User(42042050, "test_user1", "", emptySkin, zeroStats))

        val game = pendingGame.start()
        val usersRegistered = pendingGame.usersRegistered

        val user = usersRegistered.find {
            it.id == testUser.id
        }
        assertEquals(game.admin.id, pendingGame.admin.id)
        assertEquals(game.gameMode, pendingGame.gameMode)
    }
}