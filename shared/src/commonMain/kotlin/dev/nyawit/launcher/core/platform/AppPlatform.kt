package dev.nyawit.launcher.core.platform

enum class AppPlatform { ANDROID, TV, DESKTOP }

expect fun currentAppPlatform(): AppPlatform
