package com.github.polypoly.app.menu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.polypoly.app.menu.kotlin.GameMusic
import com.github.polypoly.app.ui.theme.PolypolyTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SettingsContent() }
    }
    @Preview(showBackground = true)
    @Composable
    fun SettingsPreview() { SettingsContent() }

    // ===================================================== MAIN CONTENT
    @Composable
    fun SettingsContent() {
        PolypolyTheme {
            Column {
                Button(onClick = { GameMusic.mute() }) {
                    Text(text = "mute song")
                }
                Button(onClick = { GameMusic.unMute() }) {
                    Text(text = "unMute song")
                }
            }
        }
    }
}