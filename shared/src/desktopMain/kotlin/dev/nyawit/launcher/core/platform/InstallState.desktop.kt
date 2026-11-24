package dev.nyawit.launcher.core.platform

actual fun installedVersion(
    packageName: String,
    platformContext: Any?,
    displayName: String?,
): String? {
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
