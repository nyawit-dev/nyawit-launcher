package dev.nyawit.launcher.core.platform

actual fun appVersion(platformContext: Any?): String =
    installedVersion("dev.nyawit.launcher", platformContext) ?: "1.2.6"
