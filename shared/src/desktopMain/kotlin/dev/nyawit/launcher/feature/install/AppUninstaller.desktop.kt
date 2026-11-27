package dev.nyawit.launcher.feature.install

import dev.nyawit.launcher.core.platform.DesktopLog
import dev.nyawit.launcher.core.platform.findNativeApp
import dev.nyawit.launcher.core.platform.invalidateNativeRegistryCache
import dev.nyawit.launcher.core.platform.removeInstalledRecord
import dev.nyawit.launcher.core.platform.terminateAppProcesses
import java.awt.EventQueue
import java.io.File
import java.util.concurrent.TimeUnit

actual fun uninstallApp(
    packageName: String,
    displayName: String,
    platformContext: Any?,
    onResult: (Boolean, String) -> Unit,
) {
    Thread {
        val os = System.getProperty("os.name").lowercase()
        val info = findNativeApp(displayName, packageName)
        DesktopLog.debug("Uninstall", "Hapus '$displayName' ($packageName): ${info?.executable ?: "tidak ditemukan"}")

        // 1. Matikan semua proses aplikasi yang sedang berjalan agar file tidak terkunci oleh Windows,
        // menghilangkan kebutuhan restart perangkat secara total.
        terminateAppProcesses(info?.executable, info?.installLocation, displayName)

        var ok = false
        var message = "$displayName tidak ditemukan di daftar aplikasi terpasang"

        try {
            when {
                os.contains("win") && (info?.uninstallString != null || info?.quietUninstallString != null) -> {
                    val quietCmd = info.quietUninstallString?.trim()
                    val rawCmd = info.uninstallString?.trim() ?: quietCmd.orEmpty()
                    val isMsi = rawCmd.contains("msiexec", ignoreCase = true) || (quietCmd?.contains("msiexec", ignoreCase = true) == true)

                    if (isMsi) {
                        // Ambil Product GUID: misal /I{GUID} atau /X{GUID} atau {GUID}
                        val guidMatch = Regex("\\{[A-Fa-f0-9\\-]{36}\\}").find(rawCmd)
                            ?: quietCmd?.let { Regex("\\{[A-Fa-f0-9\\-]{36}\\}").find(it) }
                        val guid = guidMatch?.value

                        if (guid != null) {
                            DesktopLog.debug("Uninstall", "Uninstall MSI per-machine untuk GUID: $guid")
                            // Selalu pasang REBOOT=ReallySuppress dan /norestart agar restart perangkat TIDAK PERNAH terjadi!
                            val psSilent = "Start-Process -FilePath 'msiexec.exe' -ArgumentList @('/x', '$guid', '/qn', '/norestart', 'REBOOT=ReallySuppress') -Verb RunAs -Wait -PassThru"
                            var exitCode = runPowershell(psSilent)
                            DesktopLog.debug("Uninstall", "Hasil MSI silent elevated exit code: $exitCode")

                            // 0 = sukses, 3010 = sukses (reboot disupresi), 1605 = sudah terhapus
                            if (exitCode == 0 || exitCode == 3010 || exitCode == 1605) {
                                ok = true
                                message = "$displayName berhasil dihapus"
                            } else {
                                // Fallback: antarmuka wizard MSI dengan elevasi, tetap menyupresi reboot
                                val psGui = "Start-Process -FilePath 'msiexec.exe' -ArgumentList @('/x', '$guid', '/norestart', 'REBOOT=ReallySuppress') -Verb RunAs -Wait -PassThru"
                                exitCode = runPowershell(psGui)
                                ok = (exitCode == 0 || exitCode == 3010 || exitCode == 1605)
                                message = if (ok) "$displayName berhasil dihapus" else "Penghapusan $displayName selesai (kode: $exitCode)"
                            }
                        } else {
                            val cmd = if (rawCmd.contains("msiexec", true)) {
                                rawCmd.replace(Regex("/[iI](?=\\{)"), "/X") + " /norestart REBOOT=ReallySuppress"
                            } else {
                                rawCmd
                            }
                            val exit = runPowershell("Start-Process cmd.exe -ArgumentList @('/c', '$cmd') -Verb RunAs -Wait -PassThru")
                            ok = exit == 0 || exit == 3010
                            message = if (ok) "$displayName berhasil dihapus" else "Proses penghapusan $displayName gagal"
                        }
                    } else {
                        // Uninstaller berbasis executable (.exe) seperti Inno Setup, NSIS, atau Electron
                        val targetCmd = quietCmd ?: rawCmd
                        DesktopLog.debug("Uninstall", "Menjalankan uninstaller EXE: $targetCmd")

                        val finalArgs = if (targetCmd.contains("unins", ignoreCase = true) &&
                            !targetCmd.contains("/silent", ignoreCase = true) &&
                            !targetCmd.contains("/verysilent", ignoreCase = true)
                        ) {
                            "$targetCmd /VERYSILENT /NORESTART /SUPPRESSMSGBOXES"
                        } else {
                            targetCmd
                        }

                        val needsElevation = (info.installLocation?.contains("Program Files", ignoreCase = true) == true) ||
                            (info.executable?.contains("Program Files", ignoreCase = true) == true) ||
                            targetCmd.contains("Program Files", ignoreCase = true)

                        val verbClause = if (needsElevation) "-Verb RunAs" else ""
                        val psCmd = "Start-Process cmd.exe -ArgumentList @('/c', '$finalArgs') $verbClause -Wait -PassThru"
                        val exitCode = runPowershell(psCmd)
                        DesktopLog.debug("Uninstall", "Hasil uninstaller EXE exit code: $exitCode")

                        ok = exitCode == 0 || exitCode == 3010
                        message = if (ok) "$displayName berhasil dihapus" else "Proses penghapusan $displayName selesai (kode: $exitCode)"
                    }

                    // Bersihkan direktori kosong yang ditinggalkan di folder instalasi
                    val loc = info.installLocation?.let(::File)
                    if (loc != null && loc.isDirectory) {
                        try {
                            val remaining = loc.walkTopDown().toList()
                            if (remaining.all { it.isDirectory || it.length() == 0L || it.name.endsWith(".log") }) {
                                loc.deleteRecursively()
                                DesktopLog.debug("Uninstall", "Membersihkan direktori kosong: ${loc.absolutePath}")
                            }
                        } catch (_: Throwable) {}
                    }
                }

                os.contains("win") -> {
                    // Fallback Windows jika tidak ada uninstall string di registry
                    ProcessBuilder("control", "appwiz.cpl").start()
                    ok = true
                    message = "Daftar aplikasi Windows dibuka — pilih $displayName untuk menghapus"
                }

                os.contains("mac") && info?.executable != null -> {
                    val app = File(info.executable)
                    if (app.exists()) {
                        try {
                            ProcessBuilder(
                                "osascript", "-e",
                                "tell application \"Finder\" to delete POSIX file \"${app.absolutePath}\"",
                            ).start().waitFor(30, TimeUnit.SECONDS)
                            ok = true
                            message = "$displayName dipindahkan ke Tempat Sampah"
                        } catch (_: Throwable) {
                            ok = app.deleteRecursively()
                            message = if (ok) "$displayName dihapus" else "Gagal menghapus $displayName"
                        }
                    } else {
                        ok = true
                        message = "$displayName telah dihapus"
                    }
                }

                else -> {
                    val token = packageName.substringAfterLast('.').lowercase()
                        .replace(Regex("[^a-z0-9]"), "-")
                    val process = ProcessBuilder("pkexec", "dpkg", "-r", token).start()
                    val finished = process.waitFor(60, TimeUnit.SECONDS)
                    ok = finished && process.exitValue() == 0
                    message = if (ok) "$displayName berhasil dihapus" else "Gagal menghapus $displayName"
                }
            }
        } catch (t: Throwable) {
            message = "Gagal menghapus: ${t.message ?: "unknown"}"
            DesktopLog.error("Uninstall", "Gagal menghapus '$displayName'", t)
        }

        // Hapus cache registry agar status langsung terbarui tanpa restart
        invalidateNativeRegistryCache()
        if (ok) {
            removeInstalledRecord(packageName)
            cleanupDownloadedInstallersForApp(displayName, packageName)
        }

        DesktopLog.debug("Uninstall", "Hasil '$displayName': ok=$ok, $message")
        EventQueue.invokeLater { onResult(ok, message) }
    }.start()
}

private fun runPowershell(command: String): Int = try {
    val process = ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command", command)
        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
        .redirectError(ProcessBuilder.Redirect.DISCARD)
        .start()
    val finished = process.waitFor(5, TimeUnit.MINUTES)
    if (finished) process.exitValue() else {
        process.destroyForcibly()
        -1
    }
} catch (_: Throwable) {
    -1
}
