package dev.nyawit.launcher.core.ui

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Di desktop, tombol fisik/sistem "Back" tidak ada; tombol navigasi UI ditangani via callback.
}
