package dev.nyawit.launcher.feature.install

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

actual fun installApp(
    url: String,
    fileName: String,
    platformContext: Any?,
    registryKey: String,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
) {
    val context = platformContext as? Context ?: run {
        onResult(false, "Konteks Android tidak tersedia")
        return
    }
    val main = Handler(Looper.getMainLooper())
    Thread {
        val file = File(context.filesDir, fileName)
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = true
            if (connection.responseCode !in 200..299) {
                connection.disconnect()
                throw IllegalStateException("HTTP ${connection.responseCode}")
            }
            connection.connectTimeout = 15000
            connection.readTimeout = 60000
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
                            onResult(false, "Izinkan pemasangan aplikasi tidak dikenal, lalu coba lagi")
                        } else {
                            onResult(false, "Izinkan Install unknown apps untuk Nyawit Launcher")
                        }
                        return@post
                    }
                    val uri: Uri = FileProvider.getUriForFile(
                        context, "${context.packageName}.fileprovider", file,
                    )
                    val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                        .setDataAndType(uri, "application/vnd.android.package-archive")
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    onResult(true, "Installer dibuka — konfirmasi pemasangan diperlukan")
                } catch (t: Throwable) {
                    file.delete()
                    onResult(false, "Gagal membuka installer: ${t.message ?: "unknown"}")
                }
            }
        } catch (t: Throwable) {
            file.delete()
            main.post { onResult(false, "Gagal mengunduh: ${t.message ?: "unknown"}") }
        } finally {
            // connection disconnected implicitly by stream close; keep file for retry on validation errors
        }
    }.start()
}
