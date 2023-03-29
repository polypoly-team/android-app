package com.github.polypoly.app.game.user

/**
 * Stub implementation of a User
 */
data class User(
    val id: Long,
    val name: String,
    val bio: String,
    val skin: Skin,
    val stats: Stats,
    val trophiesWon: List<Int>,
    val trophiesDisplay: MutableList<Int>,
){
    override fun toString(): String {
        return "User{$id: $name}"
    }

    fun hasTrophy(trophyId: Int): Boolean {
        return trophiesWon.contains(trophyId)
    }
}