package dev.nyawit.launcher.feature.catalog.ui

import android.content.Context
import android.content.Intent
import android.net.Uri

actual fun openUrl(url: String, platformContext: Any?) {
    val context = platformContext as? Context ?: return
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}
