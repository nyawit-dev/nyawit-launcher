@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicCard: Komponen kartu berkarakter komik/manga dengan tekstur Ben-Day halftone,
 * garis tinta pekat, stiker ledakan aksi pop-art, efek tactile hover lift, dan offset bayangan dinamis.
 */
@Composable
fun ComicCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    category: String? = null,
    burstText: String? = null,
    burstColor: Color = Color(0xFFFFDE00),
    burstTextColor: Color = Color(0xFF0F172A),
    statusBadge: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    iconSize: Dp = 56.dp,
    progress: Float? = null,
    progressLabel: String? = null,
    actionButton: (@Composable () -> Unit)? = null,
    secondaryAction: (@Composable () -> Unit)? = null,
    extraContent: (@Composable () -> Unit)? = null,
) {
    val c = LocalNyawitColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    var isCardFocused by remember { mutableStateOf(false) }

    val baseShadow = 4.dp
    val shadowOffset by animateDpAsState(
        targetValue = when {
            isPressed -> 1.5.dp
            isHovered || isCardFocused -> 6.dp
            else -> baseShadow
        },
        animationSpec = tween(90),
    )

    val cardLift by animateDpAsState(
        targetValue = when {
            isPressed -> 2.dp
            isHovered || isCardFocused -> (-2).dp
            else -> 0.dp
        },
        animationSpec = tween(90),
    )

    val containerColor by animateColorAsState(
        targetValue = if (isCardFocused || isHovered) c.SurfaceRaised else c.Surface,
        animationSpec = tween(100),
    )
    val borderColor by animateColorAsState(
        targetValue = if (isCardFocused || isHovered) c.Primary else c.ComicInk,
        animationSpec = tween(100),
    )

    ComicPanel(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = cardLift)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .onFocusChanged { state -> isCardFocused = state.isFocused || state.hasFocus }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    ComicSoundPlayer.play(ComicSound.CLICK)
                    onClick()
                },
            ),
        backgroundColor = containerColor,
        borderColor = borderColor,
        shadowColor = if (isCardFocused || isHovered) c.Primary.copy(alpha = 0.55f) else null,
        borderWidth = if (isCardFocused || isHovered) 2.5.dp else 2.dp,
        shadowOffset = shadowOffset,
        shape = RoundedCornerShape(10.dp),
    ) {
        // Comic Halftone screentone texture
        ComicHalftonePattern(
            modifier = Modifier.matchParentSize(),
            dotRadius = 1.2f,
            spacing = 16f,
            dotColor = if (c.isDark) c.Primary.copy(alpha = 0.08f) else Color(0xFF0F172A).copy(alpha = 0.04f),
        )

        // Comic Action Burst sticker on top corner
        if (burstText != null) {
            ComicActionBurst(
                text = burstText,
                burstColor = burstColor,
                textColor = burstTextColor,
                size = 32.dp,
                modifier = Modifier.align(Alignment.TopEnd).offset(x = 2.dp, y = (-2).dp),
            )
        }

        Column(Modifier.padding(14.dp)) {
            // Upper Bay: Category Caption & Status Stamp
            if (category != null || statusBadge != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (category != null) {
                        ComicBadge(
                            text = category.uppercase(),
                            containerColor = c.SurfaceHighlight,
                            contentColor = c.Primary,
                            borderColor = if (c.isDark) c.Primary.copy(alpha = 0.5f) else Color(0xFF0F172A),
                        )
                    } else {
                        Spacer(Modifier.width(1.dp))
                    }

                    if (statusBadge != null) {
                        statusBadge()
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Main Dock: Inked Icon Box + Title & Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = c.SurfaceRaised,
                        border = BorderStroke(
                            2.dp,
                            if (isCardFocused || isHovered) c.Primary else c.BorderHighlight,
                        ),
                        modifier = Modifier.size(iconSize),
                    ) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            icon()
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                }

                Column(Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = NyawitType.Subtitle,
                        fontWeight = FontWeight.Black,
                        color = c.Text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (subtitle != null) {
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = subtitle,
                            style = NyawitType.Caption,
                            color = c.Muted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            if (extraContent != null) {
                Spacer(Modifier.height(10.dp))
                extraContent()
            }

            // Progress bar if active
            if (progress != null) {
                Spacer(Modifier.height(12.dp))
                ComicProgressBar(
                    progress = progress,
                    label = progressLabel,
                )
            }

            // Bottom action strip
            if (actionButton != null || secondaryAction != null) {
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    if (secondaryAction != null) {
                        secondaryAction()
                    } else {
                        Spacer(Modifier.width(1.dp))
                    }

                    if (actionButton != null) {
                        actionButton()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ComicCardPreview() {
    ComicPreviewTheme {
        ComicCard(
            title = "Nyawit Music Player",
            subtitle = "Pemutar Musik Hi-Fi Multiplatform",
            category = "AUDIO",
            burstText = "HOT!",
            progress = 0.45f,
            onClick = {},
            actionButton = {
                ComicButton("PASANG!", onClick = {})
            },
            secondaryAction = {
                ComicIconButton(onClick = {}) {
                    InfoIcon(Modifier.size(18.dp), Color.White)
                }
            },
        )
    }
}
