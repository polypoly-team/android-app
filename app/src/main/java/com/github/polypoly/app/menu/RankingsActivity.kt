package com.github.polypoly.app.menu

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
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
     * The rankings view, composed of a row of rankings lists tabs
     */
    @Composable
    private fun RankingsContent() {
        // TODO: Retrieve all registered users from the database

        val tabs = listOf("Most Wins", "Most Trophies", "Most Games")
        var selectedTabIndex by remember { mutableStateOf(0) }

        Column {
            // The row of tabs at the top of the screen that lists the types of rankings
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            // The column that displays the rankings list of the currently selected tab
            LazyColumn {
                // TODO: Display the rankings list based on the selected tab
                item {
                    Text(text = "Rankings list")
                }
            }
        }
    }
}