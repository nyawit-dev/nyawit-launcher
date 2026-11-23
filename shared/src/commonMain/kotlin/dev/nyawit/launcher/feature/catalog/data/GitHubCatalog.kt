package dev.nyawit.launcher.feature.catalog.data

import dev.nyawit.launcher.core.platform.AppPlatform

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
    const val CONFIG_URL =
        "https://raw.githubusercontent.com/nyawit-dev/nyawit-launcher/main/catalog.json"
}

@Serializable
data class CatalogSource(val type: String, val name: String)

@Serializable
data class CatalogConfig(val sources: List<CatalogSource> = emptyList())

private val ACCENTS = listOf(
    0xFF3DDC97.toInt(), 0xFF5AA9E6.toInt(), 0xFFFFB454.toInt(),
    0xFFF2789F.toInt(), 0xFF9B8CFF.toInt(), 0xFF54C5EB.toInt(),
)

private val NON_APP_MARKERS = listOf(
    "assets", "asset", "docs", "doc", "website", "web", "portfolio", "blog",
    "config", "template", "starter", "dotfiles", "scripts", "library", "sdk",
    "backend", "server", "infra", "workflow", "readme", "gallery", "theme",
)

private val WEB_LANGUAGES = setOf("HTML", "CSS", "JavaScript", "TypeScript", "Vue", "Svelte", "Astro", "PHP")
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
    val platforms: List<AppPlatform>,
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

private fun platformsOf(fullName: String, description: String): List<AppPlatform> {
    val name = fullName.lowercase()
    val text = "$name ${description.lowercase()}"
    return when {
        "launcher" in name -> listOf(AppPlatform.ANDROID, AppPlatform.TV, AppPlatform.DESKTOP)
        "music" in name -> listOf(AppPlatform.ANDROID, AppPlatform.TV)
        "tv" in text || "leanback" in text -> listOf(AppPlatform.ANDROID, AppPlatform.TV)
        "desktop" in text || "jvm" in text || "compose multiplatform" in text -> listOf(AppPlatform.DESKTOP)
        else -> listOf(AppPlatform.ANDROID)
    }
}

private val PACKAGE_OVERRIDES = mapOf(
    "nyawit-dev/nyawit-launcher" to "dev.nyawit.launcher",
    "nyawit-dev/nyawit-music" to "dev.nyawit.music",
    "nyawit-dev/garis_game" to "ngga.ring.garisgame",
    "nyawit-dev/garis-arcade" to "dev.nyawit.arcade",
)

private fun packageNameOf(fullName: String): String =
    PACKAGE_OVERRIDES[fullName] ?: "dev.nyawit." +
        fullName.substringAfter('/').lowercase().replace(Regex("[^a-z0-9]"), "")

private fun isAppRepo(repo: GithubRepo): Boolean {
    val text = "${repo.name} ${repo.description.orEmpty()}".lowercase()
    val tokens = text.split(Regex("[^a-z0-9]+")).toSet()
    if (NON_APP_MARKERS.any { it in tokens }) return false
    if (repo.language in WEB_LANGUAGES) return false
    if (repo.language in APP_LANGUAGES) return true
    return repo.topics.any { it.lowercase() in APP_TOPICS }
}


private fun fallbackApps(): List<CatalogApp> {
    fun app(
        name: String,
        desc: String,
        platforms: List<AppPlatform>,
        accent: Long,
    ) = CatalogApp(
        fullName = "nyawit-dev/$name",
        name = name.replace('-', ' ').replaceFirstChar { it.uppercase() },
        description = desc,
        repoUrl = "https://github.com/nyawit-dev/$name",
        releaseUrl = "https://github.com/nyawit-dev/$name/releases/latest",
        category = when {
            "launcher" in name -> "Launcher"
            "music" in name -> "Musik"
            else -> "Game"
        },
        packageName = packageNameOf("nyawit-dev/$name"),
        platforms = platforms,
        accent = accent.toInt(),
    )
    return listOf(
        app("nyawit-launcher", "Launcher aplikasi Nyawit untuk Android, TV, dan desktop", listOf(AppPlatform.ANDROID, AppPlatform.TV, AppPlatform.DESKTOP), 0xFF3DDC97),
        app("nyawit-music", "Pemutar musik dengan dukungan Android TV", listOf(AppPlatform.ANDROID, AppPlatform.TV), 0xFF5AA9E6),
        app("garis-arcade", "Kumpulan game klasik: Snake, 2048, Tetris", listOf(AppPlatform.ANDROID), 0xFFFFB454),
        app("garis_game", "Tetris Kotlin Compose", listOf(AppPlatform.ANDROID), 0xFFF2789F),
        app("monopoly-kmp", "Game monopoli Kotlin Multiplatform", listOf(AppPlatform.ANDROID), 0xFF9B8CFF),
    )
}

class GitHubCatalog(
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    },
) {
    private suspend fun reposFrom(source: CatalogSource): List<GithubRepo> = try {
        when (source.type.lowercase()) {
            "user" -> client.get("https://api.github.com/users/${source.name}/repos?per_page=100&type=public&sort=updated").body()
            "org" -> client.get("https://api.github.com/orgs/${source.name}/repos?per_page=100&type=public&sort=updated").body()
            "repo" -> listOf(client.get("https://api.github.com/repos/${source.name}").body<GithubRepo>())
            else -> emptyList()
        }
    } catch (_: Throwable) { emptyList() }

    suspend fun loadApps(): List<CatalogApp> {
        val config: CatalogConfig = try {
            client.get(CatalogSources.CONFIG_URL).body()
        } catch (_: Throwable) {
            CatalogConfig(listOf(CatalogSource("user", CatalogSources.PRIMARY_USER)))
        }
        val sources = config.sources.ifEmpty { listOf(CatalogSource("user", CatalogSources.PRIMARY_USER)) }
        val repos = sources.fold(emptyList<GithubRepo>()) { acc, source -> acc + reposFrom(source) }
        val loaded = repos
            .filter { !it.archived && isAppRepo(it) }
            .distinctBy { it.fullName }
            .sortedWith(compareBy({ it.fullName.substringBefore('/') != CatalogSources.PRIMARY_USER }, { it.name }))
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
                    platforms = platformsOf(repo.fullName, desc),
                    accent = ACCENTS[kotlin.math.abs(repo.name.hashCode()) % ACCENTS.size],
                )
            }
        return loaded.ifEmpty { fallbackApps() }
    }

    fun close() { client.close() }

    suspend fun latestReleaseTag(fullName: String): String? = try {
        val release: GithubRelease = client.get("https://api.github.com/repos/$fullName/releases/latest").body()
        release.tag.ifBlank { null }
    } catch (_: Throwable) { null }

    suspend fun latestApkUrl(fullName: String): String? = try {
        val release: GithubRelease = client.get("https://api.github.com/repos/$fullName/releases/latest").body()
        release.assets.firstOrNull { it.name.endsWith(".apk", true) }?.downloadUrl
    } catch (_: Throwable) { null }
}
