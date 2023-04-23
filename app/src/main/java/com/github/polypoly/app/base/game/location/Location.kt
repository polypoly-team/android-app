package com.github.polypoly.app.base.game.location

import org.osmdroid.util.GeoPoint

/**
    A Zone represents a group of [Location]s, which share a common [color].
    @property localizations a list of [Location]s that belong to this zone.
    @property color the color used to represent this zone.
 */
class Zone(
    val locations: List<Location> = listOf(),
    val color: Int = 0xFFFFFFF //> hexadecimal representation
)

/**
    An enum class representing the different levels of a [Location].
 */
enum class LocalizationLevel {
    LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, LEVEL_MAX
}

/**
    A class representing a [Location], which can be owned and upgraded by a player.
    @property name the name of the [Location].
    @property basePrice the initial price of the [Location].
    @property baseTaxPrice the tax price of the [Location] when it is owned.
    @property baseMortgagePrice the mortgage price of the [Location] when it is mortgaged.
    @property latitude the latitude of the [Location] on the map.
    @property longitude the longitude of the [Location] on the map.
 */
class Location(
    val name: String = "Default",
    val basePrice: Int = 0,
    val baseTaxPrice: Int = 0,
    val baseMortgagePrice: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) {
    /**
     *   The position of the [Location] on the map.
     *   @return the [GeoPoint] representing the position of the [Location] on the map.
     */
    fun position(): GeoPoint = GeoPoint(latitude, longitude)
}