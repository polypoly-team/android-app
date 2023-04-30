package com.github.polypoly.app.ui.menu.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.grey1
import com.github.polypoly.app.ui.theme.grey2

/**
 * This activity is used to create a new profile for the user
 * It is called when the the user registers for the first time or when he/she wants to play
 * as a guest
 */
class CreateProfileActivity : ComponentActivity() {

    /**
     * The value of the nickname chosen by the user
     */
    private var nickname = mutableStateOf("")

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
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            GoBackButton()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "How do you want\nto be named?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h4,
                )
                Spacer(modifier = Modifier.height(60.dp))
                NicknameTextField()
                Spacer(modifier = Modifier.height(40.dp))
                ValidateButton()
            }
        }
    }

    /**
     * The button to go back to the previous activity
     */
    @Composable
    private fun GoBackButton() {
        val navController = rememberNavController()
        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Return Arrow",
                tint = MaterialTheme.colors.onSecondary,
                modifier = Modifier.padding(10.dp)
                    .size(60.dp)
            )
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
            onChanged = {newValue ->  nickname.value = newValue},
            maxTextLength = 15,
            testTag = "nicknameText",
        )
    }

    /**
     * The button to validate the profile creation
     * It is disabled if the nickname is empty
     */
    @Composable
    private fun ValidateButton() {
        remember { nickname }
        Button(
            onClick = {},
            shape = CircleShape,
            modifier = Modifier
                .height(50.dp)
                .width(300.dp),
            enabled = nickname.value.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                disabledBackgroundColor = if (isSystemInDarkTheme()) grey2 else grey1,
                disabledContentColor = if (isSystemInDarkTheme()) grey1 else grey2,
            )
        ) {
            Text(text = "Let's go with this nickname!")
        }
    }

}