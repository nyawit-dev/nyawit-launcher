package dev.nyawit.launcher.core.platform

import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.security.MessageDigest

internal fun launcherHome(): File = File(System.getProperty("user.home"), ".nyawit-launcher")

internal fun registryDir(): File = launcherHome().resolve("installed").apply { mkdirs() }

internal fun keyFile(key: String): File =
    File(registryDir(), key.replace(Regex("[^A-Za-z0-9._-]"), "_") + ".txt")

internal fun readRegistry(key: String): List<String> =
    keyFile(key).takeIf { it.exists() }?.readLines()?.filter { it.isNotBlank() } ?: emptyList()

internal fun recordInstalled(key: String, version: String) {
    runCatching {
        keyFile(key).writeText(version)
    }
}

internal fun removeInstalledRecord(key: String) {
    runCatching {
        keyFile(key).delete()
    }
}

internal fun sha256Of(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buffer = ByteArray(8192)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            digest.update(buffer, 0, read)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

/**
 * Membandingkan SHA-256 file dengan berkas `<url>.sha256` yang diterbitkan saat rilis.
 * Mengembalikan false hanya jika checksum tersedia dan tidak cocok.
 */
internal fun checksumMatches(downloadUrl: String, file: File): Boolean {
    var connection: HttpURLConnection? = null
    return try {
        connection = URI("$downloadUrl.sha256").toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        if (connection.responseCode !in 200..299) return true
        val expected = connection.inputStream.bufferedReader().use { it.readText() }
            .trim().split(Regex("\\s+")).firstOrNull().orEmpty()
        if (!Regex("^[a-fA-F0-9]{64}$").matches(expected)) return true
        sha256Of(file).equals(expected, ignoreCase = true)
    } catch (_: Throwable) {
        true
    } finally {
        connection?.disconnect()
    }
}
