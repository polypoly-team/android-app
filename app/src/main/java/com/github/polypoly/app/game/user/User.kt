package com.github.polypoly.app.game.user

/**
 * Stub implementation of a User
 */
data class User(
    val id: Long = 0,
    val name: String = "no-name",
    val bio: String = "no-bio",
    val skin: Skin = Skin(),
    val stats: Stats = Stats(),
    val trophiesWon: List<Int> = listOf(),
    val trophiesDisplay: MutableList<Int> = mutableListOf()
){
    override fun toString(): String {
        return "User{$id: $name}"
    }

    fun hasTrophy(trophyId: Int): Boolean {
        return trophiesWon.contains(trophyId)
    }

    override fun equals(other: Any?): Boolean {
        return other is User &&
                id == other.id && name == other.name && bio == other.bio &&
                skin == other.skin && stats == other.stats
    }
}