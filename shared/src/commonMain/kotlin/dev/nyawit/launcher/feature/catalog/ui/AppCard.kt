package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.ui.ArrowIcon
import dev.nyawit.launcher.core.ui.CheckIcon
import dev.nyawit.launcher.core.ui.DownloadIcon
import dev.nyawit.launcher.core.ui.InfoIcon
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

enum class AppStatus { NOT_INSTALLED, INSTALLED, UPDATE_AVAILABLE, UNKNOWN }

private fun AppPlatform.label() = when (this) {
    AppPlatform.ANDROID -> "Android"
    AppPlatform.TV -> "TV"
    AppPlatform.DESKTOP -> "Desktop"
}

@Composable
fun AppCard(
    app: CatalogApp,
    status: AppStatus,
    busy: Boolean,
    progress: Float? = null,
    onDetail: (() -> Unit)? = null,
    onInstall: () -> Unit,
    onOpen: () -> Unit,
) {
    val c = LocalNyawitColors.current
    val accent = Color(app.accent)
    val appear by animateFloatAsState(targetValue = 1f, animationSpec = tween(280))

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(280)) + slideInVertically(tween(280) { it / 3 }),
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = c.Surface),
            border = BorderStroke(1.dp, c.Border),
            modifier = Modifier
                .fillMaxWidth()
                .scale(appear)
                .clickable { if (status == AppStatus.INSTALLED) onOpen() else onInstall() }
                .focusable()
                .tvFocusHighlight(RoundedCornerShape(18.dp)),
        ) {
            Column {
                // Top-lit specular accent line
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(accent, accent.copy(alpha = 0.35f), Color.Transparent),
                            ),
                        ),
                )

                Column(Modifier.padding(18.dp)) {
                    // Header row: AppIcon + Name & Pills + Status chip
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppIcon(
                            fullName = app.fullName,
                            fallbackName = app.name,
                            size = 48.dp,
                            shape = RoundedCornerShape(13.dp),
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                app.name,
                                style = NyawitType.Title,
                                color = c.Text,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(3.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    color = accent.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
                                ) {
                                    Text(
                                        app.category.uppercase(),
                                        style = NyawitType.Overline,
                                        color = accent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                                Text(
                                    app.platforms.joinToString(" · ") { it.label() },
                                    style = NyawitType.Caption,
                                    color = c.Faint,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        StatusChip(status)
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        app.description,
                        style = NyawitType.Body,
                        color = c.Muted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    // Optional progress indicator when downloading
                    if (busy && progress != null) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                NyawitStrings.PROGRESS_DOWNLOADING,
                                style = NyawitType.Overline,
                                color = accent,
                            )
                            Text(
                                "${(progress * 100).toInt()}%",
                                style = NyawitType.Overline,
                                color = c.Text,
                            )
                        }
                        Spacer(Modifier.height(5.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = accent,
                            trackColor = c.SurfaceRaised,
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Action Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = if (status == AppStatus.INSTALLED) onOpen else onInstall,
                            enabled = !busy,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (status) {
                                    AppStatus.INSTALLED -> c.SurfaceRaised
                                    AppStatus.UPDATE_AVAILABLE -> c.Warning
                                    else -> c.Primary
                                },
                                contentColor = when (status) {
                                    AppStatus.UPDATE_AVAILABLE -> c.OnPrimary
                                    AppStatus.INSTALLED -> c.Text
                                    else -> c.OnPrimary
                                },
                            ),
                            border = when (status) {
                                AppStatus.INSTALLED -> BorderStroke(1.dp, c.BorderHighlight)
                                else -> null
                            },
                            shape = RoundedCornerShape(11.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .weight(1f)
                                .tvFocusHighlight(RoundedCornerShape(11.dp), 1f),
                        ) {
                            if (busy) {
                                CircularProgressIndicator(
                                    Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = if (status == AppStatus.INSTALLED) c.Text else c.OnPrimary,
                                )
                                Spacer(Modifier.width(8.dp))
                            } else if (status == AppStatus.INSTALLED) {
                                ArrowIcon(Modifier.size(16.dp), c.Text)
                                Spacer(Modifier.width(8.dp))
                            } else if (progress == null) {
                                DownloadIcon(Modifier.size(16.dp), c.OnPrimary)
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                when {
                                    busy && progress != null -> NyawitStrings.progressDownloadingPercent((progress * 100).toInt())
                                    busy -> NyawitStrings.PROGRESS_PROCESSING
                                    status == AppStatus.INSTALLED -> NyawitStrings.BTN_OPEN_APP
                                    status == AppStatus.UPDATE_AVAILABLE -> NyawitStrings.BTN_UPDATE_NOW
                                    else -> NyawitStrings.BTN_INSTALL_APP
                                },
                                fontWeight = FontWeight.Bold,
                                style = NyawitType.Caption,
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Surface(
                            color = c.SurfaceRaised,
                            shape = RoundedCornerShape(11.dp),
                            border = BorderStroke(1.dp, c.Border),
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { onDetail?.invoke() }
                                .focusable()
                                .tvFocusHighlight(RoundedCornerShape(11.dp), 1f),
                        ) {
                            Box(Modifier.fillMaxSize(), Alignment.Center) {
                                InfoIcon(Modifier.size(17.dp), c.Muted)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: AppStatus) {
    val c = LocalNyawitColors.current
    val pulse by rememberInfiniteTransition().animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000)),
    )
    val (label, color) = when (status) {
        AppStatus.NOT_INSTALLED -> NyawitStrings.CHIP_AVAILABLE to c.Faint
        AppStatus.INSTALLED -> NyawitStrings.CHIP_INSTALLED to c.Primary
        AppStatus.UPDATE_AVAILABLE -> NyawitStrings.CHIP_UPDATE to c.Warning
        AppStatus.UNKNOWN -> NyawitStrings.CHIP_UNKNOWN to c.Faint
    }
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                .scale(if (status == AppStatus.UPDATE_AVAILABLE) pulse else 1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (status == AppStatus.INSTALLED) {
                CheckIcon(Modifier.size(8.dp), color)
            } else {
                Box(
                    Modifier
                        .size(6.dp)
                        .background(color, CircleShape),
                )
            }
            Text(
                label.uppercase(),
                style = NyawitType.Overline,
                color = color,
                maxLines = 1,
            )
        }
    }
}
