package dev.nyawit.launcher.feature.catalog.ui

import java.awt.Desktop
import java.net.URI

actual fun openUrl(url: String, platformContext: Any?) {
    runCatching {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            val os = System.getProperty("os.name").lowercase()
            when {
                os.contains("win") -> ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start()
                os.contains("mac") -> ProcessBuilder("open", url).start()
                else -> ProcessBuilder("xdg-open", url).start()
            }
        }
    }
}
