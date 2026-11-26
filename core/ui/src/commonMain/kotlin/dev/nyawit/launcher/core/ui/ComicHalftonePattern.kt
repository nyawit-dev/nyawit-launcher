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
 * ComicHalftonePattern: Tekstur bintik Ben-Day dots khas cetak komik pop-art Roy Lichtenstein
 * dan screentone manga. Terasa sangat komik di tema gelap maupun terang.
 */
@Composable
fun ComicHalftonePattern(
    modifier: Modifier = Modifier,
    dotColor: Color? = null,
    dotRadius: Float = 1.5f,
    spacing: Float = 16f,
) {
    val c = LocalNyawitColors.current
    val color = dotColor ?: if (c.isDark) c.Primary.copy(alpha = 0.10f) else Color(0xFF0F172A).copy(alpha = 0.05f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        var x = 0f
        var row = 0
        while (x < width) {
            var y = if (row % 2 == 1) spacing / 2 else 0f
            while (y < height) {
                drawCircle(
                    color = color,
                    radius = dotRadius,
                    center = Offset(x, y),
                )
                y += spacing
            }
            x += spacing
            row++
        }
    }
}

@Preview
@Composable
private fun ComicHalftonePatternPreview() {
    ComicPreviewTheme {
        Box(modifier = Modifier.size(240.dp, 120.dp)) {
            ComicHalftonePattern(dotRadius = 2f, spacing = 14f)
        }
    }
}
