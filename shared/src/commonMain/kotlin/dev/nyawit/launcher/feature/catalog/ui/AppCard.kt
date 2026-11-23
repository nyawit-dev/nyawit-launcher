package dev.nyawit.launcher.feature.catalog.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp

@Composable
fun AppCard(app: CatalogApp, busy: Boolean, onInstall: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151B23)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth().tvFocusHighlight(),
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
                    Text(app.category, color = Color(app.accent), style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(app.description, color = Color(0xFF9BA7B4), style = MaterialTheme.typography.bodySmall, maxLines = 2)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onInstall,
                enabled = !busy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(app.accent),
                    contentColor = Color(0xFF0B0F14),
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().tvFocusHighlight(RoundedCornerShape(12.dp), 1f),
            ) { Text(if (busy) "Mengunduh…" else "Pasang / Perbarui", fontWeight = FontWeight.SemiBold) }
        }
    }
}
