package com.github.polypoly.app.game

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
    val name: String
) {

    private val playerToGlobalData: HashMap<User, PlayerGlobalData> = HashMap()
    private val playerToPerTurnData: HashMap<User, PlayerPerRoundData> = HashMap()
    private val localizationToOwner: HashMap<Localization, User> = HashMap()

    init {
        for (player in players) {
            playerToGlobalData[player] = PlayerGlobalData(false, initialPlayerBalance)
            playerToPerTurnData[player] = PlayerPerRoundData()
        }
    }

    companion object {
        fun launchFromPendingGame(pendingGame: PendingGame): Game {
            return Game(
                pendingGame.admin,
                pendingGame.usersRegistered,
                pendingGame.gameMode,
                pendingGame.gameMap,
                pendingGame.roundDuration,
                pendingGame.initialPlayerBalance,
                pendingGame.name
            )
        }
    }
}