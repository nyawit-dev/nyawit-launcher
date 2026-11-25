package dev.nyawit.launcher

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.nyawit.launcher.feature.catalog.ui.CatalogScreen
import dev.nyawit.launcher.theme.LauncherTheme

@Composable
fun LauncherApp(modifier: Modifier = Modifier, platformContext: Any? = null) {
    LauncherTheme(platformContext = platformContext) {
        CatalogScreen(modifier, platformContext)
    }
}
