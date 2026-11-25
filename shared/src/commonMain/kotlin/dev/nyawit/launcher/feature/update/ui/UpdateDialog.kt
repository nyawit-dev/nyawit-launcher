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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.feature.catalog.ui.FlowPhase
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

/** Dialog progres pembaruan. Tidak bisa ditutup saat masih mengunduh. */
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = !busy, dismissOnClickOutside = !busy),
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = c.Surface,
            border = BorderStroke(1.dp, c.Border),
            modifier = Modifier.fillMaxWidth(0.9f).widthIn(max = 460.dp),
        ) {
            Column(Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (busy) {
                        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp, color = tone)
                    } else {
                        Surface(
                            color = tone.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(26.dp),
                        ) {
                            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                                Text(
                                    if (phase == FlowPhase.ERROR) "!" else "✓",
                                    style = NyawitType.Caption,
                                    color = tone,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        when (phase) {
                            FlowPhase.CHECKING -> NyawitStrings.UPDATE_CHECKING
                            FlowPhase.DOWNLOADING -> NyawitStrings.UPDATE_DOWNLOADING
                            FlowPhase.DONE -> NyawitStrings.UPDATE_READY
                            FlowPhase.ERROR -> NyawitStrings.UPDATE_FAILED
                            FlowPhase.IDLE -> ""
                        },
                        style = NyawitType.Title,
                        color = c.Text,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    message.ifBlank { NyawitStrings.UPDATE_PREPARING },
                    style = NyawitType.Caption,
                    color = if (phase == FlowPhase.ERROR) tone else c.Muted,
                )
                if (phase == FlowPhase.DOWNLOADING) {
                    Spacer(Modifier.height(12.dp))
                    val fraction = progress ?: 0f
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth().height(5.dp),
                        color = tone,
                        trackColor = c.Border,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${(fraction * 100).toInt()}%",
                        style = NyawitType.Overline,
                        color = tone,
                    )
                }
                Spacer(Modifier.height(22.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = if (busy) onCancel else onDismiss) {
                        Text(
                            if (busy) NyawitStrings.BTN_CANCEL else NyawitStrings.BTN_CLOSE,
                            style = NyawitType.Caption,
                            color = if (busy) c.Danger else c.Primary,
                        )
                    }
                }
            }
        }
    }
}
