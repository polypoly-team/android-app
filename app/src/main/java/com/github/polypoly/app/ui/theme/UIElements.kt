package com.github.polypoly.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    // ========== Buttons ==========

    /**
     * Discreet button, use for non-main actions
     * @param onClick the action to perform when the button is clicked
     * @param text the text to display on the button
     * @param testTag the test tag of the button
     */
    @Composable
    fun DiscreetButton(onClick: () -> Unit, text: String, testTag: String) {
        Button(
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                disabledElevation = 0.dp
            ),
            onClick = onClick,
            shape = CircleShape,
            modifier = Modifier
                .testTag(testTag),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryVariant)
        ) {
            Text(text)
        }
    }
}