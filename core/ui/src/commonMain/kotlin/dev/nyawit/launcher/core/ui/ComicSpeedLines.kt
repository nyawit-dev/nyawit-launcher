@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.LocalNyawitColors
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicSpeedLines: Garis aksi akselerasi komik/manga (Speed/Action lines).
 */
@Composable
fun ComicSpeedLines(
    modifier: Modifier = Modifier,
    lineColor: Color? = null,
    lineCount: Int = 10,
) {
    val c = LocalNyawitColors.current
    val color = lineColor ?: if (c.isDark) c.Primary.copy(alpha = 0.18f) else Color(0xFF0F172A).copy(alpha = 0.08f)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        for (i in 0 until lineCount) {
            val startX = w * (0.55f + (i * 0.045f))
            val startY = 0f
            val endX = w * (0.75f + (i * 0.025f))
            val endY = h
            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = if (i % 2 == 0) 3f else 1.5f,
            )
        }
    }
}

@Preview
@Composable
private fun ComicSpeedLinesPreview() {
    ComicPreviewTheme {
        Box(modifier = Modifier.size(240.dp, 120.dp)) {
            ComicSpeedLines(lineCount = 12)
        }
    }
}
