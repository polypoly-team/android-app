package com.github.polypoly.app.ui.commons

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Creates a circular loader component that spins forever
 * @param size loader size
 * @param sweepAngle angle covered by the rotating circular arc
 * @param duration time length of the animation in milli seconds
 * @param animationColor Color of the animated circular arc
 * @param backgroundColor Color of the background circle
 * @param strokeWidth width of the loader's circular strokes
 * @see https://stackoverflow.com/questions/73966501/circular-loading-spinner-in-jetpack-compose for implementation referenece
 */
@Composable
fun CircularLoader(
    size: Dp = 32.dp,
    sweepAngle: Float = 90f,
    duration: Int,
    animationColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
) {
    val transition = rememberInfiniteTransition()

    val currentArcStartAngle by transition.animateValue(
        0,
        360,
        Int.VectorConverter,
        infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = LinearEasing
            )
        )
    )

    // define stroke with given width and arc ends type considering device DPI
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Square)
    }

    // draw on canvas
    Canvas(
        Modifier
            .progressSemantics()
            .size(size)
            .padding(strokeWidth / 2) // whole circle fits in the canvas
    ) {
        // draw "background" circle.
        drawCircle(backgroundColor, style = stroke)

        // draw animated arc
        drawArc(
            animationColor,
            startAngle = currentArcStartAngle.toFloat() - 90,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = stroke
        )
    }
}