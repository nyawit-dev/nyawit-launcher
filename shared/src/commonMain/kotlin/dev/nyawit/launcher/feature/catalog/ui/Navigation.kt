package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicButton
import dev.nyawit.launcher.core.ui.ComicButtonVariant
import dev.nyawit.launcher.core.ui.ComicHalftonePattern
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.GridIcon
import dev.nyawit.launcher.core.ui.InfoIcon
import dev.nyawit.launcher.core.ui.LocalResponsiveScale
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.SettingsIcon
import dev.nyawit.launcher.core.ui.SyncIcon
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

enum class AppPage { APPS, SETTINGS, ABOUT }

@Composable
fun AppSidebar(
    page: AppPage,
    version: String,
    updateTag: String?,
    total: Int,
    updateCount: Int,
    selfBusy: Boolean,
    onNavigate: (AppPage) -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
) {
    val c = LocalNyawitColors.current
    val responsiveScale = LocalResponsiveScale.current

    Row(modifier.fillMaxHeight()) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(c.Surface)
        ) {
            // Comic Halftone Dots Background
            ComicHalftonePattern(
                modifier = Modifier.matchParentSize(),
                dotColor = if (c.isDark) Color.White.copy(alpha = 0.035f) else Color.Black.copy(alpha = 0.03f),
                dotRadius = 1.2f,
                spacing = 18f,
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Top Bay: Masthead + Section Title + 3 Chapter Nav Items
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Comic Masthead & Brand Header
                    ComicPanel(
                        modifier = Modifier.fillMaxWidth(),
                        borderWidth = 2.dp,
                        shadowOffset = 3.dp,
                        backgroundColor = c.SurfaceRaised,
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ComicBadge(
                                        text = "VOL. 1",
                                        containerColor = c.Primary,
                                        contentColor = c.OnPrimary,
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "ISSUE #01",
                                        style = NyawitType.Overline,
                                        color = c.Muted,
                                        fontWeight = FontWeight.Black,
                                    )
                                }
                                if (onClose != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                if (c.isDark) Color(0xFF020617) else Color(0xFF0F172A),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { onClose() },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            "✕",
                                            color = Color.White,
                                            style = NyawitType.Caption,
                                            fontWeight = FontWeight.Black,
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                BrandMark(36)
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        NyawitStrings.BRAND_NAME,
                                        style = NyawitType.Wordmark,
                                        color = c.Text,
                                        maxLines = 1,
                                        softWrap = false,
                                    )
                                    Text(
                                        NyawitStrings.BRAND_SUBTITLE.uppercase(),
                                        style = NyawitType.Overline,
                                        color = c.Primary,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        softWrap = false,
                                    )
                                }
                            }
                        }
                    }

                    // Navigation Chapters Header
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "CHAPTER NAVIGASI",
                            style = NyawitType.Overline,
                            color = if (c.isDark) c.Primary else Color(0xFF0F172A),
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            softWrap = false,
                        )
                        Text(
                            "INDEX 01-03",
                            style = NyawitType.Overline,
                            color = c.Muted,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }

                    // Comic Chapter Navigation Items (All 3 items guaranteed visible)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        NavItem(
                            chapter = "01",
                            label = NyawitStrings.NAV_APPS,
                            subtitle = "KATALOG & MODUL",
                            selected = page == AppPage.APPS,
                            icon = { iconColor -> GridIcon(Modifier.size(18.dp), iconColor) },
                            onClick = { onNavigate(AppPage.APPS); onClose?.invoke() },
                        )
                        NavItem(
                            chapter = "02",
                            label = NyawitStrings.NAV_SETTINGS,
                            subtitle = "TEMA & PREFERENSI",
                            selected = page == AppPage.SETTINGS,
                            icon = { iconColor -> SettingsIcon(Modifier.size(18.dp), iconColor) },
                            onClick = { onNavigate(AppPage.SETTINGS); onClose?.invoke() },
                        )
                        NavItem(
                            chapter = "03",
                            label = NyawitStrings.NAV_ABOUT,
                            subtitle = "INFO & KREDIT",
                            selected = page == AppPage.ABOUT,
                            icon = { iconColor -> InfoIcon(Modifier.size(18.dp), iconColor) },
                            onClick = { onNavigate(AppPage.ABOUT); onClose?.invoke() },
                        )
                    }
                }

                // Bottom Bay: Telemetry Strip + Check Update Action Button
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ComicPanel(
                        modifier = Modifier.fillMaxWidth(),
                        borderWidth = 2.dp,
                        shadowOffset = 3.dp,
                        backgroundColor = c.SurfaceRaised,
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column(Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(8.dp)
                                        .background(
                                            if (updateCount > 0 || updateTag != null) c.Warning else c.Primary,
                                            CircleShape,
                                        ),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "TELEMETRI // RADAR",
                                    style = NyawitType.Overline,
                                    color = if (c.isDark) c.Primary else Color(0xFF0F172A),
                                    fontWeight = FontWeight.Black,
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Modul Aktif:", style = NyawitType.Caption, color = c.Muted, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                Text("$total Unit", style = NyawitType.Caption, color = c.Text, fontWeight = FontWeight.Black, maxLines = 1, softWrap = false)
                            }
                            Spacer(Modifier.height(2.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Status Sistem:", style = NyawitType.Caption, color = c.Muted, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                Text(
                                    if (updateCount > 0) "$updateCount Update!"
                                    else if (updateTag != null) "Update $updateTag"
                                    else "Terbaru ✦",
                                    style = NyawitType.Caption,
                                    color = if (updateCount > 0 || updateTag != null) c.Warning else c.Primary,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    softWrap = false,
                                )
                            }
                        }
                    }

                    // Check Update Action Button with Comic Tactile Push
                    ComicButton(
                        text = if (selfBusy) "MEMERIKSA..." else "CEK PEMBARUAN!",
                        onClick = { onUpdate(); onClose?.invoke() },
                        enabled = !selfBusy,
                        loading = selfBusy,
                        variant = if (updateCount > 0 || updateTag != null) ComicButtonVariant.WARNING else ComicButtonVariant.PRIMARY,
                        icon = { iconColor -> SyncIcon(Modifier.size(15.dp), iconColor) },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Black),
                    )
                }
            }
        }

        // Comic Spine Margin (Double Inked Lines)
        Row(Modifier.fillMaxHeight()) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(2.5.dp)
                    .background(if (c.isDark) Color(0xFF334155) else Color(0xFF0F172A))
            )
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(1.5.dp)
                    .background(if (c.isDark) c.Primary.copy(alpha = 0.25f) else Color(0xFFE2E8F0))
            )
        }
    }
}

