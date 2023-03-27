package com.github.polypoly.app.game

enum class GameMode(val description: String, private val title: String) {
    LAST_STANDING("In this game mode, the game lasts until only one player is left standing 😎", "Last Standing"),
    RICHEST_PLAYER("In this game mode, the game lasts for a fixed number of turn and the winner"
        + "is the player with the greatest net worth in the end 🤑", "Richest Player");

    override fun toString(): String {
        return title
    }


}