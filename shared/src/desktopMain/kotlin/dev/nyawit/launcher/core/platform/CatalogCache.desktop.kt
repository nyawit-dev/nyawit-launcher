package dev.nyawit.launcher.core.platform

import java.io.File

private fun cacheFile(): File =
    launcherHome().resolve("cache/catalog.json").apply { parentFile.mkdirs() }

actual fun readCatalogCache(platformContext: Any?): String? = try {
    cacheFile().takeIf { it.isFile }?.readText()?.takeIf { it.isNotBlank() }
} catch (_: Throwable) {
    null
}

actual fun writeCatalogCache(content: String, platformContext: Any?) {
    try {
        val file = cacheFile()
        val temp = File(file.parentFile, "catalog.json.tmp")
        temp.writeText(content)
        if (file.exists()) file.delete()
        temp.renameTo(file)
    } catch (_: Throwable) {
        // Cache gagal ditulis — katalog tetap dipakai dari hasil unduhan.
    }
}

actual fun clearCatalogCache(platformContext: Any?): Boolean = try {
    val file = cacheFile()
    if (file.exists()) file.delete() else true
} catch (_: Throwable) {
    false
}
