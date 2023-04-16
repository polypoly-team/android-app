package com.github.polypoly.app.game

import com.github.polypoly.app.game.user.User
import kotlin.time.Duration

class Game private constructor(
    val admin: User,
    val players: List<Player>,
    val gameMode: GameMode,
    val gameMap: List<Zone>,
    val roundDuration: Duration,
    val maxRound: Int? = null,
    private val initialPlayerBalance: Int,
    val currentRound: Int = 1,
    val name: String,
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
        return when(gameMode) {
            GameMode.LAST_STANDING -> {
                players.filter { !it.hasLose() }.size <= 1
            }
            GameMode.RICHEST_PLAYER -> {
                if(maxRound == null)
                    throw IllegalStateException("maxRound can't be null in RICHEST_PLAYER game mode")
                currentRound > maxRound
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
            return Game(
                admin = gameLobby.admin,
                players = gameLobby.usersRegistered.map { Player(
                    user = it,
                    balance = gameLobby.initialPlayerBalance,
                    ownedLocations = listOf(),
                ) },
                gameMode = gameLobby.gameMode,
                gameMap = gameLobby.gameMap,
                roundDuration = gameLobby.roundDuration,
                initialPlayerBalance = gameLobby.initialPlayerBalance,
                maxRound = gameLobby.maxRound,
                name = gameLobby.name,
                dateBegin = System.currentTimeMillis() / 1000,
            )
        }
    }
}