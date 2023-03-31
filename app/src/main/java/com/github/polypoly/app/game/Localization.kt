package com.github.polypoly.app.game

class Zone(
    val localizations: List<Localization> = listOf(),
    val color: Int = 0 //> hexadecimal representation
) {
    override fun equals(other: Any?): Boolean {
        return other is Zone &&
                (localizations.all { localization -> other.localizations.contains(localization) } &&
                        localizations.size == other.localizations.size) && color == other.color
    }
}

enum class LocalizationLevel {
    EMPTY, STUDY_ROOM, AMPHITHEATER, ARENA, STADIUM
}

class Localization(
    val name: String = "",
    val basePrice: Int = 0,
    val baseTaxPrice: Int = 0,
    val baseMortgagePrice: Int = 0,
    var currentLevel: LocalizationLevel = LocalizationLevel.values()[0]
) {
    override fun equals(other: Any?): Boolean {
        return other is Localization &&
                name == other.name && basePrice == other.basePrice && baseTaxPrice == other.baseTaxPrice &&
                baseMortgagePrice == other.baseMortgagePrice && currentLevel == other.currentLevel
    }
}