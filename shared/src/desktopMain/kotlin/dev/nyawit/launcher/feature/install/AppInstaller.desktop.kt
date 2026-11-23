package dev.nyawit.launcher.feature.install

import java.awt.Desktop
import java.awt.EventQueue
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import kotlin.system.exitProcess

actual fun installApp(
    url: String,
    fileName: String,
    platformContext: Any?,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
) {
    Thread {
        try {
            val dir = File(System.getProperty("user.home"), ".nyawit-launcher/updates")
            dir.mkdirs()
            val file = File(dir, fileName)
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = true
            connection.inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(16 * 1024)
                    var read: Int
                    var written = 0L
                    val total = connection.contentLengthLong
                    while (input.read(buffer).also { read = it } != -1) {
                        if (shouldCancel()) { file.delete(); EventQueue.invokeLater { onResult(false, "Dibatalkan") }; return@Thread }
                        output.write(buffer, 0, read)
                        written += read
                        if (total > 0) { val p = (written.toFloat() / total.toFloat()).coerceIn(0f, 1f); EventQueue.invokeLater { onProgress(p) } }
                    }
                }
            }
            EventQueue.invokeLater { onResult(true, "Pembaruan siap, menjalankan versi baru…") }
            val java = File(
                System.getProperty("java.home"),
                "bin/java${if (System.getProperty("os.name").lowercase().contains("win")) ".exe" else ""}",
            ).absolutePath
            ProcessBuilder(java, "-jar", file.absolutePath).inheritIO().start()
            exitProcess(0)
        } catch (t: Throwable) {
            EventQueue.invokeLater { onResult(false, "Gagal: ${t.message ?: "unknown"}") }
        }
    }.start()
}
