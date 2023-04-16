package com.github.polypoly.app.game

import com.github.polypoly.app.game.user.User

data class Player (
    val user: User,
    val balance: Int,
) {
    /**
     * If the player has lost the game
     * @return true if the player has lost the game, false otherwise
     */
    fun hasLose(): Boolean {
        return balance <= 0
    }
}