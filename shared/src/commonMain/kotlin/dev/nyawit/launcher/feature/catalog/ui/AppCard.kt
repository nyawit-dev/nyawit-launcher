package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp

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
    onInstall: () -> Unit,
    onOpen: () -> Unit,
) {
    val appear by animateFloatAsState(targetValue = 1f, animationSpec = tween(350))
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(350)) + slideInVertically(tween(350) { it / 2 }),
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151B23)),
            modifier = Modifier
                .fillMaxWidth()
                .scale(appear)
                .tvFocusHighlight(),
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(app.accent), shape = CircleShape, modifier = Modifier.size(44.dp)) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text(app.name.first().uppercase(), color = Color(0xFF0B0F14), fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(app.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            app.platforms.joinToString(" · ") { it.label() } + " · " + app.category,
                            color = Color(app.accent),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    StatusChip(status)
                }
                Spacer(Modifier.height(14.dp))
                Text(app.description, color = Color(0xFF9BA7B4), style = MaterialTheme.typography.bodySmall, maxLines = 2)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = if (status == AppStatus.INSTALLED) onOpen else onInstall,
                    enabled = !busy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (status) {
                            AppStatus.INSTALLED -> Color(0xFF25313A)
                            AppStatus.UPDATE_AVAILABLE -> Color(0xFFFFB454)
                            else -> Color(app.accent)
                        },
                        contentColor = when (status) {
                            AppStatus.UPDATE_AVAILABLE -> Color(0xFF0B0F14)
                            else -> Color.White
                        },
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().tvFocusHighlight(RoundedCornerShape(12.dp), 1f),
                ) {
                    Text(
                        when {
                            busy -> "Memproses…"
                            status == AppStatus.INSTALLED -> "Buka Aplikasi"
                            status == AppStatus.UPDATE_AVAILABLE -> "Perbarui"
                            else -> "Pasang"
                        },
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: AppStatus) {
    val pulse by rememberInfiniteTransition().animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900)),
    )
    val (label, color) = when (status) {
        AppStatus.NOT_INSTALLED -> "Belum dipasang" to Color(0xFF8E9BA8)
        AppStatus.INSTALLED -> "Terpasang" to Color(0xFF3DDC97)
        AppStatus.UPDATE_AVAILABLE -> "Ada pembaruan" to Color(0xFFFFB454)
        AppStatus.UNKNOWN -> "Status unknown" to Color(0xFF8E9BA8)
    }
    Surface(color = color.copy(alpha = 0.16f), shape = RoundedCornerShape(10.dp)) {
        Text(
            label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).scale(if (status == AppStatus.UPDATE_AVAILABLE) pulse else 1f),
        )
    }
}
