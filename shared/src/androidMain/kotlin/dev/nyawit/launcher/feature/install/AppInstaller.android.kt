package dev.nyawit.launcher.feature.install

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.FileProvider
import dev.nyawit.launcher.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicBoolean

private val mainHandler = Handler(Looper.getMainLooper())

private inline fun onMain(crossinline block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) block()
    else mainHandler.post { block() }
}

actual fun downloadedInstallerPath(request: InstallRequest): String {
    val context = request.platformContext as? Context ?: return ""
    return File(context.filesDir, request.fileName).absolutePath
}

private fun sha256Of(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buffer = ByteArray(8192)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) digest.update(buffer, 0, read)
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

private fun checksumMatches(downloadUrl: String, file: File): Boolean {
    var connection: HttpURLConnection? = null
    return try {
        connection = URI("$downloadUrl.sha256").toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        if (connection.responseCode !in 200..299) {
            connection.errorStream?.close()
            return true
        }
        val expected = connection.inputStream.bufferedReader().use { it.readText() }
            .trim().split(Regex("\\s+")).firstOrNull().orEmpty()
        if (!Regex("^[a-fA-F0-9]{64}$").matches(expected)) return true
        sha256Of(file).equals(expected, ignoreCase = true)
    } catch (_: Throwable) {
        true
    } finally {
        runCatching { connection?.errorStream?.close() }
        runCatching { connection?.inputStream?.close() }
        connection?.disconnect()
    }
}

actual fun canInstallPackages(platformContext: Any?): Boolean {
    val context = platformContext as? Context ?: return false
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return true
    return runCatching { context.packageManager.canRequestPackageInstalls() }.getOrDefault(false)
}

actual fun openInstallPermissionSettings(platformContext: Any?): Boolean {
    val context = platformContext as? Context ?: return false
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
    val uri = Uri.parse("package:${context.packageName}")
    var settings = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (settings.resolveActivity(context.packageManager) == null) {
        // Fallback untuk Android TV: buka daftar tanpa URI
        settings = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (settings.resolveActivity(context.packageManager) == null) {
            // Fallback detail aplikasi
            settings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    return try { context.startActivity(settings); true } catch (_: Throwable) { false }
}

private data class PendingInstall(
    val request: InstallRequest,
    val onProgress: (Float) -> Unit,
    val shouldCancel: () -> Boolean,
    val onResult: (Boolean, String) -> Unit,
)

private val pendingLock = Any()

@Volatile
private var pendingInstall: PendingInstall? = null

/** Dipanggil MainActivity.onResume() setelah izin diberikan di Pengaturan. */
fun resumePendingInstall(platformContext: Any?) {
    val context = platformContext as? Context ?: return
    val pending = synchronized(pendingLock) { pendingInstall } ?: return
    if (!canInstallPackages(context)) return
    synchronized(pendingLock) {
        if (pendingInstall === pending) pendingInstall = null
    }
    val file = File(downloadedInstallerPath(pending.request))
    if (!file.isFile || file.length() <= 1024) {
        onMain { pending.onResult(false, "File installer tidak ditemukan — ketuk Pasang untuk mengunduh ulang") }
        return
    }
    installDownloadedFile(context, pending.request, file, pending.onResult)
}

private fun fileProviderAuthority(context: Context): String {
    val appId = runCatching { BuildConfig.APP_ID }.getOrNull()?.takeIf { it.isNotBlank() }
        ?: context.packageName
    val primary = "$appId.fileprovider"
    if (runCatching { context.packageManager.resolveContentProvider(primary, 0) }.getOrNull() != null) {
        return primary
    }
    val providers = runCatching {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PROVIDERS).providers
    }.getOrNull()
    return providers?.firstOrNull { it.name == FileProvider::class.java.name }?.authority ?: primary
}

private class InstallResultReceiver(
    private val sessionId: Int,
    private val action: String,
    private val file: File?,
    private val onDone: (Boolean, String) -> Unit,
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != action) return
        if (intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1) != sessionId) return
        when (val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)) {
            PackageInstaller.STATUS_SUCCESS -> {
                runCatching { file?.delete() }
                onDone(true, "Pemasangan berhasil")
            }
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                @Suppress("DEPRECATION")
                val confirm: Intent? = intent.getParcelableExtra(Intent.EXTRA_INTENT)
                confirm?.let {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try { context.startActivity(it) } catch (_: Throwable) {}
                }
            }
            else -> {
                val detail = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                    ?.takeIf { it.isNotBlank() } ?: "status $status"
                onDone(false, "Pemasangan gagal: $detail")
            }
        }
    }
}

