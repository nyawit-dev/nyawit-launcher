pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
    // Library build-config bersama: satu sumber nilai untuk semua platform.
    includeBuild("build-logic")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
rootProject.name = "nyawit-launcher"
include(":shared")
include(":androidApp")
include(":desktopApp")
