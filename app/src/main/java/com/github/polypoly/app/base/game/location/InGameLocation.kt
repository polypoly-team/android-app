package com.github.polypoly.app.base.game.location

import com.github.polypoly.app.base.game.Player

/**
 * A class that represent an location in a game
 * @property location The location of the in-game location, that permit to know the static info of
 * the location as the name, the base price, the base tax price, etc...
 * @property level The current level of the location that determine the price and the tax price
 * @property owner The owner of the location, if the location is not owned, the owner is null
 * @property bets The list of the bets made on the location by the players in the previous round
 */
data class InGameLocation (
    var location: Location,
    var level: LocalizationLevel = LocalizationLevel.LEVEL_0,
    var owner: Player? = null,
    val bets: List<LocationBet> = listOf()
) {

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
        return location.baseTaxPrice * (level.ordinal+1)
    }

    /**
     * Compute the current price of the location
     * @return the current price of the location
     */
    fun currentPrice(): Int {
        return location.basePrice * (level.ordinal+1)
    }

    /**
     * Compute the current mortgage price of the location
     * @return the current mortgage price of the location
     */
    fun currentMortgagePrice(): Int {
        return location.baseMortgagePrice * (level.ordinal + 1)
    }

    /**
     * Compute the winning bet and set the new owner of the location
     * @return the winning bet, null if there is no bet on the location
     * @throws IllegalStateException if the location is already owned by a player
     */
    fun computeWinningBet(): LocationBet? {
        if(owner != null)
            throw IllegalStateException("The location is already owned by a player")
        if(bets.isEmpty())
            return null
        val maxAmount = bets.maxOf { it.amount }
        // case where two players have bet the same amount
        val allBetsWithMaxAmount = bets.filter { it.amount == maxAmount }
        val maxRandomNumber = allBetsWithMaxAmount.maxOf { it.randomNumber }
        // case where two players have bet the same amount and have the same random number
        val allBetsWithMaxAmountAndSameRdNb = allBetsWithMaxAmount.filter { it.randomNumber == maxRandomNumber }
        val minTimeOfTheBet = allBetsWithMaxAmountAndSameRdNb.minOf { it.timeOfTheBet }
        // case where two players have bet the same amount, have the same random number and have bet in the same time
        val allBetsWithMaxAmountAndSameRdNbAndSameTime = allBetsWithMaxAmountAndSameRdNb
            .filter { it.timeOfTheBet == minTimeOfTheBet}
        val winningBet = allBetsWithMaxAmountAndSameRdNbAndSameTime.minBy { it.player.user.id }
        owner = winningBet.player
        return winningBet
    }
}