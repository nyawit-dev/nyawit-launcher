@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicBadge: Lencana caption potongan komik/manga dengan outline tinta pekat
 * dan opsi rotasi mikro (-1.5f s/d 2f) untuk sensasi stiker komik otentik.
 */
@Composable
fun ComicBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = LocalNyawitColors.current.Primary,
    contentColor: Color = LocalNyawitColors.current.OnPrimary,
    borderColor: Color? = null,
    borderWidth: Dp = 2.dp,
    rotation: Float = 0f,
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
        border = BorderStroke(borderWidth, border),
        modifier = modifier.then(if (rotation != 0f) Modifier.rotate(rotation) else Modifier),
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
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

/**
 * ComicTag: Label tag komik kecil dengan gaya visual neo-brutalism.
 */
@Composable
fun ComicTag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    rotation: Float = 0f,
) {
    val c = LocalNyawitColors.current
    val bg = color ?: c.SurfaceRaised
    ComicBadge(
        text = text,
        modifier = modifier,
        containerColor = bg,
        contentColor = c.Text,
        borderColor = c.BorderHighlight,
        borderWidth = 1.5.dp,
        rotation = rotation,
    )
}

@Preview
@Composable
private fun ComicBadgePreview() {
    ComicPreviewTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ComicBadge("DEFAULT")
            ComicBadge("SIAP JALAN!", containerColor = Color(0xFF10B981), contentColor = Color.White, rotation = -1.5f)
            ComicBadge("ADA UPDATE!", containerColor = Color(0xFFFFB020), contentColor = Color(0xFF0F172A), rotation = 1.5f)
            ComicTag("VOL. 1")
        }
    }
}
