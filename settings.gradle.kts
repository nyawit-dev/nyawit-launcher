pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
rootProject.name = "nyawit-launcher"
include(":shared")
include(":androidApp")
include(":desktopApp")
