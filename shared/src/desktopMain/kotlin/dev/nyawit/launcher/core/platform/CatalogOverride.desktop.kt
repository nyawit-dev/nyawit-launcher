package dev.nyawit.launcher.core.platform

import java.io.File

actual fun localCatalogOverride(): String? {
    var dir: File? = File(System.getProperty("user.dir"))
    repeat(6) {
        val candidate = dir?.resolve("catalog.json")
        if (candidate != null && candidate.isFile) {
            return runCatching { candidate.readText() }.getOrNull()
        }
        dir = dir?.parentFile
    }
    return null
}
