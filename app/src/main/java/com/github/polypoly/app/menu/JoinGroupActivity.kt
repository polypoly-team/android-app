package com.github.polypoly.app.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.R
import com.github.polypoly.app.RulesObject
import com.github.polypoly.app.ui.theme.PolypolyTheme

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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(100.dp))
                        Image(
                            painter = painterResource(id = R.drawable.super_cool_logo),
                            contentDescription = "polypoly logo",
                            modifier = Modifier
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GroupTextField(15, warningState) // TODO: create a constant for the max length -> create a class for the constants
                Spacer(modifier = Modifier.height(10.dp))
                RectangleButton(
                    onClick = {
                        warningState.value = groupCodeButtonOnClick()
                        if (warningState.value == "Joined group with code $groupCode") {
                            joinGroupRoom(mContext)
                        }
                    }
                    , description = getString(R.string.join_group_button_text)
                    , testTag = "JoinGroupButton")
                Text(
                    text = warningState.value,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.testTag("warningMessage")
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            GroupListButton()
        }
    }


    /**
     * This function returns the TextField where the user prompts their group code.
     */
    @Composable
    fun GroupTextField(maxLength: Int, warningState : MutableState<String>) {
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
                warningState.value = groupCodeButtonOnClick()
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
     * This function returns the button that lets the user open the groups list.
     */
    @Composable
    fun GroupListButton() {
        var openRules by remember { mutableStateOf(false) }
        RectangleButton(
            onClick = { openRules = true },
            description = "Show Groups",
            testTag = "showGroupsButton"
        )

        if(openRules) {
            Dialog(
                onDismissRequest = { openRules = false },
            ) {
                Surface(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.95f)
                ) {
                    LazyColumn(modifier = Modifier.padding(20.dp)) {
                        item {
                            Text(
                                text = getString(R.string.group_list_title),
                                style = MaterialTheme.typography.h4
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        items(items = RulesObject.rulesChapters, itemContent = { item ->
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.h5
                            )
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.body1
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        })
                    }
                }
            }
        }
    }

    /**
     * Creates a rectangle button with a text and a linked action.
     * @param onClick (Function): The action to be performed when the button is clicked
     * @param description (String): The text to be displayed
     * @param testTag (String): The test tag to be used for testing
     */
    @Composable
    fun RectangleButton(onClick: () -> Unit, description: String = "blank description", testTag: String = "Undefined",) {
        Button(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .testTag(testTag)
                .semantics { contentDescription = description },
            onClick = onClick
        ) {
            Text(text = description)
        }
    }

    /**
     * This function is called when the user clicks on the button to join a group.
     * If the group code is empty, or the code is not in the DB, or the group is full,
     * it displays a warning message.
     * Otherwise, it calls the function to join the group.
     * @return (String): The warning message to be displayed
     */
    private fun groupCodeButtonOnClick(): String {
        if (groupCode.isEmpty()) {
            return getString(R.string.group_code_is_empty)
        } else if (!dbContainsGroupCode(groupCode)) {
            return getString(R.string.group_does_not_exist)
        } else if(groupIsFull(groupCode)){
            return getString(R.string.group_is_full)
        } else {
            return getString(R.string.joined_group_with_code) + groupCode
        }
    }

    /**
     * This function launches the group room activity and passes the group code to it.
     */
    private fun joinGroupRoom(mContext : Context) {

        // TODO: link to the group room activity
    }

    /**
     * This function checks if the group code is in the database.
     * @param groupCode (String): The group code to check
     * @return (Boolean): True if the group code is in the database, false otherwise
     * TODO: rewrite this function to check the real database
     */
    private fun dbContainsGroupCode(groupCode: String): Boolean {
        return mockGroupCodes.contains(groupCode)
    }

    /**
     * This function checks if the group is full.
     * @param groupCode (String): The group code to check
     * @return (Boolean): True if the group is full, false otherwise
     * TODO: rewrite this function to check the real database
     */
    private fun groupIsFull(groupCode: String): Boolean {
        return groupCode != "1234"
    }


    // ------------------- MOCKUP CODE -------------------
    // This code is only here to show how the group room activity should be called
    // It will be removed when the group room activity is created and the database is set up

    // mock DB
    private val mockGroupCodes = listOf("1234", "abcd", "123abc", "abc123")


}



