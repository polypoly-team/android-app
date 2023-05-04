package com.github.polypoly.app.ui.menu.rankings

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.R
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.ui.menu.MenuActivity
import com.github.polypoly.app.ui.theme.CustomTabRow
import com.github.polypoly.app.ui.theme.Padding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

/**
 * The maximum number of entries to display in a ranking list
 */
private const val maxRankingEntries: Int = 100

/**
 * The rankings activity, displaying a row of rankings lists tabs of [User]s according to
 * different [RankingCategory]s
 */
class RankingsActivity : MenuActivity("Rankings") {
    /**
     * The [RankingsViewModel] used to fetch the users from the database and compute the rankings
     */
    private val viewModel: RankingsViewModel = RankingsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fetch the users from the database
        viewModel.fetchUsers()

        setContent {
            MenuContent {
                RankingsContent()
            }
        }
    }

    /**
     * The rankings view, composed of a row of rankings lists for each [RankingCategory]
     */
    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun RankingsContent() {
        // There's a tab for each ranking category
        val tabs = RankingCategory.values()
        val state = rememberPagerState()

        Box(Modifier.fillMaxSize()) {
            // The background of the rankings view, displaying the game map
            Image(
                painter = painterResource(id = R.drawable.epfl_osm_2),
                contentDescription = "A background image of the game map",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.4f))
            ) {
                CustomTabRow(
                    tabs = tabs.map { it.description },
                    state = state
                )

                HorizontalPager(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ranking_lists"),
                    count = tabs.size,
                    state = state
                ) { page ->
                    RankingList(tabs[page])
                }
            }
        }
    }

    /**
     * The list view of rankings for the given [RankingCategory]
     *
     * @param category The category to use to rank the users
     *
     * @see RankingsContent
     */
    @Composable
    private fun RankingList(category: RankingCategory) {
        // The column that displays the rankings list of the currently selected tab
        LazyColumn(
            Modifier
                .padding(Padding.onBackground)
        ) {
            itemsIndexed(viewModel.getRanking(category).take(maxRankingEntries)) { rank, user ->
                UserRank(rank, user, category)
            }
        }
    }

    /**
     * A card displaying the [User]'s rank according to a [RankingCategory], along with their
     * statistics for the given ranking criteria
     *
     * @param rank The rank of the user
     * @param user The user to display
     * @param category The category used to rank the users
     *
     * @see RankingList
     */
    @Composable
    private fun UserRank(rank: Int, user: User, category: RankingCategory) {
        // User ranks in the list view alternate between primary and secondary color
        val cardColor = CardDefaults.cardColors(containerColor =
            if (rank % 2 == 0)
                MaterialTheme.colors.primary
            else
                MaterialTheme.colors.secondaryVariant)
        val textColor =
            if (rank % 2 == 0)
                MaterialTheme.colors.onPrimary
            else
                MaterialTheme.colors.onSecondary

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                // Flat cards
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            colors = cardColor
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                content = {
                    // User's rank, on the left
                    Text(
                        text = "#${rank + 1}",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = Padding.large, top = Padding.medium, bottom = Padding.medium),
                        color = textColor
                    )
                    // TODO: Add user's skin on the rankings
                    // User's name, centered
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                    // User's statistics for the given category, on the right
                    Text(
                        text = category.criteria(user).toString(),
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = Padding.large, top = Padding.medium, bottom = Padding.medium),
                        color = textColor
                    )
                }
            )
        }
    }

    @Preview(
        name = "Light Mode"
    )
    @Preview(
        name = "Dark Mode",
        uiMode = Configuration.UI_MODE_NIGHT_YES
    )
    /**
     * Preview of the [RankingsActivity]
     */
    @Composable
    fun RankingsPreview() {
        MenuContent {
            RankingsContent()
        }
    }
}