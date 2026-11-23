package dev.nyawit.launcher.core.platform

actual fun platformKey(): String {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> "windows"
        os.contains("mac") -> "mac"
        else -> "linux"
    }
}
