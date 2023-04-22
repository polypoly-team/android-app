package com.github.polypoly.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.rules_and_lobby.GameLobby
import com.github.polypoly.app.base.game.rules_and_lobby.GameMode
import com.github.polypoly.app.base.game.rules_and_lobby.GameRules
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.global.Settings.Companion.DB_GAME_LOBIES_PATH
import com.github.polypoly.app.global.Settings.Companion.DB_USERS_PROFILES_PATH
import com.github.polypoly.app.menu.JoinGameLobbyActivity
import com.github.polypoly.app.menu.MenuComposable
import com.github.polypoly.app.menu.kotlin.GameMusic
import com.github.polypoly.app.network.RemoteDB
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

/**
 * This activity is the view that a player will see when launching the app, the idea is that
 * this screen represents the "hub" from where all the main actions are made.
 *
 * These actions may be: creating a game, joining a game, logging in, settings, rules, leaderboards etc.
 */
class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Firebase.database
        remoteDB = RemoteDB(db, "live")

        addFakeDataToDB() //> Uncomment this line if you need to refresh the fake data within the DB

        setContent { WelcomeContent() }
    }

    /**
     * DEBUG function
     * Add fake data (possibly duplicate from tests' fake data) if the data in DB were corrupted
     */
    private fun addFakeDataToDB() {
        // Miscellaneous test data
        val zeroStats = Stats(0, 0, 0, 0, 0)
        val defaultSkin = Skin.default()

        val testUser0 = User(
            id = 0,
            name = "John",
            bio = "Hi, this is my bio :)",
            skin = Skin(0,0,0),
            stats = Stats(0, 0, 67, 28, 14),
            trophiesWon = listOf(0, 4, 8, 11, 12, 14),
            trophiesDisplay = mutableListOf(0, 4)
        )
        val testUser1 = User(12,"Carter", "Not me!", defaultSkin, zeroStats, listOf(), mutableListOf())
        val testUser2 = User(123,"Harry", "Ha!", defaultSkin, zeroStats, listOf(), mutableListOf())
        val testUser3 = User(1234,"James", "Hey!", defaultSkin, zeroStats, listOf(), mutableListOf())
        val testUser4 = User(12345,"Henri", "Ohh!", defaultSkin, zeroStats, listOf(), mutableListOf())
        val testUser5 = User(123456, "test_user_5", "", defaultSkin, zeroStats, listOf(), mutableListOf())
        val allTestUsers = listOf(testUser0, testUser1, testUser2, testUser3, testUser4, testUser5)

        val testGameLobbyFull = GameLobby(
            testUser0, GameRules(GameMode.RICHEST_PLAYER, 2, 6,
                60, 20, emptyList(), 100), "Full gameLobby", "1234"
        )
        val testGameLobbyPrivate = GameLobby(
            testUser1, GameRules(GameMode.RICHEST_PLAYER, 4, 6,
                360, 20, emptyList(), 300), "Private gameLobby", "abc123", true
        )
        val testGameLobbyAvailable1 = GameLobby(
            testUser1, GameRules(GameMode.LAST_STANDING, 3, 8,
            600, null, emptyList(), 1000), "Joinable 1", "abcd"
        )
        val testGameLobbyAvailable2 = GameLobby(
            testUser2, GameRules(GameMode.RICHEST_PLAYER, 10, 25,
            3600, 20, emptyList(), 2000), "Joinable 2", "123abc"
        )
        val testGameLobbyAvailable3 = GameLobby(
            testUser3, GameRules(GameMode.RICHEST_PLAYER, 7, 77,
            720, 20, emptyList(), 3000), "Joinable 3", "1234abc"
        )
        val testGameLobbyAvailable4 = GameLobby(
            testUser4, GameRules(GameMode.RICHEST_PLAYER, 2, 4,
            1080, 20, emptyList(), 4000), "Joinable 4", "abc1234"
        )

        val allTestGameLobbies = listOf(testGameLobbyFull, testGameLobbyPrivate, testGameLobbyAvailable1,
            testGameLobbyAvailable2, testGameLobbyAvailable3, testGameLobbyAvailable4)

        testGameLobbyFull.addUsers(listOf(testUser1, testUser2, testUser3, testUser4, testUser5))
        testGameLobbyPrivate.addUsers(listOf(testUser2))
        testGameLobbyAvailable1.addUsers(listOf(testUser2, testUser3))
        testGameLobbyAvailable2.addUsers(listOf(testUser1, testUser4))
        testGameLobbyAvailable3.addUsers(listOf(testUser1, testUser2, testUser4))

        // Helper function
        fun <T> requestAddDataToDB(data: List<T>, keys: List<String>, root: String): List<CompletableFuture<Boolean>> {
            return data.zip(keys).map {(data, key) -> remoteDB.setValue(root + key, data) }
        }

        // Add data to DB
        requestAddDataToDB(allTestUsers, allTestUsers.map{ user -> user.id.toString()}, DB_USERS_PROFILES_PATH)
        requestAddDataToDB(allTestGameLobbies, allTestGameLobbies.map(GameLobby::code), DB_GAME_LOBIES_PATH)
    }

    @Preview(showBackground = true)
    @Composable
    fun WelcomePreview() {
        WelcomeContent()
    }

    // ===================================================== MAIN CONTENT
    @Composable
    fun WelcomeContent() {
        GameMusic.setSong(LocalContext.current, R.raw.mocksong)
        GameMusic.startSong()
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // The first element is the logo of the game
                    GameLogo()
                    Spacer(modifier = Modifier.weight(1f))
                    // Then the game buttons are in the center of the screen
                    GameButtons()
                    Spacer(modifier = Modifier.weight(1f))
                    MenuComposable.RowButtons()
                }
            }
        }
    }


    // ===================================================== WELCOME COMPONENTS
    /**
     * This composable is the main image of the game, the polypoly logo that'll be displayed
     * in the welcome screen (i.e. Welcome Activity)
     */
    @Composable
    fun GameLogo() {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.tmp_happysmile),
                contentDescription = "game_logo",
            )
        }
    }

    // TODO: add the real activity directions to the buttons
    /**
     * So far, the player has two main options, join an existing game or create a new one,
     * these buttons are then used for these purposes and have a fixed size.
     */
    @Composable
    fun GameButtons() {
        val mContext = LocalContext.current
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(2.dp)
            ) {
                // Join button
                GameButton(onClick = {
                    val joinGroupIntent = Intent(mContext, JoinGameLobbyActivity::class.java)
                    startActivity(joinGroupIntent)
                }, text = "Join Game!")
                Spacer(modifier = Modifier.height(20.dp))
                // Create button
                GameButton(onClick = { /*TODO*/ }, text = "Create Game?")
            }
        }
    }

    // ============================================================= HELPERS

    /**
     * Simply a common button that'll be used for important purposes
     */
    @Composable
    fun GameButton(onClick: () -> Unit, text: String) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .width(200.dp)
                .height(70.dp),
        ) {
            Text(text = text)
        }
    }
}
