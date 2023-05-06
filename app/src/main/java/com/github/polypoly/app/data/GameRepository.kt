package com.github.polypoly.app.data

import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player

/**
 * Static singleton repository object storing data held across the entire app
 * Initial version of repository pattern
 * @see https://developer.android.com/codelabs/basic-android-kotlin-training-repository-pattern#0
 */
class GameRepository {
    companion object {
        /**
         * Game code used across the application
         */
        var gameCode: String? = null

        /**
         * Game used accross the application
         */
        var game: Game? = null

        /**
         * Player logged-in across the application
         */
        var player: Player? = null
    }
}