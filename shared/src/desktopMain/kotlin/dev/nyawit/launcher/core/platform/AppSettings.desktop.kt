package dev.nyawit.launcher.core.platform

import kotlinx.serialization.json.Json
import java.io.File

private val json = Json { ignoreUnknownKeys = true }

private fun settingsFile(): File =
    launcherHome().resolve("settings.json").apply { parentFile.mkdirs() }

private fun syncStartOnBoot(enabled: Boolean) {
    try {
        val os = System.getProperty("os.name", "").lowercase()
        if ("win" in os) {
            val appPath = System.getProperty("jpackage.app-path")
                ?: ProcessHandle.current().info().command().orElse(null)
            if (appPath != null && File(appPath).isFile) {
                val key = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run"
                if (enabled) {
                    ProcessBuilder("reg", "add", key, "/v", "NyawitLauncher", "/t", "REG_SZ", "/d", "\"$appPath\"", "/f").start()
                } else {
                    ProcessBuilder("reg", "delete", key, "/v", "NyawitLauncher", "/f").start()
                }
            }
        } else if ("linux" in os) {
            val autostartDir = File(System.getProperty("user.home"), ".config/autostart")
            val desktopFile = File(autostartDir, "nyawit-launcher.desktop")
            if (enabled) {
                autostartDir.mkdirs()
                val appPath = ProcessHandle.current().info().command().orElse("nyawit-launcher")
                desktopFile.writeText(
                    """
                    [Desktop Entry]
                    Type=Application
                    Name=Nyawit Launcher
                    Exec=$appPath
                    Hidden=false
                    NoDisplay=false
                    X-GNOME-Autostart-enabled=true
                    """.trimIndent(),
                )
            } else {
                if (desktopFile.exists()) desktopFile.delete()
            }
        }
    } catch (_: Throwable) {}
}

actual fun readLauncherSettings(platformContext: Any?): String? = try {
    settingsFile().takeIf { it.isFile }?.readText()?.takeIf { it.isNotBlank() }
} catch (_: Throwable) {
    null
}

actual fun writeLauncherSettings(content: String, platformContext: Any?) {
    try {
        val file = settingsFile()
        val temp = File(file.parentFile, "settings.json.tmp")
        temp.writeText(content)
        if (file.exists()) file.delete()
        temp.renameTo(file)

        // Sinkronkan Start on Boot ke registry / autostart OS
        try {
            val startOnBoot = json.decodeFromString<LauncherSettingsData>(content).startOnBoot
            syncStartOnBoot(startOnBoot)
        } catch (_: Throwable) {}
    } catch (_: Throwable) {
        // Abaikan kegagalan tulis pengaturan
    }
}
