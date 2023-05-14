package com.github.polypoly.app.menu.rankings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.commons.MockDB
import com.github.polypoly.app.commons.PolyPolyTest
import com.github.polypoly.app.ui.menu.rankings.RankingsActivity
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RankingsActivityTest : PolyPolyTest(false, false, true) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RankingsActivity>()

    private val tabRow = composeTestRule.onNodeWithTag("tab_row")
    private val rankingsList = composeTestRule.onNodeWithTag("ranking_lists")

    private var mockDB: MockDB = MockDB()

    private fun setUsers(users: List<User>) {
        mockDB.clear()
        users.forEach { mockDB.setValue(it) }
        remoteDB = mockDB
    }

    private fun mockUser(name: String, numberOfGame: Int, numberOfWin: Int, trophyCount: Int): User {
        return User(0, name, "", NO_SKIN,
            Stats(0, 0, numberOfGame, numberOfWin, 0),
            List(trophyCount) { 1 }, mutableListOf())
    }

    @Before
    fun setup() {
        remoteDB = mockDB
    }

    @Test
    fun tabRowIsDisplayed() {
        tabRow.assertIsDisplayed()
    }

    @Test
    fun rankingsListIsDisplayed() {
        rankingsList.assertIsDisplayed()
    }
}