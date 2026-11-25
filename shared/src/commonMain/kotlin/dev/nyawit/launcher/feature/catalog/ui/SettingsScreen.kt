package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.core.platform.LocalSettingsController
import dev.nyawit.launcher.core.platform.clearCatalogCache
import dev.nyawit.launcher.core.ui.AppLanguage
import dev.nyawit.launcher.core.ui.CheckIcon
import dev.nyawit.launcher.core.ui.GlobeIcon
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.PaletteIcon
import dev.nyawit.launcher.core.ui.ScalePreference
import dev.nyawit.launcher.core.ui.SettingsIcon
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import dev.nyawit.launcher.theme.ThemeMode
import dev.nyawit.launcher.theme.colorsFor

@Composable
fun SettingsScreen(
    platformContext: Any? = null,
    onCatalogCacheCleared: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    val settingsController = LocalSettingsController.current
    var cacheClearedNotice by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .background(c.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = c.PrimaryMuted,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, c.Primary),
                modifier = Modifier.size(46.dp),
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    SettingsIcon(Modifier.size(24.dp), color = c.Primary)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = NyawitStrings.SETTINGS_TITLE,
                    style = NyawitType.Wordmark,
                    color = c.Text,
                )
                Text(
                    text = NyawitStrings.SETTINGS_SUBTITLE,
                    style = NyawitType.Body,
                    color = c.Muted,
                )
            }
        }

        // Section 1: Tema & Tampilan
        SettingsSectionCard(
            title = NyawitStrings.SETTINGS_SEC_APPEARANCE,
            description = NyawitStrings.SETTINGS_SEC_APPEARANCE_DESC,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ThemeMode.entries.forEach { mode ->
                    val isSelected = settingsController.themeController.mode == mode
                    val modeColors = colorsFor(mode)
                    val modeName = when (mode) {
                        ThemeMode.CYBER_OBSIDIAN -> "Obsidian"
                        ThemeMode.MIDNIGHT_SAPPHIRE -> "Sapphire"
                        ThemeMode.OLED_BLACK -> "OLED Black"
                        ThemeMode.NORDIC_LIGHT -> "Nordic Light"
                    }

                    Surface(
                        color = if (isSelected) c.PrimaryMuted else c.SurfaceRaised,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) c.Primary else c.Border,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusable()
                            .tvFocusHighlight(RoundedCornerShape(10.dp), 1.02f)
                            .clickable { settingsController.setTheme(mode) },
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(Modifier.size(12.dp).background(modeColors.Background, CircleShape).border(1.dp, c.Border, CircleShape))
                                    Box(Modifier.size(12.dp).background(modeColors.Surface, CircleShape).border(1.dp, c.Border, CircleShape))
                                    Box(Modifier.size(12.dp).background(modeColors.Primary, CircleShape))
                                }
                                if (isSelected) {
                                    CheckIcon(Modifier.size(14.dp), color = c.Primary)
                                }
                            }
                            Text(
                                text = modeName,
                                style = NyawitType.Subtitle,
                                color = if (isSelected) c.Primary else c.Text,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Bahasa & Regional
        SettingsSectionCard(
            title = NyawitStrings.SETTINGS_SEC_LANGUAGE,
            description = NyawitStrings.SETTINGS_SEC_LANGUAGE_DESC,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppLanguage.entries.forEach { lang ->
                    val isSelected = settingsController.languageController.language == lang
                    Surface(
                        color = if (isSelected) c.PrimaryMuted else c.SurfaceRaised,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) c.Primary else c.Border,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusable()
                            .tvFocusHighlight(RoundedCornerShape(10.dp), 1.02f)
                            .clickable { settingsController.setLanguage(lang) },
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GlobeIcon(Modifier.size(18.dp), color = if (isSelected) c.Primary else c.Muted)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = lang.nativeName,
                                        style = NyawitType.Subtitle,
                                        color = if (isSelected) c.Primary else c.Text,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    )
                                    Text(
                                        text = lang.label,
                                        style = NyawitType.Caption,
                                        color = c.Muted,
                                    )
                                }
                            }
                            if (isSelected) {
                                CheckIcon(Modifier.size(16.dp), color = c.Primary)
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Penskalaan Layar
        SettingsSectionCard(
            title = NyawitStrings.SETTINGS_SEC_SCALE,
            description = NyawitStrings.SETTINGS_SEC_SCALE_DESC,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ScalePreference.entries.forEach { pref ->
                    val isSelected = settingsController.scalePreference == pref
                    val label = when (pref) {
                        ScalePreference.AUTO -> NyawitStrings.SETTINGS_SCALE_AUTO
                        ScalePreference.COMPACT -> NyawitStrings.SETTINGS_SCALE_COMPACT
                        ScalePreference.STANDARD -> NyawitStrings.SETTINGS_SCALE_STANDARD
                        ScalePreference.LARGE -> NyawitStrings.SETTINGS_SCALE_LARGE
                        ScalePreference.EXTRA_LARGE -> NyawitStrings.SETTINGS_SCALE_XLARGE
                    }

                    Surface(
                        color = if (isSelected) c.PrimaryMuted else c.SurfaceRaised,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) c.Primary else c.Border,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusable()
                            .tvFocusHighlight(RoundedCornerShape(8.dp), 1.02f)
                            .clickable { settingsController.setScale(pref) },
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = label,
                                style = NyawitType.Caption,
                                color = if (isSelected) c.Primary else c.Text,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            )
                            if (isSelected) {
                                CheckIcon(Modifier.size(12.dp), color = c.Primary)
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Sistem & Pembaruan
        SettingsSectionCard(
            title = NyawitStrings.SETTINGS_SEC_UPDATES,
            description = NyawitStrings.SETTINGS_SEC_UPDATES_DESC,
        ) {
            Surface(
                color = c.SurfaceRaised,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, c.Border),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { settingsController.setAutoCheck(!settingsController.autoCheckUpdates) },
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = NyawitStrings.SETTINGS_AUTO_CHECK_TITLE,
                            style = NyawitType.Subtitle,
                            color = c.Text,
                        )
                        Text(
                            text = NyawitStrings.SETTINGS_AUTO_CHECK_DESC,
                            style = NyawitType.Body,
                            color = c.Muted,
                        )
                    }
                    Surface(
                        color = if (settingsController.autoCheckUpdates) c.Primary else c.SurfaceHighlight,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (settingsController.autoCheckUpdates) c.Primary else c.Border),
                        modifier = Modifier.padding(start = 12.dp),
                    ) {
                        Text(
                            text = if (settingsController.autoCheckUpdates) "ON" else "OFF",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = NyawitType.Overline,
                            color = if (settingsController.autoCheckUpdates) c.OnPrimary else c.Muted,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // Section 5: Penyimpanan & Cache
        SettingsSectionCard(
            title = NyawitStrings.SETTINGS_SEC_STORAGE,
            description = NyawitStrings.SETTINGS_SEC_STORAGE_DESC,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = c.SurfaceRaised,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, c.Border),
                    modifier = Modifier
                        .clickable {
                            val success = clearCatalogCache(platformContext)
                            if (success) {
                                cacheClearedNotice = true
                                onCatalogCacheCleared?.invoke()
                            }
                        },
                ) {
                    Text(
                        text = NyawitStrings.SETTINGS_BTN_CLEAR_CACHE,
                        style = NyawitType.Subtitle,
                        color = c.Danger,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                AnimatedVisibility(
                    visible = cacheClearedNotice,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CheckIcon(Modifier.size(16.dp), color = c.Primary)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = NyawitStrings.SETTINGS_CACHE_CLEARED,
                            style = NyawitType.Caption,
                            color = c.Primary,
                        )
                    }
                }
            }
        }

        // Footer status persisten
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CheckIcon(Modifier.size(14.dp), color = c.Muted)
            Spacer(Modifier.width(6.dp))
            Text(
                text = NyawitStrings.SETTINGS_STORAGE_SAVED_NOTICE,
                style = NyawitType.Caption,
                color = c.Muted,
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    val c = LocalNyawitColors.current
    Surface(
        color = c.Surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, c.Border),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = NyawitType.Overline,
                    color = c.Primary,
                )
                Text(
                    text = description,
                    style = NyawitType.Body,
                    color = c.Muted,
                )
            }
            content()
        }
    }
}
