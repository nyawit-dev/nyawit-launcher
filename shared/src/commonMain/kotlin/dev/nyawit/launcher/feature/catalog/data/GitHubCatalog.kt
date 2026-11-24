package dev.nyawit.launcher.feature.catalog.data

import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.platform.readCatalogCache
import dev.nyawit.launcher.core.platform.writeCatalogCache

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val catalogJson = Json { ignoreUnknownKeys = true }

object CatalogSources {
    const val PRIMARY_USER = "nyawit-dev"
    const val CONFIG_URL =
        "https://raw.githubusercontent.com/nyawit-dev/nyawit-launcher/main/catalog.json"
}

/** Informasi pemilik/grup katalog. */
@Serializable
data class OwnerInfo(
    val name: String = "",
    val web: String? = null,
)

/**
 * Satu entri katalog. Bisa berupa grup (`owner` + `repo`) maupun entri tunggal
 * (`type` + `name`) seperti format lama.
 *
 * type: user | org | repo | app
 * - user/org: ambil semua repo publik dari akun/organisasi.
 * - repo: ambil satu repo GitHub.
 * - app: entri manual, tidak perlu repo terdeteksi otomatis.
 */
@Serializable
data class CatalogSource(
    val type: String? = null,
    val name: String? = null,
    val owner: OwnerInfo? = null,
    val repo: List<CatalogSource> = emptyList(),
    /** URL repo; mengganti URL GitHub yang dibuat dari `name`. */
    val url: String? = null,
    val label: String? = null,
    val description: String? = null,
    val category: String? = null,
    val platforms: List<String>? = null,
    val packageName: String? = null,
    val accent: String? = null,
    val hidden: Boolean = false,
    val pinned: Boolean = false,
)

@Serializable
data class CatalogConfig(
    val sources: List<CatalogSource> = emptyList(),
    /** Daftar `owner/repo` yang disembunyikan dari hasil deteksi user/org. */
    val exclude: List<String> = emptyList(),
)

/** Entri yang sudah dinormalisasi dari format grup maupun format lama. */
private data class ResolvedSource(
    val type: String,
    val name: String,
    val ownerName: String?,
    val ownerWeb: String?,
    val url: String?,
    val label: String?,
    val description: String?,
    val category: String?,
    val platforms: List<String>?,
    val packageName: String?,
    val accent: String?,
    val hidden: Boolean,
    val pinned: Boolean,
) {
    val hasDetail: Boolean
        get() = type.equals("app", ignoreCase = true) ||
            label != null || description != null || category != null ||
            platforms != null || packageName != null || accent != null || pinned
}

private fun resolveSources(entries: List<CatalogSource>): List<ResolvedSource> {
    val result = mutableListOf<ResolvedSource>()
    for (entry in entries) {
        if (entry.repo.isNotEmpty()) {
            val groupOwner = entry.owner?.name?.takeIf { it.isNotBlank() }
            val groupWeb = entry.owner?.web
            for (repo in entry.repo) {
                val type = repo.type ?: "app"
                val rawName = repo.name ?: continue
                val name = if (type.lowercase() in setOf("user", "org") || "/" in rawName) {
                    rawName
                } else {
                    "${groupOwner ?: CatalogSources.PRIMARY_USER}/$rawName"
                }
                result += ResolvedSource(
                    type = type,
                    name = name,
                    ownerName = groupOwner,
                    ownerWeb = groupWeb,
                    url = repo.url,
                    label = repo.label,
                    description = repo.description,
                    category = repo.category,
                    platforms = repo.platforms,
                    packageName = repo.packageName,
                    accent = repo.accent,
                    hidden = repo.hidden,
                    pinned = repo.pinned,
                )
            }
        } else if (entry.type != null && entry.name != null) {
            result += ResolvedSource(
                type = entry.type,
                name = entry.name,
                ownerName = entry.owner?.name?.takeIf { it.isNotBlank() },
                ownerWeb = entry.owner?.web,
                url = entry.url,
                label = entry.label,
                description = entry.description,
                category = entry.category,
                platforms = entry.platforms,
                packageName = entry.packageName,
                accent = entry.accent,
                hidden = entry.hidden,
                pinned = entry.pinned,
            )
        }
    }
    return result
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
    val pinned: Boolean = false,
    val ownerWeb: String? = null,
)

private fun parseAccent(value: String?): Int? {
    val raw = value?.trim()?.takeIf { it.isNotBlank() } ?: return null
    val hex = raw.removePrefix("#").removePrefix("0x").removePrefix("0X")
    val color = hex.toLongOrNull(16) ?: return null
    return if (hex.length == 6) (0xFF000000L or color).toInt() else color.toInt()
}

private fun parsePlatforms(values: List<String>?): List<AppPlatform>? {
    if (values.isNullOrEmpty()) return null
    val parsed = values.mapNotNull { value ->
        when (value.trim().lowercase()) {
            "android" -> AppPlatform.ANDROID
            "tv", "android-tv", "androidtv" -> AppPlatform.TV
            "desktop", "jvm", "jvm-desktop" -> AppPlatform.DESKTOP
            else -> null
        }
    }.distinct()
    return parsed.takeIf { it.isNotEmpty() }
}

private fun displayName(fullName: String): String =
    fullName.substringAfter('/').replace('-', ' ').replaceFirstChar { it.uppercase() }

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

