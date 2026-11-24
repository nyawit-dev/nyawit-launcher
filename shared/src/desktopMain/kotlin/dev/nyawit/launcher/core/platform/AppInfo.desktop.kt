package dev.nyawit.launcher.core.platform

import dev.nyawit.launcher.BuildConfig

actual fun appVersion(platformContext: Any?): String =
    installedVersion(BuildConfig.APP_ID, platformContext, BuildConfig.APP_NAME) ?: AppMetadata.VERSION
