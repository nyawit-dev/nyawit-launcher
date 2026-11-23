package dev.nyawit.launcher.core.platform

import android.content.res.Resources
import android.os.Build

actual fun isTvPlatform(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val uiMode = Resources.getSystem().configuration.uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK
        if (uiMode == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION) return true
    }
    return false
}
