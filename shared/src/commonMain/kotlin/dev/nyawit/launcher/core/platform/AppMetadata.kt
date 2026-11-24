package dev.nyawit.launcher.core.platform

import dev.nyawit.launcher.BuildConfig

/** Versi launcher, dihasilkan dari `gradle.properties` lewat plugin build-config. */
object AppMetadata {
    const val VERSION = BuildConfig.VERSION
}
