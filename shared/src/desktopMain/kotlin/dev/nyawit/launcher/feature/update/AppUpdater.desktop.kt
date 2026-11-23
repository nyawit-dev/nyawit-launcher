package dev.nyawit.launcher.feature.update

import dev.nyawit.launcher.feature.install.installApp

actual fun applyUpdate(
    url: String,
    fileName: String,
    platformContext: Any?,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
) = installApp(url, fileName, platformContext, onProgress, shouldCancel, onResult)
