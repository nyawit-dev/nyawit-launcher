package dev.nyawit.launcher.core.platform

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

internal fun resolvePackage(
    packageName: String,
    displayName: String?,
    context: Context,
): String? {
    val pm = context.packageManager
    if (runCatching { @Suppress("DEPRECATION") pm.getPackageInfo(packageName, 0) }.isSuccess) {
        return packageName
    }

    val apps = runCatching { pm.getInstalledApplications(0) }.getOrNull() ?: return null
    val wanted = displayName?.trim()?.lowercase()?.takeIf { it.length >= 3 }
    val token = packageName.substringAfterLast('.').lowercase().replace("_", "").takeIf { it.length >= 4 }

    fun labelOf(info: android.content.pm.ApplicationInfo): String =
        runCatching { pm.getApplicationLabel(info).toString().lowercase() }.getOrNull().orEmpty()

    if (wanted != null) {
        apps.firstOrNull { labelOf(it).contains(wanted) }?.let { return it.packageName }
    }
    if (token != null) {
        apps.firstOrNull {
            labelOf(it).contains(token) || it.packageName.lowercase().contains(token)
        }?.let { return it.packageName }
    }
    return null
}

actual fun installedVersion(
    packageName: String,
    platformContext: Any?,
    displayName: String?,
): String? {
    val context = platformContext as? Context ?: return null
    val resolved = resolvePackage(packageName, displayName, context) ?: return null
    return try {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(resolved, 0).versionName
    } catch (_: PackageManager.NameNotFoundException) {
        null
    }
}

actual fun launchInstalledApp(
    packageName: String,
    platformContext: Any?,
    displayName: String?,
): Boolean {
    val context = platformContext as? Context ?: return false
    val resolved = resolvePackage(packageName, displayName, context) ?: return false
    val intent = context.packageManager.getLaunchIntentForPackage(resolved) ?: return false
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return try {
        context.startActivity(intent)
        true
    } catch (_: Throwable) {
        false
    }
}
