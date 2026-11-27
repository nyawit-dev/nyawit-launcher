package dev.nyawit.launcher.core.platform

import dev.nyawit.launcher.BuildConfig

@Volatile
private var lastError: String? = null

actual fun lastLaunchError(): String? = lastError

actual fun installedVersion(
    packageName: String,
    platformContext: Any?,
    displayName: String?,
): String? {
    // Versi launcher sendiri harus mengikuti biner yang sedang berjalan.
    // Registry lama (mis. sisa installer JAR 1.2.7) tidak boleh menimpa versi build.
    if (packageName == BuildConfig.APP_ID) return BuildConfig.VERSION
    val native = findNativeApp(displayName ?: packageName, packageName)
    if (native != null) {
        // Pastikan jika path executable diketahui, file executable benar-benar ada di disk
        if (native.executable == null || java.io.File(native.executable).exists()) {
            return native.version ?: "1.0.0"
        }
    }
    return readRegistry(packageName).firstOrNull()
}

actual fun launchInstalledApp(
    packageName: String,
    platformContext: Any?,
    displayName: String?,
): Boolean {
    lastError = null
    val label = displayName?.takeIf { it.isNotBlank() } ?: packageName
    val native = findNativeApp(label, packageName)
    if (native == null) {
        lastError = "tidak ditemukan di daftar aplikasi terpasang"
        DesktopLog.warn("Launch", "Gagal membuka '$label': $lastError")
        return false
    }
    if (!launchNativeApp(native)) {
        lastError = native.executable?.let { "gagal menjalankan $it" }
            ?: "path executable tidak tersedia"
        DesktopLog.warn("Launch", "Gagal membuka '$label': $lastError")
        return false
    }
    DesktopLog.debug("Launch", "'$label' berhasil dibuka")
    return true
}

actual fun createDesktopShortcut(
    packageName: String,
    displayName: String,
    platformContext: Any?,
): Boolean {
    val native = findNativeApp(displayName, packageName) ?: return false
    val exePath = native.executable ?: return false
    val exeFile = java.io.File(exePath)
    if (!exeFile.exists()) return false

    val os = System.getProperty("os.name", "").lowercase()
    return try {
        if ("win" in os) {
            val desktopDir = System.getProperty("user.home") + "\\Desktop"
            val lnkPath = "$desktopDir\\$displayName.lnk"
            val workingDir = exeFile.parentFile?.absolutePath ?: ""
            val psCommand = "\$ws = New-Object -ComObject WScript.Shell; \$s = \$ws.CreateShortcut('$lnkPath'); \$s.TargetPath = '$exePath'; \$s.WorkingDirectory = '$workingDir'; \$s.Save()"
            ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command", psCommand).start().waitFor() == 0
        } else if ("linux" in os) {
            val desktopDir = java.io.File(System.getProperty("user.home"), "Desktop")
            if (desktopDir.exists()) {
                val shortcutFile = java.io.File(desktopDir, "$displayName.desktop")
                shortcutFile.writeText(
                    """
                    [Desktop Entry]
                    Type=Application
                    Name=$displayName
                    Exec="$exePath"
                    Terminal=false
                    """.trimIndent(),
                )
                shortcutFile.setExecutable(true)
                true
            } else false
        } else false
    } catch (_: Throwable) {
        false
    }
}
