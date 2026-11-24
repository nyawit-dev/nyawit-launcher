package dev.nyawit.launcher

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    private var installPermissionPromptShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LauncherApp(platformContext = this@MainActivity) }
    }

    override fun onResume() {
        super.onResume()
        requestInstallPermissionIfNeeded()
    }

    private fun requestInstallPermissionIfNeeded() {
        if (installPermissionPromptShown) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (packageManager.canRequestPackageInstalls()) return
        installPermissionPromptShown = true
        Toast.makeText(
            this,
            "Izinkan Nyawit Launcher memasang & memperbarui aplikasi",
            Toast.LENGTH_LONG,
        ).show()
        var intent = Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:$packageName"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intent.resolveActivity(packageManager) == null) {
            intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName"),
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
    }
}
