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
        return roundLost != null
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
     * @param amount the amount of money earned*
     * @throws IllegalArgumentException if the amount of money earned is negative
     * @throws IllegalStateException if there is no game in progress
     * @throws IllegalStateException if the player is not in the game currently in progress
     * @throws IllegalStateException if the player has already lost the game
     */
    fun earnMoney(amount: Int) {
        if(amount <= 0)
            throw IllegalArgumentException("The amount of money earned cannot be negative or zero")
        if(Game.gameInProgress == null)
            throw IllegalStateException("There are no game in progress")
        if(Game.gameInProgress?.playInThisGame(user) == false)
            throw IllegalStateException("The player is not in the game currently in progress")
        if(roundLost != null)
            throw IllegalStateException("The player has already lost the game")
        balance += amount
    }

    /**
     * Update the balance of the player with the money lost.
     * If the player has not enough money, he/she has to sell his properties or to lose.
     * @param amount the amount of money lost
     * @throws IllegalArgumentException if the amount of money lost is negative
     * @throws IllegalStateException if there is no game in progress
     * @throws IllegalStateException if the player is not in the game currently in progress
     * @throws IllegalStateException if the player has already lost the game
     */
    fun loseMoney(amount: Int) {
        if(amount <= 0)
            throw IllegalArgumentException("The amount of money lost cannot be negative or zero")
        if(Game.gameInProgress == null)
            throw IllegalStateException("There are no game in progress")
        if(Game.gameInProgress?.playInThisGame(user) == false)
            throw IllegalStateException("The player is not in the game currently in progress")
        if(roundLost != null)
            throw IllegalStateException("The player has already lost the game")
        if(amount > balance) {
            balance = 0
            if(ownedLocations.isEmpty()) {
                roundLost = Game.gameInProgress?.currentRound
                // TODO : tell the player that he lose the game
            } else {
                // TODO : ask the player if he wants to sell one of his/her properties
            }
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
     * The player bet to buy a location
     * @param location the location the player wants to buy
     * @param amount the amount of money the player wants to bet
     * @return the [LocationBet] created
     * @throws IllegalStateException if there is no game in progress
     * @throws IllegalStateException if the player is not in the game currently in progress
     * @throws IllegalArgumentException if the location is already owned by someone
     * @throws IllegalArgumentException if the amount of money bet is negative or zero
     * @throws IllegalStateException if the player has already lost the game
     * @throws IllegalArgumentException if the player has not enough money to buy the location
     */
    fun betToBuy(location: InGameLocation, amount: Int) : LocationBet {
        if(Game.gameInProgress == null)
            throw IllegalStateException("There are no game in progress")
        if(Game.gameInProgress?.playInThisGame(user) == false)
            throw IllegalStateException("The player is not in the game currently in progress")
        if(location.owner != null)
            throw IllegalArgumentException("The location is already owned by someone")
        if(amount <= 0)
            throw IllegalArgumentException("The amount of money bet cannot be negative or zero")
        if(roundLost != null)
            throw IllegalStateException("The player has already lost the game")
        if(location.currentPrice() > balance)
            throw IllegalArgumentException("The player has not bet enough money to buy the location")
        return LocationBet(this, amount, Random.nextFloat(), System.currentTimeMillis() / 1000)
        // TODO : add in the DB the bet
    }

    /**
     * Update the state of the player with all the new data
     * @param newBalance the new balance of the player
     * @param newOwnedLocations the new list of the owned locations of the player
     * @param newRoundLost the new round when the player had lost the game if the player has lost the game,
     * @throws IllegalArgumentException if the player has lost the game and the balance is not equal to 0
     * @throws IllegalArgumentException if the player has lost the game and the player owns locations
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