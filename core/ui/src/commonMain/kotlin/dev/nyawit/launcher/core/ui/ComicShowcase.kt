@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.ComicTheme
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import dev.nyawit.launcher.theme.ThemeMode
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicShowcase: Galeri pengujian interaktif lengkap di mana SELURUH komponen komik
 * memiliki aksi hidup, transisi taktil, pengatur progres dinamis, serta dialog modal interaktif.
 */
@Composable
fun ComicShowcase(
    modifier: Modifier = Modifier,
) {
    var themeMode by remember { mutableStateOf<ThemeMode>(ThemeMode.CYBER_OBSIDIAN) }

    ComicTheme(mode = themeMode) {
        val c = LocalNyawitColors.current

        // Interactive States
        var actionLog by remember { mutableStateOf("Sentuh atau klik komponen mana saja untuk mencoba aksinya!") }
        var actionCount by remember { mutableStateOf(0) }
        var selectedCategory by remember { mutableStateOf("SEMUA") }
        var searchQuery by remember { mutableStateOf("") }
        var sampleProgress by remember { mutableStateOf(0.45f) }
        var isSimulatingDownload by remember { mutableStateOf(false) }
        var isButtonLoading by remember { mutableStateOf(false) }
        var showNotice by remember { mutableStateOf(true) }
        var showConfirmDialog by remember { mutableStateOf(false) }
        var cardAppStatus by remember { mutableStateOf("TERSEDIA") }
        var arcadeScore by remember { mutableStateOf(100) }
        var speechQuoteIndex by remember { mutableStateOf(0) }

        val quotes = listOf(
            "\"Halo! Komik Nyawit memadukan pop-art neo-brutalism dan manga halftone!\"",
            "\"Tekan tombol arcade di bawah untuk merasakan tactile spring mechanical push!\"",
            "\"Garis tinta pekat berpadu dengan arsir diagonal dinamis pada bilah progres!\"",
        )

        fun logAction(message: String) {
            actionCount++
            actionLog = "[$actionCount] $message"
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(c.Background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            // Masthead with Theme Toggle & Action Feedback
            ComicPanel(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = c.SurfaceRaised,
                comicRivets = true,
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ComicActionBurst(
                                text = "VOL.2",
                                onClick = { logAction("Stiker masthead VOL.2 diklik!") },
                            )
                            ComicBadge(
                                text = "KOMIK DESIGN SYSTEM",
                                containerColor = c.Primary,
                                contentColor = c.OnPrimary,
                            )
                        }

                        // Theme Mode Action Toggle
                        ComicButton(
                            text = if (themeMode == ThemeMode.CYBER_OBSIDIAN) "☀ MODE TERANG" else "🌙 MODE GELAP",
                            onClick = {
                                themeMode = if (themeMode == ThemeMode.CYBER_OBSIDIAN) ThemeMode.NORDIC_LIGHT else ThemeMode.CYBER_OBSIDIAN
                                logAction("Tema dialihkan ke ${if (themeMode == ThemeMode.CYBER_OBSIDIAN) "DARK" else "LIGHT"}")
                            },
                            variant = ComicButtonVariant.SECONDARY,
                            shadowOffset = 2.dp,
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "NYAWIT COMIC COMPONENT GALLERY",
                        style = NyawitType.Wordmark,
                        color = c.Text,
                    )
                    Text(
                        text = "Semua komponen di bawah ini memiliki interaksi fisik taktil dan aksi yang berfungsi sepenuhnya.",
                        style = NyawitType.Body,
                        color = c.Muted,
                    )

                    Spacer(Modifier.height(12.dp))

                    // Live Action Feedback Strip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(c.Surface, RoundedCornerShape(6.dp))
                            .border(1.5.dp, c.Primary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ComicBadge(
                                text = "LOG AKSI",
                                containerColor = c.Primary,
                                contentColor = c.OnPrimary,
                            )
                            Text(
                                text = actionLog,
                                style = NyawitType.Caption,
                                fontWeight = FontWeight.Bold,
                                color = c.Text,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }

            // 01 // Action Burst Stickers (Interactive)
            ComicSectionHeader(title = "01 // ACTION BURST STICKERS (KLIK UNTUK EFEK BOUNCE)")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ComicActionBurst(
                    text = "BOOM!",
                    size = 46.dp,
                    onClick = { logAction("BOOM! Ledakan komik meletus!") },
                )
                ComicActionBurst(
                    text = "HOT!",
                    burstColor = Color(0xFFFFDE00),
                    textColor = Color(0xFF0F172A),
                    size = 46.dp,
                    onClick = { logAction("HOT! Aplikasi sedang viral dan trending!") },
                )
                ComicActionBurst(
                    text = "UP!",
                    burstColor = Color(0xFFFF4757),
                    textColor = Color.White,
                    size = 46.dp,
                    onClick = { logAction("UP! Versi pembaruan baru siap diunduh!") },
                )
                ComicActionBurst(
                    text = "GO!",
                    burstColor = Color(0xFF00D2D3),
                    textColor = Color(0xFF0F172A),
                    size = 46.dp,
                    onClick = { logAction("GO! Aplikasi siap dijalankan!") },
                )
                ComicActionBurst(
                    text = "NEW!",
                    burstColor = Color(0xFF10B981),
                    textColor = Color.White,
                    size = 46.dp,
                    onClick = { logAction("NEW! Rilis paket baru ditambahkan ke katalog!") },
                )
            }

            // 02 // Tactile 3D Buttons & Actions
            ComicSectionHeader(title = "02 // TACTILE 3D BUTTONS (DITEKAN MASUK KE BAYANGAN)")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ComicButton(
                    text = "PRIMARY",
                    onClick = { logAction("Tombol PRIMARY diklik! Menjalankan aksi utama.") },
                    burstBadge = "POW!",
                    modifier = Modifier.weight(1f),
                )
                ComicButton(
                    text = "SECONDARY",
                    onClick = { logAction("Tombol SECONDARY diklik! Menjalankan aksi sekunder.") },
                    variant = ComicButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f),
                )
                ComicButton(
                    text = "WARNING",
                    onClick = { logAction("Tombol WARNING diklik! Memeriksa peringatan sistem.") },
                    variant = ComicButtonVariant.WARNING,
                    modifier = Modifier.weight(1f),
                )
                ComicButton(
                    text = "BUKA DIALOG",
                    onClick = {
                        showConfirmDialog = true
                        logAction("Membuka ComicDialog konfirmasi.")
                    },
                    variant = ComicButtonVariant.DANGER,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ComicButton(
                    text = if (isButtonLoading) "PROSES..." else "TOGGLE LOADING",
                    onClick = {
                        isButtonLoading = !isButtonLoading
                        logAction("Status loading tombol dialihkan ke $isButtonLoading")
                    },
                    loading = isButtonLoading,
                    modifier = Modifier.weight(1f),
                )
                ComicButton(
                    text = "NONAKTIF",
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.weight(1f),
                )
                ComicIconButton(
                    onClick = { logAction("Ikon PANAH diklik!") },
                    variant = ComicButtonVariant.PRIMARY,
                ) {
                    ArrowIcon(Modifier.size(18.dp), Color.White)
                }
                ComicIconButton(
                    onClick = { logAction("Ikon INFORMASI diklik!") },
                    variant = ComicButtonVariant.SECONDARY,
                ) {
                    InfoIcon(Modifier.size(18.dp), c.Text)
                }
            }

            // 03 // Search Bar & Filter Chips (Live Filtering)
            ComicSectionHeader(title = "03 // SEARCH BAR & FILTER CHIPS (PENCARIAN AKTIF)")
            ComicSearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    logAction("Mencari aplikasi: \"$it\"")
                },
                onSearch = { logAction("Pencarian dijalankan untuk: \"$searchQuery\"") },
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("SEMUA" to 12, "AUDIO" to 5, "UTILITY" to 4, "GAME" to 3).forEach { (cat, count) ->
                    ComicFilterChip(
                        text = cat,
                        selected = selectedCategory == cat,
                        count = count,
                        onClick = {
                            selectedCategory = cat
                            logAction("Kategori difilter: $cat ($count aplikasi)")
                        },
                    )
                }
            }

            // 04 // Dynamic Animated Progress Bar with Controls
            ComicSectionHeader(title = "04 // PROGRESS BAR ANIMASI ARSIR (DENGAN KONTROL - / +)")
            ComicProgressBar(
                progress = sampleProgress,
                label = "MENGUNDUH BERKAS (${(sampleProgress * 100).toInt()}%)",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ComicButton(
                    text = "- 15%",
                    onClick = {
                        sampleProgress = (sampleProgress - 0.15f).coerceIn(0f, 1f)
                        logAction("Progres diturunkan ke ${(sampleProgress * 100).toInt()}%")
                    },
                    variant = ComicButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f),
                )
                ComicButton(
                    text = "+ 15%",
                    onClick = {
                        sampleProgress = (sampleProgress + 0.15f).coerceIn(0f, 1f)
                        logAction("Progres dinaikkan ke ${(sampleProgress * 100).toInt()}%")
                    },
                    variant = ComicButtonVariant.PRIMARY,
                    modifier = Modifier.weight(1f),
                )
                ComicButton(
                    text = "RESET 100%",
                    onClick = {
                        sampleProgress = 1.0f
                        logAction("Progres disetel penuh 100%!")
                    },
                    variant = ComicButtonVariant.WARNING,
                    modifier = Modifier.weight(1f),
                )
            }

            // 05 // Interactive Comic Notice Banners
            ComicSectionHeader(title = "05 // NOTICE BANNERS (BISA DITUTUP / DISMISS)")
            AnimatedVisibility(visible = showNotice, enter = fadeIn(), exit = fadeOut()) {
                ComicNotice(
                    text = "Pembaruan launcher v14.2.0 tersedia dengan perbaikan bug jvm dan penghapusan residu.",
                    actionLabel = "PASANG UPDATE!",
                    onAction = { logAction("Aksi 'PASANG UPDATE!' pada banner notice diklik.") },
                    onDismiss = {
                        showNotice = false
                        logAction("Banner pemberitahuan ditutup oleh pengguna.")
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (!showNotice) {
                ComicButton(
                    text = "TAMPILKAN ULANG BANNER",
                    onClick = {
                        showNotice = true
                        logAction("Banner pemberitahuan dimunculkan kembali.")
                    },
                    variant = ComicButtonVariant.SECONDARY,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // 06 // Speech Bubble with Quote Rotation
            ComicSectionHeader(title = "06 // SPEECH BUBBLE (KLIK UNTUK GANTI UCAPAN)")
            ComicSpeechBubble(
                tailDirection = BubbleTailDirection.BOTTOM_LEFT,
                modifier = Modifier.clickable {
                    speechQuoteIndex = (speechQuoteIndex + 1) % quotes.size
                    logAction("Ucapan gelembung komik diganti ke dialog #${speechQuoteIndex + 1}")
                },
            ) {
                Column {
                    Text(
                        quotes[speechQuoteIndex],
                        style = NyawitType.Subtitle,
                        fontWeight = FontWeight.Bold,
                        color = c.Text,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "▶ Sentuh balon ini untuk mengganti dialog!",
                        style = NyawitType.Caption,
                        color = c.Primary,
                    )
                }
            }

            // 07 // Master Interactive Comic Card
            ComicSectionHeader(title = "07 // MASTER COMIC CARD (INTERAKTIF)")
            ComicCard(
                title = "Nyawit Music Player",
                subtitle = "Pemutar Musik Hi-Fi Ringan Multiplatform",
                category = selectedCategory,
                burstText = if (cardAppStatus == "TERPASANG") "READY!" else "HOT!",
                progress = if (cardAppStatus == "MENGUNDUH") sampleProgress else null,
                onClick = { logAction("Kartu Nyawit Music Player diklik!") },
                actionButton = {
                    ComicButton(
                        text = when (cardAppStatus) {
                            "TERPASANG" -> "JALANKAN!"
                            "MENGUNDUH" -> "BATALKAN"
                            else -> "PASANG!"
                        },
                        onClick = {
                            when (cardAppStatus) {
                                "TERSEDIA" -> {
                                    cardAppStatus = "MENGUNDUH"
                                    logAction("Memulai unduhan Nyawit Music Player...")
                                }
                                "MENGUNDUH" -> {
                                    cardAppStatus = "TERPASANG"
                                    logAction("Pemasangan Nyawit Music Player selesai!")
                                }
                                "TERPASANG" -> {
                                    cardAppStatus = "TERSEDIA"
                                    logAction("Menjalankan Nyawit Music Player!")
                                }
                            }
                        },
                        variant = if (cardAppStatus == "TERPASANG") ComicButtonVariant.PRIMARY else ComicButtonVariant.SECONDARY,
                    )
                },
                secondaryAction = {
                    ComicIconButton(onClick = {
                        showConfirmDialog = true
                        logAction("Membuka detail modul Nyawit Music Player.")
                    }) {
                        InfoIcon(Modifier.size(18.dp), c.Text)
                    }
                },
            )

            // 08 // Interactive Vector Arcade Illustration
            ComicSectionHeader(title = "08 // VECTOR ARCADE GAMEPAD (KLIK UNTUK SCORE + BOUNCE)")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ComicArcadeIllustration(
                        size = 90.dp,
                        onClick = {
                            arcadeScore += 100
                            logAction("INSERT COIN! Skor Gamepad bertambah jadi $arcadeScore!")
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                    ComicBadge(
                        text = "1P READY // SKOR: $arcadeScore",
                        containerColor = c.SurfaceHighlight,
                        contentColor = c.Primary,
                    )
                }
            }

            // 09 // Comic Ink Dividers
            ComicSectionHeader(title = "09 // COMIC INK DIVIDERS")
            ComicDivider()
            ComicDivider(dashed = true, centerBadge = "★ NYAWIT COMIC ★")
            ComicDivider(style = ComicDividerStyle.ZIGZAG)

            Spacer(Modifier.height(16.dp))
        }

        // 10 // Real Interactive Modal Dialog
        if (showConfirmDialog) {
            ComicDialog(
                onDismissRequest = {
                    showConfirmDialog = false
                    logAction("Dialog ditutup via dismiss request.")
                },
                title = "KONFIRMASI AKSI",
                badgeText = "MODAL // BAM!",
                onCloseClick = {
                    showConfirmDialog = false
                    logAction("Dialog ditutup via tombol silang.")
                },
            ) {
                Column {
                    Text(
                        "Apakah Anda yakin ingin mengeksekusi operasi instalasi paket berkas ini ke sistem perangkat?",
                        style = NyawitType.Body,
                        color = c.Text,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ComicButton(
                            text = "YA, EKSEKUSI!",
                            onClick = {
                                showConfirmDialog = false
                                logAction("Operasi dikonfirmasi oleh pengguna! Berkas dieksekusi.")
                            },
                            variant = ComicButtonVariant.PRIMARY,
                            modifier = Modifier.weight(1f),
                        )
                        ComicButton(
                            text = "BATALKAN",
                            onClick = {
                                showConfirmDialog = false
                                logAction("Operasi dibatalkan oleh pengguna.")
                            },
                            variant = ComicButtonVariant.SECONDARY,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComicSectionHeader(title: String) {
    val c = LocalNyawitColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(18.dp)
                .background(c.Primary),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = NyawitType.Subtitle,
            fontWeight = FontWeight.Black,
            color = c.Text,
        )
    }
}

@Preview
@Composable
private fun ComicShowcaseDarkPreview() {
    ComicTheme(mode = ThemeMode.CYBER_OBSIDIAN) {
        ComicShowcase()
    }
}

@Preview
@Composable
private fun ComicShowcaseLightPreview() {
    ComicTheme(mode = ThemeMode.NORDIC_LIGHT) {
        ComicShowcase()
    }
}
