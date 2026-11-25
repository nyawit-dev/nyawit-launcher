package dev.nyawit.launcher.core.platform

import java.io.File

private fun settingsFile(): File =
    launcherHome().resolve("settings.json").apply { parentFile.mkdirs() }

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
    } catch (_: Throwable) {
        // Abaikan kegagalan tulis pengaturan
    }
}
