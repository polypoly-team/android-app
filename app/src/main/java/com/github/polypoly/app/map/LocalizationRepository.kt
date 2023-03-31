package com.github.polypoly.app.map

import com.github.polypoly.app.game.Localization
import com.github.polypoly.app.game.LocalizationLevel
import com.github.polypoly.app.game.Zone
import org.osmdroid.util.GeoPoint

/**
 * Repository for providing the zones and localizations on the map.
 */
object LocalizationRepository {

    /**
     * List of localizations in the EPFL campus area.
     */
    private val localizations = listOf(
        Localization(
            name = "BC",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            position = GeoPoint(46.51861304708622, 6.561904544895297)
        ),
        Localization(
            name = "INM",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            position = GeoPoint(46.51862967945017, 6.563195429654059)
        ),
        Localization(
            name = "INF",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            position = GeoPoint(46.5187569432044, 6.563754302940894)
        ),
        Localization(
            name = "INJ",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            position = GeoPoint(46.5183787509451, 6.563782977860895)
        ),
        Localization(
            name = "INN",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            position = GeoPoint(46.5187602318236, 6.562524865762242)
        ),
        Localization(
            name = "INR",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            position = GeoPoint(46.51837464014728, 6.562565488564992)
        )
    )

    /**
     * List of zones in the EPFL campus area. Each zone has a color associated with it.
     */
    private val zones = listOf(
        Zone(
            localizations = listOf(
                localizations[3],
                localizations[4],
                localizations[5]
            ),
            color = 0xFF00FF00.toInt()
        ),
        Zone(
            localizations = listOf(
                localizations[0],
                localizations[1],
                localizations[2],

            ),
            color = 0xFF0000FF.toInt()
        ),
    )

    /**
     * Returns the list of localizations.
     */
    fun getZones(): List<Zone> = zones
}