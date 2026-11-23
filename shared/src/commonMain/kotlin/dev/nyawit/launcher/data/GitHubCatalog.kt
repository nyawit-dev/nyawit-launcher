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
    const val PRIMARY_USER = "nyawit-dev"
    const val SECONDARY_ORG = "storytold"
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

/** GitHub-backed catalog: no application server required. Primary = user's own repos. */
class GitHubCatalog(
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    },
) {
    suspend fun loadApps(): List<CatalogApp> {
        val primary: List<GithubRepo> = client
            .get("https://api.github.com/users/${CatalogSources.PRIMARY_USER}/repos?per_page=100&type=public&sort=updated")
            .body()
        val secondary: List<GithubRepo> = try {
            client.get("https://api.github.com/orgs/${CatalogSources.SECONDARY_ORG}/repos?per_page=100&type=public")
                .body()
        } catch (_: Throwable) {
            emptyList()
        }
        val all = (primary + secondary)
            .filter { !it.archived }
            .distinctBy { it.fullName }
            .sortedWith(compareBy({ it.fullName.startsWith(CatalogSources.SECONDARY_ORG) }, { it.name }))
        return all.map { repo ->
            CatalogApp(
                name = repo.name.replace('-', ' ').replaceFirstChar { it.uppercase() },
                description = (repo.description ?: "Repository ${repo.fullName}") + if (repo.fork) " (fork)" else "",
                repoUrl = repo.htmlUrl,
                releaseUrl = "${repo.htmlUrl}/releases/latest",
                previewUrl = "https://raw.githubusercontent.com/${CatalogSources.ASSETS_REPO}/main/${repo.name}/hero.jpg",
            )
        }
    }
}
