package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * ComicPanel: Wadah panel komik/manga bergaris tinta tegas (solid ink border)
 * dan hard drop shadow yang kontras tinggi, baik di tema terang maupun tema gelap.
 */
@Composable
fun ComicPanel(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    shadowColor: Color? = null,
    borderWidth: Dp = 2.5.dp,
    shadowOffset: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val c = LocalNyawitColors.current
    val bg = backgroundColor ?: c.Surface

    // Outline tinta: di tema gelap menggunakan garis slate/tinta tegas terang atau aksen primer,
    // di tema terang menggunakan tinta hitam pekat.
    val border = borderColor ?: if (c.isDark) {
        Color(0xFF64748B) // Slate-500 tegas kontras di tema gelap
    } else {
        Color(0xFF0F172A) // Deep black ink
    }

    // Shadow di tema gelap: agar tidak menyatu dengan background gelap,
    // bayangan diberi rim/outline berwarna neon aksen primer atau kontras tinggi!
    val shadowBg = shadowColor ?: if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A)
    val shadowBorder = if (c.isDark) {
        c.Primary.copy(alpha = 0.55f)
    } else {
        Color(0xFF0F172A)
    }

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
            content = content,
        )
    }
}

/**
 * ComicBadge: Lencana caption potongan komik/manga dengan outline tinta pekat
 * dan sudut chamfered.
 */
@Composable
fun ComicBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color? = null,
    icon: (@Composable () -> Unit)? = null,
) {
    val c = LocalNyawitColors.current
    val border = borderColor ?: if (c.isDark) {
        if (containerColor == c.Primary || containerColor == c.Warning) Color(0xFF0F172A) else c.Primary.copy(alpha = 0.8f)
    } else {
        Color(0xFF0F172A)
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(2.dp, border),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (icon != null) {
                icon()
            }
            Text(
                text = text.uppercase(),
                style = NyawitType.Overline,
                color = contentColor,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
            )
        }
    }
}

/**
 * ComicHalftonePattern: Tekstur bintik Ben-Day dots khas cetak komik pop-art Roy Lichtenstein
 * dan screentone manga. Terasa sangat komik di tema gelap maupun terang.
 */
@Composable
fun ComicHalftonePattern(
    modifier: Modifier = Modifier,
    dotColor: Color? = null,
    dotRadius: Float = 2f,
    spacing: Float = 14f,
) {
    val c = LocalNyawitColors.current
    val color = dotColor ?: if (c.isDark) c.Primary.copy(alpha = 0.14f) else Color(0xFF0F172A).copy(alpha = 0.08f)

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

/**
 * ComicSpeedLines: Garis aksi akselerasi komik/manga (Speed/Action lines).
 */
@Composable
fun ComicSpeedLines(
    modifier: Modifier = Modifier,
    lineColor: Color? = null,
) {
    val c = LocalNyawitColors.current
    val color = lineColor ?: if (c.isDark) c.Primary.copy(alpha = 0.18f) else Color(0xFF0F172A).copy(alpha = 0.08f)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val lines = 8
        for (i in 0 until lines) {
            val startX = w * (0.6f + (i * 0.05f))
            val startY = 0f
            val endX = w * (0.85f + (i * 0.03f))
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

/**
 * ComicActionBurst: Stiker bintang ledakan pop-art komik (12-point Starburst / Explosion Burst)
 * untuk tulisan heboh seperti "BOOM!", "HOT!", "NEW!", "PLAY!", "UPDATE!".
 */
@Composable
fun ComicActionBurst(
    text: String,
    modifier: Modifier = Modifier,
    burstColor: Color = Color(0xFFFFDE00), // Kuning komik khas pop-art
    textColor: Color = Color(0xFF0F172A),
    outlineColor: Color = Color(0xFF0F172A),
    size: Dp = 56.dp,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        // Shadow offset layer
        Canvas(modifier = Modifier.size(size).offset(x = 2.5.dp, y = 2.5.dp)) {
            val path = createBurstPath(this.size.width, this.size.height, points = 12)
            drawPath(path, Color(0xFF000000), style = Fill)
        }

        // Foreground burst with thick ink outline
        Canvas(modifier = Modifier.size(size)) {
            val path = createBurstPath(this.size.width, this.size.height, points = 12)
            drawPath(path, burstColor, style = Fill)
            drawPath(path, outlineColor, style = Stroke(width = 2.5f.dp.toPx()))
        }

        Text(
            text = text,
            style = NyawitType.Overline,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = textColor,
            fontSize = 9.sp,
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

/**
 * ComicSpeechBubble: Balon kata komik bergaris tinta dengan ekor percakapan segitiga (tail).
 */
@Composable
fun ComicSpeechBubble(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
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
            modifier = Modifier.padding(bottom = 6.dp),
        ) {
            Box(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                content()
            }
        }
    }
}

/**
 * ComicArcadeIllustration: Gambar ilustrasi vektor pop-art komik (Gamepad + Star Sparks)
 * dengan garis tinta hitam tegas tebal dan aksen warna komik pop-art.
 */
@Composable
fun ComicArcadeIllustration(
    modifier: Modifier = Modifier,
    primaryColor: Color? = null,
    accentColor: Color = Color(0xFFFFDE00), // Pop comic yellow
) {
    val c = LocalNyawitColors.current
    val prime = primaryColor ?: c.Primary

    Canvas(modifier = modifier.size(80.dp)) {
        val w = size.width
        val h = size.height
        val ink = if (c.isDark) Color(0xFF0F172A) else Color(0xFF0F172A)
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
        // horizontal bar
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(dpadX - dpadSize, dpadY - 4f),
            size = androidx.compose.ui.geometry.Size(dpadSize * 2, 8f),
        )
        // vertical bar
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(dpadX - 4f, dpadY - dpadSize),
            size = androidx.compose.ui.geometry.Size(8f, dpadSize * 2),
        )

        // 4. Action Buttons (Right 4 colored comic dots)
        val btnRadius = 4.5f
        // Button A (yellow)
        drawCircle(
            color = accentColor,
            radius = btnRadius,
            center = Offset(w * 0.72f, h * 0.44f),
        )
        drawCircle(
            color = Color(0xFF0F172A),
            radius = btnRadius,
            center = Offset(w * 0.72f, h * 0.44f),
            style = Stroke(width = 1.5.dp.toPx()),
        )
        // Button B (red)
        drawCircle(
            color = Color(0xFFFF4757),
            radius = btnRadius,
            center = Offset(w * 0.80f, h * 0.52f),
        )
        drawCircle(
            color = Color(0xFF0F172A),
            radius = btnRadius,
            center = Offset(w * 0.80f, h * 0.52f),
            style = Stroke(width = 1.5.dp.toPx()),
        )
        // Button X (cyan)
        drawCircle(
            color = Color(0xFF00D2D3),
            radius = btnRadius,
            center = Offset(w * 0.64f, h * 0.52f),
        )
        drawCircle(
            color = Color(0xFF0F172A),
            radius = btnRadius,
            center = Offset(w * 0.64f, h * 0.52f),
            style = Stroke(width = 1.5.dp.toPx()),
        )

        // 5. Star Sparks (Kilatan bintang komik di sudut kiri atas)
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
    scope.drawPath(path, Color(0xFF0F172A), style = Stroke(width = 2.5f))
}
