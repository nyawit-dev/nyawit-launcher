package dev.nyawit.launcher.feature.update.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import nyawit_launcher.shared.generated.resources.Res
import nyawit_launcher.shared.generated.resources.update_illustration

/** Konfirmasi sebelum launcher mengunduh installer pembaruan atau informasi versi mutakhir. */
@Composable
fun UpdateConfirmDialog(
    currentVersion: String,
    newVersion: String,
    installerName: String,
    isUpToDate: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val c = LocalNyawitColors.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = c.Surface,
            border = BorderStroke(1.dp, c.Border),
            modifier = Modifier.fillMaxWidth(0.9f),
        ) {
            Column(Modifier.padding(24.dp)) {
                androidx.compose.foundation.Image(
                    painter = org.jetbrains.compose.resources.painterResource(
                        Res.drawable.update_illustration,
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    if (isUpToDate) NyawitStrings.UPDATE_UP_TO_DATE_BADGE else NyawitStrings.UPDATE_AVAILABLE_BADGE,
                    style = NyawitType.Overline,
                    color = c.Primary,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    if (isUpToDate) NyawitStrings.UPDATE_UP_TO_DATE_TITLE else NyawitStrings.updateUpgradePrompt(newVersion),
                    style = NyawitType.Title,
                    color = c.Text,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (isUpToDate) NyawitStrings.updateUpToDateDesc(currentVersion)
                    else NyawitStrings.updateCurrentVersionDesc(currentVersion),
                    style = NyawitType.Caption,
                    color = c.Muted,
                )

                Spacer(Modifier.height(20.dp))
                Surface(
                    color = c.SurfaceRaised,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = c.Primary.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(32.dp),
                        ) {
                            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                                Text(
                                    if (isUpToDate) "✓" else "↓",
                                    color = c.Primary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                installerName,
                                style = NyawitType.Caption,
                                color = c.Text,
                                maxLines = 1,
                            )
                            Text(
                                if (isUpToDate) "Paket terverifikasi dari GitHub" else "Installer bawaan platform",
                                style = NyawitType.Overline,
                                color = c.Faint,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    if (isUpToDate) "Anda sudah menikmati seluruh fitur dan perbaikan terkini. Jika mengalami kendala pada instalasi saat ini, Anda dapat memasang ulang."
                    else "Launcher akan mengunduh installer dan menyerahkannya ke sistem Anda. Ikuti proses pemasangan, lalu restart launcher jika diminta.",
                    style = NyawitType.Caption,
                    color = c.Faint,
                )

                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (isUpToDate) {
                        TextButton(
                            onClick = onConfirm,
                            modifier = Modifier.tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
                        ) {
                            Text(NyawitStrings.BTN_RETRY, style = NyawitType.Caption, color = c.Muted)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = c.Primary,
                                contentColor = c.OnPrimary,
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.tvFocusHighlight(RoundedCornerShape(10.dp), 1f),
                        ) {
                            Text(NyawitStrings.BTN_CLOSE, style = NyawitType.Caption, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
                        ) {
                            Text(NyawitStrings.BTN_LATER, style = NyawitType.Caption, color = c.Muted)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = c.Primary,
                                contentColor = c.OnPrimary,
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.tvFocusHighlight(RoundedCornerShape(10.dp), 1f),
                        ) {
                            Text(NyawitStrings.BTN_UPDATE, style = NyawitType.Caption, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
