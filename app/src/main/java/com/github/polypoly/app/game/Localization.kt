package com.github.polypoly.app.game

import org.osmdroid.util.GeoPoint

/**
    A Zone represents a group of [Localization]s, which share a common [color].
    @property localizations a list of [Localization]s that belong to this zone.
    @property color the color used to represent this zone.
 */

class Zone(
    val localizations: List<Localization>,
    val color: Int //> hexadecimal representation
)

/**
    An enum class representing the different levels of a [Localization].
 */
enum class LocalizationLevel {
    LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, LEVEL_MAX
}

/**
    A class representing a [Localization], which can be owned and upgraded by a player.
    @property name the name of the [Localization].
    @property basePrice the initial price of the [Localization].
    @property baseTaxPrice the tax price of the [Localization] when it is owned.
    @property baseMortgagePrice the mortgage price of the [Localization] when it is mortgaged.
    @property position the geographical position of the [Localization] on the map.
 */
class Localization(
    val name: String,
    val basePrice: Int,
    val baseTaxPrice: Int,
    val baseMortgagePrice: Int,
    val position: GeoPoint
)