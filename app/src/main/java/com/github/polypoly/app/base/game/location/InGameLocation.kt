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
    var locationProperty: LocationProperty = LocationProperty(),
    var level: PropertyLevel = PropertyLevel.LEVEL_0,
    var owner: Player? = null
) {
    /**
     * Tell if the location is owned by the player
     * @param player the player to check
     * @return true if the location is owned by the player, false otherwise
     */
    fun isTheOwner(player: Player?): Boolean {
        return player != null && owner?.user?.id == player.user.id
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

    fun upgrade() {
        level = when(level) {
            PropertyLevel.LEVEL_0 -> PropertyLevel.LEVEL_1
            PropertyLevel.LEVEL_1 -> PropertyLevel.LEVEL_2
            PropertyLevel.LEVEL_2 -> PropertyLevel.LEVEL_MAX
            PropertyLevel.LEVEL_MAX -> PropertyLevel.LEVEL_MAX
        }
    }
}