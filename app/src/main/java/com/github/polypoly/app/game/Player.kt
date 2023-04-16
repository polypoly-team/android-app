package com.github.polypoly.app.game

import com.github.polypoly.app.game.user.User

/**
 * A class that represent a player in a game with his/her info which are specific to the game
 */
data class Player (
    /**
     * The user behind the player
     */
    val user: User,

    /**
     * The current balance of the money of the player
     */
    val balance: Int,
) {
    /**
     * If the player has lost the game
     * @return true if the player has lost the game, false otherwise
     */
    fun hasLose(): Boolean {
        return balance <= 0
    }

    /**
     * Collect a bonus card
     * @param bonusCard the bonus card to collect
     */
    fun collectBonusCard(bonusCard: InGameBonusCard) {
        // TODO write in the database that the bonusCard is collected
        bonusCard.bonusCard.applyBonus(this)
    }
}