package com.github.polypoly.app.game.user

/**
 * Skin of a player represented as Head/Body/Legs components ids
 */
data class Skin(
    val idHead: Int = 0,
    val idBody: Int = 0,
    val idLegs: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        return other is Skin &&
                idHead == other.idHead && idBody == other.idBody && idLegs == other.idLegs
    }
}
