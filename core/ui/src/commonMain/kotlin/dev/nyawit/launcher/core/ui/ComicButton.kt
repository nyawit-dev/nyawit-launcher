@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.theme.ComicDimens
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class ComicButtonVariant {
    PRIMARY,
    SECONDARY,
    WARNING,
    DANGER,
    GHOST,
}

/**
 * ComicButton: Tombol taktil komik/arcade bergaris tinta tegas dan bayangan offset keras.
 * Menyediakan interaktivitas fisik penuh:
 * - Hover state (desktop): mengangkat sedikit atau menyalakan outline aksen primer.
 * - Pressed state: bergeser secara fisik (translate) sejajar dengan offset bayangan
 *   memberikan sensasi mekanis menekan tombol arcade sungguhan.
 * - Disabled state: tekstur arsiran garis komik (hatched pattern) dan kontras tereduksi.
 */
@Composable
fun ComicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ComicButtonVariant = ComicButtonVariant.PRIMARY,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: (@Composable (Color) -> Unit)? = null,
    burstBadge: String? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    shadowOffset: Dp = ComicDimens.ShadowOffset,
    borderWidth: Dp = ComicDimens.BorderWidth,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    textStyle: TextStyle? = null,
) {
    val c = LocalNyawitColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    var isFocused by remember { mutableStateOf(false) }

    val (containerColor, contentColor) = when (variant) {
        ComicButtonVariant.PRIMARY -> c.Primary to c.OnPrimary
        ComicButtonVariant.SECONDARY -> c.SurfaceHighlight to c.Text
        ComicButtonVariant.WARNING -> c.Warning to c.OnWarning
        ComicButtonVariant.DANGER -> c.Danger to c.OnDanger
        ComicButtonVariant.GHOST -> Color.Transparent to c.Text
    }

    val isEffectivelyEnabled = enabled && !loading

    // Tactile push physics:
    // Normal: 0dp
    // Hover: -0.5dp (subtle lift)
    // Pressed: +shadowOffset (pushes fully into the shadow floor)
    val targetOffset = when {
        !isEffectivelyEnabled -> 0.dp
        isPressed -> shadowOffset
        isHovered -> (-0.5).dp
        else -> 0.dp
    }

    val currentOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 70),
    )

    val borderColor = when {
        !isEffectivelyEnabled -> if (c.isDark) Color(0xFF475569) else Color(0xFF94A3B8)
        isFocused || isHovered -> c.Primary
        variant == ComicButtonVariant.GHOST -> c.BorderHighlight
        else -> c.ComicInk
    }

    val shadowBg = if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A)
    val shadowRim = when {
        isFocused || isHovered -> c.Primary
        else -> c.ComicShadowRim
    }

    Box(
        modifier = modifier
            .padding(bottom = shadowOffset, end = shadowOffset)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus },
    ) {
        // Shadow layer
        if (variant != ComicButtonVariant.GHOST && isEffectivelyEnabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowBg, shape)
                    .border(borderWidth, shadowRim, shape),
            )
        }

        // Foreground tactile button
        Box(
            modifier = Modifier
                .offset(x = currentOffset, y = currentOffset)
                .background(
                    if (isEffectivelyEnabled) containerColor else containerColor.copy(alpha = 0.35f),
                    shape,
                )
                .border(borderWidth, borderColor, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = isEffectivelyEnabled,
                    onClick = {
                        ComicSoundPlayer.play(ComicSound.CLICK)
                        onClick()
                    },
                )
                .padding(contentPadding)
                .defaultMinSize(minHeight = 42.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Disabled hatch lines texture
            if (!isEffectivelyEnabled && !loading) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val step = 8.dp.toPx()
                    var x = -size.height
                    while (x < size.width) {
                        drawLine(
                            color = Color(0xFF0F172A).copy(alpha = 0.18f),
                            start = Offset(x, size.height),
                            end = Offset(x + size.height, 0f),
                            strokeWidth = 1.5.dp.toPx(),
                        )
                        x += step
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = contentColor,
                    )
                } else if (icon != null) {
                    icon(if (isEffectivelyEnabled) contentColor else contentColor.copy(alpha = 0.5f))
                }

                Text(
                    text = text.uppercase(),
                    style = textStyle ?: NyawitType.Subtitle,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    color = if (isEffectivelyEnabled) contentColor else contentColor.copy(alpha = 0.45f),
                    maxLines = 1,
                    softWrap = false,
                )

                if (burstBadge != null && isEffectivelyEnabled) {
                    ComicActionBurst(
                        text = burstBadge,
                        size = 24.dp,
                        burstColor = if (variant == ComicButtonVariant.PRIMARY) c.ComicBurstYellow else c.Primary,
                        textColor = Color(0xFF0F172A),
                    )
                }
            }
        }
    }
}

