package dev.nyawit.launcher.feature.install

import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.platform.DesktopLog
import dev.nyawit.launcher.core.platform.checksumMatches
import dev.nyawit.launcher.core.platform.invalidateNativeRegistryCache
import dev.nyawit.launcher.core.platform.launcherHome
import dev.nyawit.launcher.core.platform.terminateAppProcesses
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

internal fun downloadDir(): File = launcherHome().resolve("downloads").apply { mkdirs() }

actual fun downloadedInstallerPath(request: InstallRequest): String =
    File(downloadDir(), safeFileName(request.fileName)).absolutePath

private enum class InstallerLaunch {
    /** Installer selesai dipasang tanpa interaksi (silent). */
    SILENT,
    /** Installer dibuka; pengguna menyelesaikan wizard pemasangan. */
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

private fun runPowershell(command: String, timeoutMinutes: Long = 5): Int = try {
    val process = ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command", command)
        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
        .redirectError(ProcessBuilder.Redirect.DISCARD)
        .start()
    val finished = process.waitFor(timeoutMinutes, TimeUnit.MINUTES)
    if (finished) process.exitValue() else {
        process.destroyForcibly()
        -1
    }
} catch (_: Throwable) {
    -1
}

private fun runPowershellAsync(command: String): Boolean = try {
    ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command", command)
        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
        .redirectError(ProcessBuilder.Redirect.DISCARD)
        .start()
    true
} catch (_: Throwable) {
    false
}

/**
 * Menjalankan installer native di desktop.
 * Di Windows:
 * - MSI: dijalankan elevated dengan REBOOT=ReallySuppress /norestart.
 * - EXE: dicoba silent dengan flags non-konflik, jika interaktif langsung dibuka tanpa memblokir thread.
 */
private fun openNativeInstaller(file: File): InstallerLaunch {
    val os = System.getProperty("os.name").lowercase()
    val ext = file.extension.lowercase()
    val path = file.absolutePath.replace("'", "''")

    return when {
        os.contains("win") && ext == "msi" -> {
            // Coba silent elevated MSI terlebih dahulu dengan menyupresi reboot secara total
            DesktopLog.debug("Install", "Menjalankan MSI installer elevated silent: $path")
            val psSilent = "Start-Process -FilePath 'msiexec.exe' -ArgumentList @('/i', '$path', '/qn', '/norestart', 'REBOOT=ReallySuppress') -Verb RunAs -Wait -PassThru"
            val exitCode = runPowershell(psSilent, timeoutMinutes = 5)
            DesktopLog.debug("Install", "Hasil MSI silent exit code: $exitCode")

            if (exitCode == 0 || exitCode == 3010 || exitCode == 1641) {
                runCatching { file.delete() }
                InstallerLaunch.SILENT
            } else {
                // Fallback: Luncurkan wizard MSI elevated interaktif tanpa memblokir thread 10 menit.
                // Hapus berkas installer otomatis di latar belakang setelah wizard ditutup pengguna.
                DesktopLog.debug("Install", "Membuka wizard MSI interaktif elevated: $path")
                val psGui = "Start-Process -FilePath 'msiexec.exe' -ArgumentList @('/i', '$path', '/norestart', 'REBOOT=ReallySuppress') -Verb RunAs -Wait; Remove-Item -LiteralPath '$path' -Force -ErrorAction SilentlyContinue"
                if (runPowershellAsync(psGui)) InstallerLaunch.MANUAL else openWithDesktop(file)
            }
        }

        os.contains("win") && ext == "exe" -> {
            DesktopLog.debug("Install", "Menjalankan EXE installer: $path")
            // Cek tipe installer: Inno Setup vs NSIS
            val isLikelyInno = file.name.contains("setup", ignoreCase = true)
            val psSilent = if (isLikelyInno) {
                "Start-Process -FilePath '$path' -ArgumentList @('/VERYSILENT', '/NORESTART', '/SP-', '/SUPPRESSMSGBOXES') -Wait -PassThru"
            } else {
                "Start-Process -FilePath '$path' -ArgumentList @('/S', '/NORESTART') -Wait -PassThru"
            }
            val exitCode = runPowershell(psSilent, timeoutMinutes = 3)
            DesktopLog.debug("Install", "Hasil EXE silent exit code: $exitCode")

            if (exitCode == 0 || exitCode == 3010) {
                runCatching { file.delete() }
                InstallerLaunch.SILENT
            } else {
                // Luncurkan wizard EXE interaktif dengan elevasi jika diperlukan.
                // Hapus berkas installer otomatis di latar belakang setelah wizard ditutup pengguna.
                val psGui = "Start-Process -FilePath '$path' -Verb RunAs -Wait; Remove-Item -LiteralPath '$path' -Force -ErrorAction SilentlyContinue"
                if (runPowershellAsync(psGui)) InstallerLaunch.MANUAL else openWithDesktop(file)
            }
        }

        os.contains("linux") && ext == "deb" -> {
            val process = try {
                ProcessBuilder("pkexec", "dpkg", "-i", file.absolutePath).start()
            } catch (_: Throwable) { null }
            val ok = process?.waitFor(60, TimeUnit.SECONDS) == true && process.exitValue() == 0
            if (ok) {
                runCatching { file.delete() }
                InstallerLaunch.SILENT
            } else openWithDesktop(file)
        }

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
                connection.connectTimeout = 10000
                connection.readTimeout = 30000
                val code = connection.responseCode
                if (code !in 200..299) {
                    throw IllegalStateException("HTTP $code")
                }
                connection.inputStream.use { input ->
                    FileOutputStream(temp).use { output ->
                        val buffer = ByteArray(32 * 1024)
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
                            } else {
                                // Jika ukuran tidak dilaporkan server (chunked), tampilkan progres dinamis agar UI aktif
                                val mb = written / (1024 * 1024f)
                                val p = (mb / (mb + 10f)).coerceIn(0.05f, 0.95f)
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

            // Hentikan proses lama aplikasi ini jika sedang berjalan agar file biner tidak terkunci saat pembaruan/instalasi
            val targetAppName = request.appName ?: safeName.substringBefore('-').substringBefore('.')
            terminateAppProcesses(null, null, targetAppName)

            val os = System.getProperty("os.name").lowercase()
            // Jika ini pembaruan mandiri launcher sendiri (self-update):
            // Proses launcher yang sedang berjalan saat ini mengunci berkas .cfg (nyawit-launcher.cfg) dan runtime.
            // Gunakan skrip terpisah yang memberi jeda agar proses ini keluar terlebih dahulu sebelum installer menulis berkas .cfg.
            if (request.restartAfterInstall && os.contains("win")) {
                val pid = ProcessHandle.current().pid()
                val scriptFile = File(System.getProperty("java.io.tmpdir"), "nyawit_self_update.bat")
                val installerPath = target.absolutePath.replace("'", "''")
                val isMsi = target.extension.equals("msi", ignoreCase = true)
                val installCmd = if (isMsi) {
                    "msiexec.exe /i \"$installerPath\" /qn /norestart REBOOT=ReallySuppress"
                } else {
                    "\"$installerPath\" /VERYSILENT /NORESTART"
                }

                scriptFile.writeText(
                    "@echo off\r\n" +
                    "timeout /t 2 /nobreak > nul\r\n" +
                    "taskkill /F /PID $pid > nul 2>&1\r\n" +
                    "$installCmd\r\n" +
                    "del \"$installerPath\" > nul 2>&1\r\n" +
                    "del \"%~f0\" > nul 2>&1\r\n" +
                    "exit\r\n"
                )

                DesktopLog.debug("Install", "Meluncurkan skrip self-update launcher: ${scriptFile.absolutePath}")
                ProcessBuilder("cmd.exe", "/c", "start", "/min", scriptFile.absolutePath).start()

                EventQueue.invokeLater {
                    onResult(true, "Memasang pembaruan launcher... Aplikasi akan dimulai ulang.")
                }
                Thread {
                    Thread.sleep(800)
                    kotlin.system.exitProcess(0)
                }.start()
                return@Thread
            }

            // Jalankan installer native (.msi/.exe/.deb/.rpm/.dmg/.pkg/.apk)
            val launch = openNativeInstaller(target)
            invalidateNativeRegistryCache()
            DesktopLog.debug("Install", "Hasil peluncuran installer ${target.name}: $launch")

            when (launch) {
                InstallerLaunch.SILENT -> {
                    // Berkas installer telah terpasang secara silent, hapus untuk menghemat ruang disk
                    runCatching { target.delete() }
                    EventQueue.invokeLater {
                        onResult(
                            true,
                            if (request.restartAfterInstall) {
                                "Pembaruan ${target.name} berhasil dipasang — restart launcher untuk menerapkannya"
                            } else {
                                "${target.name} berhasil dipasang"
                            },
                        )
                    }
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

actual fun cleanupDownloadedFiles(platformContext: Any?) {
    try {
        val dir = downloadDir()
        val files = dir.listFiles() ?: return
        val now = System.currentTimeMillis()
        for (file in files) {
            // Hapus file part yang usang (> 30 menit)
            if (file.name.endsWith(".part") && (now - file.lastModified() > 30 * 60 * 1000L)) {
                file.delete()
                continue
            }
            // Hapus installer launcher versi lama
            val nameLower = file.name.lowercase()
            if (nameLower.contains("nyawit-launcher") || nameLower.contains("nyawitlauncher")) {
                if (!nameLower.contains(BuildConfig.VERSION.lowercase())) {
                    file.delete()
                }
            }
        }
    } catch (_: Throwable) {}
}

fun cleanupDownloadedInstallersForApp(displayName: String, packageName: String) {
    runCatching {
        val dir = downloadDir()
        val nameClean = displayName.lowercase().replace(Regex("[^a-z0-9]"), "")
        val pkgClean = packageName.substringAfterLast('.').lowercase().replace(Regex("[^a-z0-9]"), "")
        dir.listFiles()?.filter { file ->
            val f = file.name.lowercase().replace(Regex("[^a-z0-9]"), "")
            (nameClean.isNotBlank() && f.contains(nameClean)) || (pkgClean.isNotBlank() && f.contains(pkgClean))
        }?.forEach { it.delete() }
    }
}
