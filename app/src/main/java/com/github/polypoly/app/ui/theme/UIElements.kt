package com.github.polypoly.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Object where all the common UI elements are stored, as long as their properties
 */
object UIElements {
    /**
     * All the TextFields of polypoly should have these colors
     */
    @Composable
    fun outlineTextFieldColors(): TextFieldColors {
        return TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = MaterialTheme.colors.secondary,
            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedLabelColor = MaterialTheme.colors.secondary
        )
    }

    // ================== Buttons ================== //

    /**
     * Discreet button, use for non-main actions
     * @param onClick the action to perform when the button is clicked
     * @param text the text to display on the button
     * @param testTag the test tag of the button
     * @param width the width of the button in dp (default 200)
     * @param height the height of the button in dp (default 60)
     */
    @Composable
    fun DiscreetButton(onClick: () -> Unit, text: String, testTag: String,
                       width: Int = 200, height: Int = 60) {
        Button(
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            onClick = onClick,
            shape = CircleShape,
            modifier = Modifier
                .testTag(testTag)
                .height(height.dp)
                .width(width.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryVariant)
        ) {
            Text(text)
        }
    }

    /**
     * Round button with an icon, use for main actions
     * @param onClick the action to perform when the button is clicked
     * @param icon the icon to display on the button
     * @param iconDescription the description of the icon
     * @param testTag the test tag of the button
     */
    @Composable
    fun IconRoundButton(
        onClick: () -> Unit,
        icon: ImageVector,
        iconDescription: String,
        testTag: String
    ) {
        Button(
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            onClick = onClick,
            shape = CircleShape,
            modifier = Modifier
                .testTag(testTag)
                .size(60.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary)
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = icon,
                contentDescription = iconDescription,
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }

    /**
     * Button for the main actions. Can be disabled. If disabled, the button will be greyed out.
     * @param onClick the action to do when the button is clicked
     * @param text the text to display on the button
     * @param testTag the tag to use for testing
     * @param enabled whether the button is enabled or not
     * @param width the width of the button in dp, default is 200
     * @param height the height of the button in dp, default is 60
     */
    @Composable
    fun MainActionButton(onClick: () -> Unit, text: String, testTag: String, enabled: Boolean,
                         width: Int = 200, height: Int = 60) {
        remember { enabled }
        Button(
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            onClick = onClick,
            modifier = Modifier
                .width(width.dp)
                .height(height.dp)
                .testTag(testTag),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                disabledBackgroundColor = if (isSystemInDarkTheme()) grey2 else grey1,
                disabledContentColor = if (isSystemInDarkTheme()) grey1 else grey2,
            ),
            shape = CircleShape,
        ) {
            Text(text = text)
        }
    }
}