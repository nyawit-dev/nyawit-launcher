package dev.nyawit.launcher.core.platform

import android.content.Context
import java.io.File

private fun cacheFile(context: Context?): File? =
    context?.filesDir?.let { File(it, "catalog.json") }

actual fun readCatalogCache(platformContext: Any?): String? = try {
    cacheFile(platformContext as? Context)?.takeIf { it.isFile }?.readText()?.takeIf { it.isNotBlank() }
} catch (_: Throwable) {
    null
}

actual fun writeCatalogCache(content: String, platformContext: Any?) {
    try {
        val file = cacheFile(platformContext as? Context) ?: return
        val temp = File(file.parentFile, "catalog.json.tmp")
        temp.writeText(content)
        if (file.exists()) file.delete()
        temp.renameTo(file)
    } catch (_: Throwable) {
        // Abaikan kegagalan cache.
    }
}

actual fun clearCatalogCache(platformContext: Any?): Boolean = try {
    val file = cacheFile(platformContext as? Context)
    if (file != null && file.exists()) file.delete() else true
} catch (_: Throwable) {
    false
}
