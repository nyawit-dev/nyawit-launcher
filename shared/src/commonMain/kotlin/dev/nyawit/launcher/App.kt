package dev.nyawit.launcher

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.nyawit.launcher.core.ui.WindowSize
import dev.nyawit.launcher.feature.catalog.ui.CatalogScreen
import dev.nyawit.launcher.theme.LauncherTheme

@Composable
fun LauncherApp(modifier: Modifier = Modifier, platformContext: Any? = null) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val windowSize = WindowSize(
            widthDp = maxWidth.value.toInt(),
            heightDp = maxHeight.value.toInt(),
        )

        LauncherTheme(
            platformContext = platformContext,
            windowSize = windowSize,
        ) {
            CatalogScreen(Modifier.fillMaxSize(), platformContext)
        }
    }
}
