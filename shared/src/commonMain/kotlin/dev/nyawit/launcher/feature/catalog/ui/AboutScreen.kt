package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

@Composable
fun AboutScreen(version: String, platformContext: Any?, modifier: Modifier = Modifier) {
    val c = LocalNyawitColors.current

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Surface(
            color = c.Surface,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, c.Border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BrandMark(52)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(BuildConfig.APP_NAME.uppercase(), style = NyawitType.Wordmark, color = c.Text)
                        Spacer(Modifier.height(3.dp))
                        Text("v$version  ·  build ${BuildConfig.VERSION_CODE}", style = NyawitType.Caption, color = c.Primary)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    "Launcher ringan untuk menemukan, memasang, dan memperbarui aplikasi ekosistem Nyawit langsung dari GitHub Releases. Berjalan di Android, Android TV, dan desktop dengan satu pengalaman yang sama.",
                    style = NyawitType.Body,
                    color = c.Muted,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text("INFORMASI", style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(8.dp))

        Surface(
            color = c.Surface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, c.Border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(18.dp)) {
                InfoRow("Versi", "v$version")
                InfoRow("Platform", "Android · Android TV · Desktop")
                InfoRow("Identifier", BuildConfig.APP_ID)
                InfoRow("Pemilik", BuildConfig.GITHUB_OWNER)
                InfoRow("Lisensi", "GPL-3.0")
                InfoRow("Dibangun dengan", "Kotlin Multiplatform · Compose Multiplatform")
            }
        }

        Spacer(Modifier.height(14.dp))
        Text("TAUTAN", style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
            LinkButton("Repositori") {
                openUrl("https://github.com/${BuildConfig.GITHUB_REPO_FULL}", platformContext)
            }
            LinkButton("Katalog") { openUrl(BuildConfig.CATALOG_URL, platformContext) }
            LinkButton("Organisasi") { openUrl("https://github.com/${BuildConfig.GITHUB_OWNER}", platformContext) }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            "Seluruh paket diverifikasi dengan SHA-256 bila tersedia. Tidak ada server perantara — katalog dan rilis berasal langsung dari GitHub.",
            style = NyawitType.Caption,
            color = c.Faint,
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    val c = LocalNyawitColors.current
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
        Text(label, style = NyawitType.Caption, color = c.Faint, modifier = Modifier.weight(0.4f))
        Text(value, style = NyawitType.Caption, color = c.Text, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.6f))
    }
}

@Composable
private fun LinkButton(label: String, onClick: () -> Unit) {
    val c = LocalNyawitColors.current
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = c.SurfaceRaised, contentColor = c.Text),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.tvFocusHighlight(RoundedCornerShape(10.dp), 1f),
    ) { Text(label, style = NyawitType.Caption, fontWeight = FontWeight.Bold) }
}

/** Pembuka URL lintas platform didefinisikan di platform-specific source set. */
expect fun openUrl(url: String, platformContext: Any?)
