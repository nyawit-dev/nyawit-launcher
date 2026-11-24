package dev.nyawit.launcher.feature.install

import android.content.Context
import android.content.Intent
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

actual fun downloadedInstallerPath(request: InstallRequest): String {
    val context = request.platformContext as? Context ?: return ""
    return File(context.filesDir, request.fileName).absolutePath
}

private fun sha256Of(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buffer = ByteArray(8192)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            digest.update(buffer, 0, read)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

private fun checksumMatches(downloadUrl: String, file: File): Boolean {
    var connection: HttpURLConnection? = null
    return try {
        connection = URI("$downloadUrl.sha256").toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        if (connection.responseCode !in 200..299) return true
        val expected = connection.inputStream.bufferedReader().use { it.readText() }
            .trim().split(Regex("\\s+")).firstOrNull().orEmpty()
        if (!Regex("^[a-fA-F0-9]{64}$").matches(expected)) return true
        sha256Of(file).equals(expected, ignoreCase = true)
    } catch (_: Throwable) {
        true
    } finally {
        connection?.disconnect()
    }
}

actual fun installApp(
    request: InstallRequest,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
) {
    val context = request.platformContext as? Context ?: run {
        onResult(false, "Konteks Android tidak tersedia")
        return
    }
    val main = Handler(Looper.getMainLooper())
    Thread {
        val file = request.filePath?.let(::File)?.takeIf { it.isFile && it.length() > 1024 }
            ?: File(context.filesDir, request.fileName)
        var connection: HttpURLConnection? = null
        try {
            if (request.filePath == null || !file.exists()) {
                connection = URI(request.url).toURL().openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = true
                connection.connectTimeout = 15000
                connection.readTimeout = 60000
                val code = connection.responseCode
                if (code !in 200..299) {
                    throw IllegalStateException("HTTP $code")
                }
                val total = connection.contentLengthLong
                connection.inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        val buffer = ByteArray(16 * 1024)
                        var read: Int
                        var written = 0L
                        while (input.read(buffer).also { read = it } != -1) {
                            if (shouldCancel()) {
                                file.delete()
                                main.post { onResult(false, "Unduhan dibatalkan") }
                                return@Thread
                            }
                            output.write(buffer, 0, read)
                            written += read
                            if (total > 0) {
                                val fraction = (written.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                                main.post { onProgress(fraction) }
                            }
                        }
                    }
                }
            }
            if (!checksumMatches(request.url, file)) {
                file.delete()
                throw IllegalStateException("Checksum SHA-256 tidak cocok — unduhan dibatalkan")
            }
            if (file.length() < 1024) throw IllegalStateException("File installer kosong atau rusak")

            main.post {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                        !context.packageManager.canRequestPackageInstalls()
                    ) {
                        var settings = Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:${context.packageName}"),
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (settings.resolveActivity(context.packageManager) == null) {
                            settings = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:${context.packageName}"),
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        if (settings.resolveActivity(context.packageManager) != null) {
                            context.startActivity(settings)
                            onResult(false, "Izinkan pemasangan aplikasi tidak dikenal, lalu ketuk Lanjutkan")
                        } else {
                            onResult(false, "Izinkan Install unknown apps untuk ${BuildConfig.APP_NAME}, lalu ketuk Lanjutkan")
                        }
                        return@post
                    }
                    val uri: Uri = FileProvider.getUriForFile(
                        context, "${context.packageName}.fileprovider", file,
                    )
                    @Suppress("DEPRECATION")
                    val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                        .setDataAndType(uri, "application/vnd.android.package-archive")
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    onResult(true, "Installer dibuka — konfirmasi pemasangan diperlukan")
                } catch (t: Throwable) {
                    onResult(false, "Gagal membuka installer: ${t.message ?: "unknown"}")
                }
            }
        } catch (t: Throwable) {
            main.post { onResult(false, "Gagal mengunduh: ${t.message ?: "unknown"}") }
        } finally {
            connection?.disconnect()
        }
    }.start()
}
