package dev.nyawit.launcher.feature.update.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicButton
import dev.nyawit.launcher.core.ui.ComicButtonVariant
import dev.nyawit.launcher.core.ui.ComicDialog
import dev.nyawit.launcher.core.ui.ComicProgressBar
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.feature.catalog.ui.FlowPhase
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

/** Dialog progres pembaruan dengan gaya visual komik/pop-art. */
@Composable
fun UpdateDialog(
    phase: FlowPhase,
    progress: Float?,
    message: String,
    onCancel: () -> Unit,
    onDismiss: () -> Unit,
) {
    val c = LocalNyawitColors.current
    val busy = phase == FlowPhase.CHECKING || phase == FlowPhase.DOWNLOADING
    val tone = if (phase == FlowPhase.ERROR) c.Danger else c.Primary

    val title = when (phase) {
        FlowPhase.CHECKING -> NyawitStrings.UPDATE_CHECKING
        FlowPhase.DOWNLOADING -> NyawitStrings.UPDATE_DOWNLOADING
        FlowPhase.DONE -> NyawitStrings.UPDATE_READY
        FlowPhase.ERROR -> NyawitStrings.UPDATE_FAILED
        FlowPhase.IDLE -> ""
    }

    val badge = when (phase) {
        FlowPhase.CHECKING -> "CHECKING..."
        FlowPhase.DOWNLOADING -> "DOWNLOADING"
        FlowPhase.DONE -> "READY!"
        FlowPhase.ERROR -> "ERROR!"
        FlowPhase.IDLE -> null
    }

    ComicDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        title = title,
        badgeText = badge,
        onCloseClick = if (!busy) onDismiss else null,
    ) {
        Column {
            Text(
                text = message.ifBlank { NyawitStrings.UPDATE_PREPARING },
                style = NyawitType.Body,
                color = if (phase == FlowPhase.ERROR) tone else c.Text,
            )

            if (phase == FlowPhase.DOWNLOADING) {
                Spacer(Modifier.height(16.dp))
                val fraction = progress ?: 0f
                ComicProgressBar(
                    progress = fraction,
                    label = "MENGUNDUH ${(fraction * 100).toInt()}%",
                    fillColor = tone,
                )
            }

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (busy) {
                    ComicButton(
                        text = NyawitStrings.BTN_CANCEL,
                        onClick = onCancel,
                        variant = ComicButtonVariant.DANGER,
                        shadowOffset = 2.dp,
                    )
                } else {
                    ComicButton(
                        text = NyawitStrings.BTN_CLOSE,
                        onClick = onDismiss,
                        variant = ComicButtonVariant.PRIMARY,
                        shadowOffset = 2.dp,
                    )
                }
            }
        }
    }
}
