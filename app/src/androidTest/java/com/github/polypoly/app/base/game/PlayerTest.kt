package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.bonus_card.BonusCard
import com.github.polypoly.app.base.game.bonus_card.InGameBonusCard
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.commons.PolyPolyTest.Companion.TEST_USER_0
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.utils.global.GlobalInstances
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.osmdroid.util.GeoPoint

@RunWith(JUnit4::class)
class PlayerTest: PolyPolyTest(false, false) {

    private val gameLobby = GameLobby(
        TEST_USER_1, gameRulesDefault,
        "test_game", "123456", false)

    @Before
    fun startGame() {
        GlobalInstances.currentUser = PolyPolyTest.TEST_USER_1
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
        val testPlayer = Player(TEST_USER_0, playerMoney)
        testPlayer.loseMoney(moneyLost)
        assertEquals(playerMoney - moneyLost, testPlayer.getBalance())
    }

    @Test
    fun loseMoneyDoesNotDecreaseTheMoneyOfThePlayerBelowZero() {
        val testPlayer = Player(TEST_USER_0, 100)
        testPlayer.loseMoney(150)
        assertEquals(0, testPlayer.getBalance())
    }

    @Test
    fun loseMoneyThrowsAnExceptionIfTheAmountIsNegative() {
        val testPlayer = Player(TEST_USER_0, 100)
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
        val testPlayer = Player(TEST_USER_0, 100)
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
        val testPlayer = Player(TEST_USER_0, 0, 3)
        val thrown = assertThrows(IllegalStateException::class.java) {
            testPlayer.loseMoney(50)
        }
        assertNotNull(thrown.message)
        val message = thrown.message
        assertEquals("The player has already lost the game", message)
    }

    @Test
    fun loseMoneyChangeThePlayerStatusToLostIfThePlayerHasNoMoneyLeft() {
        val playerMoney = 100
        val testPlayer = Player(TEST_USER_0, playerMoney)
        testPlayer.loseMoney(200)
        assertEquals(testPlayer.getBalance(), 0)
        assertTrue(testPlayer.hasLost())
        assertEquals(testPlayer.getRoundLost(), 1)
    }

    @Test
    fun loseMoneyDoesNotChangeThePlayerStatusToLostIfThePlayerHasTheExactAmountOfMoneyLeft() {
        val playerMoney = 100
        val testPlayer = Player(TEST_USER_0, playerMoney)
        testPlayer.loseMoney(playerMoney)
        assertFalse(testPlayer.hasLost())
        assertTrue(testPlayer.getRoundLost() == null)
    }

    @Test
    fun earnMoneyIncreaseTheMoneyOfThePlayer() {
        val playerMoney = 100
        val moneyEarned = 50
        val testPlayer = Player(TEST_USER_0, playerMoney)
        testPlayer.earnMoney(moneyEarned)
        assertEquals(playerMoney + moneyEarned, testPlayer.getBalance())
    }

    @Test
    fun earnMoneyThrowsAnExceptionIfThePlayerHasAlreadyLost() {
        val testPlayer = Player(TEST_USER_0, 0, 4)
        val thrown = assertThrows(IllegalStateException::class.java) {
            testPlayer.earnMoney(50)
        }
        assertNotNull(thrown.message)
        assertEquals("The player has already lost the game", thrown.message)
    }

    @Test
    fun canBuyFailsIfThePlayerHasLost() {
        val testPlayer = Player(TEST_USER_0, 0, 4)
        assertFalse(testPlayer.canBuy(getRandomLocation(), 300))
    }

    @Test
    fun canBuyFailsWithNegativeOrNullAmount() {
        val testPlayer = Player(TEST_USER_0, 300)
        assertFalse(testPlayer.canBuy(getRandomLocation(), -300))
        assertFalse(testPlayer.canBuy(getRandomLocation(), 0))
    }

    @Test
    fun canBuyFailsIfTheAmountIsSmallerThanTheLocationPrice() {
        val testPlayer = Player(TEST_USER_0, 100)
        val locationProperty = LocationProperty("Test", 300, 30, 60, 0.0, 0.0)
        assertFalse(testPlayer.canBuy(locationProperty, locationProperty.basePrice - 1))
    }

    @Test
    fun collectTuitionFeesToPayCardDecreaseThePlayerBalance() {
        val playerMoney = 500
        val testPlayer = Player(TEST_USER_0, playerMoney)
        val inGameBonusCard = InGameBonusCard(BonusCard.TUITION_FEES_TO_PAY, GeoPoint(0.0, 0.0))
        testPlayer.collectBonusCard(inGameBonusCard)
        assertEquals(playerMoney - 100, testPlayer.getBalance())
    }

    @Test
    fun collectTeachingAssistantPayDayCardIncreaseThePlayerBalance() {
        val playerMoney = 500
        val testPlayer = Player(TEST_USER_0, playerMoney)
        val inGameBonusCard = InGameBonusCard(BonusCard.TEACHING_ASSISTANT_PAYDAY, GeoPoint(0.0, 0.0))
        testPlayer.collectBonusCard(inGameBonusCard)
        assertEquals(playerMoney + 100, testPlayer.getBalance())
    }

}