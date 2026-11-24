package dev.nyawit.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin build-config bersama.
 *
 * Satu sumber nilai (gradle.properties) dipakai ulang oleh semua modul, dan salah
 * satu modul (`shared`) menghasilkan objek `BuildConfig` di `commonMain` sehingga
 * nilai tersebut sampai ke Android, desktop, dan kode UI Compose.
 */
class BuildConfigPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val config = project.extensions.create(
            "nyawitBuildConfig",
            NyawitBuildConfigExtension::class.java,
        )
        val providers = project.providers

        fun string(key: String) = providers.gradleProperty(key)
        fun integer(key: String) = providers.gradleProperty(key).map { raw ->
            raw.toIntOrNull()
                ?: throw IllegalArgumentException("gradle.properties: '$key' harus berupa angka, mendapat '$raw'")
        }

        config.version.set(string("releaseVersion"))
        config.versionCode.set(integer("androidVersionCode"))

        config.appId.set(string("appId"))
        config.appName.set(string("appName"))
        config.vendor.set(string("appVendor"))
        config.description.set(string("appDescription"))
        config.copyright.set(string("appCopyright"))

        config.desktopPackageName.set(string("desktopPackageName"))
        config.desktopMainClass.set(string("desktopMainClass"))

        config.githubOwner.set(string("githubOwner"))
        config.githubRepo.set(string("githubRepo"))
        config.githubBranch.set(string("githubBranch"))
        config.githubApiUrl.set(string("githubApiUrl"))
        config.catalogUrl.set(string("catalogUrl"))

        config.androidCompileSdk.set(integer("androidCompileSdk"))
        config.androidCompileSdkMinor.set(integer("androidCompileSdkMinor"))
        config.androidMinSdk.set(integer("androidMinSdk"))
        config.androidTargetSdk.set(integer("androidTargetSdk"))
        config.jvmToolchain.set(integer("jvmToolchain"))

        config.packageName.convention("dev.nyawit.launcher")
        config.outputDirectory.convention(
            project.layout.buildDirectory.dir("generated/buildconfig/commonMain/kotlin"),
        )

        project.tasks.register(
            "generateNyawitBuildConfig",
            GenerateNyawitBuildConfigTask::class.java,
            Action<GenerateNyawitBuildConfigTask> {
                version.set(config.version)
                versionCode.set(config.versionCode)
                appId.set(config.appId)
                appName.set(config.appName)
                vendor.set(config.vendor)
                appDescription.set(config.description)
                copyright.set(config.copyright)
                desktopPackageName.set(config.desktopPackageName)
                githubOwner.set(config.githubOwner)
                githubRepo.set(config.githubRepo)
                githubBranch.set(config.githubBranch)
                githubApiUrl.set(config.githubApiUrl)
                catalogUrl.set(config.catalogUrl)
                packageName.set(config.packageName)
                outputDirectory.set(config.outputDirectory)
            },
        )
    }
}
