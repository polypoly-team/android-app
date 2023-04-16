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
    private var balance: Int,

    /**
     * The list of the locations owned by the player
     */
    val ownedLocations: List<InGameLocation>,
) {
    /**
     * If the player has lost the game (i.e. if the player has no more money)
     * @return true if the player has lost the game, false otherwise
     */
    fun hasLose(): Boolean {
        return balance <= 0 && ownedLocations.isEmpty()
    }

    /**
     * Collect a bonus card
     * @param bonusCard the bonus card to collect
     */
    fun collectBonusCard(bonusCard: InGameBonusCard) {
        // TODO : write in the database that the bonusCard is collected
        // TODO : show the card to the player
        bonusCard.bonusCard.applyBonus(this)
    }

    fun winMoney(amount: Int) {
        balance += amount
    }

    fun loseMoney(amount: Int) {
        if(amount > balance) {
            balance = 0
            // TODO : ask the player if he wants to sell his properties
        }
        balance -= amount
    }
}