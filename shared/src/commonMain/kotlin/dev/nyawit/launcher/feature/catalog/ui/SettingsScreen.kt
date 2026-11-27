package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.platform.LocalSettingsController
import dev.nyawit.launcher.core.platform.clearCatalogCache
import dev.nyawit.launcher.core.platform.currentAppPlatform
import dev.nyawit.launcher.core.ui.AppLanguage
import dev.nyawit.launcher.core.ui.CheckIcon
import dev.nyawit.launcher.core.ui.ComicActionBurst
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicButton
import dev.nyawit.launcher.core.ui.ComicButtonVariant
import dev.nyawit.launcher.core.ui.ComicDialog
import dev.nyawit.launcher.core.ui.ComicDivider
import dev.nyawit.launcher.core.ui.ComicFilterChip
import dev.nyawit.launcher.core.ui.ComicHalftonePattern
import dev.nyawit.launcher.core.ui.ComicNotice
import dev.nyawit.launcher.core.ui.ComicNoticeType
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.ComicSearchBar
import dev.nyawit.launcher.core.ui.ComicSound
import dev.nyawit.launcher.core.ui.ComicSoundPlayer
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.SettingsIcon
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.install.clearAllDownloadedFiles
import dev.nyawit.launcher.feature.install.formatByteSize
import dev.nyawit.launcher.feature.install.getDownloadedFilesSize
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import dev.nyawit.launcher.theme.ThemeMode
import dev.nyawit.launcher.theme.colorsFor
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    platformContext: Any? = null,
    onCatalogCacheCleared: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    val settingsController = LocalSettingsController.current
    val profile = rememberDeviceProfile()
    val isNarrow = profile.kind == ScreenKind.PHONE || profile.widthDp < 680f
    var cacheClearedNotice by remember { mutableStateOf(false) }
    var cleanedBytesNotice by remember { mutableStateOf<String?>(null) }
    var downloadFilesSize by remember { mutableStateOf(getDownloadedFilesSize(platformContext)) }
    val initialFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (profile.kind == ScreenKind.TV) {
            delay(150)
            try {
                initialFocusRequester.requestFocus()
            } catch (_: Throwable) {}
        }
    }

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
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            // Masthead Panel
            ComicPanel(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = c.SurfaceRaised,
                comicRivets = true,
                borderWidth = 2.5.dp,
                shadowOffset = 4.dp,
            ) {
                ComicHalftonePattern(
                    modifier = Modifier.matchParentSize(),
                    dotRadius = 1.2f,
                    spacing = 16f,
                    dotColor = c.Primary.copy(alpha = 0.08f),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Surface(
                            color = c.Surface,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(2.dp, c.ComicInk),
                            modifier = Modifier.size(48.dp),
                        ) {
                            Box(Modifier.fillMaxSize(), Alignment.Center) {
                                SettingsIcon(Modifier.size(24.dp), color = c.Primary)
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
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
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = NyawitStrings.SETTINGS_SUBTITLE,
                                style = NyawitType.Body,
                                color = c.Muted,
                            )
                        }
                    }

                    if (onBack != null) {
                        ComicButton(
                            text = "KEMBALI",
                            onClick = onBack,
                            variant = ComicButtonVariant.SECONDARY,
                            shadowOffset = 2.dp,
                        )
                    }
                }
            }

            // Section 1: Tema & Tampilan
            SettingsSectionCard(
                title = NyawitStrings.SETTINGS_SEC_APPEARANCE,
                description = NyawitStrings.SETTINGS_SEC_APPEARANCE_DESC,
                badge = "TEMA",
            ) {
                val modes = ThemeMode.entries
                if (isNarrow) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        modes.chunked(2).forEachIndexed { rowIndex, rowModes ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                rowModes.forEachIndexed { colIndex, mode ->
                                    val isInitial = rowIndex == 0 && colIndex == 0
                                    ComicThemeCard(
                                        mode = mode,
                                        selected = settingsController.themeController.mode == mode,
                                        onClick = { settingsController.setTheme(mode) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .then(if (isInitial) Modifier.focusRequester(initialFocusRequester) else Modifier),
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
                        modes.forEachIndexed { index, mode ->
                            val isInitial = index == 0
                            ComicThemeCard(
                                mode = mode,
                                selected = settingsController.themeController.mode == mode,
                                onClick = { settingsController.setTheme(mode) },
                                modifier = Modifier
                                    .weight(1f)
                                    .then(if (isInitial) Modifier.focusRequester(initialFocusRequester) else Modifier),
                            )
                        }
                    }
                }
            }

            // Section 2: Bahasa & Regional
            SettingsSectionCard(
                title = NyawitStrings.SETTINGS_SEC_LANGUAGE,
                description = NyawitStrings.SETTINGS_SEC_LANGUAGE_DESC,
                badge = "BAHASA",
            ) {
                val languages = AppLanguage.entries
                if (isNarrow) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        languages.forEach { lang ->
                            ComicLanguageCard(
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
                            ComicLanguageCard(
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
                badge = "OTOMATIS",
            ) {
                ComicPanel(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = c.SurfaceRaised,
                    borderWidth = 1.5.dp,
                    shadowOffset = 2.dp,
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
                                fontWeight = FontWeight.Black,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = NyawitStrings.SETTINGS_AUTO_CHECK_DESC,
                                style = NyawitType.Body,
                                color = c.Muted,
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        ComicButton(
                            text = if (settingsController.autoCheckUpdates) "AKTIF" else "NONAKTIF",
                            onClick = { settingsController.setAutoCheck(!settingsController.autoCheckUpdates) },
                            variant = if (settingsController.autoCheckUpdates) ComicButtonVariant.PRIMARY else ComicButtonVariant.SECONDARY,
                            shadowOffset = 2.dp,
                        )
                    }
                }
            }

            // Section 4: Manajemen Penyimpanan & Pembersih Residu (Storage Manager)
            SettingsSectionCard(
                title = "MANAJEMEN PENYIMPANAN & BERKAS RESIDU",
                description = "Pantau dan bersihkan berkas paket installer serta cache katalog yang tidak terpakai.",
                badge = "STORAGE",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Item A: Pembersih Installer Berkas Unduhan
                    ComicPanel(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = c.SurfaceRaised,
                        borderWidth = 1.5.dp,
                        shadowOffset = 2.dp,
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = "BERKAS INSTALLER TERSIMPAN",
                                        style = NyawitType.Subtitle,
                                        color = c.Text,
                                        fontWeight = FontWeight.Black,
                                    )
                                    ComicBadge(
                                        text = formatByteSize(downloadFilesSize),
                                        containerColor = if (downloadFilesSize > 0) c.Warning else c.SurfaceHighlight,
                                        contentColor = if (downloadFilesSize > 0) c.OnWarning else c.Muted,
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Paket instalasi (.zip, .exe, .apk) sementara di direktori unduhan perangkat.",
                                    style = NyawitType.Body,
                                    color = c.Muted,
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            ComicButton(
                                text = "BERSIHKAN RESIDU",
                                onClick = {
                                    val freed = clearAllDownloadedFiles(platformContext)
                                    downloadFilesSize = getDownloadedFilesSize(platformContext)
                                    cleanedBytesNotice = "${formatByteSize(freed)} berkas installer berhasil dibersihkan!"
                                },
                                enabled = downloadFilesSize > 0L,
                                variant = ComicButtonVariant.DANGER,
                                shadowOffset = 2.dp,
                            )
                        }
                    }

                    AnimatedVisibility(visible = cleanedBytesNotice != null, enter = fadeIn(), exit = fadeOut()) {
                        ComicNotice(
                            text = cleanedBytesNotice.orEmpty(),
                            type = ComicNoticeType.SUCCESS,
                            onDismiss = { cleanedBytesNotice = null },
                        )
                    }

                    ComicDivider(dashed = true)

                    // Item B: Pembersih Cache Katalog
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "CACHE KATALOG APLIKASI",
                                style = NyawitType.Subtitle,
                                color = c.Text,
                                fontWeight = FontWeight.Black,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = "Menyimpan data daftar aplikasi dan metadata dari server repositori.",
                                style = NyawitType.Body,
                                color = c.Muted,
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        ComicButton(
                            text = NyawitStrings.SETTINGS_BTN_CLEAR_CACHE,
                            onClick = {
                                val success = clearCatalogCache(platformContext)
                                if (success) {
                                    cacheClearedNotice = true
                                    onCatalogCacheCleared?.invoke()
                                }
                            },
                            variant = ComicButtonVariant.SECONDARY,
                            shadowOffset = 2.dp,
                        )
                    }

                    AnimatedVisibility(visible = cacheClearedNotice, enter = fadeIn(), exit = fadeOut()) {
                        ComicNotice(
                            text = NyawitStrings.SETTINGS_CACHE_CLEARED,
                            type = ComicNoticeType.SUCCESS,
                            onDismiss = { cacheClearedNotice = false },
                        )
                    }
                }
            }

            // Seksi: Efek Suara Retro Arcade
            SettingsSectionCard(
                title = "EFEK SUARA RETRO / ARCADE SFX",
                description = "Efek suara mekanis 8-bit saat modul ditekan, D-Pad TV berpindah, dan aplikasi selesai dipasang.",
                badge = if (settingsController.soundEffectsEnabled) "AKTIF // 8-BIT" else "SENYAP",
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ComicFilterChip(
                        text = if (settingsController.soundEffectsEnabled) "EFEK SUARA: AKTIF" else "EFEK SUARA: SENYAP",
                        selected = settingsController.soundEffectsEnabled,
                        onClick = {
                            val next = !settingsController.soundEffectsEnabled
                            settingsController.updateSoundEffects(next)
                            if (next) ComicSoundPlayer.play(ComicSound.SUCCESS)
                        },
                    )

                    ComicButton(
                        text = "TES SUARA!",
                        onClick = {
                            ComicSoundPlayer.play(ComicSound.SUCCESS)
                        },
                        variant = ComicButtonVariant.SECONDARY,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }

            // Seksi: Repositori / Katalog Kustom
            var customUrlInput by remember(settingsController.customCatalogUrl) {
                mutableStateOf(settingsController.customCatalogUrl)
            }
            var repoSavedNotice by remember { mutableStateOf(false) }

            SettingsSectionCard(
                title = "REPOSITORI KATALOG KUSTOM",
                description = "URL endpoint JSON eksternal untuk menambahkan atau menyesuaikan daftar aplikasi komunitas.",
                badge = if (settingsController.customCatalogUrl.isNotBlank()) "KUSTOM" else "OFFICIAL",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ComicSearchBar(
                        query = customUrlInput,
                        onQueryChange = { customUrlInput = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ComicButton(
                            text = "SIMPAN REPO",
                            onClick = {
                                settingsController.updateCustomCatalog(customUrlInput)
                                repoSavedNotice = true
                                onCatalogCacheCleared?.invoke()
                            },
                            variant = ComicButtonVariant.PRIMARY,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        )
                        if (settingsController.customCatalogUrl.isNotBlank()) {
                            ComicButton(
                                text = "RESET KE DEFAULT",
                                onClick = {
                                    customUrlInput = ""
                                    settingsController.updateCustomCatalog("")
                                    repoSavedNotice = true
                                    onCatalogCacheCleared?.invoke()
                                },
                                variant = ComicButtonVariant.SECONDARY,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            )
                        }
                    }
                    AnimatedVisibility(visible = repoSavedNotice, enter = fadeIn(), exit = fadeOut()) {
                        ComicNotice(
                            text = if (settingsController.customCatalogUrl.isNotBlank())
                                "URL katalog kustom berhasil disimpan. Katalog akan dimuat ulang!"
                            else "Katalog dikembalikan ke repositori resmi.",
                            type = ComicNoticeType.SUCCESS,
                            onDismiss = { repoSavedNotice = false },
                        )
                    }
                }
            }

            // Seksi: Integrasi Desktop (System Tray & Start on Boot)
            if (currentAppPlatform() == AppPlatform.DESKTOP) {
                SettingsSectionCard(
                    title = "INTEGRASI DESKTOP // TRAY & STARTUP",
                    description = "Konfigurasi jalannya launcher di background Windows/Linux.",
                    badge = "DESKTOP PC",
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ComicFilterChip(
                            text = if (settingsController.minimizeToTray) "MINIMIZE KE TRAY: AKTIF" else "MINIMIZE KE TRAY: NONAKTIF",
                            selected = settingsController.minimizeToTray,
                            onClick = {
                                settingsController.updateMinimizeToTray(!settingsController.minimizeToTray)
                            },
                        )

                        ComicFilterChip(
                            text = if (settingsController.startOnBoot) "START ON BOOT: AKTIF" else "START ON BOOT: NONAKTIF",
                            selected = settingsController.startOnBoot,
                            onClick = {
                                val next = !settingsController.startOnBoot
                                settingsController.updateStartOnBoot(next)
                            },
                        )
                    }
                }
            }

            // Seksi: Cadangkan & Pulihkan Pengaturan (Backup & Restore)
            var showExportDialog by remember { mutableStateOf(false) }
            var showImportDialog by remember { mutableStateOf(false) }
            var importInputText by remember { mutableStateOf("") }
            var restoreNotice by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

            SettingsSectionCard(
                title = "CADANGKAN & PULIHKAN // BACKUP & RESTORE",
                description = "Ekspor atau pulihkan seluruh preferensi, daftar aplikasi favorit, dan kustomisasi launcher dalam format JSON.",
                badge = "DATA PENGGUNA",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ComicButton(
                            text = "EKSPOR PENGATURAN",
                            onClick = {
                                showExportDialog = true
                            },
                            variant = ComicButtonVariant.SECONDARY,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        )

                        ComicButton(
                            text = "PULIHKAN PENGATURAN",
                            onClick = {
                                importInputText = ""
                                showImportDialog = true
                            },
                            variant = ComicButtonVariant.PRIMARY,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        )
                    }

                    restoreNotice?.let { (success, msg) ->
                        ComicNotice(
                            text = msg,
                            type = if (success) ComicNoticeType.SUCCESS else ComicNoticeType.DANGER,
                            onDismiss = { restoreNotice = null },
                        )
                    }
                }
            }

            if (showExportDialog) {
                val exportedJson = remember { settingsController.exportSettings() }
                ComicDialog(
                    title = "KODE CADANGAN JSON",
                    badgeText = "EKSPOR",
                    onDismissRequest = { showExportDialog = false },
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "Salin kode cadangan pengaturan Anda di bawah ini:",
                            style = NyawitType.Body,
                            color = c.Text,
                        )
                        ComicSearchBar(
                            query = exportedJson,
                            onQueryChange = {},
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            ComicButton(
                                text = "SELESAI",
                                onClick = { showExportDialog = false },
                                variant = ComicButtonVariant.PRIMARY,
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
            }

            if (showImportDialog) {
                ComicDialog(
                    title = "PULIHKAN PENGATURAN",
                    badgeText = "IMPOR",
                    onDismissRequest = { showImportDialog = false },
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "Tempelkan kode cadangan JSON di bawah untuk memulihkan konfigurasi:",
                            style = NyawitType.Body,
                            color = c.Text,
                        )
                        ComicSearchBar(
                            query = importInputText,
                            onQueryChange = { importInputText = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                        ) {
                            ComicButton(
                                text = "BATAL",
                                onClick = { showImportDialog = false },
                                variant = ComicButtonVariant.SECONDARY,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            )
                            ComicButton(
                                text = "PULIHKAN SEKARANG",
                                onClick = {
                                    val ok = settingsController.importSettings(importInputText)
                                    if (ok) {
                                        restoreNotice = true to "Pengaturan dan favorit berhasil dipulihkan!"
                                        showImportDialog = false
                                        onCatalogCacheCleared?.invoke()
                                    } else {
                                        restoreNotice = false to "Gagal memulihkan: format JSON tidak sesuai."
                                    }
                                },
                                variant = ComicButtonVariant.PRIMARY,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            )
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
                ComicBadge(
                    text = "AUTO-SAVED // SISTEM TERSINKRONISASI",
                    containerColor = c.SurfaceRaised,
                    contentColor = c.Muted,
                )
            }
        }
    }
}

@Composable
private fun ComicThemeCard(
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

    val shadowOffset = if (selected) 3.dp else 1.5.dp
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .padding(bottom = shadowOffset, end = shadowOffset)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A), shape)
                .border(1.5.dp, if (selected) c.Primary else Color.Transparent, shape),
        )

        // Foreground
        Box(
            modifier = Modifier
                .background(if (selected) c.PrimaryMuted else c.SurfaceRaised, shape)
                .border(2.dp, if (selected) c.Primary else c.ComicInk, shape)
                .padding(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(13.dp).background(modeColors.Background, CircleShape).border(1.dp, c.ComicInk, CircleShape))
                        Box(Modifier.size(13.dp).background(modeColors.Surface, CircleShape).border(1.dp, c.ComicInk, CircleShape))
                        Box(Modifier.size(13.dp).background(modeColors.Primary, CircleShape))
                    }
                    if (selected) {
                        CheckIcon(Modifier.size(14.dp), color = c.Primary)
                    }
                }
                Text(
                    text = modeName.uppercase(),
                    style = NyawitType.Subtitle,
                    color = if (selected) c.Primary else c.Text,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
private fun ComicLanguageCard(
    lang: AppLanguage,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    val shadowOffset = if (selected) 3.dp else 1.5.dp
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .padding(bottom = shadowOffset, end = shadowOffset)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A), shape)
                .border(1.5.dp, if (selected) c.Primary else Color.Transparent, shape),
        )

        // Foreground
        Row(
            modifier = Modifier
                .background(if (selected) c.PrimaryMuted else c.SurfaceRaised, shape)
                .border(2.dp, if (selected) c.Primary else c.ComicInk, shape)
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ComicBadge(
                    text = when (lang) {
                        AppLanguage.ID -> "ID"
                        AppLanguage.EN -> "EN"
                    },
                    containerColor = if (selected) c.Primary else c.SurfaceHighlight,
                    contentColor = if (selected) c.OnPrimary else c.Muted,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = lang.nativeName.uppercase(),
                        style = NyawitType.Subtitle,
                        color = if (selected) c.Primary else c.Text,
                        fontWeight = FontWeight.Black,
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
    badge: String? = null,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.weight(1f)) {
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
                if (badge != null) {
                    ComicBadge(
                        text = badge,
                        containerColor = c.SurfaceRaised,
                        contentColor = c.Muted,
                    )
                }
            }
            content()
        }
    }
}
