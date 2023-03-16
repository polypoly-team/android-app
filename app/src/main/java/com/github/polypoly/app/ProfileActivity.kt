package com.github.polypoly.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.theme.PolypolyTheme

class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProfileAndStats()
                }
            }
        }
    }

    /**
     * Display the user profile with all the statistics of the user, his/her nickname, his/her
     * description and his/her appearance in the game
     */
    @Composable
    fun ProfileAndStats() {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(elevation = 8.dp) {
                Profile()
            }
            Statistics()
        }
    }

    /**
     * Display the information of the user's profile and the appearance of player in game
     */
    @Composable
    fun Profile() {
        Column(
            modifier = Modifier
                .padding(all = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                UserAppearance()
                Spacer(modifier = Modifier.width(30.dp))
                ProfileInfo()
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {},
                shape = CircleShape,
                modifier = Modifier.testTag("modifyProfileButton"),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(red = 240, blue = 240, green = 240))
            ) {
                Text("Modify profile")
            }
        }
    }

    /**
     * Display some statistics about the player
     */
    @Composable
    fun Statistics() {
        Column(
            modifier = Modifier
                .padding(all = 30.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("statistics", style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center)
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Stat(46, "Parties jouées")
                Spacer(modifier = Modifier.height(10.dp))
                Stat(69, "Parties gagnées")
                Spacer(modifier = Modifier.height(10.dp))
                Stat(40, "kilomètre effectué")
                Spacer(modifier = Modifier.height(10.dp))
                Stat(9, "Trophée gagné")
            }
        }
    }

    /**
     * One statistic about the player
     * @param number the number of the stat
     * @param statName the name of the stat
     */
    @Composable
    fun Stat(number: Int, statName: String) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colors.primary, CircleShape)
                    .size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(number.toString(), style = MaterialTheme.typography.body1)
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(statName, style = MaterialTheme.typography.body1)
        }
    }

    /**
     * The customisable appearance of the player in game
     */
    @Composable
    fun UserAppearance() {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(200.dp)
                .background(MaterialTheme.colors.primary)
        )
    }

    /**
     * Display the info of the player (name and description
     */
    @Composable
    fun ProfileInfo() {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Nom très long",
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "description blabla bla blablabla bla bla blabla bla bla blabla bla blabla",
                style = MaterialTheme.typography.body2
            )
        }
    }

    // =================================== PREVIEW ==============
    @Preview(
        name = "Light Mode"
    )
    @Preview(
        name = "Dark Mode",
        uiMode = Configuration.UI_MODE_NIGHT_YES
    )
    @Composable
    fun ProfilePreview() {
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                ProfileAndStats()
            }
        }
    }
}