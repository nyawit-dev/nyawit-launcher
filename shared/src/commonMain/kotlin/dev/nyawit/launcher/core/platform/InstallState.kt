package dev.nyawit.launcher.core.platform

expect fun installedVersion(
    packageName: String,
    platformContext: Any?,
    displayName: String? = null,
): String?

expect fun launchInstalledApp(
    packageName: String,
    platformContext: Any?,
    displayName: String? = null,
): Boolean

/** Alasan kegagalan `launchInstalledApp` terakhir, atau null bila berhasil. */
expect fun lastLaunchError(): String?
