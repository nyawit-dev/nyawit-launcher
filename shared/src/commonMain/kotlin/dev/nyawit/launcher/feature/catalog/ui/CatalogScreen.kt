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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.platform.currentAppPlatform
import dev.nyawit.launcher.core.platform.installedVersion
import dev.nyawit.launcher.core.platform.launchInstalledApp
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.feature.catalog.data.GitHubCatalog
import dev.nyawit.launcher.feature.catalog.ui.AppStatus
import dev.nyawit.launcher.feature.install.installApp
import dev.nyawit.launcher.feature.update.applyUpdate
import dev.nyawit.launcher.feature.update.data.UpdateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ACCENT = Color(0xFF3DDC97)
private val BG_TOP = Color(0xFF101A20)
private val BG_BOTTOM = Color(0xFF0B0F14)
private val MUTED = Color(0xFF8E9BA8)
private val CATEGORIES = listOf("Semua", "Launcher", "Game", "Musik", "Aplikasi")

@Composable
fun CatalogScreen(modifier: Modifier, platformContext: Any?) {
    val profile = rememberDeviceProfile()
    val currentPlatform = remember { currentAppPlatform() }
    val catalog = remember { GitHubCatalog() }
    val updater = remember { UpdateRepository() }
    val scope = rememberCoroutineScope()
    var apps by remember { mutableStateOf<List<CatalogApp>>(emptyList()) }
    var statuses by remember { mutableStateOf<Map<String, AppStatus>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Semua") }
    var installing by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var updateMessage by remember { mutableStateOf<String?>(null) }
    var updating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val loaded = catalog.loadApps()
            apps = loaded
            statuses = loaded.associate { app ->
                val version = withContext(Dispatchers.Default) { installedVersion(app.packageName, platformContext) }
                val tag = if (version == null) null else withContext(Dispatchers.Default) { catalog.latestReleaseTag(app.fullName) }
                val status = when {
                    version == null -> AppStatus.NOT_INSTALLED
                    tag == null -> AppStatus.INSTALLED
                    tag.removePrefix("v").trim() == version.trim() -> AppStatus.INSTALLED
                    else -> AppStatus.UPDATE_AVAILABLE
                }
                app.fullName to status
            }
        } catch (t: Throwable) { error = t.message ?: "Gagal memuat katalog" }
        finally { loading = false }
    }

    fun openApp(app: CatalogApp) {
        launchInstalledApp(app.packageName, platformContext)
    }

    fun requestSelfUpdate() {
        updating = true
        updateMessage = null
        scope.launch {
            val release = withContext(Dispatchers.Default) { updater.latest() }
            val asset = release?.let { updater.pickAsset(it) }
            updating = false
            if (asset == null) updateMessage = "Belum ada rilis platform ini"
            else {
                applyUpdate(asset.downloadUrl, asset.name, platformContext)
                updateMessage = "Mengunduh ${release.tag}"
            }
        }
    }

    fun requestInstall(app: CatalogApp) {
        installing = app.fullName
        message = null
        scope.launch {
            val url = withContext(Dispatchers.Default) { catalog.latestApkUrl(app.fullName) }
            installing = null
            if (url == null) { message = "Belum ada APK untuk ${app.name}"; return@launch }
            installApp(url, "${app.fullName.replace('/', '_')}.apk", platformContext)
            message = if (platformContext == null) "Unduhan dibuka di browser" else "Membuka installer ${app.name}"
        }
    }

    val filtered = apps.filter {
        val matchPlatform = currentPlatform in it.platforms || (currentPlatform == dev.nyawit.launcher.core.platform.AppPlatform.ANDROID && AppPlatform.TV in it.platforms)
        val matchQuery = query.isBlank() || it.name.contains(query, true) || it.description.contains(query, true)
        val matchCategory = category == "Semua" || it.category == category
        matchPlatform && matchQuery && matchCategory
    }

    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BG_TOP, BG_BOTTOM))),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = if (profile.kind == ScreenKind.PHONE) 20.dp else 36.dp, vertical = 20.dp),
        ) {
            Header(updating, ::requestSelfUpdate, updateMessage)
            Spacer(Modifier.height(22.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Cari aplikasi…", color = MUTED) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().tvFocusHighlight(RoundedCornerShape(14.dp), 1f),
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CATEGORIES.forEach { item ->
                    FilterChip(
                        selected = category == item,
                        onClick = { category = item },
                        label = { Text(item) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ACCENT,
                            selectedLabelColor = Color(0xFF0B0F14),
                            containerColor = Color(0xFF182129),
                            labelColor = MUTED,
                        ),
                        modifier = Modifier.tvFocusHighlight(RoundedCornerShape(18.dp), 1f),
                    )
                }
            }
            message?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = ACCENT, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(16.dp))
            Text("${filtered.size} aplikasi", color = MUTED, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(10.dp))

            when {
                loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = ACCENT)
                }
                error != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Gagal memuat: $error", color = Color(0xFFFF8A80))
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { error = null; loading = true }) { Text("Coba lagi") }
                }
                filtered.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Tidak ada aplikasi untuk platform ini.", color = MUTED)
                }
                profile.kind == ScreenKind.PHONE -> PhoneList(filtered, statuses, installing, ::requestInstall, ::openApp)
                else -> TabletGrid(filtered, statuses, installing, ::requestInstall, ::openApp)
            }
        }
    }
}

@Composable
private fun Header(updating: Boolean, onUpdate: () -> Unit, updateMessage: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(color = ACCENT, shape = RoundedCornerShape(14.dp), modifier = Modifier.size(48.dp)) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("N", color = Color(0xFF0B0F14), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Nyawit", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            updateMessage?.let { Text(it, color = ACCENT, style = MaterialTheme.typography.labelSmall) }
                ?: Text("Launcher dari GitHub", color = MUTED, style = MaterialTheme.typography.labelSmall)
        }
        Button(
            onClick = onUpdate,
            enabled = !updating,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D2A31), contentColor = ACCENT),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.tvFocusHighlight(RoundedCornerShape(12.dp), 1f),
        ) { Text(if (updating) "Mengecek…" else "Perbarui") }
    }
}

@Composable
private fun PhoneList(
    apps: List<CatalogApp>,
    statuses: Map<String, AppStatus>,
    installing: String?,
    onInstall: (CatalogApp) -> Unit,
    onOpen: (CatalogApp) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(apps) { app ->
            AppCard(
                app = app,
                status = statuses[app.fullName] ?: AppStatus.NOT_INSTALLED,
                busy = installing == app.fullName,
                onInstall = { onInstall(app) },
                onOpen = { onOpen(app) },
            )
        }
    }
}

@Composable
private fun TabletGrid(
    apps: List<CatalogApp>,
    statuses: Map<String, AppStatus>,
    installing: String?,
    onInstall: (CatalogApp) -> Unit,
    onOpen: (CatalogApp) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 290.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(apps) { app ->
            AppCard(
                app = app,
                status = statuses[app.fullName] ?: AppStatus.NOT_INSTALLED,
                busy = installing == app.fullName,
                onInstall = { onInstall(app) },
                onOpen = { onOpen(app) },
            )
        }
    }
}
