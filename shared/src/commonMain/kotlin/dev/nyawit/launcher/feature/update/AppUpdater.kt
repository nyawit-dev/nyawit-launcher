package dev.nyawit.launcher.feature.update

expect fun applyUpdate(
    url: String,
    fileName: String,
    platformContext: Any?,
    registryKey: String,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
)
