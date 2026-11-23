package dev.nyawit.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.data.CatalogApp
import dev.nyawit.launcher.data.GitHubCatalog
import dev.nyawit.launcher.platform.installApp
import dev.nyawit.launcher.theme.LauncherTheme
import dev.nyawit.launcher.tv.tvFocusHighlight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LauncherApp(
    modifier: Modifier = Modifier,
    platformContext: Any? = null,
) {
    LauncherTheme {
        val catalog = remember { GitHubCatalog() }
        val scope = rememberCoroutineScope()
        var apps by remember { mutableStateOf<List<CatalogApp>>(emptyList()) }
        var loading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        var query by remember { mutableStateOf("") }
        var installing by remember { mutableStateOf<String?>(null) }
        var installMessage by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            try {
                apps = catalog.loadApps()
            } catch (t: Throwable) {
                error = t.message ?: "Gagal memuat katalog GitHub"
            } finally {
                loading = false
            }
        }

        fun requestInstall(app: CatalogApp) {
            installing = app.fullName
            installMessage = null
            scope.launch {
                val url = withContext(Dispatchers.Default) { catalog.latestApkUrl(app.fullName) }
                installing = null
                if (url == null) {
                    installMessage = "Belum ada APK di Releases ${app.name}"
                    return@launch
                }
                installApp(url, "${app.fullName.replace('/', '_')}.apk", platformContext)
                installMessage = if (platformContext == null) "Membuka halaman unduhan di browser" else "Mengunduh & membuka installer"
            }
        }

        val filtered = apps.filter {
            query.isBlank() || it.name.contains(query, true) || it.description.contains(query, true)
        }

        Box(
            modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF120B2E), Color(0xFF1B1246), Color(0xFF0D0B1F)),
                    ),
                ),
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFFFC857), shape = CircleShape, modifier = Modifier.size(52.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("N", color = Color(0xFF120B2E), fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Nyawit Launcher", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Pasang & perbarui langsung dari GitHub", style = MaterialTheme.typography.bodySmall, color = Color(0xFFB9AFDE))
                    }
                }

                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Cari aplikasi…") },
                    singleLine = true,
                    leadingIcon = { Text("⌕", color = Color(0xFFFFC857)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().tvFocusHighlight(RoundedCornerShape(16.dp), 1f),
                )

                installMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = Color(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(18.dp))
                when {
                    loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFFFC857))
                    }
                    error != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: $error", color = Color(0xFFFF8A80))
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { error = null; loading = true }) { Text("Coba lagi") }
                    }
                    else -> LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 260.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(filtered) { app ->
                            AppCard(
                                app = app,
                                busy = installing == app.fullName,
                                onInstall = { requestInstall(app) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppCard(
    app: CatalogApp,
    busy: Boolean,
    onInstall: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
        modifier = Modifier.fillMaxWidth().tvFocusHighlight(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(app.accent), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            ) {
                Text(if (busy) "Mengunduh…" else "Pasang / Perbarui")
            }
        }
    }
}
