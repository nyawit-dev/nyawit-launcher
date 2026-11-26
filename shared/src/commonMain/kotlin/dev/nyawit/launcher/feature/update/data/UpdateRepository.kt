package dev.nyawit.launcher.feature.update.data

import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.platform.platformArch
import dev.nyawit.launcher.core.platform.platformKey
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

@Serializable
data class ReleaseAsset(val name: String, @SerialName("browser_download_url") val downloadUrl: String)

@Serializable
data class ReleaseInfo(@SerialName("tag_name") val tag: String, val assets: List<ReleaseAsset> = emptyList())

class UpdateRepository(
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    },
) {
    private val releaseCache = mutableMapOf<String, Pair<ReleaseInfo?, TimeSource.Monotonic.ValueTimeMark>>()

    suspend fun latest(): ReleaseInfo? = latestFor(BuildConfig.GITHUB_REPO_FULL)

    suspend fun latestFor(fullName: String, forceRefresh: Boolean = false): ReleaseInfo? {
        val cached = releaseCache[fullName]
        if (!forceRefresh && cached != null && cached.second.elapsedNow() < 3.minutes) {
            return cached.first
        }
        val info: ReleaseInfo? = try {
            client.get("https://api.github.com/repos/$fullName/releases/latest").body()
        } catch (_: Throwable) { null }
        releaseCache[fullName] = info to TimeSource.Monotonic.markNow()
        return info
    }

    fun close() { client.close() }

    /**
     * Memilih installer bawaan platform: .msi/.exe (Windows), .deb/.rpm (Linux),
     * .dmg/.pkg (macOS), .apk (Android).
     * Memperhitungkan format installer utama (.msi > .exe) dan arsitektur CPU (x64 vs ARM64)
     * agar tidak keliru mengunduh biner stub launcher mentah (.exe 400KB) dibanding paket installer (.msi).
     */
    fun pickAsset(release: ReleaseInfo): ReleaseAsset? {
        val key = platformKey()
        val arch = platformArch()
        val candidates = release.assets.filter {
            val n = it.name.lowercase()
            when (key) {
                "windows" -> n.endsWith(".msi") || n.endsWith(".exe")
                "mac" -> n.endsWith(".dmg") || n.endsWith(".pkg")
                "linux" -> n.endsWith(".deb") || n.endsWith(".rpm")
                else -> n.endsWith(".apk")
            }
        }
        if (candidates.isEmpty()) return null

        val filteredByArch = candidates.filter { asset ->
            val n = asset.name.lowercase()
            when (arch) {
                "x64" -> !n.contains("arm64") && !n.contains("aarch64") && !n.contains("arm") && !n.contains("x86.") && !n.contains("i386")
                "arm64" -> !n.contains("x86_64") && !n.contains("amd64") && !n.contains("win64") && !n.contains("x64")
                else -> true
            }
        }
        val pool = if (filteredByArch.isNotEmpty()) filteredByArch else candidates

        // Prioritas format installer utama:
        // Di Windows: .msi adalah paket installer lengkap (berisi runtime + jar).
        // .exe mentah tanpa 'setup' sering kali hanya stub launcher kecil (~400KB) yang membutuhkan .cfg.
        // Maka .msi HARUS lebih tinggi prioritasnya dibanding .exe.
        fun formatScore(name: String): Int {
            val n = name.lowercase()
            return when (key) {
                "windows" -> when {
                    n.endsWith(".msi") -> 2
                    n.endsWith(".exe") -> 1
                    else -> 0
                }
                "mac" -> when {
                    n.endsWith(".dmg") -> 2
                    n.endsWith(".pkg") -> 1
                    else -> 0
                }
                "linux" -> when {
                    n.endsWith(".deb") -> 2
                    n.endsWith(".rpm") -> 1
                    else -> 0
                }
                else -> if (n.endsWith(".apk")) 2 else 0
            }
        }

        fun archScore(name: String): Int {
            val n = name.lowercase()
            return when (arch) {
                "x64" -> if (n.contains("x64") || n.contains("x86_64") || n.contains("win64") || n.contains("amd64")) 1 else 0
                "arm64" -> if (n.contains("arm64") || n.contains("aarch64")) 1 else 0
                else -> 0
            }
        }

        // Urutkan berdasarkan format utama terlebih dahulu, lalu arsitektur CPU
        val sorted = pool.sortedWith(
            compareByDescending<ReleaseAsset> { formatScore(it.name) }
                .thenByDescending { archScore(it.name) }
        )
        return sorted.firstOrNull()
    }
}