private fun installWithPackageInstaller(context: Context, file: File, onDone: (Boolean, String) -> Unit) {
    val installer = context.packageManager.packageInstaller
    val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
    val sessionId = installer.createSession(params)
    val session = installer.openSession(sessionId)
    val action = "${context.packageName}.INSTALL_RESULT"
    val finished = AtomicBoolean(false)
    var receiver: InstallResultReceiver? = null
    var committed = false
    val timeout = Runnable {
        if (finished.compareAndSet(false, true)) {
            runCatching { receiver?.let { context.unregisterReceiver(it) } }
            onDone(false, "Pemasangan tidak selesai dalam 10 menit — periksa konfirmasi sistem")
        }
    }
    try {
        session.openWrite("base.apk", 0, file.length()).use { out ->
            file.inputStream().use { input ->
                val buffer = ByteArray(64 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) out.write(buffer, 0, read)
            }
            session.fsync(out)
        }
        receiver = InstallResultReceiver(sessionId, action, file) { ok, msg ->
            mainHandler.removeCallbacks(timeout)
            if (finished.compareAndSet(false, true)) {
                runCatching { context.unregisterReceiver(receiver) }
                onDone(ok, msg)
            }
        }
        val filter = IntentFilter(action)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(receiver, filter)
        }
        mainHandler.postDelayed(timeout, 10 * 60 * 1000L)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId,
            Intent(action).setPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0),
        )
        session.commit(pendingIntent.intentSender)
        committed = true
    } catch (t: Throwable) {
        mainHandler.removeCallbacks(timeout)
        runCatching { receiver?.let { context.unregisterReceiver(it) } }
        if (!committed) { runCatching { session.abandon() }; throw t }
    } finally {
        if (!committed) runCatching { session.close() }
    }
}

