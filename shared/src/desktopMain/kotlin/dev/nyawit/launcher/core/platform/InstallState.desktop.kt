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
    val native = findNativeApp(displayName ?: packageName, packageName) ?: return null
    // Pastikan jika path executable diketahui, file executable benar-benar ada di disk
    if (native.executable != null && !java.io.File(native.executable).exists()) {
        DesktopLog.debug("Registry", "Executable '${native.executable}' tidak ada di disk — dianggap belum terpasang")
        return null
    }
    return native.version ?: "1.0.0"
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
