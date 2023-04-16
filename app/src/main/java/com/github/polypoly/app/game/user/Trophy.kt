package com.github.polypoly.app.game.user

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A trophy that can be win during a game or at the end of a game to reward a particular
 * success of the player.
 */
data class Trophy(
    /**
     * The id of the trophy
     */
    private val id: Int,

    /**
     * The title of the trophy
     */
    private val title: String,

    /**
     * The description of the trophy
     */
    private val description: String,

    /**
     * The icon that represents the trophy
     */
    private val icon: ImageVector,
) {
    override fun toString(): String {
        return "$title: $description"
    }

    /**
     * Get the icon that represents the trophy
     */
    fun getIcon(): ImageVector {
        return icon
    }

    /**
     * Get the id of the trophy
     */
    fun getId(): Int {
        return id
    }

    companion object {
        /**
         * The list of all trophies winnable by the play
         */
        val allTrophies: List<Trophy> = listOf(
            Trophy(0, title = "trophy 0", description = "description 0", icon = Icons.Default.Terminal),
            Trophy(1, title = "trophy 1", description = "description 1", icon = Icons.Default.Person),
            Trophy(2, title = "trophy 2", description = "description 2", icon = Icons.Default.Person),
            Trophy(3, title = "trophy 3", description = "description 3", icon = Icons.Default.Person),
            Trophy(4, title = "trophy 4", description = "description 4", icon = Icons.Default.Album),
            Trophy(5, title = "trophy 5", description = "description 5", icon = Icons.Default.Person),
            Trophy(6, title = "trophy 6", description = "description 6", icon = Icons.Default.Person),
            Trophy(7, title = "trophy 7", description = "description 7", icon = Icons.Default.Person),
            Trophy(8, title = "trophy 8", description = "description 8", icon = Icons.Default.Person),
            Trophy(9, title = "trophy 9", description = "description 9", icon = Icons.Default.Person),
            Trophy(10, title = "trophy 10", description = "description 10", icon = Icons.Default.Person),
            Trophy(11, title = "trophy 11", description = "description 11", icon = Icons.Default.Person),
            Trophy(12, title = "trophy 12", description = "description 12", icon = Icons.Default.Person),
            Trophy(13, title = "trophy 13", description = "description 13", icon = Icons.Default.Person),
            Trophy(14, title = "trophy 14", description = "description 14", icon = Icons.Default.Person),
        )
    }
}