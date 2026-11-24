package dev.nyawit.launcher.core.platform

import dev.nyawit.launcher.BuildConfig

/** Versi launcher selalu mengikuti hasil build, bukan file registry lama. */
actual fun appVersion(platformContext: Any?): String = BuildConfig.VERSION
