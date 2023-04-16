package com.github.polypoly.app.game

import com.github.polypoly.app.game.user.User

/**
 * A class that represent a [Player] in a [Game] with his/her info which are specific to the [Game]
 * @property user The [User] behind the [Player]
 * @property balance The current balance of the money of the [Player]
 * @property ownedLocations The list of the [Location]s owned by the [Player]
 * @property roundLost Round the [Player] had lost the [Game] if he/she has lost the [Game],
 * null otherwise
 */
data class Player (
    val user: User,
    private var balance: Int,
    val ownedLocations: List<InGameLocation>,
    val roundLost: Int? = null,
) : Comparable<Player> {

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

    /**
     * Update the balance of the player with the money earned.
     * @param amount the amount of money earned
     */
    fun earnMoney(amount: Int) {
        balance += amount
    }

    /**
     * Update the balance of the player with the money lost.
     * If the player has not enough money, he/she has to sell his properties or to lose.
     * @param amount the amount of money lost
     */
    fun loseMoney(amount: Int) {
        if(amount > balance) {
            balance = 0
            // TODO : ask the player if he wants to sell his properties
        }
        balance -= amount
    }

    override fun compareTo(other: Player): Int {
        return if (roundLost != null && other.roundLost != null) {
            roundLost.compareTo(other.roundLost)
        } else {
            balance.compareTo(other.balance)
        }
    }
}