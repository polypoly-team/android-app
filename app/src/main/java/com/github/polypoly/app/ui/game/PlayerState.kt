package com.github.polypoly.app.ui.game

enum class PlayerState {
    INIT, // initial state, before the game starts
    ROLLING_DICE,
    MOVING,
    INTERACTING,
    BETTING,
    TRADING,
    TURN_FINISHED // mainly used by game to force the players to be ready for the next turn
}