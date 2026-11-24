package dev.nyawit.launcher.feature.update

import dev.nyawit.launcher.feature.install.InstallRequest
import dev.nyawit.launcher.feature.install.installApp

actual fun applyUpdate(
    request: InstallRequest,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
) = installApp(request, onProgress, shouldCancel, onResult)
