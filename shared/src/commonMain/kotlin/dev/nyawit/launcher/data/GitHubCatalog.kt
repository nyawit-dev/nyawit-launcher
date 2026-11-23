package dev.nyawit.launcher.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object CatalogSources {
    const val ORGANIZATION = "storytold"
    const val LAUNCHER_REPO = "storytold/craft-launcher"
    const val ASSETS_REPO = "storytold/craft-launcher-assets"
}

@Serializable
data class GithubRepo(
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("stargazers_count") val stars: Int = 0,
    val fork: Boolean = false,
    val archived: Boolean = false,
)

data class CatalogApp(
    val name: String,
    val description: String,
    val repoUrl: String,
    val releaseUrl: String,
    val previewUrl: String,
)

/** GitHub-backed catalog: no application server required. */
class GitHubCatalog(
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    },
) {
    suspend fun loadApps(): List<CatalogApp> {
        val repos: List<GithubRepo> = client
            .get("https://api.github.com/orgs/${CatalogSources.ORGANIZATION}/repos?per_page=100&type=public")
            .body()
        return repos
            .filter { !it.fork && !it.archived }
            .sortedBy { it.name }
            .map { repo ->
                CatalogApp(
                    name = repo.name.replace('-', ' ').replaceFirstChar { it.uppercase() },
                    description = repo.description ?: "Aplikasi dari ${CatalogSources.ORGANIZATION}",
                    repoUrl = repo.htmlUrl,
                    releaseUrl = "${repo.htmlUrl}/releases/latest",
                    previewUrl = "https://raw.githubusercontent.com/${CatalogSources.ASSETS_REPO}/main/${repo.name}/hero.jpg",
                )
            }
    }
}
