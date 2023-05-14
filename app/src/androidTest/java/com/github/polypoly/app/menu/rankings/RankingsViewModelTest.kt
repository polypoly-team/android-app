package com.github.polypoly.app.menu.rankings

import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.commons.MockDB
import com.github.polypoly.app.commons.PolyPolyTest.Companion.NO_SKIN
import com.github.polypoly.app.ui.menu.rankings.RankingCategory
import com.github.polypoly.app.ui.menu.rankings.RankingsViewModel
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RankingsViewModelTest {
    private lateinit var viewModel: RankingsViewModel
    private var mockDB: MockDB = MockDB()

    private fun setUsers(users: List<User>) {
        mockDB.clear()
        users.forEach { mockDB.setValue(it) }
        remoteDB = mockDB
    }

    private fun mockUser(name: String, numberOfGame: Int, numberOfWin: Int, trophyCount: Int): User {
        return User(1000, name, "", NO_SKIN,
            Stats(0, 0, numberOfGame, numberOfWin, 0),
            List(trophyCount) { 1 }, mutableListOf())
    }

    @Before
    fun setup() {
        viewModel = RankingsViewModel()
        remoteDB = mockDB
    }

    @Test
    fun rankingsViewModelComputesMostGamesRanking() {
        setUsers(
            listOf(
                mockUser("1", 10, 0, 0),
                mockUser("2", 20, 0, 0),
                mockUser("3", 30, 0, 0),
                mockUser("4", 50, 0, 0),
                mockUser("5", 40, 0, 0)
            )
        )

        viewModel.fetchUsers()

        assertEquals(
            listOf("4", "5", "3", "2", "1"),
            viewModel.getRanking(RankingCategory.GAMES).toList().map { user -> user.name }
        )
    }

    @Test
    fun rankingsViewModelComputesMostWinsRanking() {
        setUsers(
            listOf(
                mockUser("1", 0, 5, 0),
                mockUser("2", 0, 3, 0),
                mockUser("3", 0, 2, 0),
                mockUser("4", 0, 1, 0),
                mockUser("5", 0, 4, 0)
            )
        )

        viewModel.fetchUsers()

        assertEquals(
            listOf("1", "5", "2", "3", "4"),
            viewModel.getRanking(RankingCategory.WINS).toList().map { user -> user.name }
        )
    }

    @Test
    fun rankingsViewModelComputesMostTrophiesRanking() {
        setUsers(
            listOf(
                mockUser("1", 0, 0, 5),
                mockUser("2", 0, 0, 4),
                mockUser("3", 0, 0, 3),
                mockUser("4", 0, 0, 2),
                mockUser("5", 0, 0, 1)
            )
        )

        viewModel.fetchUsers()

        assertEquals(
            listOf("1", "2", "3", "4", "5"),
            viewModel.getRanking(RankingCategory.TROPHIES).toList().map { user -> user.name }
        )
    }
}