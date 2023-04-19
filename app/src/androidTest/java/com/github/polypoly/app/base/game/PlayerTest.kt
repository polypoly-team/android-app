package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.rules_and_lobby.GameLobby
import com.github.polypoly.app.base.game.rules_and_lobby.GameMode
import com.github.polypoly.app.base.game.rules_and_lobby.GameRules
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.map.LocationRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.time.Duration.Companion.hours

@RunWith(JUnit4::class)
class PlayerTest {

    private val emptySkin = Skin(0,0,0)
    private val zeroStats = Stats(0,0,0,0,0)
    private val testUser1 = User(42042042, "test_user1", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser2 = User(42042043, "test_user2", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser3 = User(42042044, "test_user3", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser4 = User(42042045, "test_user4", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser5 = User(42042046, "test_user5", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser6 = User(42042047, "test_user6", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testPlayer1 = Player(testUser1, 100, listOf())
    private val testPlayer2 = Player(testUser2, 200, listOf())
    private val testPlayer3 = Player(testUser3, 100, listOf())
    private val testPlayer4 = Player(testUser4, 0, listOf(), 4)
    private val testPlayer5 = Player(testUser5, 0, listOf(), 5)
    private val testPlayer6 = Player(testUser6, 0, listOf(), 5)
    private val testDuration = 2.hours
    private val gameRules = GameRules(GameMode.RICHEST_PLAYER, 3, 7,
        testDuration, 10, LocationRepository.getZones(), 200)
    private val gameLobby = GameLobby(testUser1, gameRules, "test_game", "123456", false)

    @Before
    fun startIntents() {
        gameLobby.addUser(testUser2)
        gameLobby.addUser(testUser3)
        gameLobby.addUser(testUser4)
        gameLobby.addUser(testUser5)
        gameLobby.addUser(testUser6)
        gameLobby.start()
    }


    @Test
    fun compareToConsiderGreaterThePlayerWithMoreMoney() {
       assertTrue(testPlayer1 < testPlayer2)
       assertTrue(testPlayer2 > testPlayer1)
    }

    @Test
    fun compareToConsiderEqualThePlayerWithSameMoney() {
        assertTrue(testPlayer1.compareTo(testPlayer3) == 0)
    }

    @Test
    fun compareToConsiderSmallerThePlayerWhoLostFirst() {
        assertTrue(testPlayer4 < testPlayer5)
    }

    @Test
    fun compareToConsiderEqualThePlayerWhoLostAtTheSameTime() {
        assertTrue(testPlayer6.compareTo(testPlayer5) == 0)
    }

    @Test
    fun loseMoneyDecreaseTheMoneyOfThePlayer() {
        val testPlayer = Player(testUser1, 100, listOf())
        testPlayer.loseMoney(50)
        assertEquals(50, testPlayer.getBalance())
    }

    @Test
    fun loseMoneyDoesNotDecreaseTheMoneyOfThePlayerBelowZero() {
        val testPlayer = Player(testUser1, 100, listOf())
        testPlayer.loseMoney(150)
        assertEquals(0, testPlayer.getBalance())
    }

    @Test
    fun loseMoneyThrowsAnExceptionIfTheAmountIsNegative() {
        val testPlayer = Player(testUser1, 100, listOf())
        try {
            testPlayer.loseMoney(-50)
            fail("Should have thrown an exception")
        } catch (e: IllegalArgumentException) {
            assertEquals("The amount of money lost cannot be negative or zero", e.message)
        }
    }

    @Test
    fun loseMoneyThrowsAnExceptionIfTheAmountIsZero() {
        val testPlayer = Player(testUser1, 100, listOf())
        try {
            testPlayer.loseMoney(0)
            fail("Should have thrown an exception")
        } catch (e: IllegalArgumentException) {
            assertEquals("The amount of money lost cannot be negative or zero", e.message)
        }
    }

    @Test
    fun loseMoneyThrowsAnExceptionIfThePlayerHasAlreadyLost() {
        val testPlayer = Player(testUser1, 0, listOf(), 3)
        try {
            testPlayer.loseMoney(50)
            fail("Should have thrown an exception")
        } catch (e: IllegalStateException) {
            assertEquals("The player has already lost the game", e.message)
        }
    }

    @Test
    fun loseMoneyChangeThePlayerStatusToLostIfThePlayerHasNoMoneyLeft() {
        val testPlayer = Player(testUser1, 100, listOf())
        testPlayer.loseMoney(150)
        assertTrue(testPlayer.hasLose())
        assertTrue(testPlayer.getRoundLost() == 1)
    }

    @Test
    fun loseMoneyDoesNotChangeThePlayerStatusToLostIfThePlayerHasTheExactAmountOfMoneyLeft() {
        val testPlayer = Player(testUser1, 100, listOf())
        testPlayer.loseMoney(100)
        assertFalse(testPlayer.hasLose())
        assertTrue(testPlayer.getRoundLost() == null)
    }

    @Test
    fun earnMoneyIncreaseTheMoneyOfThePlayer() {
        val testPlayer = Player(testUser1, 100, listOf())
        testPlayer.earnMoney(50)
        assertEquals(150, testPlayer.getBalance())
    }

    @Test
    fun earnMoneyThrowsAnExceptionIfThePlayerHasAlreadyLost() {
        val testPlayer = Player(testUser1, 0, listOf(), 4)
        try {
            testPlayer.earnMoney(50)
            fail("Should have thrown an exception")
        } catch (e: IllegalStateException) {
            assertEquals("The player has already lost the game", e.message)
        }
    }

    @Test
    fun betToBuyCreatesALocationBetWithTheCorrectArguments() {
        val testPlayer = Player(testUser1, 300, listOf())
        val bet = testPlayer.betToBuy(InGameLocation(LocationRepository.getZones()[0].locations[0]), 300)
        assertEquals(300, bet.amount)
        assertEquals(42042042, bet.player.user.id)
        assertTrue(bet.randomNumber < 1 && bet.randomNumber >= 0)
    }

    @Test
    fun betToBuyThrowsAnExceptionIfThePlayerHasAlreadyLost() {
        val testPlayer = Player(testUser1, 0, listOf(), 4)
        try {
            testPlayer.betToBuy(InGameLocation(LocationRepository.getZones()[0].locations[0]), 300)
            fail("Should have thrown an exception")
        } catch (e: IllegalStateException) {
            assertEquals("The player has already lost the game", e.message)
        }
    }

    @Test
    fun betToBuyThrowsAnExceptionIfTheAmountIsNegative() {
        val testPlayer = Player(testUser1, 300, listOf())
        try {
            testPlayer.betToBuy(InGameLocation(LocationRepository.getZones()[0].locations[0]), -300)
            fail("Should have thrown an exception")
        } catch (e: IllegalArgumentException) {
            assertEquals("The amount of money bet cannot be negative or zero", e.message)
        }
    }

    @Test
    fun betToBuyThrowsAnExceptionIfTheAmountIsZero() {
        val testPlayer = Player(testUser1, 300, listOf())
        try {
            testPlayer.betToBuy(InGameLocation(LocationRepository.getZones()[0].locations[0]), 0)
            fail("Should have thrown an exception")
        } catch (e: IllegalArgumentException) {
            assertEquals("The amount of money bet cannot be negative or zero", e.message)
        }
    }

    @Test
    fun betToBuyThrowsAnExceptionIfTheLocationIsAlreadyOwned() {
        val testPlayer = Player(testUser1, 300, listOf())
        try {
            testPlayer.betToBuy(
                location = InGameLocation(LocationRepository.getZones()[0].locations[0], owner = testPlayer2), 300)
            fail("Should have thrown an exception")
        } catch (e: IllegalArgumentException) {
            assertEquals("The location is already owned by someone", e.message)
        }
    }

    @Test
    fun betToBuyThrowsAnExceptionIfTheAmountIsSmallerThanTheLocationPrice() {
        val testPlayer = Player(testUser1, 100, listOf())
        try {
            testPlayer.betToBuy(InGameLocation(LocationRepository.getZones()[0].locations[0]), 100)
            fail("Should have thrown an exception")
        } catch (e: IllegalArgumentException) {
            assertEquals("The player has not bet enough money to buy the location", e.message)
        }
    }

}