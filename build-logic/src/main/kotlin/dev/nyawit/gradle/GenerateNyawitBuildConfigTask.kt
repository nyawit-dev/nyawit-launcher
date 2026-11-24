package dev.nyawit.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/** Menulis satu objek `BuildConfig` ke source set bersama (commonMain). */
abstract class GenerateNyawitBuildConfigTask : DefaultTask() {
    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val versionCode: Property<Int>

    @get:Input
    abstract val appId: Property<String>

    @get:Input
    abstract val appName: Property<String>

    @get:Input
    abstract val vendor: Property<String>

    @get:Input
    abstract val appDescription: Property<String>

    @get:Input
    abstract val copyright: Property<String>

    @get:Input
    abstract val desktopPackageName: Property<String>

    @get:Input
    abstract val githubOwner: Property<String>

    @get:Input
    abstract val githubRepo: Property<String>

    @get:Input
    abstract val githubBranch: Property<String>

    @get:Input
    abstract val githubApiUrl: Property<String>

    @get:Input
    abstract val catalogUrl: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val stringValues = linkedMapOf(
            "VERSION" to version.get(),
            "APP_ID" to appId.get(),
            "APP_NAME" to appName.get(),
            "VENDOR" to vendor.get(),
            "DESCRIPTION" to appDescription.get(),
            "COPYRIGHT" to copyright.get(),
            "DESKTOP_PACKAGE_NAME" to desktopPackageName.get(),
            "GITHUB_OWNER" to githubOwner.get(),
            "GITHUB_REPO" to githubRepo.get(),
            "GITHUB_REPO_FULL" to "${githubOwner.get()}/${githubRepo.get()}",
            "GITHUB_BRANCH" to githubBranch.get(),
            "GITHUB_API_URL" to githubApiUrl.get(),
            "CATALOG_URL" to catalogUrl.get(),
        )
        val intValues = linkedMapOf("VERSION_CODE" to versionCode.get())

        val file = outputDirectory.get().file("BuildConfig.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(buildString {
            appendLine("// FILE HASIL GENERASI — JANGAN DIEDIT.")
            appendLine("// Sumber tunggal: gradle.properties (plugin dev.nyawit.buildconfig).")
            appendLine("package ${packageName.get()}")
            appendLine()
            appendLine("object BuildConfig {")
            stringValues.forEach { (key, value) ->
                appendLine("    const val $key: String = \"${escape(value)}\"")
            }
            intValues.forEach { (key, value) ->
                appendLine("    const val $key: Int = $value")
            }
            appendLine("}")
        })
    }

    private fun escape(value: String) = value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "")
}
