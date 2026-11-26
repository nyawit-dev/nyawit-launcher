@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.LocalNyawitColors
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class BubbleTailDirection {
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    TOP_LEFT,
    LEFT,
}

/**
 * ComicSpeechBubble: Balon kata komik bergaris tinta dengan ekor percakapan segitiga (tail) khas komik.
 */
@Composable
fun ComicSpeechBubble(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    tailDirection: BubbleTailDirection = BubbleTailDirection.BOTTOM_LEFT,
    content: @Composable () -> Unit,
) {
    val c = LocalNyawitColors.current
    val bg = backgroundColor ?: if (c.isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    val border = borderColor ?: if (c.isDark) c.Primary else Color(0xFF0F172A)

    Box(modifier = modifier) {
        Surface(
            color = bg,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, border),
            modifier = Modifier.padding(
                bottom = if (tailDirection == BubbleTailDirection.BOTTOM_LEFT || tailDirection == BubbleTailDirection.BOTTOM_RIGHT) 8.dp else 0.dp,
                top = if (tailDirection == BubbleTailDirection.TOP_LEFT) 8.dp else 0.dp,
            ),
        ) {
            Box(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                content()
            }
        }

        // Tail pointer
        when (tailDirection) {
            BubbleTailDirection.BOTTOM_LEFT -> {
                Canvas(
                    modifier = Modifier
                        .size(14.dp, 10.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = 16.dp, y = 0.dp),
                ) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path, bg, style = Fill)
                    drawPath(path, border, style = Stroke(width = 2.dp.toPx()))
                }
            }
            BubbleTailDirection.BOTTOM_RIGHT -> {
                Canvas(
                    modifier = Modifier
                        .size(14.dp, 10.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = 0.dp),
                ) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(path, bg, style = Fill)
                    drawPath(path, border, style = Stroke(width = 2.dp.toPx()))
                }
            }
            BubbleTailDirection.TOP_LEFT -> {
                Canvas(
                    modifier = Modifier
                        .size(14.dp, 10.dp)
                        .align(Alignment.TopStart)
                        .offset(x = 16.dp, y = 0.dp),
                ) {
                    val path = Path().apply {
                        moveTo(0f, size.height)
                        lineTo(size.width, size.height)
                        lineTo(0f, 0f)
                        close()
                    }
                    drawPath(path, bg, style = Fill)
                    drawPath(path, border, style = Stroke(width = 2.dp.toPx()))
                }
            }
            BubbleTailDirection.LEFT -> {
                Canvas(
                    modifier = Modifier
                        .size(10.dp, 14.dp)
                        .align(Alignment.CenterStart)
                        .offset(x = (-8).dp, y = 0.dp),
                ) {
                    val path = Path().apply {
                        moveTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height / 2f)
                        close()
                    }
                    drawPath(path, bg, style = Fill)
                    drawPath(path, border, style = Stroke(width = 2.dp.toPx()))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ComicSpeechBubblePreview() {
    ComicPreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ComicSpeechBubble(tailDirection = BubbleTailDirection.BOTTOM_LEFT) {
                Text("Ekor di kiri bawah! Khas percakapan karakter komik.")
            }
            ComicSpeechBubble(tailDirection = BubbleTailDirection.BOTTOM_RIGHT) {
                Text("Ekor di kanan bawah! Cocok untuk ucapan balasan.")
            }
        }
    }
}
