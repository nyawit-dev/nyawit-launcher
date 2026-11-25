package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.ui.ArrowIcon
import dev.nyawit.launcher.core.ui.CheckIcon
import dev.nyawit.launcher.core.ui.ComicActionBurst
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicHalftonePattern
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.DownloadIcon
import dev.nyawit.launcher.core.ui.InfoIcon
import dev.nyawit.launcher.core.ui.LocalResponsiveScale
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

enum class AppStatus { NOT_INSTALLED, INSTALLED, UPDATE_AVAILABLE, UNKNOWN }

private fun AppPlatform.badgeLabel() = when (this) {
    AppPlatform.ANDROID -> "APK"
    AppPlatform.TV -> "TV"
    AppPlatform.DESKTOP -> "PC"
}

@Composable
fun AppCard(
    app: CatalogApp,
    status: AppStatus,
    busy: Boolean,
    progress: Float? = null,
    onDetail: (() -> Unit)? = null,
    onInstall: () -> Unit,
    onOpen: () -> Unit,
) {
    val c = LocalNyawitColors.current
    val responsiveScale = LocalResponsiveScale.current
    var isCardFocused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = if (isCardFocused) c.SurfaceRaised else c.Surface,
        animationSpec = tween(140),
    )
    val borderColor by animateColorAsState(
        targetValue = if (isCardFocused) c.Primary else if (c.isDark) Color(0xFF64748B) else Color(0xFF0F172A),
        animationSpec = tween(140),
    )

    ComicPanel(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .onFocusChanged { state -> isCardFocused = state.hasFocus }
            .focusProperties { canFocus = false }
            .clickable { if (status == AppStatus.INSTALLED) onOpen() else onInstall() },
        backgroundColor = containerColor,
        borderColor = borderColor,
        shadowColor = if (isCardFocused) c.Primary.copy(alpha = 0.55f) else null,
        borderWidth = if (isCardFocused) 2.5.dp else 2.dp,
        shadowOffset = 4.dp,
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
        if (status == AppStatus.UPDATE_AVAILABLE) {
            ComicActionBurst(
                text = "UP!",
                burstColor = Color(0xFFFF4757),
                textColor = Color(0xFFFFFFFF),
                size = 32.dp,
                modifier = Modifier.align(Alignment.TopEnd).offset(x = 2.dp, y = (-2).dp),
            )
        } else if (status == AppStatus.INSTALLED) {
            ComicActionBurst(
                text = "GO!",
                burstColor = Color(0xFFFFDE00),
                textColor = Color(0xFF0F172A),
                size = 28.dp,
                modifier = Modifier.align(Alignment.TopEnd).offset(x = 2.dp, y = (-2).dp),
            )
        }

        Column(Modifier.padding(15.dp)) {
            // Upper Bay: Comic Category Caption & State Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ComicBadge(
                    text = app.category.uppercase(),
                    containerColor = c.SurfaceHighlight,
                    contentColor = c.Primary,
                    borderColor = if (c.isDark) c.Primary.copy(alpha = 0.5f) else Color(0xFF0F172A),
                )

                StatusBadge(status)
            }

            Spacer(Modifier.height(14.dp))

            // Main Comic Dock: Inked Icon Box + Title & Publisher
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = c.SurfaceRaised,
                    border = BorderStroke(
                        2.dp,
                        if (isCardFocused) c.Primary else c.BorderHighlight,
                    ),
                    modifier = Modifier.size(responsiveScale.dp(56)),
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        AppIcon(
                            fullName = app.fullName,
                            fallbackName = app.name,
                            size = responsiveScale.dp(48),
                            shape = RoundedCornerShape(8.dp),
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        app.name,
                        style = NyawitType.Subtitle,
                        fontWeight = FontWeight.Black,
                        color = c.Text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        app.fullName.substringBefore('/'),
                        style = NyawitType.Caption,
                        color = c.Primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Micro-spec Strip: Comic platform badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                app.platforms.forEach { platform ->
                    ComicBadge(
                        text = platform.badgeLabel(),
                        containerColor = c.SurfaceRaised,
                        contentColor = c.Faint,
                        borderColor = c.Border,
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    app.packageName,
                    style = NyawitType.Caption,
                    color = c.Faint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily.Monospace,
                )
            }

            Spacer(Modifier.height(10.dp))

            // Description Body
            Text(
                app.description,
                style = NyawitType.Body,
                color = c.Muted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            // Dynamic Progress Indicator
            if (busy && progress != null) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        NyawitStrings.PROGRESS_DOWNLOADING,
                        style = NyawitType.Caption,
                        fontWeight = FontWeight.Bold,
                        color = c.Primary,
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = NyawitType.Caption,
                        fontWeight = FontWeight.Black,
                        color = c.Text,
                    )
                }
                Spacer(Modifier.height(5.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = c.Primary,
                    trackColor = c.SurfaceRaised,
                )
            }

            Spacer(Modifier.height(16.dp))

            // Comic Action Command Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val isInstalled = status == AppStatus.INSTALLED
                val isUpdate = status == AppStatus.UPDATE_AVAILABLE

                val btnContainerColor = when {
                    isUpdate -> c.Warning
                    isInstalled -> c.Primary
                    else -> c.SurfaceRaised
                }
                val btnContentColor = when {
                    isUpdate -> c.OnWarning
                    isInstalled -> c.OnPrimary
                    else -> c.Text
                }
                val btnBorder = BorderStroke(
                    2.dp,
                    when {
                        isUpdate -> if (c.isDark) c.Warning else Color(0xFF0F172A)
                        isInstalled -> if (c.isDark) c.Primary else Color(0xFF0F172A)
                        else -> c.BorderHighlight
                    },
                )

                Button(
                    onClick = if (isInstalled) onOpen else onInstall,
                    enabled = !busy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = btnContainerColor,
                        contentColor = btnContentColor,
                    ),
                    border = btnBorder,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(
                        horizontal = responsiveScale.dp(12),
                        vertical = responsiveScale.dp(8),
                    ),
                    modifier = Modifier
                        .defaultMinSize(minHeight = responsiveScale.dp(44))
                        .heightIn(min = responsiveScale.dp(44))
                        .weight(1f)
                        .tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
                ) {
                    if (busy) {
                        CircularProgressIndicator(
                            Modifier.size(responsiveScale.dp(16)),
                            strokeWidth = 2.dp,
                            color = btnContentColor,
                        )
                        Spacer(Modifier.width(8.dp))
                    } else if (isInstalled) {
                        ArrowIcon(Modifier.size(responsiveScale.dp(16)), btnContentColor)
                        Spacer(Modifier.width(8.dp))
                    } else if (progress == null) {
                        DownloadIcon(Modifier.size(responsiveScale.dp(16)), if (isUpdate) btnContentColor else c.Primary)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        when {
                            busy && progress != null -> NyawitStrings.progressDownloadingPercent((progress * 100).toInt())
                            busy -> NyawitStrings.PROGRESS_PROCESSING
                            isInstalled -> "JALANKAN! ➔"
                            isUpdate -> "PERBARUI! ➔"
                            else -> "PASANG! ➔"
                        },
                        fontWeight = FontWeight.Black,
                        style = NyawitType.Caption,
                    )
                }

                // Comic Detail Trigger
                Surface(
                    color = c.SurfaceRaised,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(2.dp, c.BorderHighlight),
                    modifier = Modifier
                        .size(responsiveScale.dp(44))
                        .clickable { onDetail?.invoke() }
                        .focusable()
                        .tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        InfoIcon(Modifier.size(responsiveScale.dp(18)), c.Text)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: AppStatus) {
    val c = LocalNyawitColors.current
    when (status) {
        AppStatus.INSTALLED -> ComicBadge(
            text = "SIAP JALAN!",
            containerColor = c.Primary,
            contentColor = c.OnPrimary,
            borderColor = if (c.isDark) c.Primary else Color(0xFF0F172A),
            icon = { CheckIcon(Modifier.size(10.dp), c.OnPrimary) },
        )
        AppStatus.UPDATE_AVAILABLE -> ComicBadge(
            text = "ADA UPDATE!",
            containerColor = c.Warning,
            contentColor = c.OnWarning,
            borderColor = if (c.isDark) c.Warning else Color(0xFF0F172A),
            icon = { Box(Modifier.size(5.dp).background(c.OnWarning, CircleShape)) },
        )
        AppStatus.NOT_INSTALLED -> ComicBadge(
            text = "TERSEDIA",
            containerColor = c.SurfaceHighlight,
            contentColor = c.Muted,
            borderColor = if (c.isDark) c.BorderHighlight else Color(0xFF334155),
        )
        AppStatus.UNKNOWN -> ComicBadge(
            text = "STATUS ?",
            containerColor = c.SurfaceRaised,
            contentColor = c.Faint,
        )
    }
}
