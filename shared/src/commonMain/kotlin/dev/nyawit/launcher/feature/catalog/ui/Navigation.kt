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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.GlobeIcon
import dev.nyawit.launcher.core.ui.GridIcon
import dev.nyawit.launcher.core.ui.InfoIcon
import dev.nyawit.launcher.core.ui.LocalLanguageController
import dev.nyawit.launcher.core.ui.LocalResponsiveScale
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.PaletteIcon
import dev.nyawit.launcher.core.ui.SettingsIcon
import dev.nyawit.launcher.core.ui.SyncIcon
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.LocalThemeController
import dev.nyawit.launcher.theme.NyawitType
import dev.nyawit.launcher.theme.ThemeMode

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

    Row(modifier.fillMaxHeight()) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(c.Surface)
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
        ) {
        // Brand Header
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BrandMark(38)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(NyawitStrings.BRAND_NAME, style = NyawitType.Wordmark, color = c.Text)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(5.dp).background(c.Primary, CircleShape))
                    Spacer(Modifier.width(5.dp))
                    Text(
                        NyawitStrings.BRAND_SUBTITLE,
                        style = NyawitType.Overline,
                        color = c.Primary
                    )
                }
            }
            if (onClose != null) {
                Surface(
                    color = c.SurfaceRaised,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, c.Border),
                    modifier = Modifier.size(32.dp).clickable { onClose() },
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            NyawitStrings.SYMBOL_CLOSE,
                            color = c.Muted,
                            style = NyawitType.Caption,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Spacer(Modifier.height(24.dp))
            Text(NyawitStrings.NAV_TITLE, style = NyawitType.Overline, color = c.Faint)
            Spacer(Modifier.height(8.dp))

            NavItem(
                label = NyawitStrings.NAV_APPS,
                selected = page == AppPage.APPS,
                icon = { iconColor -> GridIcon(Modifier.size(16.dp), iconColor) },
                onClick = { onNavigate(AppPage.APPS); onClose?.invoke() },
            )
            Spacer(Modifier.height(5.dp))
            NavItem(
                label = NyawitStrings.NAV_SETTINGS,
                selected = page == AppPage.SETTINGS,
                icon = { iconColor -> SettingsIcon(Modifier.size(16.dp), iconColor) },
                onClick = { onNavigate(AppPage.SETTINGS); onClose?.invoke() },
            )
            Spacer(Modifier.height(5.dp))
            NavItem(
                label = NyawitStrings.NAV_ABOUT,
                selected = page == AppPage.ABOUT,
                icon = { iconColor -> InfoIcon(Modifier.size(16.dp), iconColor) },
                onClick = { onNavigate(AppPage.ABOUT); onClose?.invoke() },
            )


            Spacer(Modifier.height(8.dp))
        }


        // Comic Telemetry Strip
        ComicPanel(
            modifier = Modifier.fillMaxWidth(),
            borderWidth = 2.dp,
            shadowOffset = 3.dp,
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(
                                if (updateCount > 0 || updateTag != null) c.Warning else c.Primary,
                                CircleShape,
                            ),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "STATUS TELEMETRI",
                        style = NyawitType.Overline,
                        color = c.Primary,
                        fontWeight = FontWeight.Black,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Modul:", style = NyawitType.Caption, color = c.Muted, fontWeight = FontWeight.Bold)
                    Text("$total Unit", style = NyawitType.Caption, color = c.Text, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(3.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Status Sistem:", style = NyawitType.Caption, color = c.Muted, fontWeight = FontWeight.Bold)
                    Text(
                        if (updateCount > 0) "$updateCount Update!"
                        else if (updateTag != null) "Update $updateTag"
                        else "Terbaru",
                        style = NyawitType.Caption,
                        color = if (updateCount > 0 || updateTag != null) c.Warning else c.Primary,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Check Update Comic Button
        Button(
            onClick = { onUpdate(); onClose?.invoke() },
            enabled = !selfBusy,
            colors = ButtonDefaults.buttonColors(
                containerColor = c.PrimaryMuted,
                contentColor = c.Primary,
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, if (c.isDark) c.Primary else Color(0xFF0F172A)),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = LocalResponsiveScale.current.dp(42))
                .tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
        ) {
            if (selfBusy) {
                CircularProgressIndicator(
                    Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = c.Primary,
                )
                Spacer(Modifier.width(8.dp))
            } else {
                SyncIcon(Modifier.size(15.dp), c.Primary)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                "CEK PEMBARUAN! ➔",
                style = NyawitType.Caption,
                fontWeight = FontWeight.Black,
            )
        }
    }
    Box(Modifier.fillMaxHeight().width(2.dp).background(if (c.isDark) c.BorderHighlight else Color(0xFF0F172A)))
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit,
) {
    val c = LocalNyawitColors.current
    val itemBg by animateColorAsState(
        targetValue = if (selected) c.PrimaryMuted else Color.Transparent,
        animationSpec = tween(140),
    )
    val contentColor = if (selected) c.Primary else c.Muted
    val responsiveScale = LocalResponsiveScale.current

    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = responsiveScale.dp(46))
            .clip(RoundedCornerShape(8.dp))
            .background(itemBg)
            .border(
                BorderStroke(
                    2.dp,
                    if (selected) (if (c.isDark) c.Primary else Color(0xFF0F172A)) else Color.Transparent,
                ),
                RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick)
            .focusable()
            .tvFocusHighlight(RoundedCornerShape(8.dp), 1f)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = if (selected) c.Primary else c.SurfaceHighlight,
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier.width(4.dp).height(22.dp),
        ) {}
        Spacer(Modifier.width(10.dp))
        icon(contentColor)
        Spacer(Modifier.width(10.dp))
        Text(
            label.uppercase(),
            style = NyawitType.Subtitle,
            color = if (selected) c.Text else contentColor,
            fontWeight = FontWeight.Black,
        )
    }
}
