package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

@Composable
fun AppDetailDialog(
    app: CatalogApp,
    status: AppStatus,
    onInstall: () -> Unit,
    onUpdate: () -> Unit,
    onOpen: () -> Unit,
    onUninstall: () -> Unit,
    onDismiss: () -> Unit,
) {
    val c = LocalNyawitColors.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = c.Surface,
            border = BorderStroke(1.dp, c.BorderHighlight),
            modifier = Modifier.fillMaxWidth(0.92f).widthIn(max = 560.dp),
        ) {
            Column {
                Column(
                    Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    // Title header with icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppIcon(
                            fullName = app.fullName,
                            fallbackName = app.name,
                            size = 54.dp,
                            shape = RoundedCornerShape(14.dp),
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(app.name, style = NyawitType.Title, color = c.Text)
                            Spacer(Modifier.height(3.dp))
                            Text(
                                app.fullName,
                                style = NyawitType.Caption,
                                color = c.Muted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    Text(
                        app.description,
                        style = NyawitType.Body,
                        color = c.Text,
                    )

                    Spacer(Modifier.height(18.dp))

                    // Spec grid
                    Surface(
                        color = c.SurfaceRaised,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, c.Border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                SpecItem(NyawitStrings.DETAIL_DEVELOPER, app.fullName.substringBefore('/'), c)
                                SpecItem(NyawitStrings.DETAIL_CATEGORY, app.category.uppercase(), c, c.Primary)
                            }
                            HorizontalDivider(color = c.Border.copy(alpha = 0.6f))
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
                            HorizontalDivider(color = c.Border.copy(alpha = 0.6f))
                            SpecItem(NyawitStrings.DETAIL_PACKAGE_ID, app.packageName, c)
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        NyawitStrings.DETAIL_VERIFICATION_NOTICE,
                        style = NyawitType.Caption,
                        color = c.Faint,
                    )

                    Spacer(Modifier.height(22.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        when (status) {
                            AppStatus.INSTALLED -> Button(
                                onClick = { onOpen(); onDismiss() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = c.Primary,
                                    contentColor = c.OnPrimary,
                                ),
                                shape = RoundedCornerShape(11.dp),
                                modifier = Modifier.tvFocusHighlight(RoundedCornerShape(11.dp), 1f),
                            ) { Text(NyawitStrings.BTN_OPEN, style = NyawitType.Caption, fontWeight = FontWeight.Bold) }

                            AppStatus.UPDATE_AVAILABLE -> Button(
                                onClick = { onUpdate(); onDismiss() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = c.Warning,
                                    contentColor = c.OnWarning,
                                ),
                                shape = RoundedCornerShape(11.dp),
                                modifier = Modifier.tvFocusHighlight(RoundedCornerShape(11.dp), 1f),
                            ) { Text(NyawitStrings.BTN_UPDATE, style = NyawitType.Caption, fontWeight = FontWeight.Bold) }

                            else -> Button(
                                onClick = { onInstall(); onDismiss() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = c.Primary,
                                    contentColor = c.OnPrimary,
                                ),
                                shape = RoundedCornerShape(11.dp),
                                modifier = Modifier.tvFocusHighlight(RoundedCornerShape(11.dp), 1f),
                            ) { Text(NyawitStrings.BTN_INSTALL, style = NyawitType.Caption, fontWeight = FontWeight.Bold) }
                        }
                        if (status == AppStatus.INSTALLED || status == AppStatus.UPDATE_AVAILABLE) {
                            Button(
                                onClick = { onUninstall(); onDismiss() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = c.Danger.copy(alpha = 0.15f),
                                    contentColor = c.Danger,
                                ),
                                border = BorderStroke(1.dp, c.Danger.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(11.dp),
                                modifier = Modifier.tvFocusHighlight(RoundedCornerShape(11.dp), 1f),
                            ) { Text(NyawitStrings.BTN_UNINSTALL, style = NyawitType.Caption, fontWeight = FontWeight.Bold) }
                        }
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = c.SurfaceRaised,
                                contentColor = c.Text,
                            ),
                            border = BorderStroke(1.dp, c.Border),
                            shape = RoundedCornerShape(11.dp),
                            modifier = Modifier.tvFocusHighlight(RoundedCornerShape(11.dp), 1f),
                        ) { Text(NyawitStrings.BTN_CLOSE, style = NyawitType.Caption, fontWeight = FontWeight.Bold) }
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
