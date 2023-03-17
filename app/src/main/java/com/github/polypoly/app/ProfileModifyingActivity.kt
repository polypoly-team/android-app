package com.github.polypoly.app

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.game.Skin
import com.github.polypoly.app.game.Stats
import com.github.polypoly.app.game.User
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
        val user = FakeRemoteStorage.instance.getUserProfileWithId(id)
        nickname = user.get().name
        description = user.get().bio
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
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ProfileForm() {
        val mContext = LocalContext.current
        var warningText by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NicknameTextField()
            Spacer(modifier = Modifier.height(10.dp))
            DescriptionTextField()
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                shape = CircleShape,
                modifier = Modifier.testTag("registerInfoButton"),
                onClick = {
                    if(nickname.isEmpty()) {
                        warningText = "You can't have an empty nickname!"
                    } else {
                        val id = intent.getLongExtra("userId", 0)
                        FakeRemoteStorage.instance.setUserProfileWithId(id, User(
                            id = id,
                            name = nickname,
                            bio = description,
                            skin = Skin(0,0,0),
                            stats = Stats(LocalDateTime.MIN, LocalDateTime.MAX, 45)
                        ))
                        val profileIntent = Intent(mContext, ProfileActivity::class.java)
                        startActivity(profileIntent)
                    }
                }
            ) {
                Text(text = "OK")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier = Modifier.offset(y = 200.dp),
                text = warningText,
                style = MaterialTheme.typography.body2
            )
        }
    }


    /**
     * The field where the player can write his/her nickname
     */
    @Composable
    fun NicknameTextField() {
        var text by remember { mutableStateOf(TextFieldValue(nickname))}
        OutlinedTextField(
            modifier = Modifier
                .width(300.dp)
                .testTag("nicknameText"),
            value = text,
            label = { Text("nickname") },
            singleLine = true,
            onValueChange = { newText ->
                text = if (newText.text.length > 15) text else newText
                nickname = text.text
            })
    }

    /**
     * The field where the player can write his/her description
     */
    @Composable
    fun DescriptionTextField() {
        var text by remember { mutableStateOf(TextFieldValue(description))}
        OutlinedTextField(
            modifier = Modifier
                .width(300.dp)
                .height(150.dp)
                .testTag("descriptionText"),
            value = text,
            label = { Text("description") },
            singleLine = false,
            onValueChange = { newText ->
                text = if (newText.text.length > 130) text else newText
                description = text.text
            })
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
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                ProfileForm()
            }
        }
    }
}