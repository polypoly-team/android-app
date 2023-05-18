package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.PropertyLevel
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.menu.PastGame
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.network.StorableObject
import com.github.polypoly.app.utils.global.Settings.Companion.DB_GAMES_PATH
import java.util.concurrent.CompletableFuture

/**
 * Represent the game and the current state of the game
 * @property admin the [User] who is the admin of the game
 * @property players the [Player]s of the game
 * @property rules the rules of the [Game]
 * @property inGameLocations the [InGameLocation]s of the [Game]
 * @property currentRound the current round of the [Game]
 * @property dateBegin the date and time when the [Game] has started in Unix time
 * (seconds since 1970-01-01T00:00:00Z)
 */
class Game private constructor(
    val code: String = "default-code",
    val admin: User = User(),
    var players: List<Player> = listOf(),
    val rules: GameParameters = GameParameters(),
    val dateBegin: Long = System.currentTimeMillis(),
) : StorableObject<GameDB>(GameDB::class, DB_GAMES_PATH, code) {

    private val inGameLocations: List<InGameLocation> = rules.gameMap.flatMap { zone -> zone.locationProperties.map { location ->
        InGameLocation(
            locationProperty = location,
            owner = null,
            level = PropertyLevel.LEVEL_0,
        ) } }
    var currentRound: Int = 1

    /**
     * Go to the next turn
     */
    fun nextTurn() {
        ++currentRound
        if(isGameFinished()) {
            val pastGame = endGame()
        }
    }

    /**
     * Test if the game is finished
     * @return true if the game is finished, false otherwise
     * @throws IllegalStateException if the game mode is RICHEST_PLAYER and maxRound is null
     */
    fun isGameFinished(): Boolean {
        return when(rules.gameMode) {
            GameMode.LAST_STANDING -> {
                players.filter { !it.hasLost() }.size <= 1
            }
            GameMode.RICHEST_PLAYER -> {
                if(rules.maxRound == null)
                    throw IllegalStateException("maxRound can't be null in RICHEST_PLAYER game mode")
                currentRound > rules.maxRound
            }
            GameMode.LANDLORD -> {
                if(rules.maxRound == null)
                    throw IllegalStateException("maxRound can't be null in RICHEST_PLAYER game mode")
                currentRound > rules.maxRound
            }
        }
    }

    /**
     * Return the ranking of the players
     * @return a [Map] of the [Player]s' [User] id and their rank
     */
    fun ranking(): Map<Long, Int> {
        val playersSorted = players.sortedDescending()
        val map = playersSorted.mapIndexed { index, player -> player.user.id to index+1 }.toMap().toMutableMap()
        for(i in 1 until (players.size)) {
            val currentPlayer = playersSorted[i]
            val previousPlayer = playersSorted[i-1]
            if(currentPlayer.compareTo(previousPlayer) == 0) {
                map[previousPlayer.user.id]?.let{ map[currentPlayer.user.id] = it }
            }
        }
        return map
    }

    /**
     * End the game and return a PastGame object
     * @return the PastGame object
     * @throws IllegalStateException if the game is not finished
     */
    fun endGame(): PastGame {
        if(!isGameFinished()) throw IllegalStateException("can't end the game now")
        return PastGame(
            users = players.map(Player::user),
            usersRank = ranking().map { it.key to it.value }.toMap(),
            date = dateBegin,
            duration = System.currentTimeMillis() / 1000 - dateBegin,
        )
    }

    /**
     * Test if the user is playing in this game
     * @param user the user to test
     * @return true if the user is playing in this game, false otherwise
     */
    fun playInThisGame(user: User): Boolean {
        return players.any { it.user.id == user.id }
    }

    /**
     * Get the player associated to the user id
     * @param userId the id of the user
     * @return the player associated to the user id
     */
    fun getPlayer(userId: Long): Player? {
        return players.find { it.user.id == userId }
    }

    /**
     * Compute the winner of the bets, notify the players and update the balance of the players
     * and the location owner.
     */
    fun computeAllWinnersOfBets() {
        inGameLocations.forEach {
            val winningBet = it.computeWinningBid()
            if(winningBet != null) {
                val winner = winningBet.player
                if(winner.user.currentUser) {
                    winner.loseMoney(winningBet.amount)
                    // TODO : notify the player that he has won and the other players in the bets
                    //  that they have lost
                }
                // TODO : what if a player wins but doesn't open their phone?
            }
        }
    }

    override fun toDBObject(): GameDB {
        return GameDB(
            code,
            admin,
            players,
            rules,
            dateBegin,
            currentRound
        )
    }

    override fun toLocalObject(dbObject: GameDB): CompletableFuture<StorableObject<GameDB>> {
        val game = Game(
            dbObject.code,
            dbObject.admin,
            dbObject.players,
            dbObject.rules,
            dbObject.dateBegin
        )
        game.currentRound = dbObject.round
        return CompletableFuture.completedFuture(game)
    }

    companion object {
        /**
         * Launch a game from the associated game lobby
         * @param gameLobby the game lobby from the game is launch
         * @return the new game created from the lobby
         */
        fun launchFromPendingGame(gameLobby: GameLobby): Game {
            val game = Game(
                code = gameLobby.code,
                admin = gameLobby.admin,
                players = gameLobby.usersRegistered.map { Player(
                    user = it,
                    balance = gameLobby.rules.initialPlayerBalance,
                    ownedLocations = listOf(),
                ) },
                rules = gameLobby.rules,
                dateBegin = System.currentTimeMillis() / 1000,
            )
            gameInProgress = game
            return game
        }

        /**
         * The game currently in progress
         */
        var gameInProgress: Game? = null
    }
}

data class GameDB(
    val code: String = "default-code",
    val admin: User = User(),
    var players: List<Player> = listOf(),
    val rules: GameParameters = GameParameters(),
    val dateBegin: Long = System.currentTimeMillis(),
    val round: Int = 0
)