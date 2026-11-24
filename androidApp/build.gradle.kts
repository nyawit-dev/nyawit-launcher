plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

val releaseVersion = providers.gradleProperty("releaseVersion").getOrElse("1.3.0")
val releaseVersionCode = providers.gradleProperty("androidVersionCode")
    .map { it.toIntOrNull() ?: 7 }
    .getOrElse(7)

android {
    namespace = "dev.nyawit.launcher"
    compileSdk {
        version = release(37) { minorApiLevel = 1 }
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
        applicationId = "dev.nyawit.launcher"
        minSdk = 26
        targetSdk = 37
        versionCode = releaseVersionCode
        versionName = releaseVersion
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
        jvmToolchain(21)
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
