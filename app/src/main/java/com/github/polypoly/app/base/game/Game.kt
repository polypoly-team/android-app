package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.menu.PastGame
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.network.StorableObject
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
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
 * @property inGameLocations the [InGameLocation]s of the [Game]
 */
class Game private constructor(
    val code: String = "default-code",
    val admin: User = User(),
    var players: List<Player> = listOf(),
    val rules: GameParameters = GameParameters(),
    val dateBegin: Long = System.currentTimeMillis(),
    val inGameLocations: List<InGameLocation> = rules.gameMap
        .flatMap { zone -> zone.locationProperties.map { InGameLocation(it) } },
) : StorableObject<GameDB>(GameDB::class, DB_GAMES_PATH, code) {

    val allLocations: List<LocationProperty> get() = rules.gameMap.flatMap { zone -> zone.locationProperties }

    var currentRound: Int = 1

    /**
     * Go to the next turn
     */
    fun nextTurn() {
        ++currentRound
        if (isGameFinished()) {
            val pastGame = endGame()
        }
    }

    /**
     * Test if the game is finished
     * @return true if the game is finished, false otherwise
     * @throws IllegalStateException if the game mode is RICHEST_PLAYER and maxRound is null
     */
    fun isGameFinished(): Boolean {
        return when (rules.gameMode) {
            GameMode.LAST_STANDING -> {
                players.filter { !it.hasLost() }.size <= 1
            }

            GameMode.RICHEST_PLAYER -> {
                if (rules.maxRound == null)
                    throw IllegalStateException("maxRound can't be null in RICHEST_PLAYER game mode")
                currentRound > rules.maxRound
            }

            GameMode.LANDLORD -> {
                if (rules.maxRound == null)
                    throw IllegalStateException("maxRound can't be null in LANDLORD game mode")
                currentRound > rules.maxRound
            }
        }
    }

    /**
     * Return the ranking of the players
     * @return a [Map] of the [Player]s' [User] id and their rank
     */
    fun ranking(): Map<String, Int> {
        val playersSorted = players.sortedDescending()
        val map = playersSorted.mapIndexed { index, player -> player.user.id to index + 1 }.toMap()
            .toMutableMap()
        for (i in 1 until (players.size)) {
            val currentPlayer = playersSorted[i]
            val previousPlayer = playersSorted[i - 1]
            if (currentPlayer.compareTo(previousPlayer) == 0) {
                map[previousPlayer.user.id]?.let { map[currentPlayer.user.id] = it }
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
        if (!isGameFinished()) throw IllegalStateException("can't end the game now")
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
    fun getPlayer(userId: String): Player? {
        return players.find { it.user.id == userId }
    }

    /**
     * Get the player associated to the admin of the game
     * @return the player associated to the admin of the game
     * @throws IllegalStateException if the admin is not in the game
     */
    fun getAdmin(): Player {
        return players.find { it.user.id == admin.id }
            ?: throw IllegalStateException("the admin is not in the game")
    }

    /**
     * Compute the winner of the bets, notify the players and update the balance of the players
     * and the location owner.
     */
    fun computeAllWinnersOfBets() {
        inGameLocations.forEach {
            val winningBet = it.computeWinningBid()
            if (winningBet != null) {
                val winner = winningBet.player
                if (winner.user.currentUser) {
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
            val players = gameLobby.usersRegistered.map {
                Player(it, gameLobby.rules.initialPlayerBalance)
            }
            val inGameLocations = gameLobby.rules.gameMap.flatMap { zone ->
                zone.locationProperties.map { InGameLocation(it) }
            }
            if(gameLobby.rules.gameMode == GameMode.LANDLORD)
                assignRandomLocations(inGameLocations, gameLobby, players)

            val game = Game(
                code = gameLobby.code,
                admin = gameLobby.admin,
                players = players,
                rules = gameLobby.rules,
                dateBegin = System.currentTimeMillis() / 1000,
                inGameLocations = inGameLocations
            )

            gameInProgress = game
            return game
        }

        /**
         * Assign random locations to the players and update the list of [InGameLocation]s
         * @param inGameLocations the list of [InGameLocation]s to update
         * @param gameLobby the game lobby from which the game is launch
         * @param players the list of players
         */
        private fun assignRandomLocations(inGameLocations: List<InGameLocation>,
                                          gameLobby: GameLobby,
                                          players: List<Player>) {
            fun generateRandomLocations(
                gameMapLocations: List<InGameLocation>,
                n: Int
            ): List<InGameLocation> =
                gameMapLocations.shuffled().take(n)


            players.forEach { player ->

                // Get X random [InGameLocation]s
                val randomLocationsToGive = generateRandomLocations(
                    inGameLocations.filter { it.owner == null },
                    gameLobby.rules.maxBuildingPerLandlord
                )

                // Assign the locations to the player and update the [InGameLocation]'s owner
                player.earnNewLocations(randomLocationsToGive)
            }
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