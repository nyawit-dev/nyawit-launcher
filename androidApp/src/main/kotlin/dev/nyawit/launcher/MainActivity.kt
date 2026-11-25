package dev.nyawit.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.nyawit.launcher.core.platform.currentAppContext
import dev.nyawit.launcher.feature.install.resumePendingInstall

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentAppContext = applicationContext
        setContent { LauncherApp(platformContext = this@MainActivity) }
    }

    override fun onResume() {
        super.onResume()
        resumePendingInstall(this)
    }
}
