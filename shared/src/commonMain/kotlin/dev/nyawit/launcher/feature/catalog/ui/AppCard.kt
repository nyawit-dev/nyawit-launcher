package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp

@Composable
fun AppCard(app: CatalogApp, busy: Boolean, onInstall: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
        modifier = Modifier.fillMaxWidth().tvFocusHighlight(),
    ) {
        Column(Modifier.padding(16.dp)) {
            androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Surface(color = Color(app.accent), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                    androidx.compose.foundation.layout.Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = androidx.compose.ui.Alignment.Center,
                    ) {
                        Text(app.name.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(app.name, color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(10.dp))
            Text(app.description, color = Color(0xFFD8D2EE), style = MaterialTheme.typography.bodySmall, maxLines = 2)
            Spacer(Modifier.height(12.dp))
            Text(app.releaseUrl, color = Color(0xFFFFC857), style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onInstall,
                enabled = !busy,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC857), contentColor = Color(0xFF120B2E)),
                modifier = Modifier.fillMaxWidth().tvFocusHighlight(RoundedCornerShape(12.dp), 1f),
            ) { Text(if (busy) "Mengunduh…" else "Pasang / Perbarui") }
        }
    }
}
