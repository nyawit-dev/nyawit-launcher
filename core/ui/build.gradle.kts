import dev.nyawit.gradle.NyawitBuildConfigExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kmp.library)
    id("dev.nyawit.buildconfig")
}

val config = extensions.getByType<NyawitBuildConfigExtension>()

kotlin {
    android {
        namespace = "${config.appId.get()}.core.ui"
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
            dependencies {
                implementation(libs.runtime)
                implementation(libs.foundation)
                implementation(libs.material3)
                implementation(compose.materialIconsExtended)
                implementation(libs.ui)
                implementation(libs.compose.resources)
                implementation(compose.components.uiToolingPreview)
            }
        }
        getByName("androidMain") {
            dependencies {
                implementation(libs.core.ktx)
                implementation(libs.activity.compose)
            }
        }
        getByName("desktopMain") {
            dependencies {
            }
        }
    }
}
