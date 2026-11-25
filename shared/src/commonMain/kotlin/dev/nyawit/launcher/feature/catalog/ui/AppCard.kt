package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
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
    var isCardFocused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = if (isCardFocused) c.SurfaceRaised else c.Surface,
        animationSpec = tween(180),
    )
    val borderColor by animateColorAsState(
        targetValue = if (isCardFocused) c.Primary else c.Border,
        animationSpec = tween(180),
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (isCardFocused) 2.dp else 1.dp,
            color = borderColor,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 3.dp)
            .onFocusChanged { state -> isCardFocused = state.hasFocus }
            .focusProperties { canFocus = false }
            .clickable { if (status == AppStatus.INSTALLED) onOpen() else onInstall() },
    ) {
        Column(Modifier.padding(18.dp)) {
            // Header Row: App Icon + Titles + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(13.dp),
                    color = c.SurfaceRaised,
                    border = BorderStroke(1.dp, c.BorderHighlight),
                    modifier = Modifier.size(52.dp),
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        AppIcon(
                            fullName = app.fullName,
                            fallbackName = app.name,
                            size = 48.dp,
                            shape = RoundedCornerShape(11.dp),
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        app.name,
                        style = NyawitType.Title,
                        fontWeight = FontWeight.Bold,
                        color = c.Text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                    val publisher = app.fullName.substringBefore('/')
                    Text(
                        publisher,
                        style = NyawitType.Caption,
                        color = c.Faint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.width(8.dp))
                StatusChip(status)
            }

            Spacer(Modifier.height(14.dp))

            // Metadata Chips: Category & Platform badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = c.SurfaceRaised,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, c.Border),
                ) {
                    Text(
                        app.category,
                        style = NyawitType.Caption,
                        color = c.Muted,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }

                Box(Modifier.size(3.dp).background(c.Faint, CircleShape))

                Text(
                    app.platforms.joinToString(", ") { it.label() },
                    style = NyawitType.Caption,
                    color = c.Faint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(10.dp))

            // Body: Description text with comfortable reading height
            Text(
                app.description,
                style = NyawitType.Body,
                color = c.Muted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            // Optional Download / Progress Bar
            if (busy && progress != null) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        NyawitStrings.PROGRESS_DOWNLOADING,
                        style = NyawitType.Caption,
                        fontWeight = FontWeight.SemiBold,
                        color = c.Primary,
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = NyawitType.Caption,
                        fontWeight = FontWeight.Bold,
                        color = c.Text,
                    )
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                    color = c.Primary,
                    trackColor = c.SurfaceRaised,
                )
            }

            Spacer(Modifier.height(16.dp))

            // Action Row
            val btnContainerColor = when (status) {
                AppStatus.INSTALLED -> c.Primary
                AppStatus.UPDATE_AVAILABLE -> c.Warning
                else -> c.Primary
            }
            val btnContentColor = when (status) {
                AppStatus.UPDATE_AVAILABLE -> c.OnWarning
                else -> c.OnPrimary
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = if (status == AppStatus.INSTALLED) onOpen else onInstall,
                    enabled = !busy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = btnContainerColor,
                        contentColor = btnContentColor,
                    ),
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
                            color = btnContentColor,
                        )
                        Spacer(Modifier.width(8.dp))
                    } else if (status == AppStatus.INSTALLED) {
                        ArrowIcon(Modifier.size(16.dp), btnContentColor)
                        Spacer(Modifier.width(8.dp))
                    } else if (progress == null) {
                        DownloadIcon(Modifier.size(16.dp), btnContentColor)
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

                Spacer(Modifier.width(8.dp))

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
                        InfoIcon(Modifier.size(18.dp), c.Muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: AppStatus) {
    val c = LocalNyawitColors.current
    val label = when (status) {
        AppStatus.NOT_INSTALLED -> NyawitStrings.CHIP_AVAILABLE
        AppStatus.INSTALLED -> NyawitStrings.CHIP_INSTALLED
        AppStatus.UPDATE_AVAILABLE -> NyawitStrings.CHIP_UPDATE
        AppStatus.UNKNOWN -> NyawitStrings.CHIP_UNKNOWN
    }
    val textColor = when (status) {
        AppStatus.NOT_INSTALLED -> c.Muted
        AppStatus.INSTALLED -> c.Primary
        AppStatus.UPDATE_AVAILABLE -> c.Warning
        AppStatus.UNKNOWN -> c.Faint
    }
    val bgColor = when (status) {
        AppStatus.NOT_INSTALLED -> c.SurfaceRaised
        AppStatus.INSTALLED -> c.PrimaryMuted
        AppStatus.UPDATE_AVAILABLE -> c.Warning.copy(alpha = 0.15f)
        AppStatus.UNKNOWN -> c.SurfaceRaised
    }
    val borderColor = when (status) {
        AppStatus.NOT_INSTALLED -> c.Border
        AppStatus.INSTALLED -> c.Primary.copy(alpha = 0.35f)
        AppStatus.UPDATE_AVAILABLE -> c.Warning.copy(alpha = 0.45f)
        AppStatus.UNKNOWN -> c.Border
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (status == AppStatus.INSTALLED) {
                CheckIcon(Modifier.size(10.dp), textColor)
            } else {
                Box(
                    Modifier
                        .size(5.dp)
                        .background(textColor, CircleShape),
                )
            }
            Text(
                label,
                style = NyawitType.Caption,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                maxLines = 1,
            )
        }
    }
}
