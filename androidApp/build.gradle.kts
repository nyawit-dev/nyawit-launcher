import dev.nyawit.gradle.NyawitBuildConfigExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("dev.nyawit.buildconfig")
}

val config = extensions.getByType<NyawitBuildConfigExtension>()

android {
    namespace = config.appId.get()
    compileSdk {
        version = release(config.androidCompileSdk.get()) {
            minorApiLevel = config.androidCompileSdkMinor.get()
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "nyawit123"
            keyAlias = "nyawit"
            keyPassword = "nyawit123"
        }
    }

    defaultConfig {
        applicationId = config.appId.get()
        minSdk = config.androidMinSdk.get()
        targetSdk = config.androidTargetSdk.get()
        versionCode = config.versionCode.get()
        versionName = config.version.get()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(config.jvmToolchain.get())
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.activity.compose)
    implementation(libs.runtime)
    implementation(libs.foundation)
    implementation(libs.material3)
    implementation(libs.ui)
}
