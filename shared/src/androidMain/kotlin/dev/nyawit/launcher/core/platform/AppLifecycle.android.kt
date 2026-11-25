package dev.nyawit.launcher.core.platform

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper

actual fun observeAppResume(
    platformContext: Any?,
    onResume: () -> Unit,
): () -> Unit {
    val context = platformContext as? Context ?: return {}
    val app = context.applicationContext as? Application ?: return {}
    val mainHandler = Handler(Looper.getMainLooper())
    val triggerUpdate = Runnable { onResume() }

    val activityCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityResumed(activity: Activity) {
            mainHandler.post(triggerUpdate)
            mainHandler.postDelayed(triggerUpdate, 600L)
            mainHandler.postDelayed(triggerUpdate, 1500L)
        }
        override fun onActivityPaused(activity: Activity) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit
    }
    app.registerActivityLifecycleCallbacks(activityCallback)

    val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            mainHandler.post(triggerUpdate)
            mainHandler.postDelayed(triggerUpdate, 500L)
        }
    }
    val packageFilter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addDataScheme("package")
    }
    app.registerReceiver(packageReceiver, packageFilter)

    return {
        app.unregisterActivityLifecycleCallbacks(activityCallback)
        runCatching { app.unregisterReceiver(packageReceiver) }
    }
}
