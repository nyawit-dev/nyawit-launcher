package dev.nyawit.launcher.core.platform

import android.content.Context
import java.io.File

private fun settingsFile(context: Context?): File? =
    context?.filesDir?.let { File(it, "settings.json") }

actual fun readLauncherSettings(platformContext: Any?): String? = try {
    settingsFile(platformContext as? Context)?.takeIf { it.isFile }?.readText()?.takeIf { it.isNotBlank() }
} catch (_: Throwable) {
    null
}

actual fun writeLauncherSettings(content: String, platformContext: Any?) {
    try {
        val file = settingsFile(platformContext as? Context) ?: return
        val temp = File(file.parentFile, "settings.json.tmp")
        temp.writeText(content)
        if (file.exists()) file.delete()
        temp.renameTo(file)
    } catch (_: Throwable) {
        // Abaikan kegagalan tulis pengaturan
    }
}
