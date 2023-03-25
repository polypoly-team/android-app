package com.github.polypoly.app.menu

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.game.allTrophies
import com.github.polypoly.app.network.FakeRemoteStorage
import com.github.polypoly.app.ui.theme.PolypolyTheme

class ProfileActivity : ComponentActivity() {

    //ONLY TO TEST WITHOUT THE DATABASE
    private val userId: Long = 1

    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ProfileAndStats() {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                elevation = 8.dp,
                color = MaterialTheme.colors.background
            ) {
                Profile()
            }
            Statistics()
        }
    }

    /**
     * Display the information of the user's profile and the appearance of player in game
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun Profile() {
        val mContext = LocalContext.current
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
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp
                ),
                onClick = {
                    val profileModifyingIntent = Intent(mContext, ProfileModifyingActivity::class.java)
                    profileModifyingIntent.putExtra("userId", userId)
                    startActivity(profileModifyingIntent)
                },
                shape = CircleShape,
                modifier = Modifier
                    .testTag("modifyProfileButton"),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondaryVariant)
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(all = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("statistics", style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Stat(78, "Games played")
                Spacer(modifier = Modifier.height(10.dp))
                Stat(12, "Games won")
                Spacer(modifier = Modifier.height(10.dp))
                Stat(40, "kilometers traveled")
                Spacer(modifier = Modifier.height(10.dp))
                Stat(5, "Trophies won")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("All Trophies", style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center)
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .height(((allTrophies.size * 60 - 10) / 6).dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                content = {
                    items(allTrophies.size) { index ->
                        Trophy(index)
                    }
                }
            )
        }
    }

    /**
     * A trophy that the player has won or not
     */
    @Composable
    fun Trophy(trophyIdx: Int) {
        val won = trophyIdx%4 == 0
        var toDisplay = "?"
        if(won) toDisplay = allTrophies[trophyIdx].toString()
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color = if (won) MaterialTheme.colors.secondary else
                    MaterialTheme.colors.onSecondary)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(toDisplay,
                style = MaterialTheme.typography.body1)
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
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ProfileInfo() {
        val user = FakeRemoteStorage.instance.getUserProfileWithId(userId)
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier =  Modifier.testTag("nickname"),
                text = user.get().name,
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier =  Modifier.testTag("bio"),
                text = user.get().bio,
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Trophy(0)
                Spacer(modifier = Modifier.width(10.dp))
                Trophy(4)
                Spacer(modifier = Modifier.width(10.dp))
                Trophy(16)
            }
        }
    }

    // =================================== PREVIEW ==============
    @RequiresApi(Build.VERSION_CODES.O)
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