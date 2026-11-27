package dev.nyawit.launcher.core.platform

import java.awt.Desktop
import java.io.File
import java.util.concurrent.TimeUnit

internal data class NativeAppInfo(
    val version: String?,
    val executable: String?,
    val uninstallString: String? = null,
    val quietUninstallString: String? = null,
    val installLocation: String? = null,
)

private val NAME_NON_ALNUM = Regex("[^a-z0-9]")

/** "Nyawit Music" dan "NyawitMusic" harus dianggap sama. */
internal fun normalizedName(value: String): String = NAME_NON_ALNUM.replace(value.lowercase(), "")

@Volatile
private var cachedWindowsApps: Map<String, NativeAppInfo>? = null
@Volatile
private var lastRegistryQueryTime: Long = 0L
private const val REGISTRY_CACHE_TTL_MS = 15_000L // 15 detik

/** Menghapus cache registry secara instan (dipanggil setelah pasang / hapus aplikasi). */
internal fun invalidateNativeRegistryCache() {
    cachedWindowsApps = null
    lastRegistryQueryTime = 0L
}

/**
 * Menutup/mematikan semua proses aplikasi target yang sedang berjalan sebelum instalasi/penghapusan.
 * Ini mencegah Windows mengunci file biner (.exe / .dll) yang menyebabkan pesan 'File in use'
 * dan memaksa perangkat di-restart.
 */
internal fun terminateAppProcesses(
    executablePath: String?,
    installLocation: String?,
    displayName: String,
) {
    val exeFile = executablePath?.let(::File)
    val exeName = exeFile?.name?.lowercase()
    val wantedToken = normalizedName(displayName)
    val currentPid = ProcessHandle.current().pid()

    try {
        ProcessHandle.allProcesses().forEach { handle ->
            if (handle.pid() == currentPid) return@forEach
            val info = handle.info()
            val cmd = info.command().orElse("").lowercase()
            val cmdLine = info.commandLine().orElse("").lowercase()

            val matches = when {
                executablePath != null && cmd == executablePath.lowercase() -> true
                installLocation != null && cmd.startsWith(installLocation.lowercase()) -> true
                exeName != null && exeName.length >= 4 && (cmd.endsWith(exeName) || cmd.endsWith("\\$exeName") || cmd.endsWith("/$exeName")) -> true
                wantedToken.length >= 4 && (cmd.contains(wantedToken) || cmdLine.contains(wantedToken)) -> {
                    // Jangan matikan proses Nyawit Launcher sendiri!
                    !cmd.contains("nyawit-launcher") && !cmd.contains("nyawit launcher") && !cmdLine.contains("nyawit launcher")
                }
                else -> false
            }

            if (matches) {
                DesktopLog.debug("Process", "Menghentikan proses ${handle.pid()}: $cmd")
                handle.destroy()
                try {
                    handle.onExit().get(1, TimeUnit.SECONDS)
                } catch (_: Throwable) {
                    handle.destroyForcibly()
                }
            }
        }
    } catch (t: Throwable) {
        DesktopLog.warn("Process", "Pindai ProcessHandle gagal: ${t.message}")
    }

    // Pengaman ekstra di Windows menggunakan taskkill untuk melepaskan handle file secara tuntas
    val os = System.getProperty("os.name").lowercase()
    if (os.contains("win") && exeName != null && exeName.endsWith(".exe") && !exeName.contains("launcher")) {
        try {
            ProcessBuilder("taskkill", "/F", "/IM", exeFile.name, "/T")
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()
                .waitFor(2, TimeUnit.SECONDS)
        } catch (_: Throwable) {}
    }

    // Tunggu singkat 300ms agar kernel sistem melepaskan penguncian file (file lock)
    try { Thread.sleep(300) } catch (_: Throwable) {}
}

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
    if (file.extension.equals("exe", true) && file.exists()) return file.absolutePath
    val siblings = file.parentFile?.listFiles { f ->
        f.isFile && f.extension.equals("exe", true) && !f.name.startsWith("unins", true)
    }.orEmpty()
    return siblings.firstOrNull()?.absolutePath
}

private fun executableFromLocation(path: String, wanted: String): String? {
    val dir = File(path)
    if (!dir.exists() || !dir.isDirectory) return null
    val candidates = dir.listFiles { f ->
        f.isFile && f.extension.equals("exe", true) && !f.name.startsWith("unins", true)
    }.orEmpty()
    val token = normalizedName(wanted)
    return candidates.firstOrNull { token.isNotBlank() && normalizedName(it.nameWithoutExtension).contains(token) }
        ?.absolutePath ?: candidates.firstOrNull()?.absolutePath
}

private fun executableFromUninstall(uninstallCmd: String?, wanted: String): String? {
    val raw = uninstallCmd?.trim() ?: return null
    val exeToken = Regex("\"([^\"]+\\.exe)\"|([^\\s\"]+\\.exe)", RegexOption.IGNORE_CASE)
        .find(raw)?.groupValues?.filter { it.isNotBlank() }?.getOrNull(1) ?: return null
    val uninstallerFile = File(exeToken)
    val dir = uninstallerFile.parentFile ?: return null
    if (!dir.exists() || !dir.isDirectory) return null
    val candidates = dir.listFiles { f ->
        f.isFile && f.extension.equals("exe", true) && !f.name.startsWith("unins", true)
    }.orEmpty()
    val token = normalizedName(wanted)
    return candidates.firstOrNull { token.isNotBlank() && normalizedName(it.nameWithoutExtension).contains(token) }
        ?.absolutePath ?: candidates.firstOrNull()?.absolutePath
}

