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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

@Composable
fun AppDetailDialog(app: CatalogApp, status: AppStatus, onDismiss: () -> Unit) {
    val c = LocalNyawitColors.current
    val accent = Color(app.accent)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = c.Surface,
            border = BorderStroke(1.dp, c.BorderHighlight),
            modifier = Modifier.fillMaxWidth(0.92f),
        ) {
            Column {
                // Top-lit accent bar
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(accent, accent.copy(alpha = 0.3f), Color.Transparent),
                            ),
                        ),
                )

                Column(Modifier.padding(24.dp)) {
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
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, c.Border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                SpecItem("PENGEMBANG", app.fullName.substringBefore('/'), c)
                                SpecItem("KATEGORI", app.category, c)
                            }
                            HorizontalDivider(color = c.Border.copy(alpha = 0.5f))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                SpecItem("PLATFORM", app.platforms.joinToString(", ") { it.label() }, c)
                                SpecItem(
                                    "STATUS",
                                    when (status) {
                                        AppStatus.NOT_INSTALLED -> "Tersedia"
                                        AppStatus.INSTALLED -> "Terpasang"
                                        AppStatus.UPDATE_AVAILABLE -> "Update Tersedia"
                                        AppStatus.UNKNOWN -> "Unknown"
                                    },
                                    c,
                                    valueColor = when (status) {
                                        AppStatus.INSTALLED -> c.Primary
                                        AppStatus.UPDATE_AVAILABLE -> c.Warning
                                        else -> c.Text
                                    },
                                )
                            }
                            HorizontalDivider(color = c.Border.copy(alpha = 0.5f))
                            SpecItem("PACKAGE ID", app.packageName, c)
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Paket diunduh langsung dari GitHub Releases dengan verifikasi checksum SHA-256 tanpa perantara.",
                        style = NyawitType.Caption,
                        color = c.Faint,
                    )

                    Spacer(Modifier.height(22.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = c.Primary,
                                contentColor = c.OnPrimary,
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.tvFocusHighlight(RoundedCornerShape(10.dp), 1f),
                        ) {
                            Text("Tutup", style = NyawitType.Caption, fontWeight = FontWeight.Bold)
                        }
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
        Spacer(Modifier.height(2.dp))
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
