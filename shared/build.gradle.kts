plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kmp.library)
}

val releaseVersion = providers.gradleProperty("releaseVersion").getOrElse("1.3.0")
val generatedVersionDir = layout.buildDirectory.dir("generated/version/commonMain/kotlin")

val generateVersion = tasks.register("generateVersion") {
    val version = releaseVersion
    val outputFile = generatedVersionDir.get()
        .file("dev/nyawit/launcher/core/platform/GeneratedVersion.kt").asFile
    outputs.file(outputFile)
    doLast {
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            "package dev.nyawit.launcher.core.platform\n" +
                "internal object GeneratedVersion { const val VERSION = \"${version}\" }\n",
        )
    }
}

kotlin {
    android {
        namespace = "dev.nyawit.launcher.shared"
        compileSdk {
            version = release(37) { minorApiLevel = 1 }
        }
        minSdk = 26
        androidResources.enable = true
    }
    jvm("desktop")
    jvmToolchain(21)

    sourceSets {
        getByName("commonMain") {
            kotlin.srcDir(generatedVersionDir)
            dependencies {
                implementation(libs.runtime)
                implementation(libs.foundation)
                implementation(libs.material3)
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
        dependsOn(generateVersion)
    }
}
