package dev.nyawit.launcher.core.platform

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

private fun normalize(value: String): String =
    value.lowercase().replace(Regex("[^a-z0-9]"), "")

/** Resolve an installed package without guessing from loose substring matches. */
internal fun resolvePackage(
    packageName: String,
    displayName: String?,
    context: Context,
): String? {
    val pm = context.packageManager
    // 1. Authoritative: the package id recorded in catalog.json.
    if (runCatching { @Suppress("DEPRECATION") pm.getPackageInfo(packageName, 0) }.isSuccess) {
        return packageName
    }

    val wanted = displayName?.let(::normalize)?.takeIf { it.length >= 3 } ?: return null
    val token = normalize(packageName.substringAfterLast('.'))
        .takeIf { it.length >= 4 }
        ?: normalize(displayName.orEmpty()).takeIf { it.length >= 4 }
        ?: return null

    val candidates = runCatching { pm.getInstalledApplications(0) }.getOrNull()
        ?.filter { info ->
            (info.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                normalize(runCatching { pm.getApplicationLabel(info).toString() }.getOrNull().orEmpty()) == wanted &&
                normalize(info.packageName).contains(token)
        }
        .orEmpty()

    // Ambiguous label matches must never be reported as installed.
    return candidates.singleOrNull()?.packageName
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