@Composable
private fun NavItem(
    chapter: String,
    label: String,
    subtitle: String,
    selected: Boolean,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit,
) {
    val c = LocalNyawitColors.current
    val responsiveScale = LocalResponsiveScale.current

    val shadowColor = if (selected) {
        if (c.isDark) Color(0xFF020617) else Color(0xFF0F172A)
    } else {
        if (c.isDark) Color(0xFF020617).copy(alpha = 0.6f) else Color(0xFF0F172A).copy(alpha = 0.15f)
    }

    val borderColor = if (selected) {
        if (c.isDark) Color(0xFF020617) else Color(0xFF0F172A)
    } else {
        if (c.isDark) Color(0xFF334155) else Color(0xFF0F172A)
    }

    val containerBg = if (selected) c.Primary else c.SurfaceRaised
    val contentColor = if (selected) c.OnPrimary else c.Text

    Box(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .tvFocusHighlight(RoundedCornerShape(8.dp), 1f)
    ) {
        // Comic Hard Drop Shadow
        Box(
            Modifier
                .matchParentSize()
                .offset(3.dp, 3.dp)
                .background(shadowColor, RoundedCornerShape(8.dp))
        )

        // Main Inked Nav Panel
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(containerBg)
                .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(8.dp))
                .padding(horizontal = responsiveScale.dp(12), vertical = responsiveScale.dp(10)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Chapter Pill Tag
            Box(
                Modifier
                    .background(
                        if (selected) Color(0xFF0F172A) else (if (c.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                        RoundedCornerShape(4.dp),
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            if (selected) Color(0xFF0F172A) else (if (c.isDark) Color(0xFF475569) else Color(0xFF0F172A)),
                        ),
                        RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    chapter,
                    style = NyawitType.Overline,
                    color = if (selected) c.Primary else c.Muted,
                    fontWeight = FontWeight.Black,
                )
            }

            Spacer(Modifier.width(responsiveScale.dp(10)))

            // Icon
            icon(if (selected) c.OnPrimary else c.Primary)

            Spacer(Modifier.width(responsiveScale.dp(10)))

            // Title & Subtitle
            Column(Modifier.weight(1f)) {
                Text(
                    label.uppercase(),
                    style = NyawitType.Subtitle,
                    color = contentColor,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    softWrap = false,
                )
                Text(
                    subtitle,
                    style = NyawitType.Overline,
                    color = if (selected) c.OnPrimary.copy(alpha = 0.85f) else c.Muted,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                )
            }

            if (selected) {
                Spacer(Modifier.width(4.dp))
                Text(
                    "▶",
                    style = NyawitType.Caption,
                    color = c.OnPrimary,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

