package dev.nyawit.launcher.core.ui

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Di desktop, tidak ada tombol hardware Back seperti Android
}
