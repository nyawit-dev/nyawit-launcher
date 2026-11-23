package dev.nyawit.launcher.feature.install

import java.awt.Desktop
import java.net.URI

actual fun installApp(url: String, fileName: String, platformContext: Any?) {
    try { Desktop.getDesktop().browse(URI(url)) } catch (_: Throwable) { }
}
