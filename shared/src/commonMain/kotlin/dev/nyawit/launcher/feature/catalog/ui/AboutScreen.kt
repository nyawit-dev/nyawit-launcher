package dev.nyawit.launcher.feature.catalog.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.ui.AppLanguage
import dev.nyawit.launcher.core.ui.GlobeIcon
import dev.nyawit.launcher.core.ui.LocalLanguageController
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.PaletteIcon
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.LocalThemeController
import dev.nyawit.launcher.theme.NyawitType
import dev.nyawit.launcher.theme.ThemeMode
import dev.nyawit.launcher.theme.colorsFor

@Composable
fun AboutScreen(version: String, platformContext: Any?, modifier: Modifier = Modifier) {
    val c = LocalNyawitColors.current
    val themeController = LocalThemeController.current
    val languageController = LocalLanguageController.current

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // App Hero Card
        Surface(
            color = c.Surface,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, c.Border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(c.Primary, c.Primary.copy(alpha = 0.3f), Color.Transparent),
                            ),
                        ),
                )
                Column(Modifier.padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandMark(52)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(BuildConfig.APP_NAME.uppercase(), style = NyawitType.Wordmark, color = c.Text)
                            Spacer(Modifier.height(3.dp))
                            Text("v$version  ·  build ${BuildConfig.VERSION_CODE}", style = NyawitType.Caption, color = c.Primary)
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
        }

        Spacer(Modifier.height(20.dp))
        Text(NyawitStrings.ABOUT_THEME_TITLE, style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(8.dp))

        // Multi-Theme Grid Selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.values().forEach { mode ->
                val isSelected = themeController.mode == mode
                val modeColors = colorsFor(mode)

                Surface(
                    color = if (isSelected) c.PrimaryMuted else c.Surface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) c.Primary else c.Border,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { themeController.set(mode) }
                        .focusable()
                        .tvFocusHighlight(RoundedCornerShape(14.dp), 1f),
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .background(modeColors.Background, RoundedCornerShape(8.dp))
                                .border(1.dp, modeColors.BorderHighlight, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(Modifier.size(10.dp).background(modeColors.Primary, CircleShape))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    mode.label,
                                    style = NyawitType.Caption,
                                    color = if (isSelected) c.Primary else c.Text,
                                    fontWeight = FontWeight.Bold,
                                )
                                if (isSelected) {
                                    Spacer(Modifier.width(8.dp))
                                    Surface(
                                        color = c.Primary,
                                        shape = RoundedCornerShape(4.dp),
                                    ) {
                                        Text(
                                            NyawitStrings.THEME_ACTIVE_BADGE,
                                            style = NyawitType.Overline,
                                            color = c.OnPrimary,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                mode.description,
                                style = NyawitType.Caption,
                                color = c.Muted,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(NyawitStrings.ABOUT_LANGUAGE_TITLE, style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AppLanguage.values().forEach { lang ->
                val isSelected = languageController.language == lang
                Surface(
                    color = if (isSelected) c.PrimaryMuted else c.Surface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) c.Primary else c.Border,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { languageController.set(lang) }
                        .focusable()
                        .tvFocusHighlight(RoundedCornerShape(12.dp), 1f),
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        GlobeIcon(Modifier.size(18.dp), if (isSelected) c.Primary else c.Muted)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                lang.label,
                                style = NyawitType.Caption,
                                color = if (isSelected) c.Primary else c.Text,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                lang.code.uppercase(),
                                style = NyawitType.Overline,
                                color = c.Muted,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(NyawitStrings.ABOUT_TITLE, style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(8.dp))

        Surface(
            color = c.Surface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, c.Border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(18.dp)) {
                InfoRow(NyawitStrings.ABOUT_LABEL_VERSION, "v$version")
                InfoRow(NyawitStrings.ABOUT_LABEL_PLATFORMS, NyawitStrings.ABOUT_VAL_PLATFORMS)
                InfoRow(NyawitStrings.ABOUT_LABEL_IDENTIFIER, BuildConfig.APP_ID)
                InfoRow(NyawitStrings.ABOUT_LABEL_OWNER, BuildConfig.GITHUB_OWNER)
                InfoRow(NyawitStrings.ABOUT_LABEL_LICENSE, NyawitStrings.ABOUT_VAL_LICENSE)
                InfoRow(NyawitStrings.ABOUT_LABEL_FRAMEWORK, NyawitStrings.ABOUT_VAL_FRAMEWORK)
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(NyawitStrings.ABOUT_LINKS_TITLE, style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LinkButton(NyawitStrings.ABOUT_LINK_REPO) {
                openUrl("https://github.com/${BuildConfig.GITHUB_REPO_FULL}", platformContext)
            }
            LinkButton(NyawitStrings.ABOUT_LINK_CATALOG) { openUrl(BuildConfig.CATALOG_URL, platformContext) }
            LinkButton(NyawitStrings.ABOUT_LINK_ORG) { openUrl("https://github.com/${BuildConfig.GITHUB_OWNER}", platformContext) }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            NyawitStrings.ABOUT_SECURITY_FOOTER,
            style = NyawitType.Caption,
            color = c.Faint,
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val c = LocalNyawitColors.current
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
        Text(label, style = NyawitType.Caption, color = c.Faint, modifier = Modifier.weight(0.4f))
        Text(value, style = NyawitType.Caption, color = c.Text, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.6f))
    }
}

@Composable
private fun LinkButton(label: String, onClick: () -> Unit) {
    val c = LocalNyawitColors.current
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = c.SurfaceRaised, contentColor = c.Text),
        border = BorderStroke(1.dp, c.Border),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.tvFocusHighlight(RoundedCornerShape(10.dp), 1f),
    ) { Text(label, style = NyawitType.Caption, fontWeight = FontWeight.Bold) }
}

/** Pembuka URL lintas platform didefinisikan di platform-specific source set. */
expect fun openUrl(url: String, platformContext: Any?)
