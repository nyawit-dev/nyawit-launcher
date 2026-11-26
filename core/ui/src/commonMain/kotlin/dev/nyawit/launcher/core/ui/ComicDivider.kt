@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class ComicDividerStyle {
    SOLID,
    DASHED,
    ZIGZAG,
}

/**
 * ComicDivider: Garis pemisah panel komik dengan gaya tinta solid, dashed, atau zigzag (jagged cut),
 * serta opsi stiker badge komik di tengah (centerBadge).
 */
@Composable
fun ComicDivider(
    modifier: Modifier = Modifier,
    color: Color? = null,
    thickness: Dp = 2.dp,
    dashed: Boolean = false,
    style: ComicDividerStyle = if (dashed) ComicDividerStyle.DASHED else ComicDividerStyle.SOLID,
    centerBadge: String? = null,
) {
    val c = LocalNyawitColors.current
    val strokeColor = color ?: c.BorderHighlight

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        if (style == ComicDividerStyle.ZIGZAG) {
            Canvas(modifier = Modifier.fillMaxWidth().height(10.dp)) {
                val path = Path()
                val step = 14f
                var x = 0f
                var up = true
                path.moveTo(0f, size.height / 2f)
                while (x < size.width) {
                    val y = if (up) 2f else size.height - 2f
                    path.lineTo(x + step / 2, y)
                    x += step / 2
                    up = !up
                }
                drawPath(path, strokeColor, style = Stroke(width = thickness.toPx()))
            }
        } else {
            Canvas(modifier = Modifier.fillMaxWidth().height(thickness)) {
                val pathEffect = if (style == ComicDividerStyle.DASHED) PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f) else null
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = thickness.toPx(),
                    pathEffect = pathEffect,
                )
            }
        }

        if (centerBadge != null) {
            Box(
                modifier = Modifier
                    .background(c.SurfaceRaised, RoundedCornerShape(4.dp))
                    .border(1.5.dp, strokeColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = centerBadge.uppercase(),
                    style = NyawitType.Overline,
                    fontWeight = FontWeight.Black,
                    color = c.Text,
                    fontSize = 8.sp,
                    letterSpacing = 1.sp,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ComicDividerPreview() {
    ComicPreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Panel Bagian Atas")
            ComicDivider()
            Text("Panel Garis Putus-Putus dengan Badge Tengah")
            ComicDivider(dashed = true, centerBadge = "★ NYAWIT ★")
            Text("Panel Garis Zigzag (Jagged Comic Cut)")
            ComicDivider(style = ComicDividerStyle.ZIGZAG)
            Text("Panel Bagian Bawah")
        }
    }
}
