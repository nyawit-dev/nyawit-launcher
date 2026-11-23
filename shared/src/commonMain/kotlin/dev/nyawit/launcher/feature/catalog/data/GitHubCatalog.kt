package dev.nyawit.launcher.feature.catalog.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object CatalogSources {
    const val PRIMARY_USER = "nyawit-dev"
    const val SECONDARY_ORG = "storytold"
    const val ASSETS_REPO = "storytold/craft-launcher-assets"
}

private val ACCENTS = listOf(
    0xFF7C5CFF.toInt(), 0xFF4BD6C5.toInt(), 0xFFFFC857.toInt(),
    0xFFFF7B8A.toInt(), 0xFF67B7FF.toInt(), 0xFF9BE15D.toInt(),
)

@Serializable
data class GithubRepo(
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    val fork: Boolean = false,
    val archived: Boolean = false,
)

@Serializable
data class GithubAsset(val name: String, @SerialName("browser_download_url") val downloadUrl: String)

@Serializable
data class GithubRelease(val assets: List<GithubAsset> = emptyList())

data class CatalogApp(
    val fullName: String,
    val name: String,
    val description: String,
    val repoUrl: String,
    val releaseUrl: String,
    val accent: Int,
)

class GitHubCatalog(
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    },
) {
    suspend fun loadApps(): List<CatalogApp> {
        val primary: List<GithubRepo> = client
            .get("https://api.github.com/users/${CatalogSources.PRIMARY_USER}/repos?per_page=100&type=public&sort=updated")
            .body()
        val secondary: List<GithubRepo> = try {
            client.get("https://api.github.com/orgs/${CatalogSources.SECONDARY_ORG}/repos?per_page=100&type=public").body()
        } catch (_: Throwable) { emptyList() }
        return (primary + secondary)
            .filter { !it.archived }
            .distinctBy { it.fullName }
            .sortedWith(compareBy({ it.fullName.startsWith(CatalogSources.SECONDARY_ORG) }, { it.name }))
            .map { repo ->
                CatalogApp(
                    fullName = repo.fullName,
                    name = repo.name.replace('-', ' ').replaceFirstChar { it.uppercase() },
                    description = (repo.description ?: "Repository ${repo.fullName}") + if (repo.fork) " (fork)" else "",
                    repoUrl = repo.htmlUrl,
                    releaseUrl = "${repo.htmlUrl}/releases/latest",
                    accent = ACCENTS[kotlin.math.abs(repo.name.hashCode()) % ACCENTS.size],
                )
            }
    }

    suspend fun latestApkUrl(fullName: String): String? = try {
        val release: GithubRelease = client.get("https://api.github.com/repos/$fullName/releases/latest").body()
        release.assets.firstOrNull { it.name.endsWith(".apk", true) }?.downloadUrl
    } catch (_: Throwable) { null }
}
