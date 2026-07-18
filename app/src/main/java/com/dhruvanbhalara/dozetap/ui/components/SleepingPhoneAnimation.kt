package com.dhruvanbhalara.dozetap.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Custom canvas animation illustrating a sleeping phone with pulsing breathing effect and floating Zzz particles.
 *
 * @param modifier Root layout modifier.
 */
@Composable
fun SleepingPhoneAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sleeping_phone")

    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    val zAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "z_alpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .size(160.dp)
            .scale(breathScale),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(144.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryContainer.copy(alpha = 0.7f),
                            tertiaryColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val phoneWidth = canvasWidth * 0.42f
            val phoneHeight = canvasHeight * 0.72f
            val phoneLeft = (canvasWidth - phoneWidth) / 2f
            val phoneTop = (canvasHeight - phoneHeight) / 2f + 10f

            drawRoundRect(
                color = primaryColor.copy(alpha = 0.2f),
                topLeft = Offset(phoneLeft + 4f, phoneTop + 6f),
                size = Size(phoneWidth, phoneHeight),
                cornerRadius = CornerRadius(24f, 24f)
            )

            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(phoneLeft, phoneTop),
                size = Size(phoneWidth, phoneHeight),
                cornerRadius = CornerRadius(24f, 24f),
                style = Stroke(width = 6f)
            )

            drawRoundRect(
                color = primaryContainer.copy(alpha = 0.65f),
                topLeft = Offset(phoneLeft + 6f, phoneTop + 6f),
                size = Size(phoneWidth - 12f, phoneHeight - 12f),
                cornerRadius = CornerRadius(18f, 18f)
            )

            drawRoundRect(
                color = primaryColor.copy(alpha = 0.85f),
                topLeft = Offset(canvasWidth / 2f - 12f, phoneTop + 14f),
                size = Size(24f, 6f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            val eyeY = phoneTop + phoneHeight * 0.45f
            drawLine(
                color = primaryColor,
                start = Offset(phoneLeft + 18f, eyeY),
                end = Offset(phoneLeft + 32f, eyeY),
                strokeWidth = 5f
            )
            drawLine(
                color = primaryColor,
                start = Offset(phoneLeft + phoneWidth - 32f, eyeY),
                end = Offset(phoneLeft + phoneWidth - 18f, eyeY),
                strokeWidth = 5f
            )

            val zColor = primaryColor.copy(alpha = zAlpha)
            drawLine(zColor, Offset(phoneLeft + phoneWidth + 6f, phoneTop + 10f), Offset(phoneLeft + phoneWidth + 24f, phoneTop + 10f), 4.5f)
            drawLine(zColor, Offset(phoneLeft + phoneWidth + 24f, phoneTop + 10f), Offset(phoneLeft + phoneWidth + 6f, phoneTop + 24f), 4.5f)
            drawLine(zColor, Offset(phoneLeft + phoneWidth + 6f, phoneTop + 24f), Offset(phoneLeft + phoneWidth + 24f, phoneTop + 24f), 4.5f)

            val mColor = tertiaryColor.copy(alpha = (zAlpha * 0.8f).coerceIn(0f, 1f))
            drawLine(mColor, Offset(phoneLeft + phoneWidth + 20f, phoneTop - 8f), Offset(phoneLeft + phoneWidth + 34f, phoneTop - 8f), 3.5f)
            drawLine(mColor, Offset(phoneLeft + phoneWidth + 34f, phoneTop - 8f), Offset(phoneLeft + phoneWidth + 20f, phoneTop + 2f), 3.5f)
            drawLine(mColor, Offset(phoneLeft + phoneWidth + 20f, phoneTop + 2f), Offset(phoneLeft + phoneWidth + 34f, phoneTop + 2f), 3.5f)
        }
    }
}
