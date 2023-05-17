package com.github.polypoly.app.ui.menu.profile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.network.getValue
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements

class ProfileModifyingActivity : ComponentActivity() {

    /**
     * The temporary value of nickname of the player
     */
    private var nickname: String = ""

    /**
     * The temporary value of description of the player profile
     */
    private var description: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProfileForm()
                }
            }
        }
    }

    /**
     * The form where the user can fill his/her profile info
     */
    @Composable
    fun ProfileForm() {
        val id = intent.getLongExtra("userId", 0)

        var user by remember { mutableStateOf(User()) }

        remoteDB.getValue<User>(id.toString()).thenAccept{userFound ->
            nickname = user.name
            description = user.bio
            user = userFound
        }

        ProfileFormOfUser(user)
    }

    @Composable
    fun ProfileFormOfUser(
        user: User
    ) {
        var warningText by remember { mutableStateOf("") }
        val trophiesDisplay = remember { user.trophiesDisplay.toMutableStateList() }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Write your info", style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            NicknameTextField(user.name)
            Spacer(modifier = Modifier.height(10.dp))
            DescriptionTextField(user.bio)
            Spacer(modifier = Modifier.height(40.dp))
            TrophiesSelection(trophiesDisplay, user)
            Spacer(modifier = Modifier.height(40.dp))
            ValidationButton ({ warningText = "You can't have an empty nickname!" }, trophiesDisplay)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier = Modifier.offset(y = 200.dp),
                text = warningText,
                style = MaterialTheme.typography.body2
            )
        }
    }

    /**
     * Permit to select the trophies the user want to display on his/her profile
     * @param trophiesDisplay trophies currently displayed on the user profile
     * @param user the user to whom the profile belongs
     */
    @Composable
    fun TrophiesSelection(trophiesDisplay: MutableList<Int>, user: User) {
        val maxSelected = 3
        Text("Select displayed trophies", style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(20.dp))
        TrophiesView(
            callBack = { idx ->
                if(!trophiesDisplay.contains(idx) && user.hasTrophy(idx)) {
                    if (trophiesDisplay.size >= maxSelected) trophiesDisplay.removeAt(0)
                    trophiesDisplay.add(idx)
                }
            },
            maxSelected = maxSelected,
            selected = trophiesDisplay,
            user= user)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { trophiesDisplay.clear() },
            shape = CircleShape,
            modifier = Modifier.testTag("clearTrophiesButton"),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryVariant),
        ) {
            Text("Clear")
        }
    }

    /**
     * Button to validate the form
     * @param onError call back when an error occur
     */
    @Composable
    fun ValidationButton(onError: () -> Unit, trophiesDisplay: MutableList<Int>) {
        val mContext = LocalContext.current

        val onButtonClick: () -> Unit = {
            if(nickname.isEmpty()) {
                onError()
            } else {
                val id = intent.getLongExtra("userId", 0)
                remoteDB.getValue<User>(id.toString()).thenCompose { user ->
                    remoteDB.updateValue(User(
                        id = id,
                        name = nickname,
                        bio = description,
                        skin = user.skin,
                        stats = user.stats,
                        trophiesWon = user.trophiesWon,
                        trophiesDisplay = trophiesDisplay
                    ))
                }.thenApply {
                    val profileIntent = Intent(mContext, ProfileActivity::class.java)
                    finish()
                    startActivity(profileIntent)
                }
            }
        }

        Button(
            shape = CircleShape,
            modifier = Modifier.testTag("registerInfoButton"),
            onClick = onButtonClick
        ) {
            Text(text = "Validate profile")
        }
    }

    /**
     * The field where the player can write his/her nickname
     */
    @Composable
    fun NicknameTextField(initialName: String) {
        CustomTextField(
            label = "new nickname",
            initialValue = initialName,
            onChanged = {newValue ->  nickname = newValue},
            maxTextLength = 15,
            testTag = "nicknameText",
        )
    }

    /**
     * The field where the player can write his/her description
     */
    @Composable
    fun DescriptionTextField(initialDescription: String) {
        CustomTextField(
            label = "new description",
            initialValue = initialDescription,
            onChanged = {newValue ->  description = newValue},
            singleLine = false,
            maxTextLength = 130,
            testTag = "descriptionText",
        )
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
    fun ProfileModifyingPreview() {
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                ProfileForm()
            }
        }
    }
}