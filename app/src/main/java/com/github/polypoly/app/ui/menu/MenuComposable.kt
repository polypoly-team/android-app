package com.github.polypoly.app.ui.menu

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import com.github.polypoly.app.base.menu.RulesObject
import com.github.polypoly.app.ui.menu.profile.ProfileActivity
import com.github.polypoly.app.ui.menu.rankings.RankingsActivity
import com.github.polypoly.app.ui.menu.settings.SettingsActivity
import com.github.polypoly.app.ui.theme.UIElements.IconRoundButton

object MenuComposable {
    /**
     * Constants used in the composables
     */
    private val spaceBetweenButtons = 10.dp

    // ===================================================== MENU DISPOSITIONS

    /**
     * A row of all the buttons in the menu to go to the different activities
     * (rules, profile, rankings, settings)
     */
    @Composable
    fun RowButtons() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spaceBetweenButtons, Alignment.CenterHorizontally),
            modifier = Modifier
                .padding(spaceBetweenButtons)
                .fillMaxWidth()
        ) {
            AllButtons()
        }
    }

    /**
     * A column of all the buttons in the menu to go to the different activities
     * (rules, profile, rankings, settings) with a button to retract the column
     */
    @Composable
    fun ColumnButtons() {
        Column(
            verticalArrangement = Arrangement.spacedBy(spaceBetweenButtons, Alignment.Bottom),
            modifier = Modifier
                .padding(spaceBetweenButtons)
                .fillMaxHeight()
        ) {
            var display by remember { mutableStateOf(false) }
            if(display) {
                AllButtons()
            }
            OptionButton(
                onClick = { display = !display },
                icon = if(display) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                description = "display_options"
            )
        }

    }

    /**
     * All the buttons in the menu to go to the different activities
     */
    @Composable
    private fun AllButtons() {
        RulesButton()
        ProfileButton()
        RankingsButton()
        SettingsButton()
    }

    // ===================================================== MENU BUTTONS
    /**
     * The button toggles on a dialog component where the rules are displayed.
     * The rules are scrollable and all the text is stored in RulesObject.
     *
     * The dialog box is closed when clicking outside of it
     */
    @Composable
    fun RulesButton() {
        var openRules by remember { mutableStateOf(false) }
        IconRoundButton(
            onClick = { openRules = true },
            icon = Icons.Default.ReceiptLong,
            iconDescription = "Show Rules",
            testTag = "rules_button"
        )

        if(openRules) {
            Dialog(
                onDismissRequest = { openRules = false },
            ) {
                Surface(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.98f)
                        .fillMaxHeight(0.95f)
                ) {
                    LazyColumn(modifier = Modifier.padding(20.dp)) {
                        item {
                            Text(
                                text = RulesObject.rulesTitle,
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
     * Creates a button to go to the profile of the user
     */
    @Composable
    private fun ProfileButton() {
        ActivityOptionButton(
            destinationActivity = ProfileActivity::class.java,
            icon = Icons.Default.Person,
            description = "Open Profile",
            testTag = "profile_button"
        )
    }

    /**
     * Creates a button to go to the rankings of the users
     */
    @Composable
    private fun RankingsButton() {
        ActivityOptionButton(
            destinationActivity = RankingsActivity::class.java,
            icon = Icons.Default.Equalizer,
            description = "Open Rankings",
            testTag = "rankings_button"
        )
    }

    /**
     * Creates a button to go to the settings of the app
     */
    @Composable
    fun SettingsButton() {
        ActivityOptionButton(
            destinationActivity = SettingsActivity::class.java,
            icon = Icons.Default.Settings,
            description = "Open Settings",
            testTag = "settings_button"
        )
    }


    // ===================================================== COMMON COMPOSABLES
    /**
     * Creates a button with a small image that'll be used to open other pop-ups or activities.
     */
    @Composable
    private fun OptionButton(onClick: () -> Unit, icon: ImageVector, description: String) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(70.dp)
                .semantics { contentDescription = description },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier.size(50.dp)
            )
        }
    }

    /**
     * An [IconRoundButton] that launches a given activity, giving the sender class name in an intent
     * @param destinationActivity the activity to launch
     * @param icon the icon to display
     * @param description the description of the icon
     * @param testTag the test tag of the button
     */
    @Composable
    private fun ActivityOptionButton(destinationActivity: Class<out MenuActivity>, icon: ImageVector,
                                     description: String, testTag: String) {
        val mContext = LocalContext.current
        val activityIntent = Intent(mContext, destinationActivity)
        IconRoundButton(
            onClick = { startActivity(mContext, activityIntent, null) },
            icon = icon,
            iconDescription = description,
            testTag = testTag
        )
    }
}