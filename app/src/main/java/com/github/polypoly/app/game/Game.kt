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
    val players: List<User>,
    val gameMode: GameMode,
    val gameMap: List<Zone>,
    val roundDuration: Duration,
    private val initialPlayerBalance: Int,
    val name: String,
    val dateBegin: Long,
) {

    private val playerToGlobalData: HashMap<User, PlayerGlobalData> = HashMap()
    private val playerToPerTurnData: HashMap<User, PlayerPerRoundData> = HashMap()
    private val locationToOwner: HashMap<Location, User> = HashMap()

    init {
        for (player in players) {
            playerToGlobalData[player] = PlayerGlobalData(false, initialPlayerBalance)
            playerToPerTurnData[player] = PlayerPerRoundData()
        }
    }

    fun endGame(): PastGame {
        return PastGame(
            users = players,
            usersRank = mapOf(), // TODO: add the real rank
            date = dateBegin,
            duration = System.currentTimeMillis() / 1000 - dateBegin,
        )
    }

    companion object {
        fun launchFromPendingGame(gameLobby: GameLobby): Game {
            return Game(
                gameLobby.admin,
                gameLobby.usersRegistered,
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