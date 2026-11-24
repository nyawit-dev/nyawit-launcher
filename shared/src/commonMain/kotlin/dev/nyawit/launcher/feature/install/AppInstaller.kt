package dev.nyawit.launcher.feature.install

/** All callbacks are invoked on the main thread. */
expect fun installApp(
    url: String,
    fileName: String,
    platformContext: Any?,
    registryKey: String,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
)
