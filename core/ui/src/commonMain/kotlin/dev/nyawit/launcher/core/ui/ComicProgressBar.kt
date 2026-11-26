@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicProgressBar: Indikator progres bergaya komik/arcade retro dengan garis tinta tegas
 * dan arsiran garis diagonal animasi dinamis (animated hatched stripes).
 */
@Composable
fun ComicProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 22.dp,
    fillColor: Color? = null,
    label: String? = null,
    animated: Boolean = true,
) {
    val c = LocalNyawitColors.current
    val fill = fillColor ?: c.Primary
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(120),
    )

    // Animated diagonal stripe translation
    val infiniteTransition = rememberInfiniteTransition()
    val stripeAnimOffset by if (animated && progress in 0.01f..0.99f) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 16f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
            ),
        )
    } else {
        rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(animation = tween(1000)),
        )
    }

    Box(modifier = modifier.padding(bottom = 2.dp, end = 2.dp)) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(x = 2.dp, y = 2.dp)
                .background(Color(0xFF000000), RoundedCornerShape(4.dp))
                .border(1.5.dp, c.ComicShadowRim, RoundedCornerShape(4.dp)),
        )

        // Track & Fill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(c.SurfaceRaised, RoundedCornerShape(4.dp))
                .border(2.dp, c.ComicInk, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.CenterStart,
        ) {
            // Fill bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(fill),
            ) {
                // Diagonal hatched lines texture over progress (animated sliding lines)
                Canvas(modifier = Modifier.matchParentSize()) {
                    val step = 12.dp.toPx()
                    var x = -size.height - step + (stripeAnimOffset * density) % step
                    while (x < size.width + step) {
                        drawLine(
                            color = Color(0xFF0F172A).copy(alpha = 0.22f),
                            start = Offset(x, size.height),
                            end = Offset(x + size.height, 0f),
                            strokeWidth = 2.5.dp.toPx(),
                        )
                        x += step
                    }
                }
            }

            // Power cells notches (25%, 50%, 75%)
            Canvas(modifier = Modifier.matchParentSize()) {
                val tickWidth = 1.5.dp.toPx()
                val tickColor = Color(0xFF0F172A).copy(alpha = 0.35f)
                drawLine(tickColor, Offset(size.width * 0.25f, 0f), Offset(size.width * 0.25f, size.height), tickWidth)
                drawLine(tickColor, Offset(size.width * 0.50f, 0f), Offset(size.width * 0.50f, size.height), tickWidth)
                drawLine(tickColor, Offset(size.width * 0.75f, 0f), Offset(size.width * 0.75f, size.height), tickWidth)
            }

            // Percentage / label overlay
            val text = label ?: "${(animatedProgress * 100).toInt()}%"
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = text.uppercase(),
                    style = NyawitType.Overline,
                    fontWeight = FontWeight.Black,
                    color = if (animatedProgress > 0.45f) c.OnPrimary else c.Text,
                    fontSize = 9.sp,
                    letterSpacing = 0.6.sp,
                )
                if (progress in 0.01f..0.99f) {
                    Text(
                        text = "● AKTIF",
                        style = NyawitType.Overline,
                        fontWeight = FontWeight.Black,
                        color = if (animatedProgress > 0.85f) c.OnPrimary else c.Primary,
                        fontSize = 8.sp,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ComicProgressBarPreview() {
    ComicPreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ComicProgressBar(progress = 0.25f, label = "MENGUNDUH 25%")
            ComicProgressBar(progress = 0.65f, label = "MEMASANG 65%")
            ComicProgressBar(progress = 1.0f, label = "SELESAI 100%")
        }
    }
}
