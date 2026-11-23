package dev.nyawit.launcher.feature.install

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

actual fun installApp(url: String, fileName: String, platformContext: Any?) {
    val context = platformContext as? Context ?: return
    Thread {
        try {
            val file = File(context.cacheDir, fileName)
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 15000
            connection.readTimeout = 60000
            connection.inputStream.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            Handler(Looper.getMainLooper()).post {
                val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_VIEW)
                    .setDataAndType(uri, "application/vnd.android.package-archive")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        } catch (_: Throwable) { }
    }.start()
}
