package com.github.polypoly.app

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.theme.PolypolyTheme

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
                        // For later
                        /*val registerInfoIntent = Intent(mContext, ProfileActivity::class.java)
                        registerInfoIntent.putExtra("nickname", nickname)
                        registerInfoIntent.putExtra("description", description)
                        startActivity(registerInfoIntent)*/
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
        var text by remember { mutableStateOf(TextFieldValue(""))}
        OutlinedTextField(
            modifier = Modifier
                .width(300.dp)
                .testTag("nameField"),
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
        var text by remember { mutableStateOf(TextFieldValue(""))}
        OutlinedTextField(
            modifier = Modifier
                .width(300.dp)
                .height(150.dp)
                .testTag("nameField"),
            value = text,
            label = { Text("description") },
            singleLine = false,
            onValueChange = { newText ->
                text = if (newText.text.length > 130) text else newText
                description = text.text
            })
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
                ProfileForm()
            }
        }
    }
}