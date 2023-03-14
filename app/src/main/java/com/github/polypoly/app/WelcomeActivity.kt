package com.github.polypoly.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

import com.github.polypoly.app.ui.theme.PolypolyTheme

/**
 * This activity is the view that a player will see when launching the app, the idea is that
 * this screen represents the "hub" from where all the main actions are made.
 *
 * These actions may be: creating a game, joining a game, logging in, settings, rules, leaderboards etc.
 */
class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                        RowOptionButtons()
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
                    val joinGroupIntent = Intent(mContext, JoinGroupActivity::class.java)
                    startActivity(joinGroupIntent)
                }, text = "Join Game!")
                Spacer(modifier = Modifier.height(20.dp))
                // Create button
                GameButton(onClick = { /*TODO*/ }, text = "Create Game?")
            }
        }
    }

    /**
     * Small buttons that appear in the bottom of the welcome screen.
     * Each one represents a specific option, namely (from left to right)
     * - Button 1: Rules
     * - Button 2:
     * - Button 3:
     * - Button 4:
     */
    @Composable
    fun RowOptionButtons() {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            RulesOptionButton()
            // Option Button 2
            OptionButton(onClick = { /*TODO*/ }, icon_id = R.drawable.tmp_happysmile)
            // Option Button 3
            OptionButton(onClick = { /*TODO*/ }, icon_id = R.drawable.tmp_happysmile)
            // Option Button 4
            OptionButton(onClick = { /*TODO*/ }, icon_id = R.drawable.tmp_happysmile)
        }

    }

    /**
     * The button toggles on a dialog component where the rules are displayed.
     * The rules are scrollable and all the text is stored in RulesObject.
     *
     * The dialog box is closed when clicking outside of it
     */
    @Composable
    fun RulesOptionButton() {
        var openRules by remember { mutableStateOf(false) }
        OptionButton(
            onClick = { openRules = true },
            icon_id = R.drawable.tmp_happysmile)

        if(openRules) {
            Dialog(
                onDismissRequest = { openRules = false },
            ) {
                Surface(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.95f)
                ) {
                    LazyColumn(modifier = Modifier.padding(20.dp)) {
                        item {
                            Text(
                                text = RulesObject.rulesTitle,
                                style = MaterialTheme.typography.h4
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        items(items = RulesObject.rulesChapters, itemContent = { item ->
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.h5
                            )
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.body1
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        })
                    }
                }
            }
        }


    }



    // ============================================================= HELPERS
    /**
     * Creates a square button with a small image that'll be used to open other pop-ups or activities.
     */
    @Composable
    fun OptionButton(onClick: () -> Unit, icon_id: Int) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(icon_id),
                contentDescription = "",
                modifier = Modifier.size(50.dp)
            )
        }
    }

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





    // =================================== PREVIEW ==============
    @Preview(showBackground = true)
    @Composable
    fun WelcomePreview() {
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
                    RowOptionButtons()
                }
            }
        }
    }
}
