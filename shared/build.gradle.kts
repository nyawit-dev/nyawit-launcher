plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kmp.library)
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
            dependencies {
                implementation(libs.runtime)
                implementation(libs.foundation)
                implementation(libs.material3)
                implementation(libs.ui)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.kotlinx.serialization.json)
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
                implementation(libs.core.ktx)
            }
        }
    }
}
