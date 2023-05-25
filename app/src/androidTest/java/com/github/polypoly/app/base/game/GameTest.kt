package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationBid
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.game.location.LocationPropertyRepository
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.ui.menu.lobby.GameLobbyConstants
import com.github.polypoly.app.utils.global.GlobalInstances
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GameTest: PolyPolyTest(false, false) {

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
        Game.gameInProgress?.getPlayer(testUser1.id)?.earnMoney(200)
        Game.gameInProgress?.getPlayer(testUser2.id)?.earnMoney(100)
        Game.gameInProgress?.getPlayer(testUser4.id)?.loseMoney(100)
        Game.gameInProgress?.getPlayer(testUser5.id)?.loseMoney(250)
        Game.gameInProgress?.getPlayer(testUser6.id)?.loseMoney(250)
        val ranking = Game.gameInProgress?.ranking()
        assertEquals(1, ranking?.get(testUser1.id))
        assertEquals(2, ranking?.get(testUser2.id))
        assertEquals(3, ranking?.get(testUser3.id))
        assertEquals(4, ranking?.get(testUser4.id))
        assertEquals(5, ranking?.get(testUser5.id))
        assertEquals(5, ranking?.get(testUser6.id))
    }

    @Test
    fun whenGameStartNoPlayerOnwnAnyLocation() {
        val game = gameLobby.start()
        for (location in game.inGameLocations) {
            assertTrue(location.owner == null)
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
            val game = gameLobby.start()
            for (player in Game.gameInProgress?.players!!) {
                assertEquals(i, game.getOwnedLocations(player).size)
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
            val game = gameLobby.start()
            val ownedLocationsSet = mutableSetOf<InGameLocation>()

            for (player in Game.gameInProgress?.players!!)
                for (location in game.getOwnedLocations(player)) {
                    if (ownedLocationsSet.contains(location))
                        fail("Location $location is duplicated.")
                    ownedLocationsSet.add(location)
                }
        }
    }

    @Test
    fun nextTurnAssignsLocationsCorrectly() {
        val game = Game.launchFromPendingGame(TEST_GAME_LOBBY_FULL)

        val player0 = game.getPlayer(TEST_USER_0.id)!!
        val player1 = game.getPlayer(TEST_USER_1.id)!!
        val player2 = game.getPlayer(TEST_USER_2.id)!!
        val player3 = game.getPlayer(TEST_USER_3.id)!!
        val player4 = game.getPlayer(TEST_USER_4.id)!!
        val player5 = game.getPlayer(TEST_USER_5.id)!!

        val location1 = getRandomLocation()
        val location2 = getRandomLocation(excluding = listOf(location1))
        val location3 = getRandomLocation(excluding = listOf(location1, location2))

        game.registerBid(LocationBid(location1, player0, 300))
        game.registerBid(LocationBid(location1, player1, 350))

        game.registerBid(LocationBid(location2, player2, 500))
        game.registerBid(LocationBid(location2, player3, 400))
        game.registerBid(LocationBid(location2, player4, 450))

        game.registerBid(LocationBid(location3, player5, 400))

        game.nextTurn()

        assertEquals(game.getOwnedLocations(player1).map(InGameLocation::locationProperty), listOf(location1))
        assertEquals(game.getOwnedLocations(player2).map(InGameLocation::locationProperty), listOf(location2))
        assertEquals(game.getOwnedLocations(player5).map(InGameLocation::locationProperty), listOf(location3))

        assertTrue(game.getOwnedLocations(player0).isEmpty())
        assertTrue(game.getOwnedLocations(player3).isEmpty())
        assertTrue(game.getOwnedLocations(player4).isEmpty())
    }

    @Test
    fun nextTurnMakesAllUsersLoseMoneyForBids() {
        val game = Game.launchFromPendingGame(TEST_GAME_LOBBY_FULL)

        val player0 = game.getPlayer(TEST_USER_0.id)!!
        val player1 = game.getPlayer(TEST_USER_1.id)!!
        val player2 = game.getPlayer(TEST_USER_2.id)!!
        val player3 = game.getPlayer(TEST_USER_3.id)!!
        val player4 = game.getPlayer(TEST_USER_4.id)!!
        val player5 = game.getPlayer(TEST_USER_5.id)!!

        val location1 = getRandomLocation()
        val location2 = getRandomLocation(excluding = listOf(location1))
        val location3 = getRandomLocation(excluding = listOf(location1, location2))

        game.registerBid(LocationBid(location1, player0, 300))
        game.registerBid(LocationBid(location1, player1, 350))

        game.registerBid(LocationBid(location2, player2, 500))
        game.registerBid(LocationBid(location2, player3, 400))
        game.registerBid(LocationBid(location2, player4, 450))

        game.registerBid(LocationBid(location3, player5, 400))

        game.nextTurn()

        assertEquals(700, player0.getBalance())
        assertEquals(650, player1.getBalance())
        assertEquals(500, player2.getBalance())
        assertEquals(600, player3.getBalance())
        assertEquals(550, player4.getBalance())
        assertEquals(600, player5.getBalance())
    }

    @Test
    fun registerBidThrowsIfLocationIsNotPartOfGame() {
        val game = Game.launchFromPendingGame(TEST_GAME_LOBBY_FULL)

        val player0 = game.getPlayer(TEST_USER_0.id)!!
        val location = LocationProperty(name = "I do not exist")

        assertThrows(IllegalArgumentException::class.java) {
            game.registerBid(LocationBid(location, player0, 350))
        }
    }

    @Test
    fun registerBidThrowsIfPlayerIsNotPartOfGame() {
        val game = Game.launchFromPendingGame(TEST_GAME_LOBBY_FULL)

        val player0 = Player(user = User(id = "I do not exist"))
        val location = getRandomLocation()

        assertThrows(IllegalArgumentException::class.java) {
            game.registerBid(LocationBid(location, player0, 350))
        }
    }

    @Test
    fun registerBidThrowsIfPlayerAlreadyHasABid() {
        val game = Game.launchFromPendingGame(TEST_GAME_LOBBY_FULL)

        val player0 = game.getPlayer(TEST_USER_0.id)!!

        val location1 = getRandomLocation()
        val location2 = getRandomLocation(excluding = listOf(location1))

        game.registerBid(LocationBid(location1, player0, 300))
        assertThrows(IllegalStateException::class.java) {
            game.registerBid(LocationBid(location2, player0, 350))
        }
    }

    @Test
    fun registerBidThrowsIfPlayerDoesNotHaveEnoughMoney() {
        val game = Game.launchFromPendingGame(TEST_GAME_LOBBY_FULL)

        val player0 = game.getPlayer(TEST_USER_0.id)!!
        val location = getRandomLocation()

        assertThrows(IllegalStateException::class.java) {
            game.registerBid(LocationBid(location, player0, 10_000))
        }
    }
}