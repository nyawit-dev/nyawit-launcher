@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.ComicTheme
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.ThemeMode
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Wrapper tema untuk Compose Preview di seluruh komponen komik.
 */
@Composable
fun ComicPreviewTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    ComicTheme(mode = if (darkTheme) ThemeMode.CYBER_OBSIDIAN else ThemeMode.NORDIC_LIGHT) {
        val c = LocalNyawitColors.current
        Box(
            modifier = Modifier
                .background(c.Background)
                .padding(16.dp),
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun ComicPreviewThemePreview() {
    ComicPreviewTheme {
        ComicBadge(text = "PREVIEW READY")
    }
}
