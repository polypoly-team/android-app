package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants
import com.github.polypoly.app.utils.global.GlobalInstances
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GameTest {

    private val emptySkin = Skin(0,0,0)
    private val zeroStats = Stats(0,0,0,0,0)
    private val testUser1 = User("42042042", "test_user1", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser2 = User("42042043", "test_user2", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser3 = User("42042044", "test_user3", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser4 = User("42042045", "test_user4", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser5 = User("42042046", "test_user5", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testUser6 = User("42042047", "test_user6", "", emptySkin, zeroStats, listOf(), mutableListOf())
    private val testDuration = 2
    private val gameRules = GameParameters(GameMode.RICHEST_PLAYER, 3, 7,
        testDuration, 10, LocationPropertyRepository.getZones(), 200)
    private val gameLobby = GameLobby(testUser1, gameRules, "test_game", "123456", false)


    @Before
    fun initLoby() {
        gameLobby.addUser(testUser2)
        gameLobby.addUser(testUser3)
        gameLobby.addUser(testUser4)
        gameLobby.addUser(testUser5)
        gameLobby.addUser(testUser6)
        GlobalInstances.currentUser = testUser1
    }


    @Test
    fun whenGameStartGameInProgressIsNotNull() {
        gameLobby.start()
        assertNotNull(Game.gameInProgress)
    }

    @Test
    fun whenGameStartGameInProgressHasTheCorrectRules() {
        gameLobby.start()
        assertEquals(gameRules.gameMode, Game.gameInProgress?.rules?.gameMode)
        assertEquals(gameRules.maximumNumberOfPlayers, Game.gameInProgress?.rules?.maximumNumberOfPlayers)
        assertEquals(gameRules.minimumNumberOfPlayers, Game.gameInProgress?.rules?.minimumNumberOfPlayers)
        assertEquals(gameRules.initialPlayerBalance, Game.gameInProgress?.rules?.initialPlayerBalance)
        assertEquals(gameRules.roundDuration, Game.gameInProgress?.rules?.roundDuration)
    }

    @Test
    fun whenGameStartEveryPlayerHasTheCorrectBalance() {
        gameLobby.start()
        for(player in Game.gameInProgress?.players!!) {
            assertEquals(gameRules.initialPlayerBalance, player.getBalance())
        }
    }

    @Test
    fun whenGameStartEveryPlayerHasRank1() {
        gameLobby.start()
        val ranking = Game.gameInProgress?.ranking()
        for (rank in ranking!!) {
            assertEquals(1, rank.value)
        }
    }

    @Test
    fun rankingRankPlayersCorrectly() {
        gameLobby.start()
        Game.gameInProgress?.getPlayer(testUser1.id)?.earnMoney(100)
        Game.gameInProgress?.getPlayer(testUser2.id)?.earnMoney(200)
        Game.gameInProgress?.getPlayer(testUser3.id)?.loseMoney(300)
        Game.gameInProgress?.getPlayer(testUser4.id)?.loseMoney(200)
        Game.gameInProgress?.getPlayer(testUser5.id)?.loseMoney(300)
        val ranking = Game.gameInProgress?.ranking()
        assertEquals(2, ranking?.get(testUser1.id))
        assertEquals(1, ranking?.get(testUser2.id))
        assertEquals(5, ranking?.get(testUser3.id))
        assertEquals(4, ranking?.get(testUser4.id))
        assertEquals(5, ranking?.get(testUser5.id))
        assertEquals(3, ranking?.get(testUser6.id))

    }

    @Test
    fun whenGameStartEveryPlayerHasTheCorrectLocation() {
        gameLobby.start()
        for (player in Game.gameInProgress?.players!!) {
            assertEquals(0, player.getOwnedLocations().size)
        }
    }

    @Test
    fun whenGameStartEveryPlayerHasTheCorrectLocationLandlordMode() {
        for (i in 1..GameLobbyConstants.maxBuildingPerLandlord) {
            val gameRules = GameParameters(
                GameMode.LANDLORD, 3, 7,
                testDuration, 10, LocationPropertyRepository.getZones(), 200, i
            )
            val gameLobby = GameLobby(testUser1, gameRules, "test_game", "123456", false)
            gameLobby.addUser(testUser2)
            gameLobby.addUser(testUser3)
            gameLobby.addUser(testUser4)
            gameLobby.addUser(testUser5)
            gameLobby.addUser(testUser6)
            gameLobby.start()
            for (player in Game.gameInProgress?.players!!) {
                assertEquals(i, player.getOwnedLocations().size)
            }
        }
    }

    @Test
    fun noLocationsOverlapLandlordMode() {
        for (i in 1..GameLobbyConstants.maxBuildingPerLandlord) {
            val gameRules = GameParameters(
                GameMode.LANDLORD, 3, 7,
                testDuration, 10, LocationPropertyRepository.getZones(), 200, i
            )
            val gameLobby = GameLobby(testUser1, gameRules, "test_game", "123456", false)
            gameLobby.addUser(testUser2)
            gameLobby.addUser(testUser3)
            gameLobby.addUser(testUser4)
            gameLobby.addUser(testUser5)
            gameLobby.addUser(testUser6)
            gameLobby.start()
            val ownedLocationsSet = mutableSetOf<InGameLocation>()

            for (player in Game.gameInProgress?.players!!)
                for (location in player.getOwnedLocations()) {
                    if (ownedLocationsSet.contains(location))
                        fail("Location $location is duplicated.")
                    ownedLocationsSet.add(location)
                }
        }
    }

    @Test
    fun whenNextTurnComputesTransactions() {

        gameLobby.start()
        val game = Game.gameInProgress!!
        val player1 = game.players[0]
        val player2 = game.players[1]
        val player1MoneyBefore = player1.getBalance()
        val player2MoneyBefore = player2.getBalance()

        class MockExecutedTransaction :
            GameTransaction("Executed Mock Transaction", Player(), true) {

            override fun executeTransaction() {
                player1.earnMoney(100)
            }

            override fun getTransactionDescription(): String {
                return "This is a mock transaction that has been executed :) !!!"
            }
        }

        class MockNotExecutedTransaction :
            GameTransaction("Not Executed Mock Transaction", Player(), false) {

            override fun executeTransaction() {
                player2.earnMoney(100)
            }

            override fun getTransactionDescription(): String {
                return "This is a mock transaction that is not executed yet :) !!!"
            }
        }

        game.transactions.add(MockExecutedTransaction())
        game.transactions.add(MockNotExecutedTransaction())

        game.nextTurn()

        assert(game.transactions.isEmpty())
        for (transactions in game.pastTransactions){
            for (transaction in transactions) {
                assertTrue(transaction.isExecuted())
            }
        }

        assert(player1MoneyBefore + 100 != player1.getBalance())
        assert(player2MoneyBefore + 100 == player2.getBalance())
    }
}