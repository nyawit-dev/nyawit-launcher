package dev.nyawit.launcher.core.platform

actual fun currentAppPlatform(): AppPlatform = if (isTvPlatform()) AppPlatform.TV else AppPlatform.ANDROID
