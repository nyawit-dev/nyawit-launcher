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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.platform.isTvPlatform
import kotlinx.coroutines.launch

enum class ScreenKind { PHONE, TABLET, TV }

data class DeviceProfile(val kind: ScreenKind, val widthDp: Float)

@Composable
fun rememberDeviceProfile(): DeviceProfile {
    val width = LocalWindowInfo.current.containerSize.width.toFloat()
    val kind = when {
        isTvPlatform() -> ScreenKind.TV
        width >= 840 -> ScreenKind.TABLET
        else -> ScreenKind.PHONE
    }
    return DeviceProfile(kind, width)
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
