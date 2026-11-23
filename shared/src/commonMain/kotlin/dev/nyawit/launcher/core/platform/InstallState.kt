package dev.nyawit.launcher.core.platform

expect fun installedVersion(packageName: String, platformContext: Any?): String?
expect fun launchInstalledApp(packageName: String, platformContext: Any?): Boolean
