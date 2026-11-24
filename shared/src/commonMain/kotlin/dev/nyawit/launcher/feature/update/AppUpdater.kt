package dev.nyawit.launcher.feature.update

import dev.nyawit.launcher.feature.install.InstallRequest

expect fun applyUpdate(
    request: InstallRequest,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
)
