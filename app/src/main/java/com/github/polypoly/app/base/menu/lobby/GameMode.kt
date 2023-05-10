package com.github.polypoly.app.base.menu.lobby

/**
 * Represent the different game modes
 */
enum class GameMode(val description: String, private val title: String) {
    LAST_STANDING("In this game mode, the game lasts until only one player is left standing 😎"
        , "Last Standing"),
    RICHEST_PLAYER("In this game mode, the game lasts for a fixed number of turn and the winner"
        + "is the player with the greatest net worth in the end 🤑"
        , "Richest Player"),
    LANDLORD("In this game mode, all the buildings are randomly but equally distributed between the players at the beginning of the game. " +
            "Players are passively taxed when they spend time in a building they don't own, and a trade system is available"
        , "Landlord");

    override fun toString(): String {
        return title
    }
}