package dev.nyawit.launcher.feature.update.data

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
    suspend fun latest(): ReleaseInfo? = try {
        client.get("https://api.github.com/repos/nyawit-dev/nyawit-launcher/releases/latest").body()
    } catch (_: Throwable) { null }

    fun close() { client.close() }

    fun pickAsset(release: ReleaseInfo): ReleaseAsset? {
        val key = platformKey()
        val candidates = release.assets.filter {
            val n = it.name.lowercase()
            when (key) {
                "windows" -> n.contains("windows") || n.endsWith(".msi")
                "mac" -> n.contains("mac") || n.endsWith(".dmg")
                "linux" -> n.contains("linux") || n.endsWith(".deb")
                else -> n.endsWith(".apk")
            }
        }
        return candidates.firstOrNull { it.name.endsWith(".jar") } ?: candidates.firstOrNull()
    }
}
