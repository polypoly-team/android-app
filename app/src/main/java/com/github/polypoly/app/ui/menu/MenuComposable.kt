package com.github.polypoly.app.ui.menu

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import com.github.polypoly.app.R
import com.github.polypoly.app.base.RulesObject
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
            iconDescription = stringResource(R.string.rules_button_description),
            testTag = "rules_button"
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

    @Composable
    private fun ProfileButton() {
        ActivityOptionButton(
            destinationActivity = ProfileActivity::class.java,
            icon = Icons.Default.Person,
            description = stringResource(R.string.profile_button_description),
            testTag = "profile_button"
        )
    }

    @Composable
    private fun RankingsButton() {
        ActivityOptionButton(
            destinationActivity = RankingsActivity::class.java,
            icon = Icons.Default.Equalizer,
            description = stringResource(R.string.rankings_button_description),
            testTag = "rankings_button"
        )
    }

    @Composable
    fun SettingsButton() {
        ActivityOptionButton(
            destinationActivity = SettingsActivity::class.java,
            icon = Icons.Default.Settings,
            description = stringResource(R.string.settings_button_description),
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