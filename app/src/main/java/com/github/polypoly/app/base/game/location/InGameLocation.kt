package com.github.polypoly.app.base.game.location

import com.github.polypoly.app.base.game.Player

/**
 * A class that represent an location in a game
 * @property locationProperty The location of the in-game location, that permit to know the static info of
 * the location as the name, the base price, the base tax price, etc...
 * @property level The current level of the location that determine the price and the tax price
 * @property owner The owner of the location, if the location is not owned, the owner is null
 * @property bids The list of the bids made on the location by the players in the previous round
 */
data class InGameLocation (
    var locationProperty: LocationProperty,
    var level: LocalizationLevel = LocalizationLevel.LEVEL_0,
    var owner: Player? = null,
) {

    private val bids: List<LocationBid> = listOf()

    /**
     * Tell if the location is owned by the player
     * @param player the player to check
     * @return true if the location is owned by the player, false otherwise
     */
    fun isTheOwner(player: Player): Boolean {
        return owner?.user?.id == player.user.id
    }

    /**
     * Compute the current tax of the location
     * @return the current tax of the location
     */
    fun currentTax(): Int {
        return locationProperty.baseTaxPrice * (level.ordinal+1)
    }

    /**
     * Compute the current price of the location
     * @return the current price of the location
     */
    fun currentPrice(): Int {
        return locationProperty.basePrice * (level.ordinal+1)
    }

    /**
     * Compute the current mortgage price of the location
     * @return the current mortgage price of the location
     */
    fun currentMortgagePrice(): Int {
        return locationProperty.baseMortgagePrice * (level.ordinal + 1)
    }

    /**
     * Compute the winning bid and set the new owner of the location
     * @return the winning bid, null if there is no bet on the location
     * @throws IllegalStateException if the location is already owned by a player
     */
    fun computeWinningBid(): LocationBid? {
        if(owner != null)
            throw IllegalStateException("The location is already owned by a player")
        if(bids.isEmpty())
            return null
        val maxAmount = bids.maxOf { it.amount }
        // case where two players have bet the same amount
        val betsSameAmount = bids.filter { it.amount == maxAmount }
        val maxRandomNumber = betsSameAmount.maxOf { it.randomNumber }
        // case where two players have bet the same amount and have the same random number
        val betsSameAmountRdNumber = betsSameAmount.filter { it.randomNumber == maxRandomNumber }
        val minTimeOfTheBet = betsSameAmountRdNumber.minOf { it.timeOfTheBid }
        // case where two players have bet the same amount, have the same random number and have bet in the same time
        val betsSameAmountRdNumberAndTime = betsSameAmountRdNumber
            .filter { it.timeOfTheBid == minTimeOfTheBet}
        val winningBet = betsSameAmountRdNumberAndTime.minBy { it.player.user.id }
        owner = winningBet.player
        return winningBet
    }
}