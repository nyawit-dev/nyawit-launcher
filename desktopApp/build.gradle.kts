import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(21)
    jvm("desktop")

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":shared"))
            implementation(libs.compose.desktop)
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.nyawit.launcher.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Msi, TargetFormat.Dmg)
            packageName = "nyawit-launcher"
            packageVersion = "1.2.6"
        }
    }
}
