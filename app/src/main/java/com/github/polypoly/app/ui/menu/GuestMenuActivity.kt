package com.github.polypoly.app.ui.menu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.menu.MenuComposable.RulesButton
import com.github.polypoly.app.ui.menu.MenuComposable.SettingsButton
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements.DiscreetButton

/**
 * Represent a simplified version of the menu when the user is a guest
 */
class GuestMenuActivity : ComponentActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PolypolyTheme { SimpleMenuContent() } }
    }

    /**
     * The content of the simplified menu
     */
    @Composable
    fun SimpleMenuContent() {
        val nickname: String = intent.getStringExtra("userNickname") ?: DEFAULT_NICKNAME

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(Padding.veryLarge),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Title(nickname)
            DiscoverTheMapButton()
            BottomBarButtons()
        }
    }

    /**
     * The title of the menu, with a welcome message and a description of the guest mode
     */
    @Composable
    private fun Title(nickname: String) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Welcome $nickname",
                style = MaterialTheme.typography.h4
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "As a guest, you can only visit the map without playing.",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            ReturnToSignInButton()
        }
    }

    /**
     * The button to return to the sign in page if the user wants to sign in and not stay on
     * the guest interface anymore.
     */
    @Composable
    private fun ReturnToSignInButton() {
        val mContext = LocalContext.current

        DiscreetButton(
            onClick = {
                val signInIntent = Intent(mContext, SignInActivity::class.java)
                finish()
                startActivity(signInIntent) },
            testTag = "return_sign_in_button",
            text = "Return to sign in"
        )
    }

    /**
     * The button to see the map and discover the locations of the EPFL
     */
    @Composable
    private fun DiscoverTheMapButton() {
        Button(
            onClick = { /*TODO*/ },
            shape = CircleShape,
            modifier = Modifier
                .height(50.dp)
                .width(200.dp)
        ) {
            Text(text = "Discover the map")
        }
    }

    /**
     * The buttons at the bottom of the screen (rules of the game and settings)
     */
    @Composable
    private fun BottomBarButtons() {
        Row (
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RulesButton()
            Spacer(modifier = Modifier.width(20.dp))
            SettingsButton()
        }
    }

    // ================= PREVIEW ================= //

    @Preview(
        name = "Light Mode"
    )
    @Composable
    fun SimplifiedMenuPreview() {
        PolypolyTheme {
            SimpleMenuContent()
        }
    }

    companion object {
        /**
         * The default nickname of a guest
         */
        private const val DEFAULT_NICKNAME = "Guest"
    }
}