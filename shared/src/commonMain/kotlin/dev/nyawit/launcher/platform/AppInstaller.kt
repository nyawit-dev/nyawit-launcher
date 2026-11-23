package dev.nyawit.launcher.platform

/** platformContext = Android Context on Android, unused on JVM. */
expect fun installApp(url: String, fileName: String, platformContext: Any?)
