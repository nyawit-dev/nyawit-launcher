@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.ComicDimens
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicPanel: Wadah panel komik/manga bergaris tinta tegas (solid ink border)
 * dan hard drop shadow yang kontras tinggi.
 * Menyediakan opsi dekorasi komik seperti:
 * - comicRivets: paku/sekrup mekanis di 4 sudut panel.
 * - halftoneOverlay: tekstur bintik Ben-Day halus di dalam panel.
 */
@Composable
fun ComicPanel(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    shadowColor: Color? = null,
    borderWidth: Dp = ComicDimens.BorderWidth,
    shadowOffset: Dp = ComicDimens.ShadowOffset,
    shape: Shape = RoundedCornerShape(8.dp),
    comicRivets: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val c = LocalNyawitColors.current
    val bg = backgroundColor ?: c.Surface

    val border = borderColor ?: c.ComicInk
    val shadowBg = shadowColor ?: c.ComicShadow
    val shadowBorder = c.ComicShadowRim

    Box(modifier = modifier.padding(bottom = shadowOffset, end = shadowOffset)) {
        // Hard offset comic shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowBg, shape)
                .border(borderWidth, shadowBorder, shape),
        )

        // Foreground comic panel
        Box(
            modifier = Modifier
                .background(bg, shape)
                .border(borderWidth, border, shape),
        ) {
            content()

            // Optional comic corner rivets
            if (comicRivets) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val rivetRadius = 3.dp.toPx()
                    val margin = 8.dp.toPx()
                    val rivetColor = if (c.isDark) Color(0xFF475569) else Color(0xFF94A3B8)
                    val rivetStroke = Color(0xFF0F172A)

                    val corners = listOf(
                        Offset(margin, margin),
                        Offset(size.width - margin, margin),
                        Offset(margin, size.height - margin),
                        Offset(size.width - margin, size.height - margin),
                    )

                    corners.forEach { center ->
                        drawCircle(rivetColor, rivetRadius, center, style = Fill)
                        drawCircle(rivetStroke, rivetRadius, center, style = Stroke(width = 1.dp.toPx()))
                        // Screw cross
                        drawLine(rivetStroke, Offset(center.x - 1.5f, center.y), Offset(center.x + 1.5f, center.y), 1f)
                        drawLine(rivetStroke, Offset(center.x, center.y - 1.5f), Offset(center.x, center.y + 1.5f), 1f)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ComicPanelPreview() {
    ComicPreviewTheme {
        ComicPanel(modifier = Modifier.padding(16.dp), comicRivets = true) {
            Text(
                text = "PANEL KOMIK NYAWIT // WITH RIVETS",
                modifier = Modifier.padding(20.dp),
                style = NyawitType.Subtitle,
                fontWeight = FontWeight.Black,
            )
        }
    }
}
