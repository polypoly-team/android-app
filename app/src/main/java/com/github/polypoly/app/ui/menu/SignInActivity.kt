package com.github.polypoly.app.ui.menu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.polypoly.app.base.game.location.LocationPropertyRepository.getZones
import com.github.polypoly.app.base.menu.lobby.GameLobby
import com.github.polypoly.app.base.menu.lobby.GameMode
import com.github.polypoly.app.base.menu.lobby.GameParameters
import com.github.polypoly.app.base.user.Skin
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.database.StorableObject
import com.github.polypoly.app.ui.menu.profile.CreateProfileActivity
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements.GameLogo
import com.github.polypoly.app.ui.theme.UIElements.MainActionButton
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

        addFakeDataToDB()

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
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        GameLogo()
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    SignInOrGuestButtons()
                }
            }
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
    private fun addFakeDataToDB() {
        // Miscellaneous test data
        val TEST_USER_0 = User(
            id = "0",
            name = "mcfly&cartilo",
            stats = Stats(numberOfWins = 150),
        )

        val TEST_USER_1 = User(
            id = "1",
            name = "Ouai c'est Greg",
            stats = Stats(numberOfWins = 187),
        )

        val TEST_USER_2 = User(
            id = "2",
            name = "Guillaume",
            stats = Stats(numberOfWins = 202),
        )

        val TEST_USER_3 = User(
            id = "3",
            name = "alilouch",
            stats = Stats(numberOfWins = 241),
        )

        val TEST_USER_4 = User(
            id = "4",
            name = "xX_polygamer_Xx",
            stats = Stats(numberOfWins = 179),
        )

        val TEST_USER_5 = User(
            id = "5",
            name = "m1st1gr1",
            stats = Stats(numberOfWins = 199),
        )

        val TEST_USER_6 = User(
            id = "6",
            name = "v1m0",
            stats = Stats(numberOfWins = 234),
        )

        val TEST_USER_7 = User(
            id = "7",
            name = "barnabé",
            stats = Stats(numberOfWins = 239),
        )

        val TEST_USER_8 = User(
            id = "8",
            name = "uWu_oWo",
            stats = Stats(numberOfWins = 202),
        )

        val TEST_USER_9 = User(
            id = "9",
            name = "DOGE",
            stats = Stats(numberOfWins = 287),
        )

        val TEST_USER_10 = User(
            id = "10",
            name = "bigflo",
            stats = Stats(numberOfWins = 200),
        )


        val ALL_TEST_USERS = listOf(
            TEST_USER_0,
            TEST_USER_1,
            TEST_USER_2,
            TEST_USER_3,
            TEST_USER_4,
            TEST_USER_5,
            TEST_USER_6,
            TEST_USER_7,
            TEST_USER_8,
            TEST_USER_9,
            TEST_USER_10,
        )

        // Helper function
        fun <T : StorableObject<*>> requestAddDataToDB(data: List<T>, keys: List<String>): List<CompletableFuture<Boolean>> {
            return data.zip(keys).map {(data, _) -> remoteDB.setValue(data) }
        }

        // Add data to DB
        requestAddDataToDB(ALL_TEST_USERS, ALL_TEST_USERS.map{user -> user.id })
    }

}