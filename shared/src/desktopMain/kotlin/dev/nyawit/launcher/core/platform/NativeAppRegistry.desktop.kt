package dev.nyawit.launcher.core.platform

import java.awt.Desktop
import java.io.File
import java.util.concurrent.TimeUnit

internal data class NativeAppInfo(
    val version: String?,
    val executable: String?,
    val uninstallString: String? = null,
)

private val NAME_NON_ALNUM = Regex("[^a-z0-9]")

/** "Nyawit Music" dan "NyawitMusic" harus dianggap sama. */
private fun normalizedName(value: String): String = NAME_NON_ALNUM.replace(value.lowercase(), "")

private fun runCommand(vararg command: String): String? = try {
    val process = ProcessBuilder(*command).redirectErrorStream(true).start()
    val output = process.inputStream.bufferedReader().use { it.readText() }
    if (!process.waitFor(5, TimeUnit.SECONDS)) {
        DesktopLog.warn("Registry", "Perintah timeout: ${command.joinToString(" ")}")
    }
    output
} catch (t: Throwable) {
    DesktopLog.error("Registry", "Perintah gagal: ${command.joinToString(" ")}", t)
    null
}

/** DisplayIcon sering berbentuk `"C:\...\app.exe",0` atau menunjuk file .ico. */
private fun executableFromIcon(raw: String?): String? {
    val path = raw?.trim()?.removeSurrounding("\"")?.substringBefore(',')?.trim()
        ?.removeSurrounding("\"")?.takeIf { it.isNotBlank() } ?: return null
    val file = File(path)
    if (file.extension.equals("exe", true)) return file.absolutePath
    val siblings = file.parentFile?.listFiles { f ->
        f.isFile && f.extension.equals("exe", true) && !f.name.startsWith("unins", true)
    }.orEmpty()
    return siblings.firstOrNull()?.absolutePath
}

private fun executableFromLocation(path: String, wanted: String): String? {
    val candidates = File(path).listFiles { f ->
        f.isFile && f.extension.equals("exe", true) && !f.name.startsWith("unins", true)
    }.orEmpty()
    val token = normalizedName(wanted)
    return candidates.firstOrNull { token.isNotBlank() && normalizedName(it.nameWithoutExtension).contains(token) }
        ?.absolutePath ?: candidates.firstOrNull()?.absolutePath
}

