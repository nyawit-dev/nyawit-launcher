package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.SettingsIcon
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

@Composable
fun AboutScreen(
    version: String,
    platformContext: Any?,
    onOpenSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    val profile = rememberDeviceProfile()
    val isNarrow = profile.kind == ScreenKind.PHONE || profile.widthDp < 680f

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

        if (onOpenSettings != null) {
            Spacer(Modifier.height(18.dp))
            Surface(
                color = c.Surface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, c.Border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = c.PrimaryMuted,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, c.Primary),
                            modifier = Modifier.size(38.dp),
                        ) {
                            Box(Modifier.fillMaxSize(), Alignment.Center) {
                                SettingsIcon(Modifier.size(20.dp), color = c.Primary)
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = NyawitStrings.SETTINGS_TITLE,
                                style = NyawitType.Subtitle,
                                color = c.Text,
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
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(NyawitStrings.SETTINGS_BTN_OPEN, style = NyawitType.Caption, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(NyawitStrings.ABOUT_TITLE, style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(8.dp))

        Surface(
            color = c.Surface,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, c.Border),
            modifier = Modifier.fillMaxWidth(),
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
        Text(NyawitStrings.ABOUT_LINKS_TITLE, style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(8.dp))

        val repoUrl = "https://github.com/${BuildConfig.GITHUB_OWNER}/${BuildConfig.GITHUB_REPO}"
        val catalogUrl = "https://raw.githubusercontent.com/${BuildConfig.GITHUB_OWNER}/${BuildConfig.GITHUB_REPO}/main/catalog.json"
        val orgUrl = "https://github.com/${BuildConfig.GITHUB_OWNER}"

        if (isNarrow) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
        Text(
            NyawitStrings.ABOUT_SECURITY_FOOTER,
            style = NyawitType.Caption,
            color = c.Faint,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    val c = LocalNyawitColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = NyawitType.Caption, color = c.Muted)
        Text(value, style = NyawitType.Caption, color = c.Text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MetaDivider() {
    val c = LocalNyawitColors.current
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(c.Border),
    )
}

@Composable
private fun ExternalLinkCard(title: String, url: String, modifier: Modifier = Modifier, platformContext: Any?) {
    val c = LocalNyawitColors.current
    Surface(
        color = c.Surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, c.Border),
        modifier = modifier,
        onClick = { openUrl(url, platformContext) },
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = NyawitType.Subtitle, color = c.Text)
            Spacer(Modifier.height(2.dp))
            Text("github.com ↗", style = NyawitType.Overline, color = c.Primary)
        }
    }
}
