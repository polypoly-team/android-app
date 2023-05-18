package com.github.polypoly.app.ui.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.polypoly.app.R
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.network.StorableObject
import com.github.polypoly.app.ui.menu.profile.CreateProfileActivity
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements.MainActionButton
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.isSignedIn
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.network.addOnChangeListener
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.initCurrentUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.initRemoteDB
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.isSignedIn
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CompletableFuture

/**
 * This activity is holds the sign up flow. It will be displayed whenever no user is signed in.
 */
class SignInActivity : ComponentActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private val mAuthListener: FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener {
        launchWelcomeIfReady()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRemoteDB()
        firebaseAuth = FirebaseAuth.getInstance()
        isSignedIn = false

        launchWelcomeIfReady()
        setContent { SignInContent() }
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth!!.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth!!.removeAuthStateListener(mAuthListener)
    }

    /**
     * If the user is correctly signed in Firebase, gets the user and launches [WelcomeActivity]
     */
    private fun launchWelcomeIfReady() {
        val user = firebaseAuth?.currentUser
        if (user != null) {
            initCurrentUser(user.uid, user.displayName ?: "default")

            val welcomeActivityIntent = Intent(this, WelcomeActivity::class.java)
            isSignedIn = true
            startActivity(welcomeActivityIntent)
            finish()
        }
    }

    @Deprecated("Prefer to use launchWelcomeIfReady")
    /**
     * starts the WelcomeActivity and sets the isSignedIn flag to true
     */
    private fun launchWelcome(){
        val welcomeActivityIntent = Intent(this, WelcomeActivity::class.java)
        isSignedIn = true
        startActivity(welcomeActivityIntent)
        finish()
    }

    /**
     * starts the Create Profile and sets the isSignedIn flag
     */
    private fun launchCreateProfile(signIn: Boolean) {
        val createProfileIntent = Intent(this, CreateProfileActivity::class.java)
        isSignedIn = signIn
        startActivity(createProfileIntent)
        finish()
    }

    /**
     * launched when the sign-in flow is wanted to be started
     */
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {}

    /**
     * The content of the SignInActivity
     */
    @Composable
    fun SignInContent() {
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
                    SignInOrGuestButtons()
                }
            }
        }
    }

    /**
     * The logo of polypoly
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

    /**
     * The sign-in button and the guest button to access the menu with or without an account
     */
    @Composable
    private fun SignInOrGuestButtons() {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(2.dp)
            ) {
                SignInButton()
                Spacer(modifier = Modifier.height(30.dp))
                GuestButton()
            }
        }
    }

    /**
     * This button is used to launch the sign-in flow
     */
    @Composable
    private fun SignInButton() {
        MainActionButton(
            onClick = {
                val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
                signInLauncher.launch(signInIntent)
            },
            text = "Sign in to play!",
            testTag = "sign_in_button",
            enabled = true,
        )
    }

    /**
     * This button is used to not sign in and play as a guest
     */
    @Composable
    private fun GuestButton() {
        MainActionButton(
            onClick = {
                launchCreateProfile(false)
            },
            text = "Play as guest",
            testTag = "guest_button",
            enabled = true,
        )
    }


























    /**
     * DEBUG function
     * Add fake data (possibly duplicate from tests' fake data) if the data in DB were corrupted
     */
    fun addFakeDataToDB() {
        // Miscellaneous test data
        val ZERO_STATS = Stats(0, 0, 0, 0, 0)
        val NO_SKIN = Skin(0,0,0)

        val TEST_USER_0 = User(
            id = "BOOM",
            name = "John",
            bio = "Hi, this is my bio :)",
            skin = Skin(0,0,0),
            stats = Stats(0, 0, 67, 28, 14),
            trophiesWon = listOf(0, 4, 8, 11, 12, 14),
            trophiesDisplay = mutableListOf(0, 4)
        )

        val TEST_USER_1 = User("12","Carter", "Not me!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_2 = User("123","Harry", "Ha!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_3 = User("1234","James", "Hey!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_4 = User("12345","Henri", "Ohh!", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val TEST_USER_5 = User("123456", "test_user_5", "", NO_SKIN, ZERO_STATS, listOf(), mutableListOf())
        val ALL_TEST_USERS = listOf(TEST_USER_0, TEST_USER_1, TEST_USER_2, TEST_USER_3, TEST_USER_4, TEST_USER_5)

        val TEST_GAME_LOBBY_FULL = GameLobby(
            TEST_USER_0, GameParameters(GameMode.RICHEST_PLAYER, 5, 6,
            60, 20, emptyList(), 100), "Full gameLobby", "1234"
        )
        val TEST_GAME_LOBBY_PRIVATE = GameLobby(
            TEST_USER_1, GameParameters(GameMode.RICHEST_PLAYER, 2, 4,
            900, 20, emptyList(), 120), "Private gameLobby", "abc123", true
        )
        val TEST_GAME_LOBBY_AVAILABLE_1 = GameLobby(
            TEST_USER_1, GameParameters(GameMode.LAST_STANDING, 3, 8,
            600, null, emptyList(), 1500), "Joinable 1", "abcd"
        )
        val TEST_GAME_LOBBY_AVAILABLE_2 = GameLobby(
            TEST_USER_2, GameParameters(GameMode.RICHEST_PLAYER, 5, 5,
            30, 20, emptyList(), 5000), "Joinable 2", "123abc"
        )
        val TEST_GAME_LOBBY_AVAILABLE_3 = GameLobby(
            TEST_USER_3, GameParameters(GameMode.RICHEST_PLAYER, 7, 8,
            10080, 20, emptyList(), 3000), "Joinable 3", "1234abc"
        )
        val TEST_GAME_LOBBY_AVAILABLE_4 = GameLobby(
            TEST_USER_4, GameParameters(GameMode.RICHEST_PLAYER, 2, 4,
            300, 20, emptyList(), 4000), "Joinable 4", "abc1234"
        )

        val ALL_TEST_GAME_LOBBIES = listOf(TEST_GAME_LOBBY_FULL, TEST_GAME_LOBBY_PRIVATE, TEST_GAME_LOBBY_AVAILABLE_1,
            TEST_GAME_LOBBY_AVAILABLE_2, TEST_GAME_LOBBY_AVAILABLE_3, TEST_GAME_LOBBY_AVAILABLE_4)

        TEST_GAME_LOBBY_FULL.addUsers(listOf(TEST_USER_1, TEST_USER_2, TEST_USER_3, TEST_USER_4, TEST_USER_5))
        TEST_GAME_LOBBY_PRIVATE.addUsers(listOf(TEST_USER_2))
        TEST_GAME_LOBBY_AVAILABLE_1.addUsers(listOf(TEST_USER_2, TEST_USER_3))
        TEST_GAME_LOBBY_AVAILABLE_2.addUsers(listOf(TEST_USER_1, TEST_USER_4))
        TEST_GAME_LOBBY_AVAILABLE_3.addUsers(listOf(TEST_USER_1, TEST_USER_2, TEST_USER_4))

        // Helper function
        fun <T : StorableObject<*>> requestAddDataToDB(data: List<T>, keys: List<String>): List<CompletableFuture<Boolean>> {
            return data.zip(keys).map {(data, _) -> remoteDB.setValue(data) }
        }

        // Add data to DB
        requestAddDataToDB(ALL_TEST_USERS, ALL_TEST_USERS.map{user -> user.id.toString()})
        requestAddDataToDB(ALL_TEST_GAME_LOBBIES, ALL_TEST_GAME_LOBBIES.map(GameLobby::code))
    }

}