package com.github.polypoly.app.game.user

/**
 * Stub implementation of a User
 */
data class User(
    val id: Long = 0,
    val name: String = "",
    val bio: String = "",
    val skin: Skin = Skin(),
    val stats: Stats = Stats(),
    val trophiesWon: List<Int> = listOf(),
    val trophiesDisplay: MutableList<Int> = mutableListOf(),
){
    override fun toString(): String {
        return "User{$id: $name}"
    }

    fun hasTrophy(trophyId: Int): Boolean {
        return trophiesWon.contains(trophyId)
    }
}