private fun buildApp(
    fullName: String,
    label: String? = null,
    description: String? = null,
    category: String? = null,
    platforms: List<String>? = null,
    packageName: String? = null,
    accent: String? = null,
    pinned: Boolean = false,
    ownerWeb: String? = null,
    fallbackDescription: String = "Aplikasi $fullName",
    repoUrl: String = "https://github.com/$fullName",
    seed: String = fullName,
): CatalogApp {
    val name = label?.takeIf { it.isNotBlank() } ?: displayName(fullName)
    val desc = description?.takeIf { it.isNotBlank() } ?: fallbackDescription
    return CatalogApp(
        fullName = fullName,
        name = name,
        description = desc,
        repoUrl = repoUrl,
        releaseUrl = "$repoUrl/releases/latest",
        category = category?.takeIf { it.isNotBlank() } ?: categoryOf(name, desc),
        packageName = packageName?.takeIf { it.isNotBlank() } ?: packageNameOf(fullName),
        platforms = parsePlatforms(platforms) ?: platformsOf(fullName, desc),
        accent = parseAccent(accent) ?: ACCENTS[kotlin.math.abs(seed.hashCode()) % ACCENTS.size],
        pinned = pinned,
        ownerWeb = ownerWeb?.takeIf { it.isNotBlank() },
    )
}


class GitHubCatalog(
    private val localCatalogJson: String? = null,
    /** Katalog bawaan yang dipakai bila GitHub dan cache tidak tersedia. */
    private val bundledCatalog: suspend () -> String? = { null },
    private val platformContext: Any? = null,
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) { json(catalogJson) }
    },
) {
    private suspend fun reposFrom(source: ResolvedSource): List<GithubRepo> = try {
        when (source.type.lowercase()) {
            "user" -> client.get("https://api.github.com/users/${source.name}/repos?per_page=100&type=public&sort=updated").body()
            "org" -> client.get("https://api.github.com/orgs/${source.name}/repos?per_page=100&type=public&sort=updated").body()
            "repo" -> listOf(client.get("https://api.github.com/repos/${source.name}").body<GithubRepo>())
            else -> emptyList()
        }
    } catch (_: Throwable) { emptyList() }

    suspend fun loadApps(): List<CatalogApp> {
        // 1) Unduh dari GitHub. 2) Simpan ke cache. 3) Saat offline baca cache.
        val remoteJson = try {
            client.get(CatalogSources.CONFIG_URL).body<String>().takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            null
        }
        val configJson = if (remoteJson != null) {
            writeCatalogCache(remoteJson, platformContext)
            remoteJson
        } else {
            readCatalogCache(platformContext)
                ?: localCatalogJson
                ?: bundledCatalog()?.takeIf { it.isNotBlank() }
        }
        val config = configJson?.let {
            runCatching { catalogJson.decodeFromString<CatalogConfig>(it) }.getOrNull()
        } ?: CatalogConfig(emptyList())
        val sources = resolveSources(config.sources)
        val primaryOwner = sources.firstNotNullOfOrNull { it.ownerName } ?: CatalogSources.PRIMARY_USER

        val details = sources.filter { it.hasDetail }.associateBy { it.name.lowercase() }
        val hidden = (config.exclude + sources.filter { it.hidden }.map { it.name })
            .map { it.lowercase() }.toSet()

        // Entri dengan detail lengkap dibangun langsung dari konfigurasi — tidak perlu
        // memanggil GitHub API hanya untuk membuat daftar aplikasi.
        fun canBuildDirectly(source: ResolvedSource) =
            source.type.equals("app", ignoreCase = true) ||
                (source.type.equals("repo", ignoreCase = true) && source.hasDetail)

        val discovered = sources
            .filterNot { it.hidden && it.type.lowercase() in setOf("user", "org") }
            .filterNot { canBuildDirectly(it) }
            .fold(emptyList<GithubRepo>()) { acc, source -> acc + reposFrom(source) }
            .filter { !it.archived && (isAppRepo(it) || details.containsKey(it.fullName.lowercase())) }
            .distinctBy { it.fullName }
            .mapNotNull { repo ->
                if (repo.fullName.lowercase() in hidden) return@mapNotNull null
                val detail = details[repo.fullName.lowercase()]
                buildApp(
                    fullName = repo.fullName,
                    label = detail?.label,
                    description = detail?.description ?: repo.description,
                    category = detail?.category,
                    platforms = detail?.platforms,
                    packageName = detail?.packageName,
                    accent = detail?.accent,
                    pinned = detail?.pinned ?: false,
                    ownerWeb = detail?.ownerWeb,
                    fallbackDescription = "Aplikasi ${repo.fullName}",
                    repoUrl = detail?.url ?: repo.htmlUrl,
                    seed = repo.name,
                )
            }

        val manual = sources
            .filter { canBuildDirectly(it) }
            .mapNotNull { source ->
                if (source.name.lowercase() in hidden) return@mapNotNull null
                buildApp(
                    fullName = source.name,
                    label = source.label,
                    description = source.description,
                    category = source.category,
                    platforms = source.platforms,
                    packageName = source.packageName,
                    accent = source.accent,
                    pinned = source.pinned,
                    ownerWeb = source.ownerWeb,
                    repoUrl = source.url ?: "https://github.com/${source.name}",
                )
            }

        // Entri manual menimpa hasil deteksi dengan fullName yang sama.
        val combined = (manual + discovered)
            .distinctBy { it.fullName.lowercase() }
            .sortedWith(
                compareBy(
                    { !it.pinned },
                    { it.fullName.substringBefore('/') != primaryOwner },
                    { it.name },
                ),
            )
        // Tidak ada daftar cadangan hardcode: hanya aplikasi yang tertulis di katalog yang tampil.
        return combined
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
