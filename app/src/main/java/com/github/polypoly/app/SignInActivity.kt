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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.polypoly.app.menu.kotlin.GameMusic
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : ComponentActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = FirebaseAuth.AuthStateListener {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth!!.currentUser != null) {
            launchWelcome()
        }
        setContent { SignInContent() }
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth!!.removeAuthStateListener(mAuthListener!!)
    }

    private fun launchWelcome(){
        val welcomeActivityIntent = Intent(this, WelcomeActivity::class.java)
        WelcomeActivity.isSignedIn = true
        startActivity(welcomeActivityIntent)
        finish()
    }

    /**
     * This function is called when the sign-in flow is wanted to be started
     */
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {}


    @Composable
    fun SignInContent() {
        GameMusic.setSong(LocalContext.current, R.raw.mocksong)
        GameMusic.startSong()
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = firebaseAuth?.currentUser
            if (user != null) {
                launchWelcome()
            }
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
                    SignInButton()
                }
            }
        }
    }

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
                modifier = Modifier
                    .padding(2.dp)
                    .testTag("sign_in_button")
            ) {
                Button(
                    onClick = {
                        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

                        val signInIntent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build()
                        signInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(70.dp),
                ) {
                    Text(text = "Sign in to play!")
                }
            }
        }
    }

}