package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import kotlinx.coroutines.delay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.platform.appVersion
import dev.nyawit.launcher.core.platform.localCatalogOverride
import dev.nyawit.launcher.core.platform.currentAppPlatform
import dev.nyawit.launcher.core.platform.installedVersion
import dev.nyawit.launcher.core.platform.launchInstalledApp
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.SearchIcon
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.feature.catalog.data.GitHubCatalog
import dev.nyawit.launcher.feature.catalog.ui.AppStatus
import dev.nyawit.launcher.feature.install.InstallRequest
import dev.nyawit.launcher.feature.install.downloadedInstallerPath
import dev.nyawit.launcher.feature.install.installApp
import dev.nyawit.launcher.feature.install.uninstallApp
import dev.nyawit.launcher.feature.update.applyUpdate
import dev.nyawit.launcher.feature.update.ui.UpdateConfirmDialog
import dev.nyawit.launcher.feature.update.ui.UpdateDialog
import dev.nyawit.launcher.feature.update.data.UpdateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import nyawit_launcher.shared.generated.resources.Res
import nyawit_launcher.shared.generated.resources.hero_desktop
import nyawit_launcher.shared.generated.resources.tv_focus_frame
import org.jetbrains.compose.resources.painterResource

private val CATEGORIES = listOf("Semua", "Launcher", "Game", "Musik", "Aplikasi")

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

enum class FlowPhase { IDLE, CHECKING, DOWNLOADING, DONE, ERROR }
private data class Notice(
    val text: String,
    val error: Boolean = false,
    /** Tombol aksi membuat notifikasi bertahan sampai ditutup, tidak hilang dalam 5 detik. */
    val actionLabel: String? = null,
    val onAction: () -> Unit = {},
)
private data class PendingUpdate(
    val tag: String,
    val version: String,
    val assetName: String,
    val url: String,
    val isUpToDate: Boolean = false,
)