private fun windowsApp(displayName: String): NativeAppInfo? {
    val roots = listOf(
        "HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
        "HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
        "HKLM\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
    )
    val chunks = roots.map { root -> root to runCommand("reg", "query", root, "/s") }
    DesktopLog.debug(
        "Registry",
        "Windows: ${chunks.count { it.second != null }}/${chunks.size} root registry terbaca untuk '$displayName'",
    )
    val dump = chunks.mapNotNull { it.second }.joinToString("\n")
    val wanted = normalizedName(displayName)
    for (block in dump.split("HKEY_")) {
        val name = Regex("DisplayName\\s+REG_(?:SZ|EXPAND_SZ)\\s+(.+)").find(block)
            ?.groupValues?.get(1)?.trim()
        if (name.isNullOrBlank() || name.length < 3) continue
        val candidate = normalizedName(name)
        if (!candidate.contains(wanted) && !wanted.contains(candidate)) continue

        fun value(key: String) = Regex("$key\\s+REG_(?:SZ|EXPAND_SZ)\\s+(.+)").find(block)
            ?.groupValues?.get(1)?.trim()?.removeSurrounding("\"")
        val version = value("DisplayVersion")
        val icon = value("DisplayIcon")
        val location = value("InstallLocation")
        val uninstall = value("UninstallString")
        val executable = executableFromIcon(icon)
            ?: location?.let { executableFromLocation(it, wanted) }
        DesktopLog.debug(
            "Registry",
            "Cocok: DisplayName='$name', versi=$version, icon=$icon, location=$location, exe=$executable",
        )
        if (executable == null) {
            DesktopLog.warn("Registry", "'$name' cocok tapi executable tidak ditemukan — tombol Buka akan gagal")
        }
        return NativeAppInfo(version, executable, uninstall)
    }
    DesktopLog.warn("Registry", "Windows: tidak ada entri Uninstall yang cocok dengan '$displayName'")
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
    val wanted = normalizedName(displayName)
    val dirs = listOf(
        File("/usr/share/applications"),
        File(System.getProperty("user.home"), ".local/share/applications"),
    )
    for (dir in dirs) {
        val files = dir.listFiles { f -> f.isFile && f.extension == "desktop" } ?: continue
        for (file in files) {
            val text = runCatching { file.readText() }.getOrNull() ?: continue
            val name = Regex("^Name=(.+)$", RegexOption.MULTILINE).find(text)
                ?.groupValues?.get(1)?.trim() ?: continue
            val candidate = normalizedName(name)
            if (!candidate.contains(wanted) && !wanted.contains(candidate)) continue
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
    val info = if (version != null || !executable.isNullOrBlank()) {
        NativeAppInfo(version, executable?.takeIf { it.isNotBlank() })
    } else {
        null
    }
    DesktopLog.debug("Registry", "Linux: '$displayName' → ${info?.executable ?: "tidak ditemukan"}")
    return info
}

private fun macApp(displayName: String): NativeAppInfo? {
    val wanted = normalizedName(displayName)
    val dirs = listOf(
        File("/Applications"),
        File(System.getProperty("user.home"), "Applications"),
    )
    for (dir in dirs) {
        val apps = dir.listFiles { f -> f.isDirectory && f.extension == "app" } ?: continue
        for (app in apps) {
            val name = normalizedName(app.nameWithoutExtension)
            if (!name.contains(wanted) && !wanted.contains(name)) continue
            val plist = File(app, "Contents/Info.plist")
            val version = if (plist.isFile) {
                runCommand("plutil", "-extract", "CFBundleShortVersionString", "raw", plist.absolutePath)
                    ?.lines()?.firstOrNull()?.trim()
            } else null
            DesktopLog.debug("Registry", "macOS: '$displayName' → ${app.absolutePath}")
            return NativeAppInfo(version, app.absolutePath)
        }
    }
    DesktopLog.warn("Registry", "macOS: tidak ada .app yang cocok dengan '$displayName'")
    return null
}

internal fun findNativeApp(displayName: String, packageName: String): NativeAppInfo? {
    val os = System.getProperty("os.name").lowercase()
    DesktopLog.debug("Registry", "Cari aplikasi: displayName='$displayName', packageName='$packageName', os=$os")
    return when {
        os.contains("win") -> windowsApp(displayName)
        os.contains("mac") -> macApp(displayName)
        else -> linuxApp(displayName, packageName)
    }
}

internal fun launchNativeApp(info: NativeAppInfo): Boolean {
    val target = info.executable
    if (target.isNullOrBlank()) {
        DesktopLog.warn("Launch", "Aplikasi tidak punya path executable — tombol Buka tidak bisa bekerja")
        return false
    }
    val os = System.getProperty("os.name").lowercase()
    val command = when {
        os.contains("mac") -> listOf("open", target)
        os.contains("win") -> listOf("cmd", "/c", "start", "", target)
        else -> listOf(target)
    }
    DesktopLog.debug("Launch", "Menjalankan: ${command.joinToString(" ")}")
    val started = try {
        ProcessBuilder(command).start()
        true
    } catch (t: Throwable) {
        DesktopLog.error("Launch", "ProcessBuilder gagal, mencoba Desktop.open($target)", t)
        try {
            Desktop.getDesktop().open(File(target))
            true
        } catch (t2: Throwable) {
            DesktopLog.error("Launch", "Desktop.open gagal untuk $target", t2)
            false
        }
    }
    DesktopLog.debug("Launch", if (started) "Proses diluncurkan: $target" else "Gagal meluncurkan: $target")
    return started
}
