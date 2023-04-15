package com.github.polypoly.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.activity.ComponentActivity as ComponentActivity1

/**
 * This activity is the view that a player will see when launching the app, the idea is that
 * this screen represents the "hub" from where all the main actions are made.
 *
 * These actions may be: creating a game, joining a game, logging in, settings, rules, leaderboards etc.
 */
class WelcomeActivity : ComponentActivity1() {

    private var firebaseAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = FirebaseAuth.AuthStateListener {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        setContent { WelcomeContent() }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth!!.removeAuthStateListener(mAuthListener!!)
    }

    /**
     * This function is called when the sign-in flow is wanted to be started
     */
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
            print("\n__________________ Signed in successfully _________________\n")
        } else {
            print("\n__________________ Signed in failed _________________\n")
            print(response?.error?.errorCode)
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

        val isSignedIn = remember { mutableStateOf(false) }
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            isSignedIn.value = user != null
            print("\n__________________ Signed in: ${user?.displayName} _________________\n")
        }

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
                    if(isSignedIn.value) {
                        GameButtons()
                        Spacer(modifier = Modifier.weight(1f))
                        MenuComposable.RowButtons()
                    } else {
                        SignInButton(isSignedIn)
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
    private fun SignInButton(isSignedIn: MutableState<Boolean>) {
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
