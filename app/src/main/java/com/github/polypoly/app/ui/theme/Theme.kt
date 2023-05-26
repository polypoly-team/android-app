package com.github.polypoly.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * The light theme colors
 */
private val LightColorPalette = darkColors(
    primary = redPrincipal,
    secondary = purpleLight,
    background = white,
    secondaryVariant = purpleVeryLight,
    onPrimary = Color.White,
    onBackground = purpleVeryDark,
    onSecondary = purpleVeryDark
)

/**
 * The dark theme colors
 */
private val DarkColorPalette = lightColors(
    primary = redPrincipal,
    secondary = purpleMid,
    background = purpleVeryDark,
    secondaryVariant = purpleLight,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSecondary = Color.White,
)

/**
 * init the theme in function of the system theme
 */
@Composable
fun PolypolyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}