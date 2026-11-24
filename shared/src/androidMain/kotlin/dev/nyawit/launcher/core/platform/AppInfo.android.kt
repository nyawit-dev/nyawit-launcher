package dev.nyawit.launcher.core.platform

import dev.nyawit.launcher.BuildConfig

/** Versi launcher selalu mengikuti hasil build, bukan sisa versi di perangkat. */
actual fun appVersion(platformContext: Any?): String = BuildConfig.VERSION
