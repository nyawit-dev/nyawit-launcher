package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.feature.catalog.data.CatalogApp

@Composable
fun AppDetailDialog(app: CatalogApp, status: AppStatus, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(app.name, color = Color.White) },
        text = {
            Column {
                Detail("Developer", app.fullName.substringBefore('/'))
                Detail("Repository", app.fullName)
                Detail("Kategori", app.category)
                Detail("Platform", app.platforms.joinToString(", ") {
                    when (it.name) {
                        "ANDROID" -> "Android"
                        "TV" -> "Android TV"
                        else -> "Desktop/JVM"
                    }
                })
                Detail("Package ID", app.packageName)
                Detail("Status", when (status) {
                    AppStatus.NOT_INSTALLED -> "Belum dipasang"
                    AppStatus.INSTALLED -> "Terpasang"
                    AppStatus.UPDATE_AVAILABLE -> "Pembaruan tersedia"
                    AppStatus.UNKNOWN -> "Tidak diketahui"
                })
                Detail("Rilis", app.releaseUrl)
                Detail("Sumber", app.repoUrl)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Aplikasi ini diunduh langsung dari GitHub Releases. Tidak ada server perantara.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8E9BA8),
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Tutup") } },
    )
}

@Composable
private fun Detail(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF8E9BA8))
        Text(value, style = MaterialTheme.typography.bodySmall, color = Color.White)
        Spacer(Modifier.height(8.dp))
    }
}
