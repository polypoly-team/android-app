package com.github.polypoly.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    /**
     * Callback for the FirebaseUI Activity result contract
     */
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonToGoogleSignIn: SignInButton = findViewById(R.id.google_sign_in)
        buttonToGoogleSignIn.setOnClickListener {
            signIn()
        }

        val buttonToSignOut: Button = findViewById(R.id.sign_out_button)
        buttonToSignOut.setOnClickListener {
            signOut()
        }
    }

    /**
     * kicks off the FirebaseUI sign in flow
     */
    private fun signIn(){
        // Choose authentication providers (only google yet)
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun signOut(){
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(applicationContext, "Signed Out", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            print("___________________________________\n")
            print("User Successfully Authenticated: \n")
            print("Username : " + (user?.displayName ?: "Null Username") + "\n")
            print("E-Mail address : " + (user?.email ?: "Null E-Mail address") + "\n")
            print("___________________________________\n")

            val intent = Intent(this, GreetingActivity::class.java)
            intent.putExtra("name", user!!.displayName)
            startActivity(intent)
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            print("___________________________________\n")
            print("User Failed to Authenticate: \n")
            print("Error Code : " + (response?.error?.errorCode ?: "User Canceled Sign-In Flow") + "\n")
            print("___________________________________\n")




        }
    }

}