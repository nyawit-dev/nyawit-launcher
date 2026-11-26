@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * ComicActionBurst: Stiker bintang ledakan pop-art komik (12-point Starburst / Explosion Burst)
 * untuk tulisan onomatopoeia atau status heboh seperti "BOOM!", "HOT!", "NEW!", "PLAY!", "UPDATE!".
 * Mendukung aksi interaktif: saat disentuh/diklik, stiker membal (spring bounce) dinamis.
 */
@Composable
fun ComicActionBurst(
    text: String,
    modifier: Modifier = Modifier,
    burstColor: Color = Color(0xFFFFDE00),
    textColor: Color = Color(0xFF0F172A),
    outlineColor: Color = Color(0xFF0F172A),
    size: Dp = 48.dp,
    onClick: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.22f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 600f),
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Shadow offset layer
        Canvas(modifier = Modifier.size(size).offset(x = 2.dp, y = 2.dp)) {
            val path = createBurstPath(this.size.width, this.size.height, points = 12)
            drawPath(path, Color(0xFF000000), style = Fill)
        }

        // Foreground burst with thick ink outline
        Canvas(modifier = Modifier.size(size)) {
            val path = createBurstPath(this.size.width, this.size.height, points = 12)
            drawPath(path, burstColor, style = Fill)
            drawPath(path, outlineColor, style = Stroke(width = 2.dp.toPx()))
        }

        Text(
            text = text,
            style = NyawitType.Overline,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = textColor,
            fontSize = (size.value * 0.22f).sp,
            letterSpacing = 0.5.sp,
        )
    }
}

private fun createBurstPath(width: Float, height: Float, points: Int): Path {
    val path = Path()
    val centerX = width / 2f
    val centerY = height / 2f
    val outerRadius = width.coerceAtMost(height) / 2f - 2f
    val innerRadius = outerRadius * 0.62f

    val totalPoints = points * 2
    val angleStep = (2 * PI / totalPoints).toFloat()

    for (i in 0 until totalPoints) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = i * angleStep - (PI / 2).toFloat()
        val x = centerX + radius * cos(angle)
        val y = centerY + radius * sin(angle)

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    return path
}

@Preview
@Composable
private fun ComicActionBurstPreview() {
    ComicPreviewTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ComicActionBurst(text = "BOOM!", onClick = {})
            ComicActionBurst(text = "HOT!", burstColor = Color(0xFFFFDE00), textColor = Color(0xFF0F172A), onClick = {})
            ComicActionBurst(text = "UP!", burstColor = Color(0xFFFF4757), textColor = Color.White, onClick = {})
            ComicActionBurst(text = "GO!", burstColor = Color(0xFF00D2D3), textColor = Color(0xFF0F172A), onClick = {})
        }
    }
}
