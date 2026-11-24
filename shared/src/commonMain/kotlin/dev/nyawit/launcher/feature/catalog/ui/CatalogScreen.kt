package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.platform.appVersion
import dev.nyawit.launcher.core.platform.currentAppPlatform
import dev.nyawit.launcher.core.platform.installedVersion
import dev.nyawit.launcher.core.platform.launchInstalledApp
import dev.nyawit.launcher.core.platform.localCatalogOverride
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.feature.catalog.data.GitHubCatalog
import dev.nyawit.launcher.feature.install.InstallRequest
import dev.nyawit.launcher.feature.install.downloadedInstallerPath
import dev.nyawit.launcher.feature.install.installApp
import dev.nyawit.launcher.feature.install.uninstallApp
import dev.nyawit.launcher.feature.update.applyUpdate
import dev.nyawit.launcher.feature.update.data.ReleaseInfo
import dev.nyawit.launcher.feature.update.data.UpdateRepository
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nyawit_launcher.shared.generated.resources.Res

private data class Notice(
    val text: String,
    val error: Boolean = false,
    val actionLabel: String? = null,
    val onAction: () -> Unit = {},
)

private fun isNewer(latest: String, installed: String): Boolean {
    fun parse(v: String) = v.removePrefix("v").trim().split(".").mapNotNull { it.toIntOrNull() }
    val a = parse(latest)
    val b = parse(installed)
    for (i in 0 until maxOf(a.size, b.size)) {
        val av = a.getOrElse(i) { 0 }
        val bv = b.getOrElse(i) { 0 }
        if (av != bv) return av > bv
    }
    return false
}

/**
 * Layar tunggal: daftar aplikasi dan tombol pasang.
 * Tidak ada drawer, pencarian, kategori, dialog detail, atau konfirmasi pembaruan.
 */
