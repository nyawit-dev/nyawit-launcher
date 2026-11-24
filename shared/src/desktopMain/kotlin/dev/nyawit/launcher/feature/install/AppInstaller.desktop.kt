package dev.nyawit.launcher.feature.install

import dev.nyawit.launcher.core.platform.checksumMatches
import dev.nyawit.launcher.core.platform.launcherHome
import java.awt.Desktop
import java.awt.EventQueue
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private val NATIVE_EXTENSIONS = setOf("msi", "exe", "dmg", "pkg", "deb", "rpm", "apk")

/** Menyerahkan file installer ke sistem operasi (msiexec, open, xdg-open). */
private fun openNativeInstaller(file: File) {
    val os = System.getProperty("os.name").lowercase()
    val ext = file.extension.lowercase()
    when {
        os.contains("win") && ext == "msi" ->
            ProcessBuilder("msiexec", "/i", file.absolutePath, "/norestart", "/passive").start()
        os.contains("win") && ext == "exe" -> ProcessBuilder(file.absolutePath).start()
        os.contains("mac") -> ProcessBuilder("open", file.absolutePath).start()
        os.contains("linux") -> ProcessBuilder("xdg-open", file.absolutePath).start()
        else -> Desktop.getDesktop().open(file)
    }
}

actual fun installApp(
    request: InstallRequest,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
) {
    val safeName = request.fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
    val ext = safeName.substringAfterLast('.', "").lowercase()

    // Tolak jika file berupa JAR atau bukan installer native resmi
    if (ext == "jar" || safeName.endsWith(".jar", true)) {
        onResult(
            false,
            "File .jar tidak didukung. Nyawit Launcher hanya memasang installer native platform (.msi, .exe, .dmg, .pkg, .deb, .rpm)."
        )
        return
    }

    if (ext !in NATIVE_EXTENSIONS) {
        onResult(
            false,
            "Ekstensi file '$ext' tidak didukung. Hanya installer native (.msi, .exe, .dmg, .pkg, .deb, .rpm) yang didukung."
        )
        return
    }

    Thread {
        var connection: HttpURLConnection? = null
        val downloadDir = launcherHome().resolve("downloads").apply { mkdirs() }
        val temp = File(downloadDir, "$safeName.part")
        try {
            connection = URI(request.url).toURL().openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 15000
            connection.readTimeout = 60000
            val code = connection.responseCode
            if (code !in 200..299) {
                throw IllegalStateException("HTTP $code")
            }
            connection.inputStream.use { input ->
                FileOutputStream(temp).use { output ->
                    val buffer = ByteArray(16 * 1024)
                    var read: Int
                    var written = 0L
                    val total = connection.contentLengthLong
                    while (input.read(buffer).also { read = it } != -1) {
                        if (shouldCancel()) {
                            temp.delete()
                            EventQueue.invokeLater { onResult(false, "Dibatalkan") }
                            return@Thread
                        }
                        output.write(buffer, 0, read)
                        written += read
                        if (total > 0) {
                            val p = (written.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                            EventQueue.invokeLater { onProgress(p) }
                        }
                    }
                }
            }
            if (!checksumMatches(request.url, temp)) {
                throw IllegalStateException("Checksum SHA-256 tidak cocok — unduhan dibatalkan")
            }

            // Installer native (.msi/.exe/.deb/.rpm/.dmg/.pkg/.apk): serahkan ke OS.
            if (temp.length() < 1024) throw IllegalStateException("File installer kosong atau rusak")
            val installer = File(downloadDir, safeName)
            Files.move(temp.toPath(), installer.toPath(), StandardCopyOption.REPLACE_EXISTING)
            openNativeInstaller(installer)
            EventQueue.invokeLater {
                onResult(
                    true,
                    if (request.restartAfterInstall) {
                        "Installer native ${installer.name} dibuka — selesaikan pembaruan lalu restart launcher"
                    } else {
                        "Installer native ${installer.name} dibuka — ikuti proses pemasangan"
                    },
                )
            }
        } catch (t: Throwable) {
            temp.delete()
            EventQueue.invokeLater { onResult(false, "Gagal: ${t.message ?: "unknown"}") }
        } finally {
            connection?.disconnect()
        }
    }.start()
}
