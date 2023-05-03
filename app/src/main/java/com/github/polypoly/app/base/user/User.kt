package com.github.polypoly.app.base.user

/**
 * Implementation of a User
 * @property id The id of the user, must be unique for each user
 * @property name The name of the user (can be personalized by the user)
 * @property bio The bio of the user (can be personalized by the user)
 * @property skin The skin of the user (can be personalized by the user)
 * @property stats The statistics about the user
 * @property trophiesWon The list of the trophies won by the user
 * @property trophiesDisplay The list of the trophies the user wants to display
 * @property currentUser If the user is the user currently logged in
 */
data class User(
    val id: Long = 0,
    val name: String = "default",
    val bio: String = "",
    val skin: Skin = Skin(),
    val stats: Stats = Stats(),
    val trophiesWon: List<Int> = listOf(),
    val trophiesDisplay: MutableList<Int> = mutableListOf(),
    val currentUser: Boolean = false,
) {
    override fun toString(): String {
        return "User{$id: $name}"
    }

    /**
     * Determine if the [User] has won the [Trophy]
     * @param trophyId the id of the [Trophy]
     * @return true if the [User] has won the [Trophy], false otherwise
     */
    fun hasTrophy(trophyId: Int): Boolean {
        return trophiesWon.contains(trophyId)
    }
}