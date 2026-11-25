package dev.nyawit.launcher.core.platform

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

actual fun observeAppResume(
    platformContext: Any?,
    onResume: () -> Unit,
): () -> Unit {
    val app = (platformContext as? Context)?.applicationContext as? Application ?: return {}
    val callback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityResumed(activity: Activity) = onResume()
        override fun onActivityPaused(activity: Activity) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit
    }
    app.registerActivityLifecycleCallbacks(callback)
    return { app.unregisterActivityLifecycleCallbacks(callback) }
}
