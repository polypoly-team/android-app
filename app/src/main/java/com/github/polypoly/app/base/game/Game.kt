package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.PastGame
import com.github.polypoly.app.base.game.rules_and_lobby.GameLobby
import com.github.polypoly.app.base.game.rules_and_lobby.GameMode
import com.github.polypoly.app.base.game.rules_and_lobby.GameRules
import com.github.polypoly.app.base.user.User

/**
 * Represent the game and the current state of the game
 * @property admin the [User] who is the admin of the game
 * @property players the [Player]s of the game
 * @property rules the rules of the [Game]
 */
class Game private constructor(
    val admin: User,
    private var players: List<Player>,
    val rules: GameRules,
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
     * @return a list of players sorted by their ranking
     */
    fun ranking(): Map<Player, Int> {
        val map = players.sorted().mapIndexed { index, player -> player to index }.toMap().toMutableMap()
        for(i in 1 until (players.size-1)) {
            if(players[i].compareTo(players[i-1]) == 0) {
                map[players[i-1]]?.let{ map[players[i]] = it }
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
            usersRank = ranking().map { it.key.user.id to it.value }.toMap(),
            date = dateBegin,
            duration = System.currentTimeMillis() / 1000 - dateBegin,
        )
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