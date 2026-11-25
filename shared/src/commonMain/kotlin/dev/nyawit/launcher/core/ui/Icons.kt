package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun SearchIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val center = Offset(size.width * 0.42f, size.height * 0.42f)
        val radius = size.minDimension * 0.27f
        drawCircle(Color.Transparent, radius = radius, center = center, style = Stroke(1.7.dp.toPx()))
        drawLine(
            color = color,
            start = Offset(center.x + radius * 0.72f, center.y + radius * 0.72f),
            end = Offset(size.width * 0.80f, size.height * 0.80f),
            strokeWidth = 1.7.dp.toPx(),
        )
    }
}

@Composable
fun DownloadIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val x = size.width / 2f
        drawLine(color, Offset(x, size.height * 0.16f), Offset(x, size.height * 0.70f), 1.8.dp.toPx())
        drawLine(color, Offset(x - size.width * 0.18f, size.height * 0.52f), Offset(x, size.height * 0.72f), 1.8.dp.toPx())
        drawLine(color, Offset(x + size.width * 0.18f, size.height * 0.52f), Offset(x, size.height * 0.72f), 1.8.dp.toPx())
        drawLine(color, Offset(size.width * 0.18f, size.height * 0.86f), Offset(size.width * 0.82f, size.height * 0.86f), 1.8.dp.toPx())
    }
}

@Composable
fun ArrowIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val y = size.height / 2f
        drawLine(color, Offset(size.width * 0.18f, y), Offset(size.width * 0.82f, y), 1.8.dp.toPx())
        drawLine(color, Offset(size.width * 0.62f, y - size.height * 0.20f), Offset(size.width * 0.82f, y), 1.8.dp.toPx())
        drawLine(color, Offset(size.width * 0.62f, y + size.height * 0.20f), Offset(size.width * 0.82f, y), 1.8.dp.toPx())
    }
}

@Composable
fun GridIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val pad = size.width * 0.14f
        val gap = size.width * 0.12f
        val cellW = (size.width - 2 * pad - gap) / 2f
        val cellH = (size.height - 2 * pad - gap) / 2f
        val r = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx())
        // 4 grid boxes
        drawRoundRect(color, Offset(pad, pad), Size(cellW, cellH), r)
        drawRoundRect(color, Offset(pad + cellW + gap, pad), Size(cellW, cellH), r)
        drawRoundRect(color, Offset(pad, pad + cellH + gap), Size(cellW, cellH), r)
        drawRoundRect(color, Offset(pad + cellW + gap, pad + cellH + gap), Size(cellW, cellH), r)
    }
}

@Composable
fun GamepadIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.35f)
            cubicTo(w * 0.28f, h * 0.30f, w * 0.72f, h * 0.30f, w * 0.78f, h * 0.35f)
            cubicTo(w * 0.90f, h * 0.45f, w * 0.92f, h * 0.75f, w * 0.82f, h * 0.80f)
            cubicTo(w * 0.74f, h * 0.82f, w * 0.68f, h * 0.68f, w * 0.60f, h * 0.68f)
            cubicTo(w * 0.52f, h * 0.68f, w * 0.48f, h * 0.68f, w * 0.40f, h * 0.68f)
            cubicTo(w * 0.32f, h * 0.68f, w * 0.26f, h * 0.82f, w * 0.18f, h * 0.80f)
            cubicTo(w * 0.08f, h * 0.75f, w * 0.10f, h * 0.45f, w * 0.22f, h * 0.35f)
            close()
        }
        drawPath(path, color, style = Stroke(1.7.dp.toPx()))
        // Dpad cross
        drawLine(color, Offset(w * 0.30f, h * 0.48f), Offset(w * 0.38f, h * 0.48f), 1.6.dp.toPx())
        drawLine(color, Offset(w * 0.34f, h * 0.44f), Offset(w * 0.34f, h * 0.52f), 1.6.dp.toPx())
        // Action buttons
        drawCircle(color, 1.6.dp.toPx(), Offset(w * 0.64f, h * 0.45f))
        drawCircle(color, 1.6.dp.toPx(), Offset(w * 0.70f, h * 0.51f))
    }
}

@Composable
fun MusicIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        // Note head
        drawCircle(color, w * 0.16f, Offset(w * 0.34f, h * 0.72f))
        drawCircle(color, w * 0.16f, Offset(w * 0.72f, h * 0.62f))
        // Stems
        drawLine(color, Offset(w * 0.46f, h * 0.72f), Offset(w * 0.46f, h * 0.28f), 1.8.dp.toPx())
        drawLine(color, Offset(w * 0.84f, h * 0.62f), Offset(w * 0.84f, h * 0.18f), 1.8.dp.toPx())
        // Beam
        val beam = Path().apply {
            moveTo(w * 0.46f, h * 0.28f)
            lineTo(w * 0.84f, h * 0.18f)
            lineTo(w * 0.84f, h * 0.26f)
            lineTo(w * 0.46f, h * 0.36f)
            close()
        }
        drawPath(beam, color)
    }
}

