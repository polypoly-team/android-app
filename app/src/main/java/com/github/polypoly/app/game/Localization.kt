package com.github.polypoly.app.game

class Zone(
    val localizations: List<Localization>,
    val color: Int //> hexadecimal representation
){}

enum class LocalizationLevel {
    EMPTY, STUDY_ROOM, AMPHITHEATER, ARENA, STADIUM
}

class Localization(
    val name: String,
    val basePrice: Int,
    val baseTaxPrice: Int,
    val baseMortgagePrice: Int,
    var currentLevel: LocalizationLevel
) {

}