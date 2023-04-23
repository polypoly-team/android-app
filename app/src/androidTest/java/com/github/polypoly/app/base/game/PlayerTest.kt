package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.bonus_card.BonusCard
import com.github.polypoly.app.base.game.bonus_card.InGameBonusCard
import com.github.polypoly.app.base.game.rules_and_lobby.GameLobby
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.Location
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.commons.PolyPolyTest.Companion.TEST_USER_0
import com.github.polypoly.app.map.LocationRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.osmdroid.util.GeoPoint

@RunWith(JUnit4::class)
class PlayerTest {

    private val gameLobby = GameLobby(PolyPolyTest.TEST_USER_1, PolyPolyTest.gameRulesDefault,
        "test_game", "123456", false)

    @Before
    fun startGame() {
        gameLobby.addUser(PolyPolyTest.TEST_USER_2)
        gameLobby.addUser(PolyPolyTest.TEST_USER_3)
        gameLobby.addUser(PolyPolyTest.TEST_USER_4)
        gameLobby.addUser(PolyPolyTest.TEST_USER_5)
        gameLobby.addUser(TEST_USER_0)
        gameLobby.start()
    }


    @Test
    fun greatestPlayerIsTheOneWithMoreMoney() {
       assertTrue(PolyPolyTest.testPlayer1 < PolyPolyTest.testPlayer2)
       assertTrue(PolyPolyTest.testPlayer2 > PolyPolyTest.testPlayer3)
    }

    @Test
    fun playerWithSameBalanceGreaterThan0AreEquals() {
        assertTrue(PolyPolyTest.testPlayer1.compareTo(PolyPolyTest.testPlayer3) == 0)
    }

    @Test
    fun playerWhoLostFirstIsTheSmallest() {
        assertTrue(PolyPolyTest.testPlayer4 < PolyPolyTest.testPlayer5)
    }

    @Test
    fun compareToConsiderEqualThePlayerWhoLostAtTheSameTime() {
        assertTrue(PolyPolyTest.testPlayer6.compareTo(PolyPolyTest.testPlayer5) == 0)
    }

    @Test
    fun loseMoneyDecreaseTheMoneyOfThePlayer() {
        val playerMoney = 100
        val moneyLost = 50
        val testPlayer = Player(TEST_USER_0, playerMoney, listOf())
        testPlayer.loseMoney(moneyLost)
        assertEquals(playerMoney - moneyLost, testPlayer.getBalance())
    }

    @Test
    fun loseMoneyDoesNotDecreaseTheMoneyOfThePlayerBelowZero() {
        val testPlayer = Player(TEST_USER_0, 100, listOf())
        testPlayer.loseMoney(150)
        assertEquals(0, testPlayer.getBalance())
    }

    @Test
    fun loseMoneyThrowsAnExceptionIfTheAmountIsNegative() {
        val testPlayer = Player(TEST_USER_0, 100, listOf())
        val thrown = assertThrows(IllegalArgumentException::class.java) {
            testPlayer.loseMoney(-50)
        }
        assertNotNull(thrown.message)
        val message = thrown.message
        if (message != null) {
            assertEquals("The amount of money lost cannot be negative or zero", message)
        }
    }

    @Test
    fun loseMoneyThrowsAnExceptionIfTheAmountIsZero() {
        val testPlayer = Player(TEST_USER_0, 100, listOf())
        val thrown = assertThrows(IllegalArgumentException::class.java) {
            testPlayer.loseMoney(0)
        }
        assertNotNull(thrown.message)
        val message = thrown.message
        if (message != null) {
            assertEquals("The amount of money lost cannot be negative or zero", message)
        }
    }

    @Test
    fun loseMoneyThrowsAnExceptionIfThePlayerHasAlreadyLost() {
        val testPlayer = Player(TEST_USER_0, 0, listOf(), 3)
        val thrown = assertThrows(IllegalArgumentException::class.java) {
            testPlayer.loseMoney(0)
        }
        assertNotNull(thrown.message)
        val message = thrown.message
        assertEquals("The player has already lost the game", message)
    }

    @Test
    fun loseMoneyChangeThePlayerStatusToLostIfThePlayerHasNoMoneyLeft() {
        val playerMoney = 100
        val testPlayer = Player(TEST_USER_0, playerMoney, listOf())
        testPlayer.loseMoney(150)
        assertTrue(testPlayer.getBalance() == 0)
        assertTrue(testPlayer.hasLost())
        assertTrue(testPlayer.getRoundLost() == 1)
    }

    @Test
    fun loseMoneyDoesNotChangeThePlayerStatusToLostIfThePlayerHasTheExactAmountOfMoneyLeft() {
        val playerMoney = 100
        val testPlayer = Player(TEST_USER_0, playerMoney, listOf())
        testPlayer.loseMoney(playerMoney)
        assertFalse(testPlayer.hasLost())
        assertTrue(testPlayer.getRoundLost() == null)
    }

