package dev.nyawit.launcher.core.platform

// Desktop tidak punya siklus hidup Activity; status disegarkan setelah aksi selesai.
actual fun observeAppResume(
    platformContext: Any?,
    onResume: () -> Unit,
): () -> Unit = {}
