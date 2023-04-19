package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.PastGame
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocalizationLevel
import com.github.polypoly.app.base.game.rules_and_lobby.GameLobby
import com.github.polypoly.app.base.game.rules_and_lobby.GameMode
import com.github.polypoly.app.base.game.rules_and_lobby.GameRules
import com.github.polypoly.app.base.user.User

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
    val admin: User,
    var players: List<Player>,
    val rules: GameRules,
    val inGameLocations: List<InGameLocation>,
    val currentRound: Int = 1,
    val dateBegin: Long,
) {

    /**
     * Go to the next turn
     */
    fun nextTurn() {
        // TODO update the data with the DB
        if(isGameFinished()) {
            val pastGame = endGame()
            // TODO send the pastGame to the DB
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
                players.filter { !it.hasLose() }.size <= 1
            }
            GameMode.RICHEST_PLAYER -> {
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
            if(playersSorted[i].compareTo(playersSorted[i-1]) == 0) {
                map[playersSorted[i-1].user.id]?.let{ map[playersSorted[i].user.id] = it }
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
            users = players.map { it.user },
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
            val winningBet = it.computeWinningBet()
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

    companion object {
        /**
         * Launch a game from the associated game lobby
         * @param gameLobby the game lobby from the game is launch
         * @return the new game created from the lobby
         */
        fun launchFromPendingGame(gameLobby: GameLobby): Game {
            val game = Game(
                admin = gameLobby.admin,
                players = gameLobby.usersRegistered.map { Player(
                    user = it,
                    balance = gameLobby.rules.initialPlayerBalance,
                    ownedLocations = listOf(),
                ) },
                inGameLocations = gameLobby.rules.gameMap.flatMap { it.locations.map { location ->
                    InGameLocation(
                        location = location,
                        owner = null,
                        level = LocalizationLevel.LEVEL_0,
                        bets = listOf(),
                    ) } },
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