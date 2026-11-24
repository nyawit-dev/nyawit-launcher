package dev.nyawit.launcher.core.platform

import dev.nyawit.launcher.BuildConfig

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
    val native = findNativeApp(displayName ?: packageName, packageName) ?: return false
    return launchNativeApp(native)
}
