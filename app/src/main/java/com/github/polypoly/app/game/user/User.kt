package com.github.polypoly.app.game.user

/**
 * Stub implementation of a User
 */
data class User(
    /**
     * The id of the user, must be unique for each user
     */
    val id: Long = 0,

    /**
     * The name of the user (can be personalized by the user)
     */
    val name: String = "",

    /**
     * The bio of the user (can be personalized by the user)
     */
    val bio: String = "",

    /**
     * The skin of the user (can be personalized by the user)
     */
    val skin: Skin = Skin(),

    /**
     * The statistics about the user
     */
    val stats: Stats = Stats(),

    /**
     * The list of the trophies won by the user
     */
    val trophiesWon: List<Int> = listOf(),

    /**
     * The list of the trophies the user wants to display
     */
    val trophiesDisplay: MutableList<Int> = mutableListOf(),
){
    override fun toString(): String {
        return "User{$id: $name}"
    }

    /**
     * Determine if the user has won the trophy
     * @param trophyId the id of the trophy
     * @return true if the user has won the trophy, false otherwise
     */
    fun hasTrophy(trophyId: Int): Boolean {
        return trophiesWon.contains(trophyId)
    }
}