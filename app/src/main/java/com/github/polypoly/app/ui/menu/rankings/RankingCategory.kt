package com.github.polypoly.app.ui.menu.rankings

import com.github.polypoly.app.R
import com.github.polypoly.app.base.user.User

/**
 * The different categories of rankings
 *
 * @property description The name of the category
 * @property criteria The criteria to use to rank the users
 */
enum class RankingCategory(val description: Int, val criteria: (User) -> Int) {
    WINS(R.string.rankings_ranking_category_wins, { user -> user.stats.numberOfWins }),
    TROPHIES(R.string.rankings_ranking_category_trophies, { user -> user.trophiesWon.count() }),
    GAMES(R.string.rankings_ranking_category_games, { user -> user.stats.numberOfGames })
}