@Composable
fun CatalogScreen(modifier: Modifier, platformContext: Any?) {
    val profile = rememberDeviceProfile()
    val currentPlatform = remember { currentAppPlatform() }
    var version by remember { mutableStateOf(appVersion(platformContext)) }
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
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Semua") }
    var selfPhase by remember { mutableStateOf(FlowPhase.IDLE) }
    var appPhase by remember { mutableStateOf<Pair<String, FlowPhase>?>(null) }
    var notice by remember { mutableStateOf<Notice?>(null) }
    var launcherUpdateTag by remember { mutableStateOf<String?>(null) }
    var selfProgress by remember { mutableStateOf<Float?>(null) }
    var appProgress by remember { mutableStateOf<Pair<String, Float>?>(null) }
    var cancelSelf by remember { mutableStateOf(false) }
    var cancelApp by remember { mutableStateOf(false) }
    var reloadKey by remember { mutableStateOf(0) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val searchFocus = remember { FocusRequester() }
    var updateCount by remember { mutableStateOf(0) }
    var detailApp by remember { mutableStateOf<CatalogApp?>(null) }
    var pendingUpdate by remember { mutableStateOf<PendingUpdate?>(null) }

    LaunchedEffect(notice) {
        val current = notice
        if (current != null && current.actionLabel == null) {
            delay(5000)
            notice = null
        }
    }
    LaunchedEffect(selfPhase) {
        if (selfPhase == FlowPhase.DONE || selfPhase == FlowPhase.ERROR) {
            delay(3000)
            selfPhase = FlowPhase.IDLE
        }
    }
    LaunchedEffect(appPhase) {
        if (appPhase?.second == FlowPhase.DONE || appPhase?.second == FlowPhase.ERROR) {
            delay(3000)
            appPhase = null
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            catalog.close()
            updater.close()
        }
    }
    if (profile.kind == ScreenKind.TV) {
        LaunchedEffect(Unit) { searchFocus.requestFocus() }
    }

    LaunchedEffect(reloadKey) {
        try {
            val loaded = catalog.loadApps()
            apps = loaded
            statuses = coroutineScope {
                loaded.map { app ->
                    async(Dispatchers.Default) {
                        val version = installedVersion(app.packageName, platformContext, app.name)
                        val tag = if (version == null) null else catalog.latestReleaseTag(app.fullName)
                        val status = when {
                            version == null -> AppStatus.NOT_INSTALLED
                            tag == null -> AppStatus.INSTALLED
                            !isNewer(tag, version) -> AppStatus.INSTALLED
                            else -> AppStatus.UPDATE_AVAILABLE
                        }
                        app.fullName to status
                    }
                }.awaitAll().toMap()
            }
        } catch (t: Throwable) { error = t.message ?: "Gagal memuat katalog" }
        finally { loading = false }
        val release = withContext(Dispatchers.Default) { updater.latest() }
        val tag = release?.tag
        if (tag != null && isNewer(tag, version)) {
            launcherUpdateTag = release.tag
            notice = Notice("Pembaruan launcher ${release.tag} tersedia")
        }
        updateCount = statuses.count { it.value == AppStatus.UPDATE_AVAILABLE }
        if (updateCount > 0 && launcherUpdateTag == null) {
            notice = Notice("$updateCount aplikasi memiliki pembaruan")
        }
    }

    fun uninstall(app: CatalogApp) {
        uninstallApp(app.packageName, app.name, platformContext) { ok, msg ->
            notice = Notice("${app.name}: $msg", error = !ok)
            if (ok) reloadKey++
        }
    }

    fun openApp(app: CatalogApp) {
        if (!launchInstalledApp(app.packageName, platformContext, app.name)) {
            notice = Notice("Gagal membuka ${app.name}", error = true)
        }
    }

    fun requestSelfUpdate() {
        if (selfPhase == FlowPhase.CHECKING || selfPhase == FlowPhase.DOWNLOADING) return
        selfPhase = FlowPhase.CHECKING
        notice = Notice("Mengecek pembaruan…")
        scope.launch {
            val release = withContext(Dispatchers.Default) { updater.latest() }
            if (release == null) {
                selfPhase = FlowPhase.ERROR
                notice = Notice("Gagal mengecek rilis terbaru", error = true)
                return@launch
            }
            val asset = updater.pickAsset(release)
            if (asset == null) {
                selfPhase = FlowPhase.ERROR
                notice = Notice("Rilis ${release.tag} tidak menyediakan installer untuk platform ini", error = true)
                return@launch
            }
            val newer = isNewer(release.tag, version)
            selfPhase = FlowPhase.IDLE
            if (newer) {
                launcherUpdateTag = release.tag
                notice = Notice("Pembaruan launcher ${release.tag} tersedia")
                pendingUpdate = PendingUpdate(
                    tag = release.tag,
                    version = release.tag.removePrefix("v"),
                    assetName = asset.name,
                    url = asset.downloadUrl,
                    isUpToDate = false,
                )
            } else {
                launcherUpdateTag = null
                notice = Notice("Aplikasi sudah versi terbaru (v$version)")
                pendingUpdate = PendingUpdate(
                    tag = release.tag,
                    version = release.tag.removePrefix("v"),
                    assetName = asset.name,
                    url = asset.downloadUrl,
                    isUpToDate = true,
                )
            }
        }
    }

    fun startPendingUpdate() {
        val pending = pendingUpdate ?: return
        pendingUpdate = null
        selfPhase = FlowPhase.DOWNLOADING
        cancelSelf = false
        selfProgress = 0f
        notice = Notice(if (pending.isUpToDate) "Mengunduh ulang ${pending.tag}…" else "Mengunduh ${pending.tag}…")
        applyUpdate(
            InstallRequest(
                url = pending.url,
                fileName = pending.assetName,
                version = pending.version,
                registryKey = BuildConfig.APP_ID,
                platformContext = platformContext,
                restartAfterInstall = true,
            ),
            onProgress = { selfProgress = it },
            shouldCancel = { cancelSelf },
        ) { ok, msg ->
            selfProgress = null
            selfPhase = if (ok) FlowPhase.DONE else FlowPhase.ERROR
            notice = Notice(if (ok) "Pemasangan ${pending.tag}: $msg" else msg, error = !ok)
            if (ok) scope.launch {
                delay(2500)
                version = withContext(Dispatchers.Default) { appVersion(platformContext) }
                launcherUpdateTag = null
            }
        }
    }

    /** Menjalankan unduhan/instalasi. Bila `filePath` diisi, unduhan dilewati dan installer dibuka lagi. */
    fun startInstall(app: CatalogApp, request: InstallRequest) {
        if (appPhase?.first == app.fullName && appPhase?.second == FlowPhase.DOWNLOADING) return
        cancelApp = false
        appPhase = app.fullName to FlowPhase.DOWNLOADING
        appProgress = app.fullName to 0f
        notice = Notice(if (request.filePath != null) "Membuka installer ${app.name}…" else "Mengunduh ${app.name}…")
        installApp(
            request,
            onProgress = { appProgress = app.fullName to it },
            shouldCancel = { cancelApp },
        ) { ok, msg ->
            appProgress = null
            appPhase = app.fullName to if (ok) FlowPhase.DONE else FlowPhase.ERROR
            if (ok) scope.launch {
                val installed = withContext(Dispatchers.Default) { installedVersion(app.packageName, platformContext, app.name) }
                statuses = statuses + (app.fullName to if (installed != null) AppStatus.INSTALLED else AppStatus.NOT_INSTALLED)
                updateCount = statuses.count { it.value == AppStatus.UPDATE_AVAILABLE }
            }
            val path = downloadedInstallerPath(request)
            notice = Notice(
                text = "${app.name}: $msg",
                error = !ok,
                actionLabel = if (path.isNotBlank()) "Lanjutkan" else null,
                onAction = { startInstall(app, request.copy(filePath = path)) },
            )
        }
    }

    fun requestInstall(app: CatalogApp) {
        if (appPhase?.first == app.fullName && appPhase?.second != FlowPhase.DONE) return
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
            val version = selected.third
            if (url == null || fileName == null) {
                appPhase = app.fullName to FlowPhase.ERROR
                notice = Notice("Rilis ${app.name} tidak menyediakan installer untuk platform ini", error = true)
                return@launch
            }
            startInstall(
                app,
                InstallRequest(
                    url = url,
                    fileName = fileName,
                    version = version,
                    registryKey = app.packageName,
                    platformContext = platformContext,
                    restartAfterInstall = false,
                ),
            )
        }
    }

    val filtered = apps.filter {
        val matchPlatform = when (currentPlatform) {
            AppPlatform.DESKTOP -> AppPlatform.DESKTOP in it.platforms
            else -> AppPlatform.ANDROID in it.platforms || AppPlatform.TV in it.platforms
        }
        val matchQuery = query.isBlank() || it.name.contains(query, true) || it.description.contains(query, true)
        val matchCategory = category == "Semua" || it.category == category
        matchPlatform && matchQuery && matchCategory
    }

    val c = LocalNyawitColors.current
    val selfBusy = selfPhase == FlowPhase.CHECKING || selfPhase == FlowPhase.DOWNLOADING
    val featured = filtered.firstOrNull { it.pinned }
    val regular = if (featured == null) filtered else filtered.filter { it.fullName != featured.fullName }
    val noticeProgress = selfProgress ?: appProgress?.second
    val progressSuffix = when {
        selfProgress != null -> " ${(selfProgress!! * 100).toInt()}%"
        appProgress != null -> " ${(appProgress!!.second * 100).toInt()}%"
        else -> ""
    }

    val isMobile = profile.kind == ScreenKind.PHONE

    val screenContent = @Composable {
        Box(modifier.fillMaxSize().background(c.Background)) {
            Row(Modifier.fillMaxSize()) {
                if (profile.kind == ScreenKind.DESKTOP || profile.kind == ScreenKind.TABLET) {
                    CatalogSidebar(
                        version = version,
                        updateTag = launcherUpdateTag,
                        selfPhase = selfPhase,
                        category = category,
                        categories = CATEGORIES,
                        onCategory = { category = it },
                        onUpdate = ::requestSelfUpdate,
                        total = filtered.size,
                        updateCount = updateCount,
                        modifier = Modifier.width(216.dp),
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(
                            horizontal = if (profile.kind == ScreenKind.PHONE) 16.dp else 24.dp,
                            vertical = if (profile.kind == ScreenKind.PHONE) 14.dp else 20.dp,
                        ),
                ) {
                    if (profile.kind == ScreenKind.DESKTOP || profile.kind == ScreenKind.TABLET) {
                        Image(
                            painter = painterResource(Res.drawable.hero_desktop),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(132.dp).clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.height(18.dp))
                    }
                    if (profile.kind == ScreenKind.TV) {
                        Image(
                            painter = painterResource(Res.drawable.tv_focus_frame),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(72.dp).alpha(0.45f).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.height(12.dp))
                        Header(version, launcherUpdateTag, selfPhase, ::requestSelfUpdate)
                        AnimatedVisibility(visible = selfBusy, enter = fadeIn(), exit = fadeOut()) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(3.dp),
                                color = c.Primary,
                                trackColor = c.SurfaceRaised,
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                    if (profile.kind == ScreenKind.PHONE) {
                        MobileHeader(
                            version = version,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            updateAvailable = launcherUpdateTag != null || updateCount > 0,
                        )
                        AnimatedVisibility(visible = selfBusy, enter = fadeIn(), exit = fadeOut()) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(3.dp).padding(top = 4.dp),
                                color = c.Primary,
                                trackColor = c.SurfaceRaised,
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                    }

                    CatalogSearchBar(query, { query = it }, searchFocus)
                    Spacer(Modifier.height(12.dp))
                    if (profile.kind == ScreenKind.TV) {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CATEGORIES.forEach { item ->
                                FilterChip(
                                    selected = category == item,
                                    onClick = { category = item },
                                    label = { Text(item, style = NyawitType.Caption) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = c.Primary,
                                        selectedLabelColor = c.OnPrimary,
                                        containerColor = c.Surface,
                                        labelColor = c.Muted,
                                    ),
                                    modifier = Modifier.tvFocusHighlight(RoundedCornerShape(18.dp), 1f),
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    AnimatedVisibility(
                        visible = notice != null,
                        enter = fadeIn() + slideInVertically { -it / 2 },
                        exit = fadeOut(),
                    ) {
                        notice?.let { n ->
                            NoticeBanner(
                                text = n.text + progressSuffix,
                                isError = n.error,
                                busy = selfBusy || appPhase?.second == FlowPhase.DOWNLOADING,
                                progress = noticeProgress,
                                onCancel = { if (selfProgress != null) cancelSelf = true else cancelApp = true },
                                actionLabel = n.actionLabel,
                                onAction = n.onAction,
                                onDismiss = { notice = null },
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    SectionTitle(category, filtered.size, updateCount)
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
                        filtered.isEmpty() -> EmptyState()
                        else -> {
                            featured?.let { app ->
                                FeaturedApp(
                                    app = app,
                                    status = statuses[app.fullName] ?: AppStatus.NOT_INSTALLED,
                                    onOpen = { openApp(app) },
                                    onInstall = { requestInstall(app) },
                                    onDetail = { detailApp = app },
                                )
                                Spacer(Modifier.height(14.dp))
                            }
                            if (regular.isNotEmpty()) {
                                if (profile.kind == ScreenKind.PHONE) {
                                    PhoneList(regular, statuses, appPhase, appProgress, { detailApp = it }, ::requestInstall, ::openApp)
                                } else {
                                    AppGrid(
                                        columns = when (profile.kind) {
                                            ScreenKind.TABLET -> 2
                                            ScreenKind.TV, ScreenKind.DESKTOP -> 3
                                            ScreenKind.PHONE -> 1
                                        },
                                        apps = regular,
                                        statuses = statuses,
                                        appPhase = appPhase,
                                        appProgress = appProgress,
                                        onDetail = { detailApp = it },
                                        onInstall = ::requestInstall,
                                        onOpen = ::openApp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            detailApp?.let { app ->
                AppDetailDialog(
                    app = app,
                    status = statuses[app.fullName] ?: AppStatus.NOT_INSTALLED,
                    onInstall = { requestInstall(app) },
                    onUpdate = { requestInstall(app) },
                    onOpen = { openApp(app) },
                    onUninstall = { uninstall(app) },
                    onDismiss = { detailApp = null },
                )
            }
            pendingUpdate?.let { pending ->
                UpdateConfirmDialog(
                    currentVersion = version,
                    newVersion = pending.version,
                    installerName = pending.assetName,
                    isUpToDate = pending.isUpToDate,
                    onConfirm = { startPendingUpdate() },
                    onDismiss = { pendingUpdate = null },
                )
            }
            if (selfPhase != FlowPhase.IDLE) {
                UpdateDialog(
                    phase = selfPhase,
                    progress = selfProgress,
                    message = notice?.text.orEmpty(),
                    onCancel = { cancelSelf = true },
                    onDismiss = { selfPhase = FlowPhase.IDLE },
                )
            }
        }
    }

    if (isMobile) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = c.Surface,
                    drawerContentColor = c.Text,
                    modifier = Modifier.width(280.dp),
                ) {
                    CatalogSidebar(
                        version = version,
                        updateTag = launcherUpdateTag,
                        selfPhase = selfPhase,
                        category = category,
                        categories = CATEGORIES,
                        onCategory = { category = it },
                        onUpdate = ::requestSelfUpdate,
                        total = filtered.size,
                        updateCount = updateCount,
                        onClose = { scope.launch { drawerState.close() } },
                    )
                }
            },
        ) {
            screenContent()
        }
    } else {
        screenContent()
    }
}

@Composable
private fun Header(version: String, updateTag: String?, selfPhase: FlowPhase, onUpdate: () -> Unit) {
    val c = LocalNyawitColors.current
    val busy = selfPhase == FlowPhase.CHECKING || selfPhase == FlowPhase.DOWNLOADING
    Row(verticalAlignment = Alignment.CenterVertically) {
        BrandMark(44)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("NYAWIT", style = NyawitType.Wordmark, color = c.Text)
                Box(Modifier.size(5.dp).background(c.Primary, CircleShape))
                Text("LAUNCHER", style = NyawitType.Overline, color = c.Primary)
            }
            Spacer(Modifier.height(2.dp))
            Text(
                if (updateTag != null) "v$version  ·  Pembaruan $updateTag siap"
                else "v$version  ·  Ekosistem Multiplatform",
                style = NyawitType.Caption,
                color = if (updateTag != null) c.Warning else c.Muted,
            )
        }
        Button(
            onClick = onUpdate,
            enabled = !busy,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (updateTag != null) c.Primary else c.SurfaceRaised,
                contentColor = if (updateTag != null) c.OnPrimary else c.Text,
            ),
            shape = RoundedCornerShape(11.dp),
            modifier = Modifier.tvFocusHighlight(RoundedCornerShape(11.dp), 1f),
        ) {
            if (busy) {
                CircularProgressIndicator(
                    Modifier.size(15.dp),
                    strokeWidth = 2.dp,
                    color = if (updateTag != null) c.OnPrimary else c.Primary,
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                when (selfPhase) {
                    FlowPhase.CHECKING -> "Mengecek"
                    FlowPhase.DOWNLOADING -> "Mengunduh"
                    FlowPhase.DONE -> "Selesai"
                    FlowPhase.ERROR -> "Coba lagi"
                    FlowPhase.IDLE -> if (updateTag != null) "Update" else "Cek update"
                },
                style = NyawitType.Caption,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun PhoneList(
    apps: List<CatalogApp>,
    statuses: Map<String, AppStatus>,
    appPhase: Pair<String, FlowPhase>?,
    appProgress: Pair<String, Float>?,
    onDetail: (CatalogApp) -> Unit,
    onInstall: (CatalogApp) -> Unit,
    onOpen: (CatalogApp) -> Unit,
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
                busy = appPhase?.let { (name, phase) ->
                    name == app.fullName && (phase == FlowPhase.DOWNLOADING || phase == FlowPhase.CHECKING)
                } ?: false,
                progress = if (appPhase?.first == app.fullName) appProgress?.second else null,
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
    appPhase: Pair<String, FlowPhase>?,
    appProgress: Pair<String, Float>?,
    onDetail: (CatalogApp) -> Unit,
    onInstall: (CatalogApp) -> Unit,
    onOpen: (CatalogApp) -> Unit,
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
                busy = appPhase?.let { (name, phase) ->
                    name == app.fullName && (phase == FlowPhase.DOWNLOADING || phase == FlowPhase.CHECKING)
                } ?: false,
                progress = if (appPhase?.first == app.fullName) appProgress?.second else null,
                onDetail = { onDetail(app) },
                onInstall = { onInstall(app) },
                onOpen = { onOpen(app) },
            )
         }
    }
}