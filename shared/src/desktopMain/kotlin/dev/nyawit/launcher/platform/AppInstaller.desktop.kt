package dev.nyawit.launcher.platform

import java.awt.Desktop
import java.net.URI

actual fun installApp(url: String, fileName: String, platformContext: Any?) {
    try {
        Desktop.getDesktop().browse(URI(url))
    } catch (_: Throwable) {
        // Browser unavailable: release URL remains visible in the card.
    }
}
