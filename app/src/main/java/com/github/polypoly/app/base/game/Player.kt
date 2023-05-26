package com.github.polypoly.app.base.game

import com.github.polypoly.app.base.game.bonus_card.InGameBonusCard
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.data.GameRepository

/**
 * A class that represent a [Player] in a [Game] with his/her info which are specific to the [Game]
 * @property user The [User] behind the [Player]
 * @property balance The current balance of the money of the [Player]
 * @property ownedLocations The list of the [InGameLocation]s owned by the [Player]
 * @property roundLost Round the [Player] had lost the [Game] if he/she has lost the [Game],
 * null otherwise
 */
data class Player (
    val user: User = User(),
    private var balance: Int = 0,
    private var roundLost: Int? = null,
    private var lastMilestone : Int = 0
) : Comparable<Player> {

    /**
     * If the player has lost the game (i.e. if the player has no more money)
     * @return true if the player has lost the game, false otherwise
     */
    fun hasLost(): Boolean {
        return roundLost != null
    }

    /**
     * Collect a bonus card
     * @param bonusCard the bonus card to collect
     */
    fun collectBonusCard(bonusCard: InGameBonusCard) {
        // TODO : write in the database that the bonusCard is collected
        // TODO : show the card to the player
        bonusCard.bonusCard.effect(this)
    }

    /**
     * Update the balance of the player with the money earned.
     * @param amount the amount of money earned*
     * @throws IllegalArgumentException if the amount of money earned is negative
     * @throws IllegalStateException if the player has already lost the game
     */
    fun earnMoney(amount: Int) {
        if(amount <= 0)
            throw IllegalArgumentException("The amount of money earned cannot be negative or zero")
        if(roundLost != null)
            throw IllegalStateException("The player has already lost the game")
        balance += amount
    }

    /**
     * Update the balance of the player with the money lost.
     * If the player has not enough money, he/she has to sell his properties or to lose.
     * @param amount the amount of money lost
     * @throws IllegalArgumentException if the amount of money lost is negative
     * @throws IllegalStateException if the player has already lost the game
     */
    fun loseMoney(amount: Int) {
        if(amount <= 0)
            throw IllegalArgumentException("The amount of money lost cannot be negative or zero")
        if(roundLost != null)
            throw IllegalStateException("The player has already lost the game")
        if(amount > balance) {
            balance = 0
            roundLost = GameRepository.game?.currentRound
        } else {
            balance -= amount
        }
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

    /**
     * The player bid to buy a location
     * @param location the location the player wants to buy
     * @param amount the amount of money the player wants to bid
     * @return true iff the player can buy this location with this amount
     */
    fun canBuy(location: LocationProperty, amount: Int) : Boolean {
        return amount in 1..balance && roundLost == null && amount >= location.basePrice
    }

    /**
     * Earn a new location
     * @param location the location the player has earned
     * @throws IllegalArgumentException if the location is already owned by someone
     */
    fun earnNewLocation(location: InGameLocation) {
        if(location.owner != null)
            throw IllegalArgumentException("The location is already owned by ${location.owner}")
        location.owner = this
    }

    /**
     * Earn a new locations
     * @param location the list of locations the player has earned
     */
    fun earnNewLocations(location: List<InGameLocation>) {
        for (loc in location) {
            earnNewLocation(loc)
        }
    }

    /**
     * Loose a location
     * @param location the location the player has lost
     */
    fun looseLocation(location: InGameLocation) {
        if (location.owner != this)
            throw IllegalArgumentException("The location is not owned by $this but by ${location.owner ?: "null"}")
        location.owner = null
    }

    /**
     * give the last milestone reached by the player
     */
    fun getLastMilestone() : Int {
        return lastMilestone
    }

    /**
     * increment the last milestone reached by the player
     */
    fun incrementLastMilestone() {
        lastMilestone++
    }

    /**
     * Compare the player with another player.
     * Comparing two players that have not lost the game is based on the balance of the player.
     * Comparing two players that have lost the game is based on the round when the player had
     * lost the game.
     * A player that has lost the game is always inferior to a player that has not lost the game.
     */
    override fun compareTo(other: Player): Int {
        return if (roundLost != null && other.roundLost != null) {
            roundLost!!.compareTo(other.roundLost!!)
        } else {
            if(roundLost != null)
                return -1
            if(other.roundLost != null)
                return 1
            balance.compareTo(other.balance)
        }
    }
}