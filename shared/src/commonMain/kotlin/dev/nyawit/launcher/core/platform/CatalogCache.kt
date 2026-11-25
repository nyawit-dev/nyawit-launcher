package dev.nyawit.launcher.core.platform

/** Cache katalog: ditulis setelah berhasil mengunduh dari GitHub, dibaca saat offline. */
expect fun readCatalogCache(platformContext: Any?): String?

expect fun writeCatalogCache(content: String, platformContext: Any?)

expect fun clearCatalogCache(platformContext: Any?): Boolean
