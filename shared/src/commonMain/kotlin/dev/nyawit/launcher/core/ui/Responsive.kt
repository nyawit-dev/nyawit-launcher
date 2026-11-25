package dev.nyawit.launcher.core.ui

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.platform.isTvPlatform
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

data class DeviceProfile(val kind: ScreenKind, val widthDp: Float)

/**
 * Konfigurasi Skala Responsif untuk berbagai jenis layar.
 */
data class ResponsiveScale(
    val factor: Float = 1f,
    val columns: Int = 3,
    val sidebarWidth: Dp = 224.dp,
    val isTv: Boolean = false,
)

val LocalResponsiveScale = staticCompositionLocalOf { ResponsiveScale() }

@Composable
fun rememberDeviceProfile(): DeviceProfile {
    val width = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp().value }
    val kind = when {
        isTvPlatform() -> ScreenKind.TV
        width >= 1200 -> ScreenKind.DESKTOP
        width >= 600 -> ScreenKind.TABLET
        else -> ScreenKind.PHONE
    }
    return remember(kind, width) { DeviceProfile(kind, width) }
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
        val factor = scalePreference.factor ?: when (profile.kind) {
            ScreenKind.TV -> 1.30f
            ScreenKind.TABLET -> 1.08f
            ScreenKind.PHONE -> if (profile.widthDp < 360f) 0.92f else 1.0f
            ScreenKind.DESKTOP -> if (profile.widthDp >= 1800f) 1.15f else 1.0f
        }
        val columns = when (profile.kind) {
            ScreenKind.PHONE -> 1
            ScreenKind.TABLET -> 2
            ScreenKind.TV -> 3
            ScreenKind.DESKTOP -> if (profile.widthDp >= 1600f) 4 else 3
        }
        val sidebarWidth = when (profile.kind) {
            ScreenKind.TV -> 260.dp
            ScreenKind.DESKTOP -> if (profile.widthDp >= 1600f) 248.dp else 224.dp
            else -> 280.dp // Drawer width on phone/tablet
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
): Modifier {
    var focused by remember { mutableStateOf(false) }
    val requester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    return this
        .bringIntoViewRequester(requester)
        .onFocusChanged { state ->
            focused = state.isFocused
            if (state.isFocused) scope.launch { requester.bringIntoView() }
        }
        .graphicsLayer {
            val s = if (focused) scale else 1f
            scaleX = s
            scaleY = s
        }
        .clip(shape)
        .then(if (focused) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, shape) else Modifier)
}
