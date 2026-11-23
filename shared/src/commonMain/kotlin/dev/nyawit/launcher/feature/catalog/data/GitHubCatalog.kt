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
}

private val ACCENTS = listOf(
    0xFF3DDC97.toInt(), 0xFF5AA9E6.toInt(), 0xFFFFB454.toInt(),
    0xFFF2789F.toInt(), 0xFF9B8CFF.toInt(), 0xFF54C5EB.toInt(),
)

private val NON_APP_MARKERS = listOf(
    "assets", "asset", "docs", "doc", "website", "web", "portfolio", "blog",
    "config", "template", "starter", "dotfiles", "scripts", "library", "sdk",
    "backend", "server", "infra", "workflow", "readme", "gallery", "theme",
)

private val APP_LANGUAGES = setOf("Kotlin", "Java", "Dart", "Swift", "Rust", "C++", "C#", "Objective-C")
private val APP_TOPICS = setOf("app", "android", "game", "launcher", "music", "desktop", "mobile", "android-tv")

@Serializable
data class GithubRepo(
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    val language: String? = null,
    val topics: List<String> = emptyList(),
    val fork: Boolean = false,
    val archived: Boolean = false,
)

@Serializable
data class GithubAsset(val name: String, @SerialName("browser_download_url") val downloadUrl: String)

@Serializable
data class GithubRelease(@SerialName("tag_name") val tag: String = "", val assets: List<GithubAsset> = emptyList())

data class CatalogApp(
    val fullName: String,
    val name: String,
    val description: String,
    val repoUrl: String,
    val releaseUrl: String,
    val category: String,
    val packageName: String,
    val accent: Int,
)

private fun categoryOf(name: String, description: String): String {
    val text = "$name ${description}".lowercase()
    return when {
        "launcher" in text -> "Launcher"
        listOf("game", "tetris", "snake", "arcade", "2048").any { it in text } -> "Game"
        "music" in text || "player" in text -> "Musik"
        else -> "Aplikasi"
    }
}

private fun packageNameOf(fullName: String): String = when {
    fullName.contains("launcher") -> "dev.nyawit.launcher"
    fullName.contains("music") -> "dev.nyawit.music"
    fullName.contains("arcade") -> "dev.nyawit.arcade"
    else -> "dev.nyawit." + fullName.substringAfter('/').replace(Regex("[^a-z0-9]"), "")
}

private fun isAppRepo(repo: GithubRepo): Boolean {
    val text = "${repo.name} ${repo.description.orEmpty()}".lowercase()
    if (NON_APP_MARKERS.any { it in text }) return false
    if (repo.language in APP_LANGUAGES) return true
    return repo.topics.any { it.lowercase() in APP_TOPICS }
}

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
            .filter { !it.archived && isAppRepo(it) }
            .distinctBy { it.fullName }
            .sortedBy { it.name }
            .map { repo ->
                val name = repo.name.replace('-', ' ').replaceFirstChar { it.uppercase() }
                val desc = repo.description?.takeIf { it.isNotBlank() } ?: "Aplikasi ${repo.fullName}"
                CatalogApp(
                    fullName = repo.fullName,
                    name = name,
                    description = desc,
                    repoUrl = repo.htmlUrl,
                    releaseUrl = "${repo.htmlUrl}/releases/latest",
                    category = categoryOf(name, desc),
                    packageName = packageNameOf(repo.fullName),
                    accent = ACCENTS[kotlin.math.abs(repo.name.hashCode()) % ACCENTS.size],
                )
            }
    }

    suspend fun latestReleaseTag(fullName: String): String? = try {
        val release: GithubRelease = client.get("https://api.github.com/repos/$fullName/releases/latest").body()
        release.tag.ifBlank { null }
    } catch (_: Throwable) { null }

    suspend fun latestApkUrl(fullName: String): String? = try {
        val release: GithubRelease = client.get("https://api.github.com/repos/$fullName/releases/latest").body()
        release.assets.firstOrNull { it.name.endsWith(".apk", true) }?.downloadUrl
    } catch (_: Throwable) { null }
}