private fun loadAllInstalledWindowsApps(): Map<String, NativeAppInfo> {
    val roots = listOf(
        "HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
        "HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
        "HKLM\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
    )
    val chunks = roots.mapNotNull { root -> runCommand("reg", "query", root, "/s") }
    val dump = chunks.joinToString("\n")
    val result = mutableMapOf<String, NativeAppInfo>()

    for (block in dump.split("HKEY_")) {
        fun value(key: String) = Regex("$key\\s+REG_(?:SZ|EXPAND_SZ)\\s+(.+)").find(block)
            ?.groupValues?.get(1)?.trim()?.removeSurrounding("\"")

        val name = value("DisplayName") ?: continue
        if (name.length < 3) continue

        val version = value("DisplayVersion")
        val icon = value("DisplayIcon")
        val location = value("InstallLocation")
        val uninstall = value("UninstallString")
        val quietUninstall = value("QuietUninstallString")

        val executable = executableFromIcon(icon)
            ?: location?.let { executableFromLocation(it, name) }
            ?: executableFromUninstall(uninstall, name)

        val info = NativeAppInfo(
            version = version,
            executable = executable,
            uninstallString = uninstall,
            quietUninstallString = quietUninstall,
            installLocation = location ?: executable?.let { File(it).parentFile?.absolutePath },
        )

        val norm = normalizedName(name)
        result[norm] = info

        // Daftarkan juga token tanpa nomor versi (misal "nyawitapi233" -> "nyawitapi")
        val alphaOnly = norm.replace(Regex("[0-9]"), "")
        if (alphaOnly.length >= 3 && !result.containsKey(alphaOnly)) {
            result[alphaOnly] = info
        }
    }

    DesktopLog.debug("Registry", "Windows: ${result.size} entri aplikasi berhasil dimuat ke cache")
    return result
}

private fun windowsApp(displayName: String): NativeAppInfo? {
    val now = System.currentTimeMillis()
    val cache = cachedWindowsApps
    val appsMap = if (cache != null && (now - lastRegistryQueryTime) < REGISTRY_CACHE_TTL_MS) {
        cache
    } else {
        val freshlyLoaded = loadAllInstalledWindowsApps()
        cachedWindowsApps = freshlyLoaded
        lastRegistryQueryTime = now
        freshlyLoaded
    }

    val wanted = normalizedName(displayName)
    // 1. Pencarian langsung
    appsMap[wanted]?.let { return it }

    // 2. Pencarian substring/token
    val matched = appsMap.entries.firstOrNull { (k, _) ->
        (k.length >= 3 && wanted.contains(k)) || (wanted.length >= 3 && k.contains(wanted))
    }?.value
    if (matched != null) return matched

    // 3. Fallback: telusuri direktori instalasi standar Windows
    val commonDirs = listOfNotNull(
        System.getenv("LOCALAPPDATA")?.let { File(it, "Programs") },
        System.getenv("ProgramFiles")?.let { File(it) },
        System.getenv("ProgramFiles(x86)")?.let { File(it) },
    )
    for (parent in commonDirs) {
        if (!parent.exists() || !parent.isDirectory) continue
        val match = parent.listFiles { f ->
            f.isDirectory && normalizedName(f.name).let { it.contains(wanted) || wanted.contains(it) }
        }?.firstOrNull() ?: continue

        val exe = executableFromLocation(match.absolutePath, displayName)
        if (exe != null) {
            return NativeAppInfo(
                version = "1.0.0",
                executable = exe,
                installLocation = match.absolutePath,
            )
        }
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
            return NativeAppInfo(version, app.absolutePath, installLocation = app.absolutePath)
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
    val exeFile = File(target)
    if (!exeFile.exists()) {
        DesktopLog.warn("Launch", "File executable tidak ditemukan di disk: $target")
        return false
    }
    val workDir = exeFile.parentFile ?: File(System.getProperty("user.home"))
    val os = System.getProperty("os.name").lowercase()
    val command = when {
        os.contains("mac") -> listOf("open", target)
        os.contains("win") -> listOf("cmd", "/c", "start", "", "/D", workDir.absolutePath, target)
        else -> listOf(target)
    }
    DesktopLog.debug("Launch", "Menjalankan: ${command.joinToString(" ")} (workingDir: ${workDir.absolutePath})")
    val started = try {
        ProcessBuilder(command)
            .directory(workDir)
            .start()
        true
    } catch (t: Throwable) {
        DesktopLog.error("Launch", "ProcessBuilder gagal, mencoba Desktop.open($target)", t)
        try {
            Desktop.getDesktop().open(exeFile)
            true
        } catch (t2: Throwable) {
            DesktopLog.error("Launch", "Desktop.open gagal untuk $target", t2)
            false
        }
    }
    DesktopLog.debug("Launch", if (started) "Proses diluncurkan: $target" else "Gagal meluncurkan: $target")
    return started
}
