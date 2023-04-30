package com.github.polypoly.app.ui.menu.profile

import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.theme.UIElements

/**
 * A field where the user can write his/her info, as his/her nickname for example
 * @param label The label of the field
 * @param initialValue Initial Value of the field when it is built
 * @param onChanged Call when the content of the field is changed
 * @param singleLine If the Text Field has juste one line
 * @param maxTextLength The number of the maximum characters accepted
 * @param testTag The test tag of the field
 */
@Composable
fun CustomTextField(label: String, initialValue: String, onChanged: (newValue: String) -> Unit,
                    singleLine: Boolean = true, maxTextLength: Int, testTag: String) {
    var text by remember { mutableStateOf(TextFieldValue(initialValue)) }

    OutlinedTextField(
        modifier = Modifier
            .width(300.dp)
            .testTag(testTag),
        value = text,
        label = { Text(label) },
        singleLine = singleLine,
        colors = UIElements.outlineTextFieldColors(),
        maxLines = 5,
        onValueChange = { newText ->
            text = if (newText.text.length > maxTextLength) text else {
                val lines = newText.text.split("\n")
                if (lines.size > 4) text else newText
            }
            onChanged(text.text)
        }
    )
}