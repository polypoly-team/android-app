package com.github.polypoly.app.game

import org.osmdroid.util.GeoPoint

/**
 * A class that represent an bonus card in a game
 */
data class InGameBonusCard (
    /**
     * The bonus card of the in-game bonus card that permit to know the static info of the bonus card
     * as the title, the description, etc...
     */
    var bonusCard: BonusCard,

    /**
     * The position of the bonus card on the map
     */
    var position: GeoPoint,
)