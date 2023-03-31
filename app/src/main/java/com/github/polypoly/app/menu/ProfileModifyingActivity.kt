package com.github.polypoly.app.menu

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import com.github.polypoly.app.game.user.Skin
import com.github.polypoly.app.game.user.Stats
import com.github.polypoly.app.game.user.User
import com.github.polypoly.app.menu.shared_component.TrophiesView
import com.github.polypoly.app.network.FakeRemoteStorage
import com.github.polypoly.app.ui.theme.PolypolyTheme
import java.time.LocalDateTime

class ProfileModifyingActivity : ComponentActivity() {

    /**
     * The temporary value of nickname of the player
     */
    private var nickname: String = ""

    /**
     * The temporary value of description of the player profile
     */
    private var description: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra("userId", 0)

        val user = FakeRemoteStorage.instance.getUserWithId(id).get()
        nickname = user.name
        description = user.bio

        setContent {
            PolypolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProfileForm(user)
                }
            }
        }
    }

    /**
     * The form where the user can fill his/her profile info
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ProfileForm(user: User) {
        var warningText by remember { mutableStateOf("") }
        val trophiesDisplay = remember { user.trophiesDisplay.toMutableStateList() }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Write your info", style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            NicknameTextField()
            Spacer(modifier = Modifier.height(10.dp))
            DescriptionTextField()

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
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ValidationButton(onError: () -> Unit, trophiesDisplay: MutableList<Int>) {
        val mContext = LocalContext.current
        Button(
            shape = CircleShape,
            modifier = Modifier.testTag("registerInfoButton"),
            onClick = {
                if(nickname.isEmpty()) {
                    onError()
                } else {
                    val id = intent.getLongExtra("userId", 0)
                    val user = FakeRemoteStorage.instance.getUserWithId(id).get()
                    FakeRemoteStorage.instance.updateUser(User(
                        id = id,
                        name = nickname,
                        bio = description,
                        skin = user.skin,
                        stats = user.stats,
                        trophiesWon = user.trophiesWon,
                        trophiesDisplay = trophiesDisplay
                    )
                    )
                    val profileIntent = Intent(mContext, ProfileActivity::class.java)
                    startActivity(profileIntent)
                }
            }
        ) {
            Text(text = "Validate profile")
        }
    }

    /**
     * A field where the user can write his/her info, as his/her nickname for example
     * @param label The label of the field
     * @param initialValue Initial Value of the field when it is built
     * @param onChanged Call when the content of the field is changed
     * @param singleLine If the Text Field has juste one line
     * @param maxTextLength The number of the maximum characters accepted
     * @param testTag The test tag of the field
     */
    @Composable
    fun CustomTextField(label: String, initialValue: String, onChanged: (newValue: String) -> Unit,
                        singleLine: Boolean = true, maxTextLength: Int, testTag: String) {
        var text by remember { mutableStateOf(TextFieldValue(initialValue))}
        OutlinedTextField(
            modifier = Modifier
                .width(300.dp)
                .testTag(testTag),
            value = text,
            label = { Text(label) },
            singleLine = singleLine,
            colors =  TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = MaterialTheme.colors.onSecondary,
                focusedLabelColor = MaterialTheme.colors.primary,
                unfocusedLabelColor = MaterialTheme.colors.onSecondary,
            ),
            maxLines = 5,
            onValueChange = { newText ->
                text = if (newText.text.length > maxTextLength) text else {
                    val lines = newText.text.split("\n")
                    if (lines.size > 4) text else newText
                }
                onChanged(text.text)
            }
        )
    }

    /**
     * The field where the player can write his/her nickname
     */
    @Composable
    fun NicknameTextField() {
        CustomTextField(
            label = "nickname",
            initialValue = nickname,
            onChanged = {newValue ->  nickname = newValue},
            maxTextLength = 15,
            testTag = "nicknameText",
        )
    }

    /**
     * The field where the player can write his/her description
     */
    @Composable
    fun DescriptionTextField() {
        CustomTextField(
            label = "description",
            initialValue = description,
            onChanged = {newValue ->  description = newValue},
            singleLine = false,
            maxTextLength = 130,
            testTag = "descriptionText",
        )
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
    fun ProfileModifyingPreview() {
        PolypolyTheme {
            val id = intent.getLongExtra("userId", 0)
            val user = FakeRemoteStorage.instance.getUserWithId(id).get()
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                ProfileForm(user)
            }
        }
    }
}