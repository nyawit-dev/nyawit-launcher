package dev.nyawit.launcher.core.platform

import dev.nyawit.launcher.BuildConfig

@Volatile
private var lastError: String? = null

actual fun lastLaunchError(): String? = lastError

actual fun installedVersion(
    packageName: String,
    platformContext: Any?,
    displayName: String?,
): String? {
    // Versi launcher sendiri harus mengikuti biner yang sedang berjalan.
    // Registry lama (mis. sisa installer JAR 1.2.7) tidak boleh menimpa versi build.
    if (packageName == BuildConfig.APP_ID) return BuildConfig.VERSION
    readRegistry(packageName).firstOrNull()?.let { return it }
    return findNativeApp(displayName ?: packageName, packageName)?.version
}

actual fun launchInstalledApp(
    packageName: String,
    platformContext: Any?,
    displayName: String?,
): Boolean {
    lastError = null
    val label = displayName?.takeIf { it.isNotBlank() } ?: packageName
    val native = findNativeApp(label, packageName)
    if (native == null) {
        lastError = "tidak ditemukan di daftar aplikasi terpasang"
        DesktopLog.warn("Launch", "Gagal membuka '$label': $lastError")
        return false
    }
    if (!launchNativeApp(native)) {
        lastError = native.executable?.let { "gagal menjalankan $it" }
            ?: "path executable tidak tersedia"
        DesktopLog.warn("Launch", "Gagal membuka '$label': $lastError")
        return false
    }
    DesktopLog.debug("Launch", "'$label' berhasil dibuka")
    return true
}
