package dev.nyawit.launcher.feature.install

import dev.nyawit.launcher.core.platform.findNativeApp
import java.awt.EventQueue
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
        var ok = false
        var message = "$displayName tidak ditemukan di daftar aplikasi terpasang"

        try {
            when {
                os.contains("win") && info?.uninstallString != null -> {
                    val raw = info.uninstallString.trim()
                    val command = if (raw.contains("msiexec", true)) {
                        val silent = raw.replace(Regex("/[iI](?=\\{)"), "/X")
                        listOf("cmd", "/c", "$silent /quiet /norestart")
                    } else {
                        listOf("cmd", "/c", raw)
                    }
                    ok = ProcessBuilder(*command.toTypedArray())
                        .start().waitFor(60, TimeUnit.SECONDS)
                    message = if (ok) "$displayName dihapus" else "Proses penghapusan $displayName gagal"
                }

                os.contains("win") -> {
                    // Tidak ada UninstallString: buka Panel Kontrol agar pengguna bisa memilih sendiri.
                    ProcessBuilder("control", "appwiz.cpl").start()
                    ok = true
                    message = "Daftar aplikasi Windows dibuka — hapus $displayName dari sana"
                }

                os.contains("mac") && info?.executable != null -> {
                    val app = info.executable
                    ProcessBuilder(
                        "osascript", "-e",
                        "tell application \"Finder\" to delete POSIX file \"$app\"",
                    ).start().waitFor(30, TimeUnit.SECONDS)
                    ok = true
                    message = "$displayName dipindahkan ke Tempat Sampah"
                }

                else -> {
                    val token = packageName.substringAfterLast('.').lowercase()
                        .replace(Regex("[^a-z0-9]"), "-")
                    ok = ProcessBuilder("pkexec", "dpkg", "-r", token)
                        .start().waitFor(60, TimeUnit.SECONDS)
                    message = if (ok) "$displayName dihapus" else "Gagal menghapus $displayName"
                }
            }
        } catch (t: Throwable) {
            message = "Gagal menghapus: ${t.message ?: "unknown"}"
        }
        EventQueue.invokeLater { onResult(ok, message) }
    }.start()
}
