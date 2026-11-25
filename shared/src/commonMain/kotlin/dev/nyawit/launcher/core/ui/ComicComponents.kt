package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

/**
 * ComicPanel: Wadah panel bergaya komik/manga dengan outline tinta tegas (ink outline)
 * dan bayangan offset keras (hard drop shadow) khas pop-art komik.
 */
@Composable
fun ComicPanel(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    shadowColor: Color? = null,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(10.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val c = LocalNyawitColors.current
    val bg = backgroundColor ?: c.Surface
    val border = borderColor ?: if (c.isDark) c.BorderHighlight else Color(0xFF1E293B)
    val shadow = shadowColor ?: if (c.isDark) Color(0xFF020408) else Color(0xFF0F172A)

    Box(modifier = modifier.padding(bottom = shadowOffset, end = shadowOffset)) {
        // Hard offset comic shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadow, shape)
                .border(borderWidth, border.copy(alpha = 0.5f), shape),
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
 * ComicBadge: Lencana caption bergaya potongan komik/manga dengan sudut chamfered
 * tegas dan outline tinta pekat.
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
    val border = borderColor ?: if (c.isDark) contentColor.copy(alpha = 0.8f) else Color(0xFF0F172A)

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.5.dp, border),
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
