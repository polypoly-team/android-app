package com.github.polypoly.app.ui.menu.profile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.R
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.base.user.Trophy.Companion.allTrophies
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.utils.global.Settings.Companion.DB_USERS_PROFILES_PATH
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.ui.menu.MenuActivity
import com.github.polypoly.app.ui.theme.PolypolyTheme

class ProfileActivity : MenuActivity("Profile") {

    //ONLY TO TEST WITHOUT THE DATABASE
    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           MenuContent { ProfileContent() }
        }
    }

    // ===================================================== MAIN CONTENT
    @Composable
    fun ProfileContent() {
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                ProfileAndStats()
            }
        }
    }

    /**
     * Display the user profile with all the statistics of the user, his/her nickname, his/her
     * description and his/her appearance in the game
     */
    @Composable
    fun ProfileAndStats() {

        var user by remember { mutableStateOf(User()) }
        remoteDB.getValue<User>(DB_USERS_PROFILES_PATH + userId).thenAccept{userFound ->
            user = userFound
        }


        var profileHeight by remember { mutableStateOf(340.dp) }
        val localDensity = LocalDensity.current

        // In order for the shadow of the surface elevation to be displayed on the bottom of the
        // screen, I had to put the elements one on top of the other and not in a column
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(profileHeight))
                StatisticsAndTrophies(user)
            }
            Surface(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        profileHeight = with(localDensity) { coordinates.size.height.toDp() }
                    }
                    .testTag("profileSurface"),
                elevation = 8.dp,
                color = MaterialTheme.colors.background
            ) {
                Profile(user)
            }
        }
    }

    /**
     * Display the information of the user's profile and the appearance of player in game
     * @param user the user to whom the profile belongs
     */
    @Composable
    fun Profile(user: User) {
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
                ProfileInfo(user)
            }
            Spacer(modifier = Modifier.height(30.dp))
            ModifyProfileButton()
        }
    }

    /**
     * A button to go to the page where the user can modify his/her profile
     */
    @Composable
    fun ModifyProfileButton() {
        val mContext = LocalContext.current
        Button(
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            onClick = {
                val profileModifyingIntent = Intent(mContext, ProfileModifyingActivity::class.java)
                profileModifyingIntent.putExtra("userId", userId)
                finish()
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

    /**
     * Display some statistics about the player and all the trophies that the
     * player has won or can win.
     * @param user the user to whom the profile belongs
     */
    @Composable
    fun StatisticsAndTrophies(user: User) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .paint(
                    painterResource(id = R.drawable.epfl_osm),
                    contentScale = ContentScale.FillHeight
                )
                .background(color = Color.Black.copy(alpha = 0.4f))
                .padding(all = 30.dp)
                .testTag("statisticsAndTrophies"),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Statistics(user)
            Spacer(modifier = Modifier.height(20.dp))
            Trophies(user)
        }
    }

    /**
     * Display some statistics about the player in a Box
     * @param user the user to whom the profile belongs
     */
    @Composable
    fun Statistics(user: User) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "statistics", style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                StatisticsView(user)
            }
        }
    }

    /**
     * The list of all the statistics about the player
     * @param user the user to whom the profile belongs
     */
    @Composable
    fun StatisticsView(user: User) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Stat(user.stats.numberOfGames, "Games played")
            Spacer(modifier = Modifier.height(10.dp))
            Stat(user.stats.numberOfWins, "Games won")
            Spacer(modifier = Modifier.height(10.dp))
            Stat(user.stats.kilometersTraveled, "kilometers traveled")
            Spacer(modifier = Modifier.height(10.dp))
            Stat(user.trophiesWon.size, "Trophies won")
        }
    }

    /**
     * Display all the trophies that the player has won or can win.
     * The title and description of the trophy is displayed when you click on it.
     * @param user the user to whom the profile belongs
     */
    @Composable
    fun Trophies(user: User) {
        val selectedTrophy = remember { mutableStateListOf(0) }

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Trophies", style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(20.dp))
                TrophyDescriptionBubble(selectedTrophy, user)
                Spacer(modifier = Modifier.height(15.dp))
                TrophiesView(callBack = {
                    idx ->
                    selectedTrophy.clear()
                    selectedTrophy.add(idx)
                }, maxSelected = 1, selected = selectedTrophy, user = user)
            }
        }
    }

    /**
     * A bubble above the trophies grid that display the title and the description of the
     * selected trophy.
     * @param selectedTrophy the trophy currently selected
     * @param user the user to whom the profile belongs
     */
    @Composable
    fun TrophyDescriptionBubble(selectedTrophy: MutableList<Int>, user: User) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.secondaryVariant,
                    shape = RoundedCornerShape(20.dp)
                )
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            val trophyId = selectedTrophy.first()
            Text(
                if(user.hasTrophy(trophyId)) allTrophies[trophyId].toString()
                else "???",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.body2,
                textAlign = if(user.hasTrophy(trophyId)) TextAlign.Left else
                    TextAlign.Center
            )
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
     * Display the main trophies the user want to show to other players
     * @param user the user to whom the profile belongs
     */
    @Composable
    fun DisplayedTrophies(user: User) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DisplayedTrophy(user, 0)
            Spacer(modifier = Modifier.width(10.dp))
            DisplayedTrophy(user, 1)
            Spacer(modifier = Modifier.width(10.dp))
            DisplayedTrophy(user, 2)
        }
    }

    /**
     * Display one of the main trophies the user want to show to other players
     * @param user the user to whom the profile belongs
     * @param idxSlot the index of the slot where the trophy is showed
     */
    @Composable
    fun DisplayedTrophy(user: User, idxSlot: Int) {
        // test if the user has won enough trophies to display a trophy on this slot
        if(user.trophiesDisplay.size > idxSlot) {
            TrophyView(allTrophies[user.trophiesDisplay[idxSlot]],
                won = true, selected = true, disable = true)
        } else {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colors.secondaryVariant
                    )
                    .size(50.dp)
                    .testTag("emptySlot$idxSlot"),
                contentAlignment = Alignment.Center,
            ){}
        }
    }

    /**
     * Display the info of the player (name and description
     * @param user the user to whom the profile belongs
     */
    @Composable
    fun ProfileInfo(user: User) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier =  Modifier.testTag("nickname"),
                text = user.name,
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier =  Modifier.testTag("bio"),
                text = user.bio,
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(20.dp))
            DisplayedTrophies(user = user)
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