package dev.nyawit.launcher.feature.update

import dev.nyawit.launcher.feature.install.installApp

actual fun applyUpdate(url: String, fileName: String, platformContext: Any?) {
    installApp(url, fileName, platformContext)
}
