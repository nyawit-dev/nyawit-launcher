package dev.nyawit.launcher

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.nyawit.launcher.LauncherApp

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Nyawit Launcher") {
        LauncherApp()
    }
}
