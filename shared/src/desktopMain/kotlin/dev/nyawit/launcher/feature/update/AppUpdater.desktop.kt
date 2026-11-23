package dev.nyawit.launcher.feature.update

import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.exitProcess

actual fun applyUpdate(url: String, fileName: String, platformContext: Any?) {
    Thread {
        try {
            val dir = File(System.getProperty("user.home"), ".nyawit-launcher/updates")
            dir.mkdirs()
            val file = File(dir, fileName)
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 15000
            connection.inputStream.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            val java = File(System.getProperty("java.home"), "bin/java${if (System.getProperty("os.name").lowercase().contains("win")) ".exe" else ""}").absolutePath
            ProcessBuilder(java, "-jar", file.absolutePath).inheritIO().start()
            exitProcess(0)
        } catch (_: Throwable) { }
    }.start()
}