@Composable
fun CatalogScreen(modifier: Modifier, platformContext: Any?) {
    val profile = rememberDeviceProfile()
    val currentPlatform = remember { currentAppPlatform() }
    val version = remember { appVersion(platformContext) }
    val catalog = remember {
        GitHubCatalog(
            localCatalogJson = localCatalogOverride(),
            bundledCatalog = { Res.readBytes("files/catalog.json").decodeToString() },
            platformContext = platformContext,
        )
    }
    val updater = remember { UpdateRepository() }
    val scope = rememberCoroutineScope()

    var apps by remember { mutableStateOf<List<CatalogApp>>(emptyList()) }
    var statuses by remember { mutableStateOf<Map<String, AppStatus>>(emptyMap()) }
    var busyApps by remember { mutableStateOf<Set<String>>(emptySet()) }
    var progress by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<Notice?>(null) }
    var selfBusy by remember { mutableStateOf(false) }
    var selfUpdateDone by remember { mutableStateOf(false) }
    var updateTag by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableStateOf(0) }
    var detailApp by remember { mutableStateOf<CatalogApp?>(null) }
    var page by remember { mutableStateOf(AppPage.APPS) }
    var query by remember { mutableStateOf("") }
    val searchFocus = remember { FocusRequester() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    DisposableEffect(Unit) {
        onDispose {
            catalog.close()
            updater.close()
        }
    }

    LaunchedEffect(notice) {
        if (notice != null && notice?.actionLabel == null) {
            delay(5000)
            notice = null
        }
    }
    if (profile.kind == ScreenKind.TV) {
        LaunchedEffect(Unit) { searchFocus.requestFocus() }
    }

    /** Pembaruan launcher berjalan sendiri tanpa dialog konfirmasi. */
    fun autoUpdate(release: ReleaseInfo) {
        if (selfBusy) return
        val asset = updater.pickAsset(release) ?: return
        selfBusy = true
        notice = Notice("Memasang pembaruan launcher ${release.tag}…")
        applyUpdate(
            InstallRequest(
                url = asset.downloadUrl,
                fileName = asset.name,
                version = release.tag.removePrefix("v"),
                registryKey = BuildConfig.APP_ID,
                platformContext = platformContext,
                restartAfterInstall = true,
            ),
            onProgress = {},
            shouldCancel = { false },
        ) { ok, msg ->
            selfBusy = false
            notice = Notice(
                if (ok) "Pembaruan launcher ${release.tag} dipasang otomatis"
                else "Gagal memperbarui launcher: $msg",
                error = !ok,
            )
        }
    }

    fun checkUpdates() {
        if (selfBusy) return
        scope.launch {
            val release = withContext(Dispatchers.Default) { updater.latest() }
            if (release == null) {
                notice = Notice("Gagal mengecek rilis terbaru", error = true)
                return@launch
            }
            if (isNewer(release.tag, version)) {
                updateTag = release.tag
                autoUpdate(release)
            } else {
                notice = Notice("Aplikasi sudah versi terbaru (v$version)")
            }
        }
    }

    /** Menjalankan unduhan/instalasi dari request yang sudah disiapkan. */
    fun startInstall(app: CatalogApp, request: InstallRequest) {
        if (app.fullName in busyApps) return
        busyApps = busyApps + app.fullName
        progress = progress + (app.fullName to 0f)
        notice = Notice(if (request.filePath != null) "Membuka installer ${app.name}…" else "Mengunduh ${app.name}…")
        val cachedPath = downloadedInstallerPath(request)
        installApp(
            request,
            onProgress = { progress = progress + (app.fullName to it) },
            shouldCancel = { false },
        ) { ok, msg ->
            busyApps = busyApps - app.fullName
            progress = progress - app.fullName
            scope.launch {
                val installed = withContext(Dispatchers.Default) {
                    installedVersion(app.packageName, platformContext, app.name)
                }
                statuses = statuses + (
                    app.fullName to if (installed != null) AppStatus.INSTALLED else AppStatus.NOT_INSTALLED
                )
            }
            notice = Notice(
                text = "${app.name}: $msg",
                error = !ok,
                actionLabel = if (!ok && cachedPath.isNotBlank()) "Lanjutkan" else null,
                onAction = { startInstall(app, request.copy(filePath = cachedPath)) },
            )
            reloadKey++
        }
    }

    /** Unduh lalu pasang aplikasi sekali ketuk. */
    fun install(app: CatalogApp) {
        if (app.fullName in busyApps) return
        scope.launch {
            val selected = withContext(Dispatchers.Default) {
                if (currentPlatform == AppPlatform.DESKTOP) {
                    val release = updater.latestFor(app.fullName)
                    val asset = release?.let { updater.pickAsset(it) }
                    Triple(asset?.downloadUrl, asset?.name, release?.tag?.removePrefix("v") ?: "1.0.0")
                } else {
                    Triple(catalog.latestApkUrl(app.fullName), "${app.fullName.replace('/', '_')}.apk", "1.0.0")
                }
            }
            val url = selected.first
            val fileName = selected.second
            if (url == null || fileName == null) {
                notice = Notice("${app.name} tidak menyediakan installer untuk platform ini", error = true)
                return@launch
            }
            startInstall(
                app,
                InstallRequest(
                    url = url,
                    fileName = fileName,
                    version = selected.third,
                    registryKey = app.packageName,
                    platformContext = platformContext,
                ),
            )
        }
    }

    /** Pembaruan memakai jalur unduh yang sama dengan instalasi. */
    fun update(app: CatalogApp) = install(app)

    fun uninstall(app: CatalogApp) {
        uninstallApp(app.packageName, app.name, platformContext) { ok, msg ->
            notice = Notice("${app.name}: $msg", error = !ok)
            if (ok) reloadKey++
        }
    }

    fun open(app: CatalogApp) {
        if (!launchInstalledApp(app.packageName, platformContext, app.name)) {
            notice = Notice("Gagal membuka ${app.name}", error = true)
        }
    }

    LaunchedEffect(reloadKey) {
        try {
            val loaded = catalog.loadApps()
            apps = loaded
            statuses = coroutineScope {
                loaded.map { app ->
                    async(Dispatchers.Default) {
                        val installed = installedVersion(app.packageName, platformContext, app.name)
                        val tag = if (installed == null) null else catalog.latestReleaseTag(app.fullName)
                        val status = when {
                            installed == null -> AppStatus.NOT_INSTALLED
                            tag == null || !isNewer(tag, installed) -> AppStatus.INSTALLED
                            else -> AppStatus.UPDATE_AVAILABLE
                        }
                        app.fullName to status
                    }
                }.awaitAll().toMap()
            }
        } catch (t: Throwable) {
            error = t.message ?: "Gagal memuat katalog"
        } finally {
            loading = false
        }
        val release = withContext(Dispatchers.Default) { updater.latest() }
        if (!selfUpdateDone && release != null && isNewer(release.tag, version)) {
            selfUpdateDone = true
            updateTag = release.tag
            autoUpdate(release)
        }
    }

    val updateCount = statuses.values.count { it == AppStatus.UPDATE_AVAILABLE }
    val visibleApps = apps.filter { app ->
        val matchPlatform = when (currentPlatform) {
            AppPlatform.DESKTOP -> AppPlatform.DESKTOP in app.platforms
            else -> AppPlatform.ANDROID in app.platforms || AppPlatform.TV in app.platforms
        }
        val matchQuery = query.isBlank() ||
            app.name.contains(query, true) || app.description.contains(query, true)
        matchPlatform && matchQuery
    }
    val isPhone = profile.kind == ScreenKind.PHONE
    val c = LocalNyawitColors.current

    val content = @Composable {
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (isPhone) 16.dp else 24.dp,
                    vertical = if (isPhone) 14.dp else 20.dp,
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isPhone) {
                    Button(
                        onClick = { scope.launch { drawerState.open() } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = c.SurfaceRaised,
                            contentColor = c.Text,
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(38.dp),
                    ) { Text("☰", style = NyawitType.Caption) }
                    Spacer(Modifier.width(10.dp))
                }
                BrandMark(34)
                Spacer(Modifier.width(10.dp))
                Text("NYAWIT LAUNCHER", style = NyawitType.Wordmark, color = c.Text)
                Spacer(Modifier.width(8.dp))
                Text("v$version", style = NyawitType.Caption, color = c.Muted)
            }
            Spacer(Modifier.height(12.dp))
            AnimatedVisibility(
                visible = notice != null,
                enter = fadeIn() + slideInVertically { -it / 2 },
                exit = fadeOut(),
            ) {
                notice?.let { n ->
                    NoticeBanner(
                        text = n.text,
                        isError = n.error,
                        busy = false,
                        progress = null,
                        onCancel = {},
                        actionLabel = n.actionLabel,
                        onAction = n.onAction,
                        onDismiss = { notice = null },
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            when (page) {
                AppPage.APPS -> {
                    CatalogSearchBar(query, { query = it }, searchFocus)
                    Spacer(Modifier.height(12.dp))
                    when {
                        loading -> LoadingPlaceholder()
                        error != null -> Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text("Katalog tidak dapat dimuat", style = NyawitType.Title, color = c.Text)
                            Spacer(Modifier.height(6.dp))
                            Text(error ?: "", style = NyawitType.Caption, color = c.Muted)
                            Spacer(Modifier.height(18.dp))
                            Button(
                                onClick = { error = null; loading = true; reloadKey++ },
                                colors = ButtonDefaults.buttonColors(containerColor = c.Primary, contentColor = c.OnPrimary),
                                shape = RoundedCornerShape(10.dp),
                            ) { Text("Coba lagi") }
                        }
                        visibleApps.isEmpty() -> EmptyState()
                        isPhone -> AppList(
                            apps = visibleApps,
                            statuses = statuses,
                            busyApps = busyApps,
                            progress = progress,
                            onInstall = ::install,
                            onOpen = ::open,
                            onDetail = { detailApp = it },
                        )
                        else -> AppGrid(
                            columns = if (profile.kind == ScreenKind.TABLET) 2 else 3,
                            apps = visibleApps,
                            statuses = statuses,
                            busyApps = busyApps,
                            progress = progress,
                            onInstall = ::install,
                            onOpen = ::open,
                            onDetail = { detailApp = it },
                        )
                    }
                }
                AppPage.ABOUT -> AboutScreen(version, platformContext)
            }
        }
    }

    if (isPhone) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = c.Surface,
                    drawerContentColor = c.Text,
                    modifier = Modifier.width(280.dp),
                ) {
                    AppSidebar(
                        page = page,
                        version = version,
                        updateTag = updateTag,
                        total = visibleApps.size,
                        updateCount = updateCount,
                        selfBusy = selfBusy,
                        onNavigate = { page = it },
                        onUpdate = ::checkUpdates,
                        onClose = { scope.launch { drawerState.close() } },
                    )
                }
            },
        ) { content() }
    } else {
        Row(Modifier.fillMaxSize().background(c.Background)) {
            AppSidebar(
                page = page,
                version = version,
                updateTag = updateTag,
                total = visibleApps.size,
                updateCount = updateCount,
                selfBusy = selfBusy,
                onNavigate = { page = it },
                onUpdate = ::checkUpdates,
                modifier = Modifier.width(216.dp),
            )
            Box(Modifier.weight(1f).fillMaxSize().background(c.Background)) { content() }
        }
    }

    detailApp?.let { app ->
        AppDetailDialog(
            app = app,
            status = statuses[app.fullName] ?: AppStatus.NOT_INSTALLED,
            onInstall = { install(app) },
            onUpdate = { update(app) },
            onOpen = { open(app) },
            onUninstall = { uninstall(app) },
            onDismiss = { detailApp = null },
        )
    }
}

