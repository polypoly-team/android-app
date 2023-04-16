package com.github.polypoly.app.game

import com.github.polypoly.app.game.user.User
import kotlin.time.Duration

class PlayerGlobalData (
    var hasLost: Boolean,
    var balance: Int,
) {}

class PlayerPerRoundData {
    // TODO: add specific data for round process
}

class Game private constructor(
    val admin: User,
    val players: List<Player>,
    val gameMode: GameMode,
    val gameMap: List<Zone>,
    val roundDuration: Duration,
    private val initialPlayerBalance: Int,
    val name: String,
    val dateBegin: Long,
) {

    private val playerToGlobalData: HashMap<Player, PlayerGlobalData> = HashMap()
    private val playerToPerTurnData: HashMap<Player, PlayerPerRoundData> = HashMap()
    private val locationToOwner: HashMap<Location, User> = HashMap()

    init {
        for (player in players) {
            playerToGlobalData[player] = PlayerGlobalData(false, initialPlayerBalance)
            playerToPerTurnData[player] = PlayerPerRoundData()
        }
    }

    /**
     * Test if the game is finished
     * @return true if the game is finished, false otherwise
     */
    fun isGameFinished(): Boolean {
        return when(gameMode) {
            GameMode.LAST_STANDING -> {
                // TODO : Test if the number of players alive is 1
                false
            }
            GameMode.RICHEST_PLAYER -> {
                // TODO : Test if the number of rounds is over
                false
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
     */
    fun endGame(): PastGame {
        return PastGame(
            users = players.map { it.user },
            usersRank = ranking().map { it.key.user.id to it.value }.toMap(),
            date = dateBegin,
            duration = System.currentTimeMillis() / 1000 - dateBegin,
        )
    }

    companion object {
        fun launchFromPendingGame(gameLobby: GameLobby): Game {
            return Game(
                gameLobby.admin,
                gameLobby.usersRegistered.map { Player(
                    user = it,
                    balance = gameLobby.initialPlayerBalance,
                    ownedLocations = listOf(),
                ) },
                gameLobby.gameMode,
                gameLobby.gameMap,
                gameLobby.roundDuration,
                gameLobby.initialPlayerBalance,
                gameLobby.name,
                dateBegin = System.currentTimeMillis() / 1000,
            )
        }
    }
}