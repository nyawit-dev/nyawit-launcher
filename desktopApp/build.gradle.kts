import dev.nyawit.gradle.NyawitBuildConfigExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("dev.nyawit.buildconfig")
}

val config = extensions.getByType<NyawitBuildConfigExtension>()

kotlin {
    jvmToolchain(config.jvmToolchain.get())
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
        mainClass = config.desktopMainClass.get()
        jvmArgs("--enable-native-access=ALL-UNNAMED")
        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.Msi, TargetFormat.Dmg)
            packageName = config.desktopPackageName.get()
            packageVersion = config.version.get()
            description = "${config.appName.get()} (${config.version.get()})"
            vendor = config.vendor.get()
            copyright = config.copyright.get()

            val iconsDir = project.file("icons")
            val windowsIcon = iconsDir.resolve("windows/nyawit.ico")
            val macIcon = iconsDir.resolve("mac/nyawit.icns")
            val linuxIcon = iconsDir.resolve("linux/nyawit.png")
            val fallbackIcon = rootProject.file("shared/src/commonMain/composeResources/drawable/app_icon.png")

            windows {
                iconFile.set(if (windowsIcon.exists()) windowsIcon else fallbackIcon)
                // UUID stabil agar MSI rilis baru meng-upgrade instalasi lama, bukan membuat entri baru.
                upgradeUuid = "66A5709F-69D3-46F2-BB89-6617A0DB6B64"
                menuGroup = config.vendor.get()
                shortcut = true
                menu = true
                dirChooser = true
            }
            macOS {
                iconFile.set(if (macIcon.exists()) macIcon else fallbackIcon)
                bundleID = config.appId.get()
            }
            linux {
                iconFile.set(if (linuxIcon.exists()) linuxIcon else fallbackIcon)
                shortcut = true
            }
        }
    }
}
