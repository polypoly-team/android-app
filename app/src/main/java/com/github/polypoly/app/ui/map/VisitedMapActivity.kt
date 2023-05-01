package com.github.polypoly.app.ui.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.polypoly.app.ui.theme.PolypolyTheme

/**
 * The map the user can visit when he/she is connected as a guest
 */
class VisitedMapActivity : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolypolyTheme {
                MapUI.MapView()
            }
        }
    }


    // =============== Preview =============== //

    @Preview(
        name = "Light Mode"
    )
    @Composable
    fun ProfilePreview() {
        PolypolyTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                MapUI.MapView()
            }
        }
    }
}