@Composable
private fun AppList(
    apps: List<CatalogApp>,
    statuses: Map<String, AppStatus>,
    busyApps: Set<String>,
    progress: Map<String, Float>,
    onInstall: (CatalogApp) -> Unit,
    onOpen: (CatalogApp) -> Unit,
    onDetail: (CatalogApp) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(apps) { app ->
            AppCard(
                app = app,
                status = statuses[app.fullName] ?: AppStatus.NOT_INSTALLED,
                busy = app.fullName in busyApps,
                progress = progress[app.fullName],
                onDetail = { onDetail(app) },
                onInstall = { onInstall(app) },
                onOpen = { onOpen(app) },
            )
        }
    }
}

@Composable
private fun AppGrid(
    columns: Int,
    apps: List<CatalogApp>,
    statuses: Map<String, AppStatus>,
    busyApps: Set<String>,
    progress: Map<String, Float>,
    onInstall: (CatalogApp) -> Unit,
    onOpen: (CatalogApp) -> Unit,
    onDetail: (CatalogApp) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(apps) { app ->
            AppCard(
                app = app,
                status = statuses[app.fullName] ?: AppStatus.NOT_INSTALLED,
                busy = app.fullName in busyApps,
                progress = progress[app.fullName],
                onDetail = { onDetail(app) },
                onInstall = { onInstall(app) },
                onOpen = { onOpen(app) },
            )
        }
    }
}
