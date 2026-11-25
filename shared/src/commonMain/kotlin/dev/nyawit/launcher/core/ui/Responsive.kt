package dev.nyawit.launcher.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.platform.currentAppPlatform
import dev.nyawit.launcher.core.platform.isTvPlatform
import dev.nyawit.launcher.theme.LocalNyawitColors
import kotlinx.coroutines.launch

enum class ScreenKind { PHONE, TABLET, DESKTOP, TV }

@Immutable
data class WindowSize(
    val widthDp: Int,
    val heightDp: Int,
) {
    val isLandscape: Boolean get() = widthDp > heightDp
    val minDimension: Int get() = minOf(widthDp, heightDp)
}

val LocalWindowSize = staticCompositionLocalOf { WindowSize(1280, 720) }
val LocalUiScale = staticCompositionLocalOf { 1f }

/**
 * Global UI scale calculation dari WindowSize (mengadopsi arsitektur teruji Diggo POS).
 * Menghitung skala density secara seragam di tingkat root aplikasi.
 */
fun calculateUiScale(widthDp: Int, heightDp: Int, isTv: Boolean = false): Float {
    if (isTv) {
        return if (heightDp <= 560) 0.92f else 1.0f
    }

    val minDimension = minOf(widthDp, heightDp)
    val isLandscape = widthDp > heightDp

    // Ponsel (< 600dp)
    if (minDimension < 600) {
        return if (isLandscape) {
            (heightDp / 380f).coerceIn(0.85f, 1.0f)
        } else {
            (widthDp / 390f).coerceIn(0.85f, 1.04f)
        }
    }

    // Tablet (600..900dp)
    if (minDimension in 600..900) {
        return (minDimension / 768f).coerceIn(0.96f, 1.08f)
    }

    // Desktop / Monitor Layar Lebar
    return when {
        widthDp >= 2560 -> 1.18f
        widthDp >= 1920 -> 1.08f
        widthDp >= 1440 -> 1.04f
        widthDp >= 1100 -> 1.0f
        else -> 0.96f
    }
}

enum class ScalePreference(val factor: Float?, val labelKey: String) {
    AUTO(null, "AUTO"),
    COMPACT(0.85f, "COMPACT"),
    STANDARD(1.0f, "STANDARD"),
    LARGE(1.15f, "LARGE"),
    EXTRA_LARGE(1.30f, "EXTRA_LARGE");

    companion object {
        fun fromKey(key: String): ScalePreference =
            entries.find { it.name.equals(key, ignoreCase = true) } ?: AUTO
    }
}

data class DeviceProfile(
    val kind: ScreenKind,
    val widthDp: Float,
    val heightDp: Float,
    val isLandscape: Boolean = false,
)

/**
 * Konfigurasi Skala Responsif untuk berbagai jenis layar.
 */
data class ResponsiveScale(
    val factor: Float = 1f,
    val columns: Int = 3,
    val sidebarWidth: Dp = 280.dp,
    val isTv: Boolean = false,
) {
    fun dp(base: Dp): Dp = base
    fun dp(base: Int): Dp = base.dp
    fun dp(base: Float): Dp = base.dp
}

val LocalResponsiveScale = staticCompositionLocalOf { ResponsiveScale() }
val LocalDeviceProfile = staticCompositionLocalOf {
    DeviceProfile(
        kind = ScreenKind.DESKTOP,
        widthDp = 1280f,
        heightDp = 720f,
        isLandscape = true,
    )
}

@Composable
fun rememberDeviceProfile(): DeviceProfile = LocalDeviceProfile.current

/**
 * Menghitung faktor skala otomatis yang adaptif dan mulus sesuai ukuran fisik & DPI layar.
 */
