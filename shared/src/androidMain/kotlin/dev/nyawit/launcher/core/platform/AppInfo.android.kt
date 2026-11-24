package dev.nyawit.launcher.core.platform

actual fun appVersion(platformContext: Any?): String =
    installedVersion("dev.nyawit.launcher", platformContext, "Nyawit Launcher") ?: AppMetadata.VERSION
