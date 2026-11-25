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
import dev.nyawit.launcher.core.ui.GlobeIcon
import dev.nyawit.launcher.core.ui.GridIcon
import dev.nyawit.launcher.core.ui.InfoIcon
import dev.nyawit.launcher.core.ui.LocalLanguageController
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


        // Ecosystem Status Card
        Surface(
            color = c.SurfaceRaised,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, c.BorderHighlight),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(7.dp)
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
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Modul:", style = NyawitType.Caption, color = c.Muted)
                    Text("$total Unit", style = NyawitType.Caption, color = c.Text, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(3.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Status Sistem:", style = NyawitType.Caption, color = c.Muted)
                    Text(
                        if (updateCount > 0) "$updateCount Pembaruan"
                        else if (updateTag != null) "Update $updateTag"
                        else "Terbaru",
                        style = NyawitType.Caption,
                        color = if (updateCount > 0 || updateTag != null) c.Warning else c.Primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Check Update Button
        Button(
            onClick = { onUpdate(); onClose?.invoke() },
            enabled = !selfBusy,
            colors = ButtonDefaults.buttonColors(
                containerColor = c.PrimaryMuted,
                contentColor = c.Primary,
            ),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, c.Primary.copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .tvFocusHighlight(RoundedCornerShape(10.dp), 1f),
        ) {
            if (selfBusy) {
                CircularProgressIndicator(
                    Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = c.Primary
                )
                Spacer(Modifier.width(8.dp))
            } else {
                SyncIcon(Modifier.size(15.dp), c.Primary)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                NyawitStrings.BTN_CHECK_UPDATES.uppercase(),
                style = NyawitType.Caption,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Box(Modifier.fillMaxHeight().width(1.5.dp).background(c.BorderHighlight))
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
        animationSpec = tween(180),
    )
    val contentColor = if (selected) c.Primary else c.Muted

    Row(
        Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(itemBg)
            .border(
                BorderStroke(
                    1.dp,
                    if (selected) c.Primary.copy(alpha = 0.45f) else Color.Transparent,
                ),
                RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick)
            .focusable()
            .tvFocusHighlight(RoundedCornerShape(10.dp), 1f)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = if (selected) c.Primary else c.SurfaceHighlight,
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier.width(3.dp).height(20.dp),
        ) {}
        Spacer(Modifier.width(10.dp))
        icon(contentColor)
        Spacer(Modifier.width(10.dp))
        Text(
            label,
            style = NyawitType.Subtitle,
            color = if (selected) c.Text else contentColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