    @Test
    fun earnMoneyIncreaseTheMoneyOfThePlayer() {
        val playerMoney = 100
        val moneyEarned = 50
        val testPlayer = Player(TEST_USER_0, playerMoney, listOf())
        testPlayer.earnMoney(moneyEarned)
        assertEquals(playerMoney + moneyEarned, testPlayer.getBalance())
    }

    @Test
    fun earnMoneyThrowsAnExceptionIfThePlayerHasAlreadyLost() {
        val testPlayer = Player(TEST_USER_0, 0, listOf(), 4)
        val thrown = assertThrows(IllegalStateException::class.java) {
            testPlayer.earnMoney(50)
        }
        assertNotNull(thrown.message)
        assertEquals("The player has already lost the game", thrown.message)
    }

    @Test
    fun betToBuyCreatesALocationBetWithTheCorrectArguments() {
        val amountOfTheBet = 300
        val testPlayer = Player(TEST_USER_0, 300, listOf())
        val bet = testPlayer.bidToBuy(InGameLocation(LocationRepository.getZones()[0].locations[0]), amountOfTheBet)
        assertEquals(amountOfTheBet, bet.amount)
        assertEquals(TEST_USER_0.id, bet.player.user.id)
        assertTrue(bet.randomNumber < 1 && bet.randomNumber >= 0)
    }

    @Test
    fun betToBuyThrowsAnExceptionIfThePlayerHasAlreadyLost() {
        val testPlayer = Player(TEST_USER_0, 0, listOf(), 4)
        val thrown = assertThrows(IllegalStateException::class.java) {
            testPlayer.bidToBuy(InGameLocation(LocationRepository.getZones()[0].locations[0]), 300)
        }
        assertNotNull(thrown.message)
         assertEquals("The player has already lost the game", thrown.message)
    }

    @Test
    fun betToBuyThrowsAnExceptionIfTheAmountIsNegative() {
        val testPlayer = Player(TEST_USER_0, 300, listOf())
        val thrown = assertThrows(IllegalArgumentException::class.java) {
            testPlayer.bidToBuy(InGameLocation(LocationRepository.getZones()[0].locations[0]), -300)
        }
        assertNotNull(thrown.message)
        val message = thrown.message
        assertEquals("The amount of money bet cannot be negative or zero", message)
    }

    @Test
    fun betToBuyThrowsAnExceptionIfTheAmountIsZero() {
        val testPlayer = Player(TEST_USER_0, 300, listOf())
        val thrown = assertThrows(IllegalArgumentException::class.java) {
            testPlayer.bidToBuy(InGameLocation(LocationRepository.getZones()[0].locations[0]), 0)
        }
        assertNotNull(thrown.message)
        assertEquals("The amount of money bet cannot be negative or zero", thrown.message)
    }

    @Test
    fun betToBuyThrowsAnExceptionIfTheLocationIsAlreadyOwned() {
        val testPlayer = Player(TEST_USER_0, 300, listOf())
        val thrown = assertThrows(IllegalArgumentException::class.java) {
            testPlayer.bidToBuy(
                location = InGameLocation(LocationRepository.getZones()[0].locations[0], owner = PolyPolyTest.testPlayer2), 300)
        }
        assertNotNull(thrown.message)
        assertEquals("The location is already owned by someone", thrown.message)
    }

    @Test
    fun betToBuyThrowsAnExceptionIfTheAmountIsSmallerThanTheLocationPrice() {
        val testPlayer = Player(TEST_USER_0, 100, listOf())
        val thrown = assertThrows(IllegalArgumentException::class.java) {
            val location = Location("Test", 300, 30, 60, 0.0, 0.0)
            testPlayer.bidToBuy(InGameLocation(location), 100)
        }
        assertNotNull(thrown.message)
        assertEquals("The player has not bet enough money to buy the location", thrown.message)
    }

    @Test
    fun collectTuitionFeesToPayCardDecreaseThePlayerBalance() {
        val playerMoney = 500
        val testPlayer = Player(TEST_USER_0, playerMoney, listOf())
        val inGameBonusCard = InGameBonusCard(BonusCard.TUITION_FEES_TO_PAY, GeoPoint(0.0, 0.0))
        testPlayer.collectBonusCard(inGameBonusCard)
        assertEquals(playerMoney - 100, testPlayer.getBalance())
    }

    @Test
    fun collectTeachingAssistantPayDayCardIncreaseThePlayerBalance() {
        val playerMoney = 500
        val testPlayer = Player(TEST_USER_0, playerMoney, listOf())
        val inGameBonusCard = InGameBonusCard(BonusCard.TEACHING_ASSISTANT_PAYDAY, GeoPoint(0.0, 0.0))
        testPlayer.collectBonusCard(inGameBonusCard)
        assertEquals(playerMoney + 100, testPlayer.getBalance())
    }

}