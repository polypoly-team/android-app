package com.github.polypoly.app.ui.menu.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.theme.PolypolyTheme

class CreateProfileActivity : ComponentActivity() {

    /**
     * The value of the nickname chosen by the user
     */
    private var nickname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                CreateProfileContent()
            }
        }
    }

    /**
     * The content of the activity
     */
    @Composable
    private fun CreateProfileContent() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Choose your nickname")
            Spacer(modifier = Modifier.height(40.dp))
            NicknameTextField()
            Spacer(modifier = Modifier.height(40.dp))
            ValidateButton()
        }
    }

    /**
     * The text field where the user can choose his/her nickname
     */
    @Composable
    private fun NicknameTextField() {
        CustomTextField(
            label = "nickname",
            initialValue = "",
            onChanged = {newValue ->  nickname = newValue},
            maxTextLength = 15,
            testTag = "nicknameText",
        )
    }

    /**
     * The button to validate the profile creation
     */
    @Composable
    private fun ValidateButton() {
        Button(
            onClick = {},
            shape = CircleShape,
            modifier = Modifier
                .height(50.dp)
                .width(300.dp)
        ) {
            Text(text = "Let's go with this nickname!")
        }
    }

}