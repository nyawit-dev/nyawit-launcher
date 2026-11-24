package dev.nyawit.gradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

/**
 * Konfigurasi aplikasi yang dibaca dari `gradle.properties`.
 *
 * Extension ini dipakai oleh semua modul:
 * - `shared` menghasilkan `BuildConfig.kt` ke `commonMain` (terbagi ke Android & desktop).
 * - `androidApp` / `desktopApp` memakai nilainya langsung di DSL build masing-masing.
 */
abstract class NyawitBuildConfigExtension {
    abstract val version: Property<String>
    abstract val versionCode: Property<Int>

    abstract val appId: Property<String>
    abstract val appName: Property<String>
    abstract val vendor: Property<String>
    abstract val description: Property<String>
    abstract val copyright: Property<String>

    abstract val desktopPackageName: Property<String>
    abstract val desktopMainClass: Property<String>

    abstract val githubOwner: Property<String>
    abstract val githubRepo: Property<String>
    abstract val githubBranch: Property<String>
    abstract val githubApiUrl: Property<String>
    abstract val catalogUrl: Property<String>

    abstract val androidCompileSdk: Property<Int>
    abstract val androidCompileSdkMinor: Property<Int>
    abstract val androidMinSdk: Property<Int>
    abstract val androidTargetSdk: Property<Int>
    abstract val jvmToolchain: Property<Int>

    /** Package Kotlin untuk objek `BuildConfig` yang dihasilkan. */
    abstract val packageName: Property<String>

    /** Lokasi `BuildConfig.kt` hasil generasi. */
    abstract val outputDirectory: DirectoryProperty
}
