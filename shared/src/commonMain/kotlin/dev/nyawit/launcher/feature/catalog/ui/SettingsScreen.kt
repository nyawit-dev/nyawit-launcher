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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.platform.LocalSettingsController
import dev.nyawit.launcher.core.platform.clearCatalogCache
import dev.nyawit.launcher.core.ui.AppLanguage
import dev.nyawit.launcher.core.ui.CheckIcon
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.GlobeIcon
import dev.nyawit.launcher.core.ui.LocalResponsiveScale
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.SettingsIcon
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
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
    val profile = rememberDeviceProfile()
    val isNarrow = profile.kind == ScreenKind.PHONE || profile.widthDp < 680f
    var cacheClearedNotice by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .background(c.Background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 840.dp)
                .padding(
                    horizontal = if (isNarrow) 16.dp else 24.dp,
                    vertical = if (isNarrow) 16.dp else 20.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Deck Command Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = c.SurfaceRaised,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, c.BorderHighlight),
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        SettingsIcon(Modifier.size(24.dp), color = c.Primary)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = NyawitStrings.SETTINGS_TITLE.uppercase(),
                            style = NyawitType.Wordmark,
                            fontWeight = FontWeight.Black,
                            color = c.Text,
                        )
                        ComicBadge(
                            text = "KONFIGURASI SISTEM",
                            containerColor = c.Primary,
                            contentColor = c.OnPrimary,
                        )
                    }
                    Spacer(Modifier.height(2.dp))
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
                val modes = ThemeMode.entries
                if (isNarrow) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        modes.chunked(2).forEach { rowModes ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                rowModes.forEach { mode ->
                                    ThemeModeCard(
                                        mode = mode,
                                        selected = settingsController.themeController.mode == mode,
                                        onClick = { settingsController.setTheme(mode) },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        modes.forEach { mode ->
                            ThemeModeCard(
                                mode = mode,
                                selected = settingsController.themeController.mode == mode,
                                onClick = { settingsController.setTheme(mode) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            // Section 2: Bahasa & Regional
            SettingsSectionCard(
                title = NyawitStrings.SETTINGS_SEC_LANGUAGE,
                description = NyawitStrings.SETTINGS_SEC_LANGUAGE_DESC,
            ) {
                val languages = AppLanguage.entries
                if (isNarrow) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        languages.forEach { lang ->
                            LanguageCard(
                                lang = lang,
                                selected = settingsController.languageController.language == lang,
                                onClick = { settingsController.setLanguage(lang) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        languages.forEach { lang ->
                            LanguageCard(
                                lang = lang,
                                selected = settingsController.languageController.language == lang,
                                onClick = { settingsController.setLanguage(lang) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            // Section 3: Sistem & Pembaruan
            SettingsSectionCard(
                title = NyawitStrings.SETTINGS_SEC_UPDATES,
                description = NyawitStrings.SETTINGS_SEC_UPDATES_DESC,
            ) {
                Surface(
                    color = c.SurfaceRaised,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, c.BorderHighlight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { settingsController.setAutoCheck(!settingsController.autoCheckUpdates) }
                        .focusable()
                        .tvFocusHighlight(RoundedCornerShape(10.dp)),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = NyawitStrings.SETTINGS_AUTO_CHECK_TITLE,
                                style = NyawitType.Subtitle,
                                color = c.Text,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = NyawitStrings.SETTINGS_AUTO_CHECK_DESC,
                                style = NyawitType.Body,
                                color = c.Muted,
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Surface(
                            color = if (settingsController.autoCheckUpdates) c.Primary else c.SurfaceHighlight,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (settingsController.autoCheckUpdates) c.Primary else c.Border),
                        ) {
                            Text(
                                text = if (settingsController.autoCheckUpdates) "AKTIF" else "NONAKTIF",
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
                        color = c.Danger.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, c.Danger.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .clickable {
                                val success = clearCatalogCache(platformContext)
                                if (success) {
                                    cacheClearedNotice = true
                                    onCatalogCacheCleared?.invoke()
                                }
                            }
                            .focusable()
                            .tvFocusHighlight(RoundedCornerShape(8.dp)),
                    ) {
                        Text(
                            text = NyawitStrings.SETTINGS_BTN_CLEAR_CACHE,
                            style = NyawitType.Caption,
                            color = c.Danger,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    AnimatedVisibility(
                        visible = cacheClearedNotice,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Surface(
                            color = c.PrimaryMuted,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, c.Primary.copy(alpha = 0.35f)),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CheckIcon(Modifier.size(14.dp), color = c.Primary)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = NyawitStrings.SETTINGS_CACHE_CLEARED,
                                    style = NyawitType.Caption,
                                    color = c.Primary,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }

            // Footer status persisten
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CheckIcon(Modifier.size(14.dp), color = c.Primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = NyawitStrings.SETTINGS_STORAGE_SAVED_NOTICE,
                    style = NyawitType.Caption,
                    color = c.Muted,
                )
            }
        }
    }
}

@Composable
private fun ThemeModeCard(
    mode: ThemeMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    val modeColors = colorsFor(mode)
    val modeName = when (mode) {
        ThemeMode.CYBER_OBSIDIAN -> "Obsidian"
        ThemeMode.MIDNIGHT_SAPPHIRE -> "Sapphire"
        ThemeMode.OLED_BLACK -> "OLED Black"
        ThemeMode.NORDIC_LIGHT -> "Nordic Light"
    }

    Surface(
        color = if (selected) c.PrimaryMuted else c.SurfaceRaised,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) c.Primary else c.BorderHighlight,
        ),
        modifier = modifier
            .focusable()
            .tvFocusHighlight(RoundedCornerShape(10.dp), 1.02f)
            .clickable { onClick() },
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(Modifier.size(13.dp).background(modeColors.Background, CircleShape).border(1.dp, c.Border, CircleShape))
                    Box(Modifier.size(13.dp).background(modeColors.Surface, CircleShape).border(1.dp, c.Border, CircleShape))
                    Box(Modifier.size(13.dp).background(modeColors.Primary, CircleShape))
                }
                if (selected) {
                    CheckIcon(Modifier.size(14.dp), color = c.Primary)
                }
            }
            Text(
                text = modeName,
                style = NyawitType.Subtitle,
                color = if (selected) c.Primary else c.Text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun LanguageCard(
    lang: AppLanguage,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    Surface(
        color = if (selected) c.PrimaryMuted else c.SurfaceRaised,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) c.Primary else c.BorderHighlight,
        ),
        modifier = modifier
            .focusable()
            .tvFocusHighlight(RoundedCornerShape(10.dp), 1.02f)
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (selected) c.Primary.copy(alpha = 0.2f) else c.SurfaceHighlight,
                    shape = RoundedCornerShape(6.dp),
                ) {
                    Text(
                        when (lang) {
                            AppLanguage.ID -> "ID"
                            AppLanguage.EN -> "EN"
                        },
                        style = NyawitType.Overline,
                        color = if (selected) c.Primary else c.Muted,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = lang.nativeName,
                        style = NyawitType.Subtitle,
                        color = if (selected) c.Primary else c.Text,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    )
                    Text(
                        text = lang.label,
                        style = NyawitType.Caption,
                        color = c.Muted,
                    )
                }
            }
            if (selected) {
                CheckIcon(Modifier.size(16.dp), color = c.Primary)
            }
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
    ComicPanel(
        modifier = Modifier.fillMaxWidth(),
        borderWidth = 2.dp,
        shadowOffset = 3.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = c.Surface,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title.uppercase(),
                    style = NyawitType.Subtitle,
                    color = c.Primary,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = description,
                    style = NyawitType.Caption,
                    color = c.Muted,
                )
            }
            content()
        }
    }
}