private fun installWithIntent(context: Context, file: File, onDone: (Boolean, String) -> Unit) {
    try {
        val authority = fileProviderAuthority(context)
        val uri = FileProvider.getUriForFile(context, authority, file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Berikan izin URI eksplisit ke calon package installer di sistem (TV & Mobile)
        val pm = context.packageManager
        val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        for (info in resolveInfos) {
            val pkg = info.activityInfo.packageName
            context.grantUriPermission(pkg, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)
        onDone(true, "Installer sistem dibuka — ikuti konfirmasi pemasangan")
    } catch (t: Throwable) {
        onDone(false, "Gagal membuka installer: ${t.message ?: "unknown"}")
    }
}

private fun installDownloadedFile(
    context: Context,
    request: InstallRequest,
    file: File,
    onResult: (Boolean, String) -> Unit,
) {
    val deliver: (Boolean, String) -> Unit = { ok, msg ->
        onMain {
            onResult(ok, msg)
            if (ok && request.restartAfterInstall) {
                mainHandler.postDelayed({ kotlin.system.exitProcess(0) }, 750L)
            }
        }
    }
    // Menggunakan Intent ACTION_VIEW di Main Thread agar dialog installer Android TV langsung tampil tanpa terblokir BAL
    onMain {
        try {
            installWithIntent(context, file, deliver)
        } catch (_: Throwable) {
            Thread {
                try {
                    installWithPackageInstaller(context, file, deliver)
                } catch (t: Throwable) {
                    onMain { onResult(false, "Gagal membuka installer: ${t.message ?: "unknown"}") }
                }
            }.start()
        }
    }
}

actual fun installApp(
    request: InstallRequest,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
) {
    val context = request.platformContext as? Context ?: run {
        onMain { onResult(false, "Konteks Android tidak tersedia") }
        return
    }
    synchronized(pendingLock) { pendingInstall = null }
    if (shouldCancel()) {
        onMain { onResult(false, "Dibatalkan") }
        return
    }
    Thread {
        var connection: HttpURLConnection? = null
        try {
            val target = File(downloadedInstallerPath(request))
            target.parentFile?.mkdirs()
            val provided = request.filePath?.let(::File)?.takeIf { it.isFile && it.length() > 1024 }
            if (provided != null && provided.absolutePath != target.absolutePath) {
                provided.copyTo(target, overwrite = true)
            }
            if (provided == null) {
                val part = File(target.parentFile, "${target.name}.part")
                connection = URI(request.url).toURL().openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = true
                connection.connectTimeout = 15000
                connection.readTimeout = 60000
                val code = connection.responseCode
                if (code !in 200..299) throw IllegalStateException("HTTP $code")
                val total = connection.contentLengthLong
                connection.inputStream.use { input ->
                    FileOutputStream(part).use { output ->
                        val buffer = ByteArray(16 * 1024)
                        var read: Int
                        var written = 0L
                        while (input.read(buffer).also { read = it } != -1) {
                            if (shouldCancel()) {
                                part.delete()
                                onMain { onResult(false, "Unduhan dibatalkan") }
                                return@Thread
                            }
                            output.write(buffer, 0, read)
                            written += read
                            if (total > 0) {
                                val fraction = (written.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                                onMain { onProgress(fraction) }
                            } else {
                                val mb = written / (1024 * 1024f)
                                val fraction = (mb / (mb + 10f)).coerceIn(0.05f, 0.95f)
                                onMain { onProgress(fraction) }
                            }
                        }
                    }
                }
                if (part.length() < 1024) {
                    part.delete()
                    throw IllegalStateException("File installer kosong atau rusak")
                }
                if (!part.renameTo(target)) {
                    part.copyTo(target, overwrite = true)
                    part.delete()
                }
            }
            if (!checksumMatches(request.url, target)) {
                target.delete()
                onMain { onResult(false, "Checksum SHA-256 tidak cocok — unduhan dibatalkan") }
                return@Thread
            }
            if (target.length() < 1024) {
                onMain { onResult(false, "File installer kosong atau rusak") }
                return@Thread
            }
            onMain { onProgress(1f) }
            if (!canInstallPackages(context)) {
                synchronized(pendingLock) {
                    pendingInstall = PendingInstall(request, onProgress, shouldCancel, onResult)
                }
                val opened = openInstallPermissionSettings(context)
                onMain {
                    onResult(
                        false,
                        if (opened) {
                            "Izinkan ${BuildConfig.APP_NAME} memasang aplikasi tidak dikenal di Pengaturan, lalu kembali — pemasangan akan dilanjutkan otomatis (atau ketuk Lanjutkan)."
                        } else {
                            "Buka Pengaturan → Aplikasi → ${BuildConfig.APP_NAME} → Install unknown apps → izinkan, lalu ketuk Lanjutkan."
                        },
                    )
                }
                return@Thread
            }
            synchronized(pendingLock) { pendingInstall = null }
            installDownloadedFile(context, request, target, onResult)
        } catch (t: Throwable) {
            onMain { onResult(false, "Gagal mengunduh: ${t.message ?: "unknown"}") }
        } finally {
            connection?.disconnect()
        }
    }.start()
}

actual fun cleanupDownloadedFiles(platformContext: Any?) {
    val context = platformContext as? Context ?: return
    try {
        val files = context.filesDir.listFiles() ?: return
        val now = System.currentTimeMillis()
        for (file in files) {
            if (file.name.endsWith(".part") && (now - file.lastModified() > 30 * 60 * 1000L)) {
                file.delete()
            }
        }
    } catch (_: Throwable) {}
}

actual fun getDownloadedFilesSize(platformContext: Any?): Long {
    val context = platformContext as? Context ?: return 0L
    return try {
        val filesDir = context.filesDir.listFiles()?.filter { it.isFile && (it.name.endsWith(".apk") || it.name.endsWith(".part")) } ?: emptyList()
        val cacheDir = context.cacheDir.listFiles()?.filter { it.isFile } ?: emptyList()
        (filesDir + cacheDir).sumOf { it.length() }
    } catch (_: Throwable) {
        0L
    }
}

actual fun clearAllDownloadedFiles(platformContext: Any?): Long {
    val context = platformContext as? Context ?: return 0L
    return try {
        val filesDir = context.filesDir.listFiles()?.filter { it.isFile && (it.name.endsWith(".apk") || it.name.endsWith(".part")) } ?: emptyList()
        val cacheDir = context.cacheDir.listFiles()?.filter { it.isFile } ?: emptyList()
        var totalDeleted = 0L
        for (file in filesDir + cacheDir) {
            val len = file.length()
            if (file.delete()) {
                totalDeleted += len
            }
        }
        totalDeleted
    } catch (_: Throwable) {
        0L
    }
}
