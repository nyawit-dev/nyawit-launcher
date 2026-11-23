package dev.nyawit.launcher.feature.catalog.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.feature.catalog.data.GitHubCatalog
import dev.nyawit.launcher.feature.install.installApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CatalogScreen(modifier: Modifier, platformContext: Any?) {
    val profile = rememberDeviceProfile()
    val catalog = remember { GitHubCatalog() }
    val scope = rememberCoroutineScope()
    var apps by remember { mutableStateOf<List<CatalogApp>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var installing by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try { apps = catalog.loadApps() }
        catch (t: Throwable) { error = t.message ?: "Gagal memuat katalog GitHub" }
        finally { loading = false }
    }

    fun requestInstall(app: CatalogApp) {
        installing = app.fullName
        message = null
        scope.launch {
            val url = withContext(Dispatchers.Default) { catalog.latestApkUrl(app.fullName) }
            installing = null
            if (url == null) { message = "Belum ada APK di Releases ${app.name}"; return@launch }
            installApp(url, "${app.fullName.replace('/', '_')}.apk", platformContext)
            message = if (platformContext == null) "Membuka halaman unduhan di browser" else "Mengunduh & membuka installer"
        }
    }

    val filtered = apps.filter { query.isBlank() || it.name.contains(query, true) || it.description.contains(query, true) }

    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF120B2E), Color(0xFF1B1246), Color(0xFF0D0B1F)))),
    ) {
        Column(Modifier.fillMaxSize().padding(if (profile.kind == ScreenKind.PHONE) 20.dp else 32.dp)) {
            Header(profile.kind)
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
            message?.let { Spacer(Modifier.height(8.dp)); Text(it, color = Color(0xFFFFC857), style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(18.dp))
            when {
                loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = Color(0xFFFFC857)) }
                error != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: $error", color = Color(0xFFFF8A80))
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { error = null; loading = true }) { Text("Coba lagi") }
                }
                profile.kind == ScreenKind.PHONE -> PhoneList(filtered, installing, ::requestInstall)
                else -> TabletGrid(filtered, installing, ::requestInstall)
            }
        }
    }
}

@Composable
private fun Header(kind: ScreenKind) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(color = Color(0xFFFFC857), shape = CircleShape, modifier = Modifier.size(if (kind == ScreenKind.PHONE) 52.dp else 64.dp)) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { Text("N", color = Color(0xFF120B2E), fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text("Nyawit Launcher", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Pasang & perbarui langsung dari GitHub", style = MaterialTheme.typography.bodySmall, color = Color(0xFFB9AFDE))
        }
    }
}

@Composable
private fun PhoneList(apps: List<CatalogApp>, installing: String?, onInstall: (CatalogApp) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(apps) { app -> AppCard(app, installing == app.fullName) { onInstall(app) } }
    }
}

@Composable
private fun TabletGrid(apps: List<CatalogApp>, installing: String?, onInstall: (CatalogApp) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(apps) { app -> AppCard(app, installing == app.fullName) { onInstall(app) } }
    }
}
