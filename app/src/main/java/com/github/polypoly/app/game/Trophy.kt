package com.github.polypoly.app.game

/**
 * A trophy that can be win during a game or at the end of a game to reward a particular
 * success of the player.
 */
class Trophy(
    private val id: Int,
    private val title: String,
    private val description: String,
) {
    override fun toString(): String {
        return "$id"
    }
}

/**
 * The list of all trophies winnable by the play
 */
val allTrophies: List<Trophy> = listOf(
    Trophy(0, title = "trophy 0", description = "description 0"),
    Trophy(1, title = "trophy 1", description = "description 1"),
    Trophy(2, title = "trophy 2", description = "description 2"),
    Trophy(3, title = "trophy 3", description = "description 3"),
    Trophy(4, title = "trophy 4", description = "description 4"),
    Trophy(5, title = "trophy 5", description = "description 5"),
    Trophy(6, title = "trophy 6", description = "description 6"),
    Trophy(7, title = "trophy 7", description = "description 7"),
    Trophy(8, title = "trophy 8", description = "description 8"),
    Trophy(9, title = "trophy 9", description = "description 9"),
    Trophy(10, title = "trophy 10", description = "description 10"),
    Trophy(11, title = "trophy 11", description = "description 11"),
    Trophy(12, title = "trophy 12", description = "description 12"),
    Trophy(13, title = "trophy 13", description = "description 13"),
    Trophy(14, title = "trophy 14", description = "description 14"),
    Trophy(15, title = "trophy 15", description = "description 15"),
    Trophy(16, title = "trophy 16", description = "description 16"),
    Trophy(17, title = "trophy 17", description = "description 17"),
)