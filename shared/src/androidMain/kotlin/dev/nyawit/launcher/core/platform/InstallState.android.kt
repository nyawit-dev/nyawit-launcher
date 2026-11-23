package dev.nyawit.launcher.core.platform

import android.content.Context
import android.content.pm.PackageManager

actual fun installedVersion(packageName: String, platformContext: Any?): String? {
    val context = platformContext as? Context ?: return null
    return try {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(packageName, 0).versionName
    } catch (_: PackageManager.NameNotFoundException) { null }
}

actual fun launchInstalledApp(packageName: String, platformContext: Any?): Boolean {
    val context = platformContext as? Context ?: return false
    val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    return try { context.startActivity(intent); true } catch (_: Throwable) { false }
}
