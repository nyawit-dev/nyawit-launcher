package dev.nyawit.launcher.core.platform

actual fun platformKey(): String {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> "windows"
        os.contains("mac") -> "mac"
        else -> "linux"
    }
}

actual fun platformArch(): String {
    val arch = System.getProperty("os.arch")?.lowercase().orEmpty()
    return when {
        arch.contains("aarch64") || arch.contains("arm64") -> "arm64"
        arch.contains("64") || arch.contains("x86_64") || arch.contains("amd64") -> "x64"
        else -> "x86"
    }
}
