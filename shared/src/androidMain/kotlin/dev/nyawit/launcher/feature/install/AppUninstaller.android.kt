package dev.nyawit.launcher.feature.install

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import dev.nyawit.launcher.core.platform.resolvePackage

actual fun uninstallApp(
    packageName: String,
    displayName: String,
    platformContext: Any?,
    onResult: (Boolean, String) -> Unit,
) {
    val context = platformContext as? Context
    if (context == null) {
        onResult(false, "Konteks Android tidak tersedia")
        return
    }
    val resolved = resolvePackage(packageName, displayName, context)
    if (resolved == null) {
        onResult(false, "$displayName tidak terpasang")
        return
    }
    @Suppress("DEPRECATION")
    val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
        .setData(Uri.parse("package:$resolved"))
        .putExtra(Intent.EXTRA_RETURN_RESULT, false)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val main = Handler(Looper.getMainLooper())
    try {
        context.startActivity(intent)
        main.post { onResult(true, "Konfirmasi penghapusan $displayName ditampilkan") }
    } catch (t: Throwable) {
        main.post { onResult(false, "Gagal membuka penghapusan: ${t.message ?: "unknown"}") }
    }
}
