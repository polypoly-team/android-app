package com.github.polypoly.app.base.game.location

import android.location.Location
import org.osmdroid.util.GeoPoint

/**
    A Zone represents a group of [LocationProperty]s, which share a common [color].
    @property localizations a list of [LocationProperty]s that belong to this zone.
    @property color the color used to represent this zone.
 */
data class Zone(
    val locationProperties: List<LocationProperty> = listOf(),
    val color: Int = 0xFFFFFFF //> hexadecimal representation
)

/**
    An enum class representing the different levels of a [LocationProperty].
 */
enum class PropertyLevel {
    LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3, LEVEL_MAX
}

/**
    A class representing a [LocationProperty], which can be owned and upgraded by a player.
    @property name the name of the [LocationProperty].
    @property basePrice the initial price of the [LocationProperty].
    @property baseTaxPrice the tax price of the [LocationProperty] when it is owned.
    @property baseMortgagePrice the mortgage price of the [LocationProperty] when it is mortgaged.
    @property latitude the latitude of the [LocationProperty] on the map.
    @property longitude the longitude of the [LocationProperty] on the map.
 */
data class LocationProperty(
    val name: String = "Default",
    val basePrice: Int = 0,
    val baseTaxPrice: Int = 0,
    val baseMortgagePrice: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = "",
    val positivePoint: String = "",
    val negativePoint: String = "",
) {
    /**
     *   The position of the [LocationProperty] on the map.
     *   @return the [GeoPoint] representing the position of the [LocationProperty] on the map.
     */
    fun position(): GeoPoint = GeoPoint(latitude, longitude)

    /**
     *  The distance between the [LocationProperty] and a [Location].
     *
     *  @param location the [Location] to compare with.
     *
     *  @return the distance between the [LocationProperty] and the [Location].
     */
    fun distanceTo(location: Location): Float {
        val locationPropertyLocation = Location("")
        locationPropertyLocation.latitude = latitude
        locationPropertyLocation.longitude = longitude
        return location.distanceTo(locationPropertyLocation)
    }
}