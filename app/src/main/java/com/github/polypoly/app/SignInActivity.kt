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
import com.github.polypoly.app.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.global.GlobalInstances.Companion.isSignedIn
import com.github.polypoly.app.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.menu.kotlin.GameMusic
import com.github.polypoly.app.network.RemoteDB
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignInActivity : ComponentActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = FirebaseAuth.AuthStateListener {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Global initialization of the database
        val db = Firebase.database
        remoteDB = RemoteDB(db, "live")

        firebaseAuth = FirebaseAuth.getInstance()
        isSignedIn = false
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

    /**
     * starts the WelcomeActivity
     */
    private fun launchWelcome(){
        val welcomeActivityIntent = Intent(this, WelcomeActivity::class.java)
        isSignedIn = true
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
                currentUser = user
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
     * This button is used to launch the sign-in flow
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