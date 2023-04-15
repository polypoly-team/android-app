package com.github.polypoly.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.polypoly.app.menu.JoinGameLobbyActivity
import com.github.polypoly.app.menu.MenuComposable
import com.github.polypoly.app.menu.kotlin.GameMusic

import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.google.firebase.auth.FirebaseAuth

/**
 * This activity is the view that a player will see when launching the app, the idea is that
 * this screen represents the "hub" from where all the main actions are made.
 *
 * These actions may be: creating a game, joining a game, logging in, settings, rules, leaderboards etc.
 */
class WelcomeActivity : ComponentActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    var isSignedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            isSignedIn = user != null
        }

        setContent { WelcomeContent() }
    }


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    /**
     * This function is called when the sign-in flow is completed
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            print("__________________ Signed in successfully _________________")
        } else {
            print("__________________ Signed in FAILLEUUUUDD _________________")
        }
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
                    if(isSignedIn) {
                        GameButtons()
                        Spacer(modifier = Modifier.weight(1f))
                        MenuComposable.RowButtons()
                    } else {
                        SignInButton()
                    }
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

    /**
     * This button is used to sign in the user, it'll be displayed if the user is not signed in
     */
    @Composable
    private fun SignInButton() {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(2.dp)
            ) {
                GameButton(onClick = {
                    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

                    val signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build()
                    signInLauncher.launch(signInIntent)
                }, text = "Sign in to play!")
            }
        }
    }
}
