package dev.nyawit.launcher.feature.install

import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.platform.checksumMatches
import dev.nyawit.launcher.core.platform.DesktopLog
import dev.nyawit.launcher.core.platform.launcherHome
import java.awt.Desktop
import java.awt.EventQueue
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit

private val NATIVE_EXTENSIONS = setOf("msi", "exe", "dmg", "pkg", "deb", "rpm", "apk")

private fun safeFileName(fileName: String) =
    fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")

private fun downloadDir(): File = launcherHome().resolve("downloads").apply { mkdirs() }

actual fun downloadedInstallerPath(request: InstallRequest): String =
    File(downloadDir(), safeFileName(request.fileName)).absolutePath

private enum class InstallerLaunch {
    /** Installer selesai dipasang tanpa interaksi (silent). */
    SILENT,
    /** Installer dibuka; pengguna harus menyelesaikan konfirmasi. */
    MANUAL,
    /** Installer gagal dibuka sama sekali. */
    FAILED,
}

private fun openWithDesktop(file: File): InstallerLaunch = try {
    Desktop.getDesktop().open(file)
    InstallerLaunch.MANUAL
} catch (_: Throwable) {
    InstallerLaunch.FAILED
}

/**
 * Installer per-machine (MSI jpackage) menolak install tanpa hak admin (Error 1925).
 * ProcessBuilder tidak bisa memicu UAC, jadi minta ShellExecute via PowerShell dengan verb RunAs.
 */
private fun runElevated(file: File): InstallerLaunch {
    val isMsi = file.extension.equals("msi", true)
    val command = if (isMsi) {
        "Start-Process -FilePath 'msiexec' -ArgumentList @('/i', '${file.absolutePath}', '/norestart') -Verb RunAs -Wait"
    } else {
        "Start-Process -FilePath '${file.absolutePath}' -Verb RunAs -Wait"
    }
    val process = try {
        ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command", command)
            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
            .redirectError(ProcessBuilder.Redirect.DISCARD)
            .start()
    } catch (_: Throwable) {
        return InstallerLaunch.FAILED
    }
    val finished = try {
        process.waitFor(10, TimeUnit.MINUTES)
    } catch (_: Throwable) {
        false
    }
    if (!finished) {
        process.destroyForcibly()
        return InstallerLaunch.FAILED
    }
    return if (process.exitValue() == 0) InstallerLaunch.MANUAL else InstallerLaunch.FAILED
}

private fun fallbackInstaller(file: File): InstallerLaunch {
    val os = System.getProperty("os.name").lowercase()
    return if (os.contains("win")) runElevated(file) else openWithDesktop(file)
}

private fun silentSucceeded(exitCode: Int, ext: String): Boolean = when (ext) {
    // MSI: 0 sukses, 3010 sukses tapi perlu restart, 1641 sukses + restart dimulai.
    "msi" -> exitCode == 0 || exitCode == 3010 || exitCode == 1641
    else -> exitCode == 0
}

/** Menjalankan installer silent dan menunggu hasilnya. Fallback membuka installer bila gagal. */
private fun runSilentInstaller(command: List<String>, file: File): InstallerLaunch {
    val process = try {
        ProcessBuilder(command)
            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
            .redirectError(ProcessBuilder.Redirect.DISCARD)
            .start()
    } catch (_: Throwable) {
        return fallbackInstaller(file)
    }
    val finished = try {
        process.waitFor(10, TimeUnit.MINUTES)
    } catch (_: Throwable) {
        false
    }
    if (!finished) {
        process.destroyForcibly()
        return fallbackInstaller(file)
    }
    return if (silentSucceeded(process.exitValue(), file.extension.lowercase())) {
        InstallerLaunch.SILENT
    } else {
        // Exit code gagal (mis. butuh hak admin) — minta elevasi UAC.
        fallbackInstaller(file)
    }
}

/** Menjalankan installer native. Di Windows dijalankan silent agar langsung terpasang. */
private fun openNativeInstaller(file: File): InstallerLaunch {
    val os = System.getProperty("os.name").lowercase()
    val ext = file.extension.lowercase()
    return when {
        // /quiet: tanpa UI, /norestart: jangan reboot otomatis.
        os.contains("win") && ext == "msi" -> runSilentInstaller(
            listOf("msiexec", "/i", file.absolutePath, "/quiet", "/norestart"), file,
        )
        // Flag tambahan diabaikan oleh installer yang tidak mendukungnya.
        os.contains("win") && ext == "exe" -> runSilentInstaller(
            listOf(file.absolutePath, "/S", "/VERYSILENT", "/NORESTART"), file,
        )
        else -> openWithDesktop(file)
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

    DesktopLog.debug("Install", "Mulai unduh: ${request.url} → $safeName")
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
            DesktopLog.debug("Install", "Unduhan selesai: ${target.absolutePath} (${target.length()} byte)")
            if (!checksumMatches(request.url, target)) {
                target.delete()
                throw IllegalStateException("Checksum SHA-256 tidak cocok — unduhan dibatalkan")
            }

            // Installer native (.msi/.exe/.deb/.rpm/.dmg/.pkg/.apk): serahkan ke OS.
            val launch = openNativeInstaller(target)
            DesktopLog.debug("Install", "Hasil peluncuran installer ${target.name}: $launch")
            when (launch) {
                InstallerLaunch.SILENT -> EventQueue.invokeLater {
                    onResult(
                        true,
                        if (request.restartAfterInstall) {
                            "Pembaruan ${target.name} berhasil dipasang — restart launcher untuk menerapkannya"
                        } else {
                            "${target.name} berhasil dipasang"
                        },
                    )
                }

                InstallerLaunch.MANUAL -> EventQueue.invokeLater {
                    onResult(
                        true,
                        "Installer ${target.name} dibuka — ikuti konfirmasi pemasangan hingga selesai",
                    )
                }

                InstallerLaunch.FAILED -> EventQueue.invokeLater {
                    onResult(false, "Gagal membuka installer ${target.name} — jalankan file dari ~/.nyawit-launcher/downloads/")
                }
            }
        } catch (t: Throwable) {
            DesktopLog.error("Install", "Gagal mengunduh/memasang ${request.fileName}", t)
            EventQueue.invokeLater { onResult(false, "Gagal membuka installer: ${t.message ?: "unknown"}") }
        } finally {
            connection?.disconnect()
        }
    }.start()
}


actual fun canInstallPackages(platformContext: Any?): Boolean = true
actual fun openInstallPermissionSettings(platformContext: Any?): Boolean = false
