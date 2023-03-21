package com.github.polypoly.app.map

import com.github.polypoly.app.game.Localization
import com.github.polypoly.app.game.LocalizationLevel
import org.osmdroid.util.GeoPoint

object LocalizationRepository {
    private val localizations = listOf(
        Localization(
            name = "BC",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            currentLevel = LocalizationLevel.STUDY_ROOM,
            position = GeoPoint(46.51861304708622, 6.561904544895297)
        ),
        Localization(
            name = "INM",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            currentLevel = LocalizationLevel.AMPHITHEATER,
            position = GeoPoint(46.51862967945017, 6.563195429654059)
        ),
        Localization(
            name = "INF",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            currentLevel = LocalizationLevel.ARENA,
            position = GeoPoint(46.5187569432044, 6.563754302940894)
        ),
        Localization(
            name = "INJ",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            currentLevel = LocalizationLevel.STADIUM,
            position = GeoPoint(46.5183787509451, 6.563782977860895)
        ),
        Localization(
            name = "INN",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            currentLevel = LocalizationLevel.STUDY_ROOM,
            position = GeoPoint(46.5187602318236, 6.562524865762242)
        ),
        Localization(
            name = "INR",
            basePrice = 200,
            baseTaxPrice = 20,
            baseMortgagePrice = 50,
            currentLevel = LocalizationLevel.STUDY_ROOM,
            position = GeoPoint(46.51837464014728, 6.562565488564992)
        )
    )

    fun getLocalizations(): List<Localization> = localizations
}