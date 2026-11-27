@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.nyawit.launcher.theme.ComicDimens
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicDialog: Dialog modal pop-up dengan border komik tebal, hard offset shadow, strip header aksi,
 * dan animasi transisi spring pop-in khas komik (BAM!).
 */
@Composable
fun ComicDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    onCloseClick: (() -> Unit)? = onDismissRequest,
    content: @Composable () -> Unit,
) {
    val c = LocalNyawitColors.current
    val shape = RoundedCornerShape(12.dp)

    var hasPoppedIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hasPoppedIn = true
    }

    val popScale by animateFloatAsState(
        targetValue = if (hasPoppedIn) 1f else 0.88f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 500f),
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .scale(popScale)
                .padding(16.dp)
                .padding(bottom = ComicDimens.LargeShadowOffset, end = ComicDimens.LargeShadowOffset),
        ) {
            // Shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = ComicDimens.LargeShadowOffset, y = ComicDimens.LargeShadowOffset)
                    .background(if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A), shape)
                .border(2.5.dp, c.Primary.copy(alpha = 0.6f), shape),
            )

            // Foreground container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.Surface, shape)
                    .border(2.5.dp, c.ComicInk, shape),
            ) {
                // Header strip with halftone accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(c.SurfaceRaised, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .border(1.5.dp, c.BorderHighlight, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                ) {
                    ComicHalftonePattern(
                        modifier = Modifier.matchParentSize(),
                        dotRadius = 1.0f,
                        spacing = 14f,
                        dotColor = c.Primary.copy(alpha = 0.08f),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = title.uppercase(),
                                style = NyawitType.Title,
                                fontWeight = FontWeight.Black,
                                color = c.Text,
                            )
                            if (badgeText != null) {
                                ComicBadge(
                                    text = badgeText,
                                    containerColor = c.Primary,
                                    contentColor = c.OnPrimary,
                                )
                            }
                        }

                        if (onCloseClick != null) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(c.SurfaceHighlight, RoundedCornerShape(4.dp))
                                    .border(1.5.dp, c.ComicInk, RoundedCornerShape(4.dp))
                                    .clickable {
                                        ComicSoundPlayer.play(ComicSound.POP)
                                        onCloseClick()
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                CloseIcon(modifier = Modifier.size(16.dp), color = c.Muted)
                            }
                        }
                    }
                }

                // Body content
                Box(modifier = Modifier.padding(18.dp)) {
                    content()
                }
            }
        }
    }
}

@Preview
@Composable
private fun ComicDialogPreview() {
    ComicPreviewTheme {
        ComicPanel(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            backgroundColor = LocalNyawitColors.current.Surface,
            borderWidth = 2.5.dp,
            shadowOffset = 6.dp,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalNyawitColors.current.SurfaceRaised)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("KONFIRMASI AKSI", style = NyawitType.Title, fontWeight = FontWeight.Black)
                    ComicBadge(text = "DIALOG // INFO")
                }
                ComicDivider()
                Column(Modifier.padding(18.dp)) {
                    Text("Apakah Anda yakin ingin memasang modul aplikasi ini?")
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ComicButton("PASANG SEKARANG!", onClick = {})
                        ComicButton("BATAL", onClick = {}, variant = ComicButtonVariant.SECONDARY)
                    }
                }
            }
        }
    }
}
