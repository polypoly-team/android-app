package com.github.polypoly.app.data

/**
 * Static repository object storing data held across the entire app
 * Initial version of repository pattern
 * @see https://developer.android.com/codelabs/basic-android-kotlin-training-repository-pattern#0
 */
class GameRepository {
    companion object {
        /**
         * Singleton object storing the game code used across the application
         */
        var gameCode: String? = null
    }
}