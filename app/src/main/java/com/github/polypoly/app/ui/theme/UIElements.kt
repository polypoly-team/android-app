package com.github.polypoly.app.ui.theme

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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

    /**
     * All the checkboxes of polypoly should have these colors
     */
    @Composable
    fun checkboxColors() : CheckboxColors {
        return CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colors.primary,
            uncheckedColor = MaterialTheme.colors.secondary,
            checkmarkColor = MaterialTheme.colors.onPrimary,
            disabledColor = MaterialTheme.colors.onSecondary,
            disabledIndeterminateColor = MaterialTheme.colors.onSecondary,
        )
    }

    /**
     * common big button
     * @param onClick the action to do when the button is clicked
     * @param text the text to show in the button
     * @param enabled if the button is enabled or not
     * @param testTag the test tag of the button
     * @return the button
     */
    @Composable
    fun BigButton(onClick: () -> Unit, text: String, enabled: Boolean = true, testTag: String = "Undefined") {
        Button(
            onClick = onClick,
            modifier = Modifier
                .width(200.dp)
                .height(70.dp)
                .testTag(testTag),
            enabled = enabled
        ) {
            Text(text = text)
        }
    }
}