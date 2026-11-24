package dev.nyawit.launcher.core.platform

import java.io.File
import java.util.concurrent.TimeUnit

internal data class NativeAppInfo(
    val version: String?,
    val executable: String?,
    val uninstallString: String? = null,
)

private fun runCommand(vararg command: String): String? = try {
    val process = ProcessBuilder(*command).redirectErrorStream(true).start()
    val output = process.inputStream.bufferedReader().use { it.readText() }
    process.waitFor(5, TimeUnit.SECONDS)
    output
} catch (_: Throwable) {
    null
}

private fun windowsApp(displayName: String): NativeAppInfo? {
    val roots = listOf(
        "HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
        "HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
        "HKLM\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
    )
    val dump = roots.mapNotNull { runCommand("reg", "query", it, "/s") }.joinToString("\n")
    val wanted = displayName.lowercase().trim()
    for (block in dump.split("HKEY_")) {
        val name = Regex("DisplayName\\s+REG_SZ\\s+(.+)").find(block)?.groupValues?.get(1)?.trim()
        if (name.isNullOrBlank() || name.length < 3) continue
        val lower = name.lowercase()
        if (!lower.contains(wanted) && !wanted.contains(lower)) continue

        val version = Regex("DisplayVersion\\s+REG_SZ\\s+(.+)").find(block)
            ?.groupValues?.get(1)?.trim()
        val icon = Regex("DisplayIcon\\s+REG_SZ\\s+(.+)").find(block)
            ?.groupValues?.get(1)?.trim()?.removeSurrounding("\"")
        val location = Regex("InstallLocation\\s+REG_SZ\\s+(.+)").find(block)
            ?.groupValues?.get(1)?.trim()?.removeSurrounding("\"")
        val uninstall = Regex("UninstallString\\s+REG_SZ\\s+(.+)").find(block)
            ?.groupValues?.get(1)?.trim()
        val executable = icon?.takeIf { it.endsWith(".exe", true) }
            ?: location?.let { path ->
                File(path).listFiles { f -> f.isFile && f.extension.equals("exe", true) }
                    ?.firstOrNull()?.absolutePath
            }
        return NativeAppInfo(version, executable, uninstall)
    }
    return null
}

private fun linuxApp(displayName: String, packageName: String): NativeAppInfo? {
    val tokens = listOf(
        packageName.substringAfterLast('.').lowercase().replace(Regex("[^a-z0-9]"), "-"),
        displayName.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-'),
    ).filter { it.length >= 3 }

    var version: String? = null
    for (token in tokens) {
        val info = runCommand("dpkg", "-s", token) ?: continue
        if ("Status: install ok installed" in info) {
            version = Regex("Version:\\s*(.+)").find(info)?.groupValues?.get(1)?.trim()
            break
        }
    }

    var executable: String? = null
    val wanted = displayName.lowercase().trim()
    val dirs = listOf(
        File("/usr/share/applications"),
        File(System.getProperty("user.home"), ".local/share/applications"),
    )
    for (dir in dirs) {
        val files = dir.listFiles { f -> f.isFile && f.extension == "desktop" } ?: continue
        for (file in files) {
            val text = runCatching { file.readText() }.getOrNull() ?: continue
            val name = Regex("^Name=(.+)$", RegexOption.MULTILINE).find(text)
                ?.groupValues?.get(1)?.trim()?.lowercase() ?: continue
            if (!name.contains(wanted) && !wanted.contains(name)) continue
            executable = Regex("^Exec=(.+)$", RegexOption.MULTILINE).find(text)
                ?.groupValues?.get(1)?.trim()
                ?.substringBefore(" ")
                ?.removeSurrounding("\"")
            if (!executable.isNullOrBlank()) break
        }
        if (!executable.isNullOrBlank()) break
    }
    if (executable.isNullOrBlank()) {
        for (token in tokens) {
            val path = runCommand("which", token)?.lines()?.firstOrNull()?.trim()
            if (!path.isNullOrBlank()) {
                executable = path
                break
            }
        }
    }
    return if (version != null || !executable.isNullOrBlank()) {
        NativeAppInfo(version, executable?.takeIf { it.isNotBlank() })
    } else {
        null
    }
}

private fun macApp(displayName: String): NativeAppInfo? {
    val wanted = displayName.lowercase().trim()
    val dirs = listOf(
        File("/Applications"),
        File(System.getProperty("user.home"), "Applications"),
    )
    for (dir in dirs) {
        val apps = dir.listFiles { f -> f.isDirectory && f.extension == "app" } ?: continue
        for (app in apps) {
            val name = app.nameWithoutExtension.lowercase()
            if (!name.contains(wanted) && !wanted.contains(name)) continue
            val plist = File(app, "Contents/Info.plist")
            val version = if (plist.isFile) {
                runCommand("plutil", "-extract", "CFBundleShortVersionString", "raw", plist.absolutePath)
                    ?.lines()?.firstOrNull()?.trim()
            } else null
            return NativeAppInfo(version, app.absolutePath)
        }
    }
    return null
}

internal fun findNativeApp(displayName: String, packageName: String): NativeAppInfo? {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> windowsApp(displayName)
        os.contains("mac") -> macApp(displayName)
        else -> linuxApp(displayName, packageName)
    }
}

internal fun launchNativeApp(info: NativeAppInfo): Boolean {
    val target = info.executable ?: return false
    return try {
        val os = System.getProperty("os.name").lowercase()
        when {
            os.contains("mac") -> ProcessBuilder("open", target).start()
            os.contains("win") -> ProcessBuilder("cmd", "/c", "start", "", target).start()
            else -> ProcessBuilder(target).start()
        }
        true
    } catch (_: Throwable) {
        false
    }
}
