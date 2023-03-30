package com.github.polypoly.app.menu

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.ui.theme.PolypolyTheme

/**
 * This class represents the super class of all the MenuActivities. It has a top bar
 * to easily return to the caller activity
 */
open class MenuActivity(name: String) : ComponentActivity() {
    private val activityName: String
    init { activityName = name }

    // ===================================================== MAIN CONTENT
    @Composable
    fun MenuContent(content: @Composable () -> Unit) {
        PolypolyTheme {
            Column {
                TopAppBar(
                    title = { Text(text = activityName) },
                    navigationIcon = {
                        IconButton(onClick = {
                            finish()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "back_icon")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White,
                    elevation = 10.dp
                )
                Box {
                    content()
                }
            }
        }
    }
}