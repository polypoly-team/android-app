package com.github.polypoly.app.ui.menu

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.theme.PolypolyTheme

/**
 * This class represents the super class of all the MenuActivities. It has a top bar
 * to easily return to the caller activity
 */
open class MenuActivity(val name: Int) : ComponentActivity() {
    // ===================================================== MAIN CONTENT
    @Composable
    fun MenuContent(content: @Composable () -> Unit) {
        PolypolyTheme {
            Column {
                TopAppBar(
                    title = { Text(text = stringResource(name)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            finish()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "back_icon")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.background,
                    elevation = 10.dp
                )
                Box {
                    content()
                }
            }
        }
    }
}