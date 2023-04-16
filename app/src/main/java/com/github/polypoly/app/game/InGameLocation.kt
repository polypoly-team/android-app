package com.github.polypoly.app.game

/**
 * A class that represent an location in a game
 * @property location The location of the in-game location, that permit to know the static info of
 * the location as the name, the base price, the base tax price, etc...
 * @property level The current level of the location that determine the price and the tax price
 * @property owner The owner of the location, if the location is not owned, the owner is null
 */
data class InGameLocation (
    var location: Location,
    var level: LocalizationLevel,
    var owner: Player?,
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
}