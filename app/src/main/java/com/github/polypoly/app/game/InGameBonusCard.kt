package com.github.polypoly.app.game

import org.osmdroid.util.GeoPoint

/**
 * A class that represent an bonus card in a game
 * @property bonusCard The bonus card of the in-game bonus card that permit to know the static info
 * of the bonus card as the title, the description, etc...
 * @property position The position of the bonus card on the map
 */
data class InGameBonusCard (
    var bonusCard: BonusCard,
    var position: GeoPoint,
)