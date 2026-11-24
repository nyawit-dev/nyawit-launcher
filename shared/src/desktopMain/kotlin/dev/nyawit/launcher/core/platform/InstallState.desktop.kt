package dev.nyawit.launcher.core.platform

import java.io.File

private fun keyFile(key: String): File =
    File(System.getProperty("user.home"), ".nyawit-launcher/installed/${key.replace(Regex("[^A-Za-z0-9._-]"), "_")}.txt")

private fun readRegistry(key: String): List<String> =
    keyFile(key).takeIf { it.exists() }?.readLines()?.filter { it.isNotBlank() } ?: emptyList()

actual fun installedVersion(packageName: String, platformContext: Any?): String? =
    readRegistry(packageName).firstOrNull()

actual fun launchInstalledApp(packageName: String, platformContext: Any?): Boolean {
    val path = readRegistry(packageName).getOrNull(1) ?: return false
    val file = File(path)
    if (!file.exists()) return false
    val java = File(
        System.getProperty("java.home"),
        "bin/java${if (System.getProperty("os.name").lowercase().contains("win")) ".exe" else ""}",
    ).absolutePath
    return try {
        ProcessBuilder(java, "-jar", file.absolutePath).inheritIO().start()
        true
    } catch (_: Throwable) { false }
}
