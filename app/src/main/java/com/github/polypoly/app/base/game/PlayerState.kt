package com.github.polypoly.app.base.game

enum class PlayerState {
    INIT, // initial state, before the game starts
    ROLLING_DICE,
    MOVING,
    INTERACTING,
    BIDDING,
    TRADING,
    TURN_FINISHED // mainly used by game to force the players to be ready for the next turn
}