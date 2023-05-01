package com.github.polypoly.app.ui.theme

import androidx.compose.material.*
import androidx.compose.runtime.Composable

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
}