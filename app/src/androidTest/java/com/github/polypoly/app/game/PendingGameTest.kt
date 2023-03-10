package com.github.polypoly.app.game

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@RunWith(JUnit4::class)
class PendingGameTest {

    private val testUser = User("test_user", 42042042)
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
            pendingGame.addUser(User("test-$n", n))
        for (n in testMinNumberPlayers..testMaxNumberPlayers) {
            pendingGame.addUser(User("test-$n", n.toLong()))
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
            pendingGame.addUser(User("test-$n", n))
        }
        for (n in testMinNumberPlayers..testMaxNumberPlayers)
            pendingGame.addUser(User("test-$n", n.toLong()))
//        assertThrows(IllegalArgumentException.class, -> pendingGame.addUser(testUser))
    }
}