import dev.nyawit.gradle.NyawitBuildConfigExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kmp.library)
    id("dev.nyawit.buildconfig")
}

val config = extensions.getByType<NyawitBuildConfigExtension>()
val buildConfigDir = config.outputDirectory

kotlin {
    android {
        namespace = "${config.appId.get()}.shared"
        compileSdk {
            version = release(config.androidCompileSdk.get()) {
                minorApiLevel = config.androidCompileSdkMinor.get()
            }
        }
        minSdk = config.androidMinSdk.get()
        androidResources.enable = true
    }
    jvm("desktop")
    jvmToolchain(config.jvmToolchain.get())

    sourceSets {
        getByName("commonMain") {
            // BuildConfig hasil generasi ikut terkompilasi di semua platform.
            kotlin.srcDir(buildConfigDir)
            dependencies {
                api(project(":core:ui"))
                implementation(libs.runtime)
                implementation(libs.foundation)
                implementation(libs.material3)
                implementation(compose.materialIconsExtended)
                implementation(libs.ui)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.compose.resources)
            }
        }
        getByName("androidMain") {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.core.ktx)
                implementation(libs.activity.compose)
            }
        }
        getByName("desktopMain") {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}

tasks.configureEach {
    if (name.startsWith("compile") && (name.contains("Kotlin") || name.endsWith("Main"))) {
        dependsOn(tasks.named("generateNyawitBuildConfig"))
    }
}
