package com.github.polypoly.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.theme.PolypolyTheme
import timber.log.Timber
import java.io.Console

@Suppress("UNUSED_EXPRESSION")
class JoinGroupActivity : ComponentActivity() {

    /**
     * The attributes of the class
     */
    private var groupCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                // This is the surface where all the view lies
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(100.dp))
                        Image(
                            painter = painterResource(id = R.drawable.super_cool_logo),
                            contentDescription = "polypoly logo",
                            modifier = Modifier
                                .width(200.dp)
                                .height(200.dp)
                                .testTag("logo"),
                            )
                        Spacer(modifier = Modifier.height(50.dp))
                        GroupForm()
                    }
                }
            }
        }
    }


    /**
     * Component where the user can write the group number. If the group number is valid,
     * the button lets the player join the group. Otherwise, it displays a warning message.
     */
    @Composable
    fun GroupForm() {
        val mContext = LocalContext.current
        val warningState = remember { mutableStateOf("") }

        // Contains all the form, centered in the screen
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GroupTextField(15, warningState)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("JoinGroupButton"),
                onClick = {
                    warningState.value = groupCodeButtonOnClick(mContext)
                    if (warningState.value == "Joined group with code $groupCode") {
                        joinGroupRoom(mContext)
                    }
                }
            ) {
                Text(text = getString(R.string.join_group_button_text))
            }
            Text(
                text = warningState.value,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.testTag("warningMessage")
            )
        }
    }


    /**
     * This function returns the TextField where the user prompts their group code.
     * @param maxLength (Int): The maximal allowed code length
     */
    @Composable
    fun GroupTextField(maxLength: Int, warningState : MutableState<String>) {
        val mContext = LocalContext.current
        val focusManager = LocalFocusManager.current

        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier
                .width(200.dp)
                .testTag("groupCodeField"),
            // When user clicks on enter button, the focus is removed and the button is clicked
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                warningState.value = groupCodeButtonOnClick(mContext)
            }),
            value = text,
            label = { Text("Enter a group code") },
            singleLine = true,
            // text can only be letters and numbers (avoids ghost characters as the Enter key)
            onValueChange = { newText ->
                text = if (newText.matches(Regex("[a-zA-Z\\d]*")) && newText.length <= maxLength) newText else text
                groupCode = text
            }

        )

    }

    /**
     * This function is called when the user clicks on the button to join a group.
     * If the group code is empty, or the code is not in the DB, or the group is full,
     * it displays a warning message.
     * Otherwise, it calls the function to join the group.
     */
    private fun groupCodeButtonOnClick(mContext : Context): String {
        if (groupCode.isEmpty()) {
            return "Group code is empty"
            //showLoggableToast("Group code is empty")
        } else if (!dbContainsGroupCode(groupCode)) {
            return "Group does not exist"
            //showLoggableToast("Group does nos exist")
        } else if(groupIsFull(groupCode)){
            return "Group is full"
            //showLoggableToast("Group is full")

        } else {
            return "Joined group with code $groupCode"
        }
    }

    fun Context.showLoggableToast(message: String) {
        Timber.tag("Toast").d(message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * This function launches the group room activity and passes the group code to it.
     */
    private fun joinGroupRoom(mContext : Context) {
        val groupIntent = Intent(mContext, GreetingActivity::class.java)
        groupIntent.putExtra("groupCode", groupCode)

        // TODO: link to the group room activity
        Toast.makeText(mContext, "Joined group with code $groupCode", Toast.LENGTH_LONG).show()
    }

    /**
     * This function checks if the group code is in the database.
     * @param groupCode (String): The group code to check
     * TODO: rewrite this function to check the real database
     */
    private fun dbContainsGroupCode(groupCode: String): Boolean {
        return mockGroupCodes.contains(groupCode)
    }

    /**
     * This function checks if the group is full.
     * @param groupCode (String): The group code to check
     * TODO: rewrite this function to check the real database
     */
    private fun groupIsFull(groupCode: String): Boolean {
        return false
    }


    // ------------------- MOCKUP CODE -------------------
    // This code is only here to show how the group room activity should be called
    // It will be removed when the group room activity is created and the database is set up

    // mock DB
    private val mockGroupCodes = listOf("1234", "abcd", "123abc", "abc123")


}