@Composable
fun RocketIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.50f, h * 0.14f)
            cubicTo(w * 0.66f, h * 0.30f, w * 0.70f, h * 0.55f, w * 0.66f, h * 0.70f)
            lineTo(w * 0.34f, h * 0.70f)
            cubicTo(w * 0.30f, h * 0.55f, w * 0.34f, h * 0.30f, w * 0.50f, h * 0.14f)
            close()
        }
        drawPath(path, color, style = Stroke(1.7.dp.toPx()))
        // Fins
        val leftFin = Path().apply {
            moveTo(w * 0.34f, h * 0.60f)
            lineTo(w * 0.20f, h * 0.74f)
            lineTo(w * 0.34f, h * 0.72f)
            close()
        }
        drawPath(leftFin, color)
        val rightFin = Path().apply {
            moveTo(w * 0.66f, h * 0.60f)
            lineTo(w * 0.80f, h * 0.74f)
            lineTo(w * 0.66f, h * 0.72f)
            close()
        }
        drawPath(rightFin, color)
        // Window
        drawCircle(color, w * 0.08f, Offset(w * 0.50f, h * 0.42f))
    }
}

@Composable
fun SyncIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.30f
        // Circular arc
        drawArc(
            color = color,
            startAngle = 40f,
            sweepAngle = 260f,
            useCenter = false,
            topLeft = Offset(cx - r, cy - r),
            size = Size(r * 2, r * 2),
            style = Stroke(1.8.dp.toPx()),
        )
        // Arrow head on arc end
        val ax = cx + r * kotlin.math.cos(40.0 * kotlin.math.PI / 180.0).toFloat()
        val ay = cy + r * kotlin.math.sin(40.0 * kotlin.math.PI / 180.0).toFloat()
        val arrow = Path().apply {
            moveTo(ax, ay - 3.dp.toPx())
            lineTo(ax + 5.dp.toPx(), ay + 3.dp.toPx())
            lineTo(ax - 2.dp.toPx(), ay + 4.dp.toPx())
            close()
        }
        drawPath(arrow, color)
    }
}

@Composable
fun MenuIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        val w = size.width
        val h = size.height
        val startX = w * 0.15f
        val endX = w * 0.85f
        drawLine(color, Offset(startX, h * 0.28f), Offset(endX, h * 0.28f), stroke)
        drawLine(color, Offset(startX, h * 0.50f), Offset(endX, h * 0.50f), stroke)
        drawLine(color, Offset(startX, h * 0.72f), Offset(endX, h * 0.72f), stroke)
    }
}

@Composable
fun InfoIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.40f
        drawCircle(color, radius = r, center = Offset(cx, cy), style = Stroke(1.7.dp.toPx()))
        // Dot
        drawCircle(color, radius = 1.6.dp.toPx(), center = Offset(cx, cy - r * 0.40f))
        // Stem
        drawLine(color, Offset(cx, cy - r * 0.10f), Offset(cx, cy + r * 0.48f), 1.8.dp.toPx())
    }
}

@Composable
fun PaletteIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.40f
        drawCircle(color, radius = r, center = Offset(cx, cy), style = Stroke(1.6.dp.toPx()))
        // Color wells
        drawCircle(color, 1.8.dp.toPx(), Offset(cx - r * 0.40f, cy - r * 0.25f))
        drawCircle(color, 1.8.dp.toPx(), Offset(cx, cy - r * 0.50f))
        drawCircle(color, 1.8.dp.toPx(), Offset(cx + r * 0.40f, cy - r * 0.25f))
        drawCircle(color, 1.8.dp.toPx(), Offset(cx - r * 0.25f, cy + r * 0.25f))
    }
}

@Composable
fun CheckIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawLine(color, Offset(w * 0.20f, h * 0.52f), Offset(w * 0.42f, h * 0.76f), 2.dp.toPx())
        drawLine(color, Offset(w * 0.42f, h * 0.76f), Offset(w * 0.82f, h * 0.28f), 2.dp.toPx())
    }
}

@Composable
fun GlobeIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.40f
        drawCircle(color, radius = r, center = Offset(cx, cy), style = Stroke(1.6.dp.toPx()))
        // Equator
        drawLine(color, Offset(cx - r, cy), Offset(cx + r, cy), 1.5.dp.toPx())
        // Meridian lines
        val ovalPath = Path().apply {
            moveTo(cx, cy - r)
            cubicTo(cx + r * 0.55f, cy - r * 0.5f, cx + r * 0.55f, cy + r * 0.5f, cx, cy + r)
            cubicTo(cx - r * 0.55f, cy + r * 0.5f, cx - r * 0.55f, cy - r * 0.5f, cx, cy - r)
            close()
        }
        drawPath(ovalPath, color, style = Stroke(1.4.dp.toPx()))
    }
}

@Composable
fun SettingsIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val innerR = size.minDimension * 0.16f
        val rimR = size.minDimension * 0.32f
        val outerR = size.minDimension * 0.42f
        drawCircle(color, radius = innerR, center = Offset(cx, cy), style = Stroke(1.7.dp.toPx()))
        drawCircle(color, radius = rimR, center = Offset(cx, cy), style = Stroke(1.5.dp.toPx()))

        val teeth = 6
        for (i in 0 until teeth) {
            val angle = (i * (2.0 * kotlin.math.PI / teeth)).toFloat()
            val cosA = kotlin.math.cos(angle)
            val sinA = kotlin.math.sin(angle)
            drawLine(
                color = color,
                start = Offset(cx + rimR * cosA, cy + rimR * sinA),
                end = Offset(cx + outerR * cosA, cy + outerR * sinA),
                strokeWidth = 2.4.dp.toPx(),
            )
        }
    }
}
