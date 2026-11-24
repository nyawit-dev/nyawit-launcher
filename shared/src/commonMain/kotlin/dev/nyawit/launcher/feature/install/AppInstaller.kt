package dev.nyawit.launcher.feature.install

/** Data yang dibutuhkan untuk mengunduh dan memasang satu paket aplikasi. */
data class InstallRequest(
    val url: String,
    val fileName: String,
    /** Versi dari rilis GitHub; dipakai sebagai versi terpasang di registry. */
    val version: String,
    val registryKey: String,
    val platformContext: Any? = null,
    /** true hanya untuk pembaruan launcher sendiri (proses lama akan keluar). */
    val restartAfterInstall: Boolean = false,
)

/** All callbacks are invoked on the main thread. */
expect fun installApp(
    request: InstallRequest,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
)