fun calculateAutoScale(profile: DeviceProfile): Float {
    if (profile.kind == ScreenKind.TV) {
        return 1.0f // Android TV sudah memiliki DisplayMetrics terkalibrasi untuk 10-foot UI
    }
    return when (profile.kind) {
        ScreenKind.PHONE -> when {
            profile.isLandscape -> 0.90f
            profile.widthDp < 360f -> (profile.widthDp / 375f).coerceIn(0.82f, 0.94f)
            profile.widthDp > 460f -> 1.04f
            else -> 1.0f
        }
        ScreenKind.TABLET -> when {
            profile.widthDp < 768f -> 1.02f
            profile.widthDp <= 1024f -> 1.08f
            else -> 1.12f
        }
        ScreenKind.DESKTOP -> when {
            profile.widthDp < 900f -> 0.94f
            profile.widthDp <= 1366f -> 1.0f
            profile.widthDp <= 1680f -> 1.04f
            profile.widthDp <= 1920f -> 1.08f
            profile.widthDp <= 2560f -> 1.15f
            else -> 1.25f // 4K UHD Ultra-wide
        }
        ScreenKind.TV -> 1.0f
    }
}

/**
 * Menghitung skala responsif adaptif sesuai dengan ukuran dan orientasi layar.
 */
@Composable
fun rememberResponsiveScale(
    profile: DeviceProfile,
    scalePreference: ScalePreference = ScalePreference.AUTO,
): ResponsiveScale {
    return remember(profile, scalePreference) {
        val factor = scalePreference.factor ?: calculateAutoScale(profile)

        val sidebarWidth = when (profile.kind) {
            ScreenKind.TV -> 250.dp
            ScreenKind.DESKTOP -> when {
                profile.widthDp >= 2560f -> 340.dp
                profile.widthDp >= 1920f -> 300.dp
                profile.widthDp >= 1440f -> 280.dp
                profile.widthDp >= 1100f -> 260.dp
                else -> 240.dp
            }
            else -> minOf(300.dp, (profile.widthDp * 0.82f).dp)
        }
        val isCompactLayout = (profile.kind == ScreenKind.PHONE || profile.widthDp < 860f) && profile.kind != ScreenKind.TV
        val availableWidth = if (isCompactLayout) profile.widthDp else (profile.widthDp - sidebarWidth.value)

        val columns = when {
            isCompactLayout && profile.kind == ScreenKind.PHONE && !profile.isLandscape -> 1
            availableWidth < 620f -> 1
            availableWidth < 1000f -> 2
            availableWidth < 1450f -> 3
            availableWidth < 1950f -> 4
            else -> 5
        }
        ResponsiveScale(
            factor = factor,
            columns = columns,
            sidebarWidth = sidebarWidth,
            isTv = profile.kind == ScreenKind.TV,
        )
    }
}

/**
 * Menghitung Density terskala agar seluruh elemen teks dan Dp di seluruh aplikasi
 * beradaptasi secara presisi dengan jarak pandang dan resolusi layar perangkat.
 */
@Composable
fun rememberScaledDensity(scaleFactor: Float): Density {
    val currentDensity = LocalDensity.current
    return remember(currentDensity, scaleFactor) {
        Density(
            density = currentDensity.density * scaleFactor,
            fontScale = currentDensity.fontScale,
        )
    }
}

@Composable
fun Modifier.tvFocusHighlight(
    shape: Shape = RoundedCornerShape(20.dp),
    scale: Float = 1.03f,
    borderWidth: Dp = 2.dp,
): Modifier {
    var focused by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (focused) scale else 1f,
        animationSpec = tween(180),
    )
    val requester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    val c = LocalNyawitColors.current
    return this
        .bringIntoViewRequester(requester)
        .onFocusChanged { state ->
            focused = state.isFocused || state.hasFocus
            if (focused) scope.launch { requester.bringIntoView() }
        }
        .graphicsLayer {
            scaleX = animatedScale
            scaleY = animatedScale
        }
        .then(
            if (focused) Modifier.border(borderWidth, c.Primary, shape)
            else Modifier
        )
}
