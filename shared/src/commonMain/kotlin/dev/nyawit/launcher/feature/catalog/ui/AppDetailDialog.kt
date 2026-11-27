package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.platform.createDesktopShortcut
import dev.nyawit.launcher.core.platform.currentAppPlatform
import dev.nyawit.launcher.core.ui.ComicActionBurst
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicButton
import dev.nyawit.launcher.core.ui.ComicButtonVariant
import dev.nyawit.launcher.core.ui.ComicDivider
import dev.nyawit.launcher.core.ui.ComicIconButton
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.LocalResponsiveScale
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.feature.catalog.data.GitHubCatalog
import dev.nyawit.launcher.feature.catalog.data.ReleaseDetails
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

@Composable
fun AppDetailDialog(
    app: CatalogApp,
    status: AppStatus,
    catalog: GitHubCatalog? = null,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
    onInstall: () -> Unit,
    onUpdate: () -> Unit,
    onOpen: () -> Unit,
    onUninstall: () -> Unit,
    onDismiss: () -> Unit,
) {
    val c = LocalNyawitColors.current
    val responsiveScale = LocalResponsiveScale.current

    var releaseInfo by remember { mutableStateOf<ReleaseDetails?>(null) }
    var loadingChangelog by remember { mutableStateOf(false) }

    LaunchedEffect(app.fullName) {
        if (catalog != null) {
            loadingChangelog = true
            releaseInfo = catalog.latestReleaseInfo(app.fullName)
            loadingChangelog = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        ComicPanel(
            shape = RoundedCornerShape(12.dp),
            backgroundColor = c.Surface,
            borderWidth = 2.5.dp,
            borderColor = if (c.isDark) c.Primary else Color(0xFF0F172A),
            shadowOffset = 6.dp,
            modifier = Modifier.fillMaxWidth(0.92f).widthIn(max = responsiveScale.dp(600)),
        ) {
            Column {
                Column(
                    Modifier
                        .padding(22.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    // Header Bar with Category & Favorite Pin
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ComicBadge(
                                text = "DOKUMEN MODUL // DETAIL",
                                containerColor = c.Primary,
                                contentColor = c.OnPrimary,
                            )
                            if (onToggleFavorite != null) {
                                ComicBadge(
                                    text = if (isFavorite) "★ FAVORIT" else "☆ FAVORITKAN",
                                    containerColor = if (isFavorite) c.Warning else c.SurfaceHighlight,
                                    contentColor = if (isFavorite) c.OnWarning else c.Text,
                                    borderColor = if (isFavorite) c.Warning else c.BorderHighlight,
                                    onClick = onToggleFavorite,
                                )
                            }
                        }

                        ComicBadge(
                            text = app.category.uppercase(),
                            containerColor = c.SurfaceRaised,
                            contentColor = c.Muted,
                            borderColor = c.Border,
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Title header with icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = c.SurfaceRaised,
                            border = BorderStroke(2.dp, c.BorderHighlight),
                            modifier = Modifier.size(responsiveScale.dp(58)),
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
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(app.name, style = NyawitType.Title, fontWeight = FontWeight.Black, color = c.Text)
                            Spacer(Modifier.height(3.dp))
                            Text(
                                app.fullName,
                                style = NyawitType.Caption,
                                color = c.Primary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        app.description,
                        style = NyawitType.Body,
                        color = c.Text,
                    )

                    Spacer(Modifier.height(16.dp))

                    // Spec grid
                    Surface(
                        color = c.SurfaceRaised,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.5.dp, c.ComicInk),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                SpecItem(NyawitStrings.DETAIL_DEVELOPER, app.fullName.substringBefore('/'), c)
                                SpecItem(NyawitStrings.DETAIL_CATEGORY, app.category.uppercase(), c, c.Primary)
                            }
                            ComicDivider(dashed = true)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                SpecItem(NyawitStrings.DETAIL_PLATFORM, app.platforms.joinToString(", ") { it.label() }, c)
                                SpecItem(
                                    NyawitStrings.DETAIL_STATUS,
                                    when (status) {
                                        AppStatus.NOT_INSTALLED -> NyawitStrings.CHIP_AVAILABLE
                                        AppStatus.INSTALLED -> NyawitStrings.CHIP_INSTALLED
                                        AppStatus.UPDATE_AVAILABLE -> NyawitStrings.CHIP_UPDATE_AVAILABLE
                                        AppStatus.UNKNOWN -> NyawitStrings.CHIP_UNKNOWN
                                    },
                                    c,
                                    valueColor = when (status) {
                                        AppStatus.INSTALLED -> c.Primary
                                        AppStatus.UPDATE_AVAILABLE -> c.Warning
                                        else -> c.Text
                                    },
                                )
                            }
                            ComicDivider(dashed = true)
                            SpecItem(NyawitStrings.DETAIL_PACKAGE_ID, app.packageName, c)
                        }
                    }

                    // Changelog Section
                    Spacer(Modifier.height(16.dp))
                    ComicPanel(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = c.SurfaceRaised,
                        borderWidth = 1.5.dp,
                        shadowOffset = 2.dp,
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "CATATAN RILIS // WHAT'S NEW",
                                    style = NyawitType.Overline,
                                    fontWeight = FontWeight.Black,
                                    color = c.Primary,
                                )
                                if (releaseInfo?.tag != null) {
                                    ComicBadge(
                                        text = releaseInfo?.tag.orEmpty(),
                                        containerColor = c.Surface,
                                        contentColor = c.Text,
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            when {
                                loadingChangelog -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        CircularProgressIndicator(Modifier.size(14.dp), strokeWidth = 2.dp, color = c.Primary)
                                        Text("Memuat catatan rilis dari GitHub...", style = NyawitType.Caption, color = c.Muted)
                                    }
                                }
                                releaseInfo != null && releaseInfo!!.changelog.isNotBlank() -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 160.dp)
                                            .verticalScroll(rememberScrollState()),
                                    ) {
                                        Text(
                                            text = releaseInfo!!.changelog,
                                            style = NyawitType.Caption,
                                            color = c.Text,
                                            lineHeight = 18.sp,
                                        )
                                    }
                                }
                                else -> {
                                    Text(
                                        text = "Tidak ada catatan rilis khusus untuk rilis ini.",
                                        style = NyawitType.Caption,
                                        color = c.Muted,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        NyawitStrings.DETAIL_VERIFICATION_NOTICE,
                        style = NyawitType.Caption,
                        color = c.Faint,
                    )

                    Spacer(Modifier.height(20.dp))

                    // Action buttons
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        when (status) {
                            AppStatus.INSTALLED -> ComicButton(
                                text = "JALANKAN!",
                                onClick = { onOpen(); onDismiss() },
                                variant = ComicButtonVariant.PRIMARY,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Black),
                            )

                            AppStatus.UPDATE_AVAILABLE -> ComicButton(
                                text = "PERBARUI!",
                                onClick = { onUpdate(); onDismiss() },
                                variant = ComicButtonVariant.WARNING,
                                burstBadge = "UP!",
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Black),
                            )

                            else -> ComicButton(
                                text = "PASANG SEKARANG!",
                                onClick = { onInstall(); onDismiss() },
                                variant = ComicButtonVariant.PRIMARY,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Black),
                            )
                        }

                        if (status == AppStatus.INSTALLED || status == AppStatus.UPDATE_AVAILABLE) {
                            ComicButton(
                                text = "HAPUS!",
                                onClick = { onUninstall(); onDismiss() },
                                variant = ComicButtonVariant.DANGER,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Black),
                            )
                        }

                        if (status == AppStatus.INSTALLED && currentAppPlatform() == AppPlatform.DESKTOP) {
                            var shortcutNotice by remember { mutableStateOf<String?>(null) }
                            ComicButton(
                                text = shortcutNotice ?: "BUAT PINTASAN",
                                onClick = {
                                    val ok = createDesktopShortcut(app.packageName, app.name)
                                    shortcutNotice = if (ok) "TERBUAT!" else "GAGAL"
                                },
                                variant = ComicButtonVariant.SECONDARY,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Bold),
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        ComicButton(
                            text = NyawitStrings.BTN_CLOSE,
                            onClick = onDismiss,
                            variant = ComicButtonVariant.SECONDARY,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }
        }
    }
}

private fun AppPlatform.label() = when (this) {
    AppPlatform.ANDROID -> "Android"
    AppPlatform.TV -> "TV"
    AppPlatform.DESKTOP -> "Desktop"
}

@Composable
private fun SpecItem(label: String, value: String, c: dev.nyawit.launcher.theme.NyawitColors, valueColor: Color = c.Text) {
    Column {
        Text(label, style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(3.dp))
        Text(
            value,
            style = NyawitType.Caption,
            color = valueColor,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
