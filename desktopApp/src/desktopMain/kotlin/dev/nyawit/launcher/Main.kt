package dev.nyawit.launcher

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.LauncherApp
import org.jetbrains.skia.Image

private fun windowIcon() =
    object {}.javaClass.classLoader.getResourceAsStream("nyawit.png")?.use { it.readBytes() }
        ?.let { Image.makeFromEncoded(it).toComposeImageBitmap() }
        ?.let { BitmapPainter(it) }

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = BuildConfig.APP_NAME, icon = windowIcon()) {
        LauncherApp()
    }
}
