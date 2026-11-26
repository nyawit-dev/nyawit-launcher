package dev.nyawit.launcher.feature.update.data

import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.core.platform.platformKey
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ReleaseAsset(val name: String, @SerialName("browser_download_url") val downloadUrl: String)

@Serializable
data class ReleaseInfo(@SerialName("tag_name") val tag: String, val assets: List<ReleaseAsset> = emptyList())

class UpdateRepository(
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    },
) {
    suspend fun latest(): ReleaseInfo? = latestFor(BuildConfig.GITHUB_REPO_FULL)

    suspend fun latestFor(fullName: String): ReleaseInfo? = try {
        client.get("https://api.github.com/repos/$fullName/releases/latest").body()
    } catch (_: Throwable) { null }

    fun close() { client.close() }

    /**
     * Memilih installer bawaan platform: .exe/.msi (Windows), .deb/.rpm (Linux),
     * .dmg/.pkg (macOS), .apk (Android).
     * Windows mengutamakan .exe Inno Setup karena memuat wizard komik kustom;
     * .msi jpackage dipakai sebagai fallback.
     * File .jar tidak didukung — hanya installer native resmi platform yang dipilih.
     */
    fun pickAsset(release: ReleaseInfo): ReleaseAsset? {
        val key = platformKey()
        val candidates = release.assets.filter {
            val n = it.name.lowercase()
            when (key) {
                "windows" -> n.endsWith(".msi") || n.endsWith(".exe")
                "mac" -> n.endsWith(".dmg") || n.endsWith(".pkg")
                "linux" -> n.endsWith(".deb") || n.endsWith(".rpm")
                else -> n.endsWith(".apk")
            }
        }
        val primary = when (key) {
            "windows" -> ".exe"
            "mac" -> ".dmg"
            "linux" -> ".deb"
            else -> ".apk"
        }
        val secondary = when (key) {
            "windows" -> ".msi"
            "mac" -> ".pkg"
            "linux" -> ".rpm"
            else -> null
        }
        return candidates.firstOrNull { it.name.endsWith(primary, true) }
            ?: secondary?.let { ext -> candidates.firstOrNull { it.name.endsWith(ext, true) } }
    }
}
