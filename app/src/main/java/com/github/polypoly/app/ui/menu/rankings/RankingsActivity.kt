package com.github.polypoly.app.ui.menu.rankings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.network.getAllValues
import com.github.polypoly.app.ui.menu.MenuActivity
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.utils.global.Settings.Companion.DB_USERS_PROFILES_PATH

/**
 * The rankings activity, composed of a row of rankings lists tabs
 */
class RankingsActivity : MenuActivity("Rankings") {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenuContent {
                RankingsContent()
            }
        }
    }

    /**
     * The different categories of rankings
     *
     * @property name The name of the category
     * @property criteria The criteria to use to rank the users
     */
    enum class RankingCategory(val description: String, val criteria: (User) -> Int) {
        WINS("Most Wins", { user -> user.stats.numberOfWins }),
        TROPHIES("Most Trophies", { user -> user.trophiesWon.count() }),
        GAMES("Most Games", { user -> user.stats.numberOfGames })
    }

    /**
     * The rankings view, composed of a row of rankings lists for each [RankingCategory]
     */
    @Composable
    private fun RankingsContent() {
        var users: List<User> = emptyList()

        // Retrieve all registered users from the database
        remoteDB.getAllValues<User>(DB_USERS_PROFILES_PATH).thenAccept { users = it }

        val tabs = RankingCategory.values()
        var selectedTabIndex by remember { mutableStateOf(0) }

        Column {
            // The row of tabs at the top of the screen that lists the types of rankings
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary) {
                tabs.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = category.description,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            // The column that displays the rankings list of the currently selected tab
            LazyColumn {
                items(computeRanking(users, tabs[selectedTabIndex])) {
                    Text(text = it.name)
                    Text(text = tabs[selectedTabIndex].criteria(it).toString())
                }
            }
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