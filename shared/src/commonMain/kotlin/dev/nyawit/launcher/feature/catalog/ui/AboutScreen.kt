package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.ui.ComicActionBurst
import dev.nyawit.launcher.core.ui.ComicArcadeIllustration
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicHalftonePattern
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.SettingsIcon
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

@Composable
fun AboutScreen(
    version: String,
    platformContext: Any?,
    onOpenSettings: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    val profile = rememberDeviceProfile()
    val isNarrow = profile.kind == ScreenKind.PHONE || profile.widthDp < 680f

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
                .padding(horizontal = if (isNarrow) 16.dp else 24.dp, vertical = 20.dp),
        ) {
            // App Hero Card (Comic Storyboard Panel)
            ComicPanel(
                modifier = Modifier.fillMaxWidth(),
                borderWidth = 2.5.dp,
                borderColor = if (c.isDark) c.Primary else Color(0xFF0F172A),
                shadowOffset = 5.dp,
                shape = RoundedCornerShape(8.dp),
                backgroundColor = c.Surface,
            ) {
                // Comic Halftone dots screentone
                ComicHalftonePattern(
                    modifier = Modifier.matchParentSize(),
                    dotRadius = 1.6f,
                    spacing = 16f,
                    dotColor = if (c.isDark) c.Primary.copy(alpha = 0.12f) else Color(0xFF0F172A).copy(alpha = 0.05f),
                )

                Column(Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ComicActionBurst(
                                text = "CORE!",
                                burstColor = Color(0xFFFFDE00),
                                textColor = Color(0xFF0F172A),
                                size = 36.dp,
                            )
                            ComicBadge(
                                text = "CORE RUNTIME // SYSTEM",
                                containerColor = c.PrimaryMuted,
                                contentColor = c.Primary,
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ComicBadge(
                                text = "EDISI STABIL",
                                containerColor = c.SurfaceRaised,
                                contentColor = c.Text,
                                borderColor = if (c.isDark) c.Primary.copy(alpha = 0.5f) else Color(0xFF0F172A),
                            )
                            if (onBack != null) {
                                Surface(
                                    onClick = onBack,
                                    color = c.SurfaceRaised,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.5.dp, c.BorderHighlight),
                                    modifier = Modifier.tvFocusHighlight(shape = RoundedCornerShape(8.dp), scale = 1.05f),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Text("←", color = c.Primary, fontWeight = FontWeight.Black)
                                        Text(
                                            "KEMBALI",
                                            style = NyawitType.Caption,
                                            color = c.Text,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BrandMark(56)
                        Spacer(Modifier.width(18.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                BuildConfig.APP_NAME.uppercase(),
                                style = NyawitType.Wordmark,
                                color = c.Text,
                                fontWeight = FontWeight.Black,
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ComicBadge(
                                    text = "v$version",
                                    containerColor = c.Primary,
                                    contentColor = c.OnPrimary,
                                )
                                Text(
                                    "BUILD ${BuildConfig.VERSION_CODE}",
                                    style = NyawitType.Overline,
                                    color = c.Muted,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        if (!isNarrow) {
                            ComicArcadeIllustration(
                                modifier = Modifier.size(72.dp),
                                primaryColor = c.Primary,
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        NyawitStrings.ABOUT_DESC,
                        style = NyawitType.Body,
                        color = c.Muted,
                    )
                }
            }

            if (onOpenSettings != null) {
                Spacer(Modifier.height(18.dp))
                ComicPanel(
                    modifier = Modifier.fillMaxWidth(),
                    borderWidth = 2.dp,
                    shadowOffset = 3.dp,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = c.Surface,
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = c.PrimaryMuted,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(2.dp, c.Primary),
                                modifier = Modifier.size(42.dp),
                            ) {
                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    SettingsIcon(Modifier.size(22.dp), color = c.Primary)
                                }
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = NyawitStrings.SETTINGS_TITLE.uppercase(),
                                    style = NyawitType.Subtitle,
                                    color = c.Text,
                                    fontWeight = FontWeight.Black,
                                )
                                Text(
                                    text = NyawitStrings.SETTINGS_SUBTITLE,
                                    style = NyawitType.Caption,
                                    color = c.Muted,
                                )
                            }
                        }

                        Button(
                            onClick = onOpenSettings,
                            colors = ButtonDefaults.buttonColors(containerColor = c.Primary, contentColor = c.OnPrimary),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(2.dp, c.BorderHighlight),
                            modifier = Modifier.tvFocusHighlight(RoundedCornerShape(6.dp), 1.05f),
                        ) {
                            Text(
                                "${NyawitStrings.SETTINGS_BTN_OPEN.uppercase()} ➔",
                                style = NyawitType.Caption,
                                fontWeight = FontWeight.Black,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            ComicBadge(
                text = "SPESIFIKASI ARSIP // ${NyawitStrings.ABOUT_TITLE.uppercase()}",
                containerColor = c.PrimaryMuted,
                contentColor = c.Primary,
            )
            Spacer(Modifier.height(10.dp))

            ComicPanel(
                modifier = Modifier.fillMaxWidth(),
                borderWidth = 2.dp,
                shadowOffset = 3.dp,
                shape = RoundedCornerShape(8.dp),
                backgroundColor = c.Surface,
            ) {
                Column(Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                    MetaRow(NyawitStrings.ABOUT_LABEL_VERSION, "v$version (${BuildConfig.VERSION_CODE})")
                    MetaDivider()
                    MetaRow(NyawitStrings.ABOUT_LABEL_PLATFORMS, NyawitStrings.ABOUT_VAL_PLATFORMS)
                    MetaDivider()
                    MetaRow(NyawitStrings.ABOUT_LABEL_IDENTIFIER, BuildConfig.APP_ID)
                    MetaDivider()
                    MetaRow(NyawitStrings.ABOUT_LABEL_OWNER, BuildConfig.GITHUB_OWNER)
                    MetaDivider()
                    MetaRow(NyawitStrings.ABOUT_LABEL_LICENSE, NyawitStrings.ABOUT_VAL_LICENSE)
                    MetaDivider()
                    MetaRow(NyawitStrings.ABOUT_LABEL_FRAMEWORK, NyawitStrings.ABOUT_VAL_FRAMEWORK)
                }
            }

            Spacer(Modifier.height(20.dp))
            ComicBadge(
                text = "KOMUNITAS & SUMBER DAYA",
                containerColor = c.SurfaceRaised,
                contentColor = c.Text,
            )
            Spacer(Modifier.height(10.dp))

            val repoUrl = "https://github.com/${BuildConfig.GITHUB_OWNER}/${BuildConfig.GITHUB_REPO}"
            val catalogUrl = "https://raw.githubusercontent.com/${BuildConfig.GITHUB_OWNER}/${BuildConfig.GITHUB_REPO}/main/catalog.json"
            val orgUrl = "https://github.com/${BuildConfig.GITHUB_OWNER}"

            if (isNarrow) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ExternalLinkCard(NyawitStrings.ABOUT_LINK_REPO, repoUrl, Modifier.fillMaxWidth(), platformContext)
                    ExternalLinkCard(NyawitStrings.ABOUT_LINK_CATALOG, catalogUrl, Modifier.fillMaxWidth(), platformContext)
                    ExternalLinkCard(NyawitStrings.ABOUT_LINK_ORG, orgUrl, Modifier.fillMaxWidth(), platformContext)
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ExternalLinkCard(NyawitStrings.ABOUT_LINK_REPO, repoUrl, Modifier.weight(1f), platformContext)
                    ExternalLinkCard(NyawitStrings.ABOUT_LINK_CATALOG, catalogUrl, Modifier.weight(1f), platformContext)
                    ExternalLinkCard(NyawitStrings.ABOUT_LINK_ORG, orgUrl, Modifier.weight(1f), platformContext)
                }
            }

            Spacer(Modifier.height(24.dp))
            ComicPanel(
                modifier = Modifier.fillMaxWidth(),
                borderWidth = 1.5.dp,
                borderColor = c.BorderHighlight,
                shadowOffset = 2.dp,
                shape = RoundedCornerShape(6.dp),
                backgroundColor = c.SurfaceRaised,
            ) {
                Text(
                    NyawitStrings.ABOUT_SECURITY_FOOTER,
                    style = NyawitType.Caption,
                    color = c.Muted,
                    modifier = Modifier.padding(14.dp),
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    val c = LocalNyawitColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = NyawitType.Caption, color = c.Muted, fontWeight = FontWeight.Bold)
        Text(value, style = NyawitType.Caption, color = c.Text, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun MetaDivider() {
    val c = LocalNyawitColors.current
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.5.dp)
            .background(c.BorderHighlight.copy(alpha = 0.5f)),
    )
}

@Composable
private fun ExternalLinkCard(title: String, url: String, modifier: Modifier = Modifier, platformContext: Any?) {
    val c = LocalNyawitColors.current
    ComicPanel(
        modifier = modifier
            .tvFocusHighlight(RoundedCornerShape(8.dp), 1.02f)
            .clickable { openUrl(url, platformContext) },
        borderWidth = 2.dp,
        shadowOffset = 3.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = c.Surface,
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = NyawitType.Subtitle, color = c.Text, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            Text("BUKA REPO ➔", style = NyawitType.Overline, color = c.Primary, fontWeight = FontWeight.Black)
        }
    }
}
