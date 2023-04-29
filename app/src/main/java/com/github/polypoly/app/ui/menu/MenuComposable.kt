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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import com.github.polypoly.app.R
import com.github.polypoly.app.base.RulesObject
import com.github.polypoly.app.ui.menu.profile.ProfileActivity
import com.github.polypoly.app.ui.menu.settings.SettingsActivity

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
                iconId = if(display) R.drawable.tmp_sadsmile else R.drawable.tmp_happysmile,
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
    private fun RulesButton() {
        var openRules by remember { mutableStateOf(false) }
        OptionButton(
            onClick = { openRules = true },
            iconId = R.drawable.tmp_happysmile,
            description = "Show Rules"
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
            iconId = R.drawable.tmp_happysmile,
            description = "Open Profile"
        )
    }

    @Composable
    private fun RankingsButton() {
        // TODO: delete this OptionButton and uncomment the block below when RankingActivity exists
        OptionButton(
            onClick = {},
            iconId = R.drawable.tmp_happysmile,
            description = "Open Rankings"
        )
        /*ActivityOptionButton(
            destinationActivity = RankingsActivity::class.java,
            iconId = R.drawable.tmp_happysmile,
            description = "Open Rankings"
        )*/
    }

    @Composable
    private fun SettingsButton() {
        ActivityOptionButton(
            destinationActivity = SettingsActivity::class.java,
            iconId = R.drawable.tmp_happysmile,
            description = "Open Settings"
        )
    }


    // ===================================================== COMMON COMPOSABLES
    /**
     * Creates a button with a small image that'll be used to open other pop-ups or activities.
     */
    @Composable
    private fun OptionButton(onClick: () -> Unit, iconId: Int, description: String) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(70.dp)
                .semantics { contentDescription = description },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(iconId),
                contentDescription = "",
                modifier = Modifier.size(50.dp)
            )
        }
    }

    /**
     * An OptionButton that launches a given activity, giving the sender class name in an intent
     */
    @Composable
    private fun ActivityOptionButton(destinationActivity: Class<out MenuActivity>, iconId: Int, description: String) {
        val mContext = LocalContext.current
        val activityIntent = Intent(mContext, destinationActivity)
        OptionButton(
            onClick = { startActivity(mContext, activityIntent, null) },
            iconId = iconId,
            description = description
        )
    }
}