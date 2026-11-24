import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val releaseVersion = providers.gradleProperty("releaseVersion").getOrElse("1.3.0")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(21)
    jvm("desktop")

    sourceSets {
        getByName("desktopMain") {
            dependencies {
                implementation(project(":shared"))
                implementation(libs.compose.desktop)
                implementation(libs.runtime)
                implementation(libs.foundation)
                implementation(libs.material3)
                implementation(libs.ui)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.nyawit.launcher.MainKt"
        jvmArgs("--enable-native-access=ALL-UNNAMED")
        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.Msi, TargetFormat.Dmg)
            packageName = "nyawit-launcher"
            packageVersion = releaseVersion
            windows {
                iconFile = file("icons/windows/nyawit.ico")
                // UUID stabil agar MSI rilis baru meng-upgrade instalasi lama, bukan membuat entri baru.
                upgradeUuid = "66A5709F-69D3-46F2-BB89-6617A0DB6B64"
                shortcut = true
                menu = true
                menuGroup = "Nyawit"
            }
            linux { iconFile = file("icons/linux/nyawit.png") }
            macOS { iconFile = file("icons/mac/nyawit.icns") }
        }
    }
}
