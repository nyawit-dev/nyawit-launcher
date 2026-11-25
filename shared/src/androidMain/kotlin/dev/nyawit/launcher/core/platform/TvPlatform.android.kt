package dev.nyawit.launcher.core.platform

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources

@Volatile
var currentAppContext: Context? = null

actual fun isTvPlatform(): Boolean {
    val ctx = currentAppContext
    if (ctx != null) {
        val pm = ctx.packageManager
        if (pm.hasSystemFeature("android.software.leanback") ||
            pm.hasSystemFeature("android.hardware.type.television") ||
            pm.hasSystemFeature("android.software.leanback_only")
        ) {
            return true
        }
        val uiMode = ctx.resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK
        if (uiMode == Configuration.UI_MODE_TYPE_TELEVISION) return true
    }
    val systemUiMode = Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_TYPE_MASK
    return systemUiMode == Configuration.UI_MODE_TYPE_TELEVISION
}