/**
 * ComicIconButton: Tombol ikon bujur sangkar dengan interaktivitas taktil penuh komik/arcade.
 */
@Composable
fun ComicIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ComicButtonVariant = ComicButtonVariant.SECONDARY,
    enabled: Boolean = true,
    size: Dp = 44.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    icon: @Composable () -> Unit,
) {
    val c = LocalNyawitColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    var isFocused by remember { mutableStateOf(false) }

    val (containerColor, _) = when (variant) {
        ComicButtonVariant.PRIMARY -> c.Primary to c.OnPrimary
        ComicButtonVariant.SECONDARY -> c.SurfaceHighlight to c.Text
        ComicButtonVariant.WARNING -> c.Warning to c.OnWarning
        ComicButtonVariant.DANGER -> c.Danger to c.OnDanger
        ComicButtonVariant.GHOST -> Color.Transparent to c.Text
    }

    val shadowOffset = 3.dp
    val targetOffset = when {
        !enabled -> 0.dp
        isPressed -> shadowOffset
        isHovered -> (-0.5).dp
        else -> 0.dp
    }

    val currentOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 70),
    )

    val borderColor = when {
        !enabled -> if (c.isDark) Color(0xFF475569) else Color(0xFF94A3B8)
        isFocused || isHovered -> c.Primary
        else -> c.ComicInk
    }

    Box(
        modifier = modifier
            .padding(bottom = shadowOffset, end = shadowOffset)
            .size(size)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus },
    ) {
        // Shadow
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A), shape)
                    .border(2.dp, if (isFocused || isHovered) c.Primary else c.ComicShadowRim, shape),
            )
        }

        // Foreground
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = currentOffset, y = currentOffset)
                .background(if (enabled) containerColor else containerColor.copy(alpha = 0.35f), shape)
                .border(2.dp, borderColor, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = {
                        ComicSoundPlayer.play(ComicSound.CLICK)
                        onClick()
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
    }
}

@Preview
@Composable
private fun ComicButtonPreview() {
    ComicPreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ComicButton("PRIMARY ACTION", onClick = {}, burstBadge = "NEW!")
            ComicButton("SECONDARY ACTION", onClick = {}, variant = ComicButtonVariant.SECONDARY)
            ComicButton("WARNING ACTION", onClick = {}, variant = ComicButtonVariant.WARNING)
            ComicButton("DANGER ACTION", onClick = {}, variant = ComicButtonVariant.DANGER)
            ComicButton("LOADING ACTION", onClick = {}, loading = true)
            ComicButton("DISABLED ACTION", onClick = {}, enabled = false)
        }
    }
}

@Preview
@Composable
private fun ComicIconButtonPreview() {
    ComicPreviewTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ComicIconButton(onClick = {}, variant = ComicButtonVariant.PRIMARY) {
                ArrowIcon(Modifier.size(18.dp), Color.White)
            }
            ComicIconButton(onClick = {}, variant = ComicButtonVariant.SECONDARY) {
                InfoIcon(Modifier.size(18.dp), Color.White)
            }
            ComicIconButton(onClick = {}, enabled = false) {
                CloseIcon(Modifier.size(18.dp), Color.Gray)
            }
        }
    }
}
