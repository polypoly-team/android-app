package com.github.polypoly.app.ui.menu.rankings

import com.github.polypoly.app.base.user.User

/**
 * The different categories of rankings
 *
 * @property description The name of the category
 * @property criteria The criteria to use to rank the users
 */
enum class RankingCategory(val description: String, val criteria: (User) -> Int) {
    WINS("Most Wins", { user -> user.stats.numberOfWins }),
    TROPHIES("Most Trophies", { user -> user.trophiesWon.count() }),
    GAMES("Most Games", { user -> user.stats.numberOfGames })
}

