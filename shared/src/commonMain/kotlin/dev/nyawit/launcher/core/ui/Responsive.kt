package dev.nyawit.launcher.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
    val sidebarWidth: Dp = 224.dp,
    val isTv: Boolean = false,
) {
    fun dp(base: Dp): Dp = base * factor
    fun dp(base: Int): Dp = (base * factor).dp
    fun dp(base: Float): Dp = (base * factor).dp
}

val LocalResponsiveScale = staticCompositionLocalOf { ResponsiveScale() }

@Composable
fun rememberDeviceProfile(): DeviceProfile {
    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize
    val width = with(density) { containerSize.width.toDp().value }
    val height = with(density) { containerSize.height.toDp().value }
    val minDimension = minOf(width, height)
    val isLandscape = width > height
    val platform = remember { currentAppPlatform() }

    val kind = when {
        isTvPlatform() || platform == AppPlatform.TV -> ScreenKind.TV
        platform == AppPlatform.DESKTOP -> ScreenKind.DESKTOP
        minDimension >= 600f -> ScreenKind.TABLET
        else -> ScreenKind.PHONE
    }
    return remember(kind, width, height, isLandscape) {
        DeviceProfile(
            kind = kind,
            widthDp = width,
            heightDp = height,
            isLandscape = isLandscape,
        )
    }
}

/**
 * Menghitung faktor skala otomatis yang adaptif dan mulus sesuai ukuran fisik & DPI layar.
 */
fun calculateAutoScale(profile: DeviceProfile): Float {
    if (profile.kind == ScreenKind.TV) {
        return 1.35f // 10-foot UI experience untuk TV
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
            profile.widthDp <= 1680f -> 1.06f
            profile.widthDp <= 1920f -> 1.12f
            profile.widthDp <= 2560f -> 1.24f
            else -> 1.38f // 4K UHD Ultra-wide
        }
        ScreenKind.TV -> 1.35f
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
            ScreenKind.TV -> 260.dp
            ScreenKind.DESKTOP -> if (profile.widthDp >= 1600f) 248.dp else 224.dp
            else -> minOf(280.dp, (profile.widthDp * 0.82f).dp)
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
            fontScale = currentDensity.fontScale * scaleFactor,
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
