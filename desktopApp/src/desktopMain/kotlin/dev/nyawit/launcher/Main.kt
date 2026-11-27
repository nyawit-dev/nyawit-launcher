package dev.nyawit.launcher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.nyawit.launcher.BuildConfig
import dev.nyawit.launcher.LauncherApp
import dev.nyawit.launcher.core.platform.readLauncherSettings
import org.jetbrains.skia.Image

private fun windowIcon() =
    object {}.javaClass.classLoader.getResourceAsStream("nyawit.png")?.use { it.readBytes() }
        ?.let { Image.makeFromEncoded(it).toComposeImageBitmap() }
        ?.let { BitmapPainter(it) }

fun main() = application {
    val icon = remember { windowIcon() }
    var isVisible by remember { mutableStateOf(true) }
    val windowState = rememberWindowState()

    // Integrasi System Tray (Windows / Linux taskbar notification area)
    if (icon != null) {
        Tray(
            icon = icon,
            tooltip = BuildConfig.APP_NAME,
            onAction = {
                isVisible = true
                windowState.isMinimized = false
            },
            menu = {
                Item(
                    text = "Buka ${BuildConfig.APP_NAME}",
                    onClick = {
                        isVisible = true
                        windowState.isMinimized = false
                    },
                )
                Separator()
                Item(
                    text = "Keluar",
                    onClick = ::exitApplication,
                )
            },
        )
    }

    Window(
        onCloseRequest = {
            val raw = readLauncherSettings(null)
            val minimizeToTray = raw == null || !raw.contains("\"minimizeToTray\":false")
            if (minimizeToTray && icon != null) {
                isVisible = false
            } else {
                exitApplication()
            }
        },
        visible = isVisible,
        state = windowState,
        title = BuildConfig.APP_NAME,
        icon = icon,
    ) {
        LauncherApp()
    }
}
