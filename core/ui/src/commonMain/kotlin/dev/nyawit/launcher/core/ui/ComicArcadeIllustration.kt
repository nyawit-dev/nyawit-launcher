@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.LocalNyawitColors
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicArcadeIllustration: Gambar ilustrasi vektor pop-art komik (Gamepad + Star Sparks)
 * dengan garis tinta hitam tegas tebal, aksen warna komik pop-art, dan aksi klik interaktif (spring bounce).
 */
@Composable
fun ComicArcadeIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    primaryColor: Color? = null,
    accentColor: Color = Color(0xFFFFDE00),
    onClick: (() -> Unit)? = null,
) {
    val c = LocalNyawitColors.current
    val prime = primaryColor ?: c.Primary
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 500f),
    )

    Canvas(
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
    ) {
        val w = this.size.width
        val h = this.size.height
        val inkStroke = 2.5.dp.toPx()

        // 1. Shadow Gamepad Body
        val bodyRect = Rect(w * 0.12f + 4f, h * 0.28f + 4f, w * 0.88f + 4f, h * 0.74f + 4f)
        drawRoundRect(
            color = Color(0xFF000000),
            topLeft = Offset(bodyRect.left, bodyRect.top),
            size = androidx.compose.ui.geometry.Size(bodyRect.width, bodyRect.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
        )

        // 2. Foreground Gamepad Body
        val fgRect = Rect(w * 0.12f, h * 0.28f, w * 0.88f, h * 0.74f)
        drawRoundRect(
            color = prime,
            topLeft = Offset(fgRect.left, fgRect.top),
            size = androidx.compose.ui.geometry.Size(fgRect.width, fgRect.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
        )
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(fgRect.left, fgRect.top),
            size = androidx.compose.ui.geometry.Size(fgRect.width, fgRect.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
            style = Stroke(width = inkStroke),
        )

        // 3. D-Pad (Left cross)
        val dpadX = w * 0.28f
        val dpadY = h * 0.51f
        val dpadSize = 14f
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(dpadX - dpadSize, dpadY - 4f),
            size = androidx.compose.ui.geometry.Size(dpadSize * 2, 8f),
        )
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(dpadX - 4f, dpadY - dpadSize),
            size = androidx.compose.ui.geometry.Size(8f, dpadSize * 2),
        )

        // 4. Action Buttons (Right 4 colored comic dots)
        val btnRadius = 4.5f
        drawCircle(color = accentColor, radius = btnRadius, center = Offset(w * 0.72f, h * 0.44f))
        drawCircle(color = Color(0xFF0F172A), radius = btnRadius, center = Offset(w * 0.72f, h * 0.44f), style = Stroke(width = 1.5.dp.toPx()))

        drawCircle(color = Color(0xFFFF4757), radius = btnRadius, center = Offset(w * 0.80f, h * 0.52f))
        drawCircle(color = Color(0xFF0F172A), radius = btnRadius, center = Offset(w * 0.80f, h * 0.52f), style = Stroke(width = 1.5.dp.toPx()))

        drawCircle(color = Color(0xFF00D2D3), radius = btnRadius, center = Offset(w * 0.64f, h * 0.52f))
        drawCircle(color = Color(0xFF0F172A), radius = btnRadius, center = Offset(w * 0.64f, h * 0.52f), style = Stroke(width = 1.5.dp.toPx()))

        // 5. Star Sparks
        drawComicSparkle(this, Offset(w * 0.16f, h * 0.16f), 8f, accentColor)
        drawComicSparkle(this, Offset(w * 0.84f, h * 0.18f), 6f, Color(0xFFFFFFFF))
    }
}

private fun drawComicSparkle(scope: androidx.compose.ui.graphics.drawscope.DrawScope, center: Offset, radius: Float, color: Color) {
    val path = Path()
    path.moveTo(center.x, center.y - radius)
    path.quadraticTo(center.x, center.y, center.x + radius, center.y)
    path.quadraticTo(center.x, center.y, center.x, center.y + radius)
    path.quadraticTo(center.x, center.y, center.x - radius, center.y)
    path.quadraticTo(center.x, center.y, center.x, center.y - radius)
    path.close()
    scope.drawPath(path, color)
    scope.drawPath(path, Color(0xFF0F172A), style = Stroke(width = 1.2f))
}

@Preview
@Composable
private fun ComicArcadeIllustrationPreview() {
    ComicPreviewTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ComicArcadeIllustration(size = 70.dp, onClick = {})
            ComicArcadeIllustration(size = 90.dp, accentColor = Color(0xFFFF4757), onClick = {})
        }
    }
}
