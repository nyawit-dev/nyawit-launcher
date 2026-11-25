package dev.nyawit.launcher.core.platform

import java.io.File
import java.time.Instant

/** Log ringan untuk diagnosis fitur desktop. File: ~/.nyawit-launcher/logs/launcher.log */
internal object DesktopLog {
    private val logFile: File by lazy {
        launcherHome().resolve("logs").apply { mkdirs() }.resolve("launcher.log")
    }

    fun debug(tag: String, message: String) = write("DEBUG", tag, message)

    fun warn(tag: String, message: String) = write("WARN", tag, message)

    fun error(tag: String, message: String, throwable: Throwable? = null) {
        val detail = throwable?.let {
            " | ${it.javaClass.simpleName}: ${it.message ?: "no message"}"
        }.orEmpty()
        write("ERROR", tag, message + detail)
    }

    private fun write(level: String, tag: String, message: String) {
        val line = "${Instant.now()} $level [$tag] $message"
        System.err.println(line)
        runCatching {
            if (logFile.exists() && logFile.length() > 2_000_000L) {
                logFile.copyTo(File(logFile.parentFile, "launcher.log.old"), overwrite = true)
                logFile.writeText("")
            }
            logFile.appendText(line + System.lineSeparator())
        }
    }
}
