@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicFilterChip: Tombol chip filter kategori dengan gaya komik pop-art,
 * mekanika taktil tekan penuh (pushes into shadow), dan indikator aktif yang tegas.
 */
@Composable
fun ComicFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    count: Int? = null,
    focusRequester: FocusRequester? = null,
) {
    val c = LocalNyawitColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(6.dp)

    val shadowBase = if (selected || isFocused) 2.5.dp else 1.5.dp
    val targetOffset = when {
        isPressed -> shadowBase
        isHovered -> (-0.5).dp
        else -> 0.dp
    }

    val currentOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = tween(70),
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            selected -> c.Primary
            isFocused || isHovered -> c.SurfaceHighlight
            else -> c.SurfaceRaised
        },
        animationSpec = tween(100),
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) c.OnPrimary else c.Text,
        animationSpec = tween(100),
    )

    val borderColor = when {
        isFocused || isHovered -> c.Primary
        selected -> Color(0xFF0F172A)
        else -> c.ComicInk
    }

    Box(modifier = modifier.padding(bottom = shadowBase, end = shadowBase)) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowBase, y = shadowBase)
                .background(if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A), shape)
                .border(1.5.dp, if (selected || isFocused || isHovered) c.Primary.copy(alpha = 0.6f) else Color.Transparent, shape),
        )

        // Foreground
        Row(
            modifier = Modifier
                .offset(x = currentOffset, y = currentOffset)
                .background(bgColor, shape)
                .border(2.dp, borderColor, shape)
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(c.OnPrimary, CircleShape),
                )
            }

            Text(
                text = text.uppercase(),
                style = NyawitType.Overline,
                color = contentColor,
                fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                letterSpacing = 0.8.sp,
                maxLines = 1,
            )

            if (count != null && count > 0) {
                Box(
                    modifier = Modifier
                        .background(if (selected) Color(0xFF0F172A) else c.Surface, RoundedCornerShape(3.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = count.toString(),
                        style = NyawitType.Overline,
                        color = if (selected) c.Primary else c.Muted,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ComicFilterChipPreview() {
    ComicPreviewTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ComicFilterChip(text = "SEMUA", selected = true, count = 12, onClick = {})
            ComicFilterChip(text = "AUDIO", selected = false, count = 5, onClick = {})
            ComicFilterChip(text = "UTILITY", selected = false, count = 3, onClick = {})
        }
    }
}
