package dev.nyawit.launcher.feature.install

import dev.nyawit.launcher.BuildConfig
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

private fun safeFileName(fileName: String) =
    fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")

private fun downloadDir(): File = launcherHome().resolve("downloads").apply { mkdirs() }

actual fun downloadedInstallerPath(request: InstallRequest): String =
    File(downloadDir(), safeFileName(request.fileName)).absolutePath

/** Menjalankan installer native. Di Windows dijalankan silent agar langsung terpasang. */
private fun openNativeInstaller(file: File) {
    val os = System.getProperty("os.name").lowercase()
    val ext = file.extension.lowercase()

    fun launch(vararg command: String) {
        try {
            ProcessBuilder(*command).start()
        } catch (_: Throwable) {
            // Fallback bila perintah tidak tersedia: biarkan asosiasi file yang membuka installer.
            Desktop.getDesktop().open(file)
        }
    }

    when {
        // /quiet: tanpa wizard, /norestart: jangan reboot otomatis.
        os.contains("win") && ext == "msi" ->
            launch("msiexec", "/i", file.absolutePath, "/quiet", "/norestart")
        // Akhiran NSIS (/S) dan Inno Setup (/VERYSILENT) diabaikan bila tidak didukung.
        os.contains("win") && ext == "exe" ->
            launch(file.absolutePath, "/S", "/VERYSILENT", "/NORESTART")
        os.contains("mac") -> launch("open", file.absolutePath)
        os.contains("linux") -> launch("xdg-open", file.absolutePath)
        else -> Desktop.getDesktop().open(file)
    }
}

actual fun installApp(
    request: InstallRequest,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
) {
    val safeName = safeFileName(request.fileName)
    val ext = safeName.substringAfterLast('.', "").lowercase()

    // Tolak jika file berupa JAR atau bukan installer native resmi
    if (ext == "jar" || safeName.endsWith(".jar", true)) {
        onResult(
            false,
            "File .jar tidak didukung. ${BuildConfig.APP_NAME} hanya memasang installer native platform (.msi, .exe, .dmg, .pkg, .deb, .rpm)."
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
        try {
            val installer = File(downloadDir(), safeName)
            val reusable = request.filePath?.let(::File)
                ?.takeIf { it.isFile && it.length() > 1024 }

            if (reusable == null) {
                val temp = File(downloadDir(), "$safeName.part")
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
                if (temp.length() < 1024) throw IllegalStateException("File installer kosong atau rusak")
                Files.move(temp.toPath(), installer.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }

            val target = reusable ?: installer
            if (!checksumMatches(request.url, target)) {
                target.delete()
                throw IllegalStateException("Checksum SHA-256 tidak cocok — unduhan dibatalkan")
            }

            // Installer native (.msi/.exe/.deb/.rpm/.dmg/.pkg/.apk): serahkan ke OS.
            openNativeInstaller(target)
            EventQueue.invokeLater {
                onResult(
                    true,
                    if (request.restartAfterInstall) {
                        "Pembaruan ${target.name} sedang dipasang otomatis — restart launcher setelah selesai"
                    } else {
                        "Installer ${target.name} sedang dipasang otomatis — tunggu hingga selesai"
                    },
                )
            }
        } catch (t: Throwable) {
            EventQueue.invokeLater { onResult(false, "Gagal membuka installer: ${t.message ?: "unknown"}") }
        } finally {
            connection?.disconnect()
        }
    }.start()
}
