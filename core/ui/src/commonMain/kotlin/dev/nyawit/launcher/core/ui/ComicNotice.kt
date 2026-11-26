@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.ComicDimens
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class ComicNoticeType {
    INFO,
    SUCCESS,
    WARNING,
    DANGER,
}

/**
 * ComicNotice: Banner pemberitahuan dan status bergaya komik/manga dengan balon aksi,
 * garis tinta tegas, dan tombol aksi terintegrasi.
 */
@Composable
fun ComicNotice(
    text: String,
    modifier: Modifier = Modifier,
    type: ComicNoticeType = ComicNoticeType.INFO,
    isError: Boolean = false,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    progress: Float? = null,
) {
    val c = LocalNyawitColors.current
    val shape = RoundedCornerShape(8.dp)

    val effectiveType = if (isError) ComicNoticeType.DANGER else type

    val (accentColor, titleText, burstText, burstColor, burstTextColor) = when (effectiveType) {
        ComicNoticeType.INFO -> {
            if (progress != null) {
                tuple5(c.Primary, "PROSES AKTIF...", "WORK!", c.Primary, c.OnPrimary)
            } else {
                tuple5(c.Primary, "INFO SISTEM", "HEY!", c.ComicBurstYellow, Color(0xFF0F172A))
            }
        }
        ComicNoticeType.SUCCESS -> tuple5(Color(0xFF10B981), "BERHASIL!", "YAY!", Color(0xFF10B981), Color.White)
        ComicNoticeType.WARNING -> tuple5(c.Warning, "PERHATIAN!", "WARN!", c.Warning, Color(0xFF0F172A))
        ComicNoticeType.DANGER -> tuple5(c.Danger, "PERINGATAN!", "OUCH!", c.Danger, Color.White)
    }

    Box(modifier = modifier.padding(bottom = ComicDimens.ShadowOffset, end = ComicDimens.ShadowOffset)) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = ComicDimens.ShadowOffset, y = ComicDimens.ShadowOffset)
                .background(if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A), shape)
                .border(2.dp, accentColor.copy(alpha = 0.6f), shape),
        )

        // Foreground
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(c.Surface, shape)
                .border(2.dp, c.ComicInk, shape)
                .padding(14.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ComicActionBurst(
                            text = burstText,
                            burstColor = burstColor,
                            textColor = burstTextColor,
                            size = 30.dp,
                        )
                        Text(
                            text = titleText,
                            style = NyawitType.Subtitle,
                            fontWeight = FontWeight.Black,
                            color = accentColor,
                        )
                    }

                    if (onDismiss != null) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(c.SurfaceRaised, RoundedCornerShape(4.dp))
                                .border(1.5.dp, c.ComicInk, RoundedCornerShape(4.dp))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center,
                        ) {
                            CloseIcon(modifier = Modifier.size(14.dp), color = c.Muted)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = text,
                    style = NyawitType.Body,
                    color = c.Text,
                )

                if (progress != null) {
                    Spacer(Modifier.height(10.dp))
                    ComicProgressBar(
                        progress = progress,
                        fillColor = accentColor,
                    )
                }

                if (actionLabel != null && onAction != null) {
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        ComicButton(
                            text = actionLabel,
                            onClick = onAction,
                            variant = when (effectiveType) {
                                ComicNoticeType.DANGER -> ComicButtonVariant.DANGER
                                ComicNoticeType.WARNING -> ComicButtonVariant.WARNING
                                else -> ComicButtonVariant.PRIMARY
                            },
                            shadowOffset = 2.5.dp,
                        )
                    }
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
private fun <A, B, C, D, E> tuple5(a: A, b: B, c: C, d: D, e: E) = Tuple5(a, b, c, d, e)

@Preview
@Composable
private fun ComicNoticePreview() {
    ComicPreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ComicNotice(text = "Semua modul launcher terhubung dan siap digunakan.", type = ComicNoticeType.INFO)
            ComicNotice(text = "Pemasangan paket berkas berhasil diselesaikan!", type = ComicNoticeType.SUCCESS)
            ComicNotice(text = "Perangkat mendeteksi versi lama, disarankan memperbarui.", type = ComicNoticeType.WARNING, actionLabel = "UPDATE!")
            ComicNotice(text = "Gagal memproses berkas installer.", isError = true, actionLabel = "COBA LAGI")
        }
    }
}
