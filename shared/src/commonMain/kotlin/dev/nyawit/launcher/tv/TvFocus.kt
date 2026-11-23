package dev.nyawit.launcher.tv

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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/** Focus ring + subtle scale for remote/keyboard navigation on Android TV. */
@Composable
fun Modifier.tvFocusHighlight(
    shape: Shape = RoundedCornerShape(20.dp),
    scale: Float = 1.03f,
): Modifier {
    var focused by remember { mutableStateOf(false) }
    val bringIntoView = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    return this
        .bringIntoViewRequester(bringIntoView)
        .onFocusChanged { state ->
            focused = state.isFocused
            if (state.isFocused) scope.launch { bringIntoView.bringIntoView() }
        }
        .graphicsLayer {
            val s = if (focused) scale else 1f
            scaleX = s
            scaleY = s
        }
        .clip(shape)
        .then(
            if (focused) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, shape)
            else Modifier,
        )
}
