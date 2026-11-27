package dev.nyawit.launcher.feature.update.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.ui.ComicActionBurst
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicButton
import dev.nyawit.launcher.core.ui.ComicButtonVariant
import dev.nyawit.launcher.core.ui.ComicDialog
import dev.nyawit.launcher.core.ui.ComicDivider
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

/** Konfirmasi sebelum launcher mengunduh installer pembaruan atau informasi versi mutakhir (Comic Style). */
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

    val title = if (isUpToDate) NyawitStrings.UPDATE_UP_TO_DATE_TITLE else NyawitStrings.updateUpgradePrompt(newVersion)
    val badge = if (isUpToDate) "LATEST // V$currentVersion" else "UPDATE // V$newVersion"

    ComicDialog(
        onDismissRequest = onDismiss,
        title = title,
        badgeText = badge,
        onCloseClick = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Hero Status Strip
            ComicPanel(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = c.SurfaceRaised,
                borderWidth = 1.5.dp,
                shadowOffset = 2.dp,
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ComicActionBurst(
                        text = if (isUpToDate) "OK!" else "NEW!",
                        size = 38.dp,
                        burstColor = if (isUpToDate) c.Primary else c.ComicBurstYellow,
                        textColor = c.OnPrimary,
                    )
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = if (isUpToDate) "VERSI APLIKASI TERKINI" else "PEMBARUAN SIAP DIUNDUH",
                            style = NyawitType.Subtitle,
                            fontWeight = FontWeight.Black,
                            color = c.Text,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (isUpToDate) NyawitStrings.updateUpToDateDesc(currentVersion)
                            else NyawitStrings.updateCurrentVersionDesc(currentVersion),
                            style = NyawitType.Caption,
                            color = c.Muted,
                        )
                    }
                }
            }

            if (!isUpToDate) {
                ComicPanel(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = c.Surface,
                    borderWidth = 1.5.dp,
                    shadowOffset = 2.dp,
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            text = "PAKET PEMBARUAN:",
                            style = NyawitType.Overline,
                            fontWeight = FontWeight.Black,
                            color = c.Primary,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = installerName,
                            style = NyawitType.Caption,
                            fontWeight = FontWeight.Bold,
                            color = c.Text,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Proses pembaruan akan mengunduh dan menerapkan berkas instalasi terbaru tanpa perlu me-restart perangkat Anda.",
                            style = NyawitType.Caption,
                            color = c.Muted,
                        )
                    }
                }
            }

            ComicDivider(dashed = true)

            // Action Command Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (isUpToDate) {
                    ComicButton(
                        text = NyawitStrings.BTN_CLOSE,
                        onClick = onDismiss,
                        variant = ComicButtonVariant.PRIMARY,
                        shadowOffset = 2.dp,
                    )
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ComicButton(
                            text = NyawitStrings.BTN_CANCEL,
                            onClick = onDismiss,
                            variant = ComicButtonVariant.SECONDARY,
                            shadowOffset = 2.dp,
                        )
                        ComicButton(
                            text = NyawitStrings.BTN_UPDATE,
                            onClick = onConfirm,
                            variant = ComicButtonVariant.PRIMARY,
                            burstBadge = "GO!",
                            shadowOffset = 2.dp,
                        )
                    }
                }
            }
        }
    }
}
