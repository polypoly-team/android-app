package com.github.polypoly.app.ui.menu.rankings

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.database.getAllValues
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB

/**
 * The view model of the rankings activity
 *
 * @see RankingsActivity
 */
class RankingsViewModel: ViewModel() {
    /**
     * The list of users to rank
     */
    private var users: List<User> = emptyList()

    /**
     * The rankings of the users according to the different categories
     *
     * @see RankingCategory
     */
    private val rankings: Map<RankingCategory, SnapshotStateList<User>> = enumValues<RankingCategory>()
        .associateWith { SnapshotStateList() }

    /**
     * Fetches the users from the database and recomputes the rankings
     */
    fun fetchUsers() {
        // Request all registered users from the database
        // TODO: Update this call on the DB operations are encapsulated
        remoteDB.getAllValues<User>().thenAccept {
            users = it
            // Recompute the users' rankings on reply
            computeRankings()
        }
    }

    /**
     * Gets the ranking of the given [RankingCategory]
     *
     * @param category The category to get the ranking of
     *
     * @return The ranking of the given category
     *
     * @throws IllegalArgumentException If the given category is invalid
     */
    fun getRanking(category: RankingCategory): SnapshotStateList<User> {
        return rankings[category] ?: error("Invalid ranking category $category")
    }

    /**
     * Computes the rankings of the users according to the different categories
     */
    private fun computeRankings() {
        rankings.forEach { (category, ranking) ->
            ranking.clear()
            ranking.addAll(computeRanking(users, category))
        }
    }

    /**
     * Computes the ranking of the given users according to the given category
     *
     * @param users The users to rank
     * @param category The category to use to rank the users
     *
     * @return The list of users, ranked according to the given category
     */
    private fun computeRanking(users: List<User>, category: RankingCategory): List<User> {
        return users.sortedByDescending { user -> category.criteria(user) }
    }
}