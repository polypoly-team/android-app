package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.bonus_card.InGameBonusCard
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationBet
import com.github.polypoly.app.base.user.User
import kotlin.random.Random

/**
 * A class that represent a [Player] in a [Game] with his/her info which are specific to the [Game]
 * @property user The [User] behind the [Player]
 * @property balance The current balance of the money of the [Player]
 * @property ownedLocations The list of the [InGameLocation]s owned by the [Player]
 * @property roundLost Round the [Player] had lost the [Game] if he/she has lost the [Game],
 * null otherwise
 */
data class Player (
    val user: User,
    private var balance: Int,
    private var ownedLocations: List<InGameLocation>,
    private var roundLost: Int? = null,
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
     * @throws IllegalStateException if there is no game in progress
     */
    fun loseMoney(amount: Int) {
        if(Game.gameInProgress == null)
            throw IllegalStateException("There are no game in progress")
        if(Game.gameInProgress?.playInThisGame(user) == false)
            throw IllegalStateException("The player is not in the game currently in progress")
        if(amount > balance) {
            balance = 0
            if(ownedLocations.isEmpty()) {
                roundLost = Game.gameInProgress?.currentRound
                // TODO : tell the player that he lose the game
            } else {
                // TODO : ask the player if he wants to sell one of his/her properties
            }
        }
        balance -= amount
    }

    /**
     * Get the current balance of the [Player]
     * @return the balance
     */
    fun getBalance(): Int {
        return balance
    }

    /**
     * Get the index of the round when the user had lost the game if the player had lost the game.
     * @return the round when the user had lost the game or null if the player had not lost.
     */
    fun getRoundLost(): Int? {
        return roundLost
    }

    fun betToBuy(location: InGameLocation, amount: Int) {
        if(Game.gameInProgress == null)
            throw IllegalStateException("There are no game in progress")
        if(Game.gameInProgress?.playInThisGame(user) == false)
            throw IllegalStateException("The player is not in the game currently in progress")
        if(location.owner != null)
            throw IllegalArgumentException("The location is already owned by someone")
        if(location.currentPrice() > balance)
            throw IllegalArgumentException("The player has not enough money to buy the location")
        LocationBet(this, amount, Random.nextFloat(), System.currentTimeMillis() / 1000)
        // TODO : add in the DB the bet
    }

    /**
     * Update the state of the player with all the new data
     * @param newBalance
     * @param newOwnedLocations
     * @param newRoundLost
     */
    fun updateState(newBalance: Int, newOwnedLocations: List<InGameLocation>, newRoundLost: Int?) {
        if(balance > 0 && roundLost != null)
            throw IllegalArgumentException("If the player has lost the game, the balance of the player must be equal to 0")
        if(ownedLocations.isNotEmpty() && roundLost != null)
            throw IllegalArgumentException("If the player has lost the game, the player can't owned locations")
        balance = newBalance
        ownedLocations = newOwnedLocations
        roundLost = newRoundLost
    }

    override fun compareTo(other: Player): Int {
        return if (roundLost != null && other.roundLost != null) {
            roundLost!!.compareTo(other.roundLost!!)
        } else {
            balance.compareTo(other.balance)
        }
    }
}