package dev.nyawit.launcher.core.platform

import android.os.Build

actual fun platformKey(): String = "android"

actual fun platformArch(): String {
    val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: ""
    return when {
        abi.contains("arm64") -> "arm64"
        abi.contains("armeabi") -> "arm"
        abi.contains("x86_64") -> "x64"
        abi.contains("x86") -> "x86"
        else -> abi
    }
}
