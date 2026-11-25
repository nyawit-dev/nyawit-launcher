package dev.nyawit.launcher.feature.install

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import dev.nyawit.launcher.core.platform.resolvePackage

actual fun uninstallApp(
    packageName: String,
    displayName: String,
    platformContext: Any?,
    onResult: (Boolean, String) -> Unit,
) {
    val context = platformContext as? Context
    val main = Handler(Looper.getMainLooper())
    if (context == null) {
        main.post { onResult(false, "Konteks Android tidak tersedia") }
        return
    }
    val targetPackage = resolvePackage(packageName, displayName, context) ?: packageName

    main.post {
        try {
            // Intent resmi modern untuk uninstall di Android TV dan Mobile: ACTION_DELETE
            var intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$targetPackage")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (intent.resolveActivity(context.packageManager) == null) {
                // Fallback 1: Legacy ACTION_UNINSTALL_PACKAGE
                @Suppress("DEPRECATION")
                intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                    data = Uri.parse("package:$targetPackage")
                    putExtra(Intent.EXTRA_RETURN_RESULT, false)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (intent.resolveActivity(context.packageManager) == null) {
                    // Fallback 2: Detail aplikasi di Pengaturan
                    intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:$targetPackage")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            }
            context.startActivity(intent)
            onResult(true, "Konfirmasi penghapusan $displayName ditampilkan")
        } catch (t: Throwable) {
            onResult(false, "Gagal membuka penghapusan: ${t.message ?: "unknown"}")
        }
    }
}
