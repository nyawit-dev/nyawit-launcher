package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.NyawitColors
import dev.nyawit.launcher.theme.NyawitType
import nyawit_launcher.shared.generated.resources.Res
import nyawit_launcher.shared.generated.resources.garis_arcade
import nyawit_launcher.shared.generated.resources.garis_game
import nyawit_launcher.shared.generated.resources.monopoly_kmp
import nyawit_launcher.shared.generated.resources.nyawit_launcher
import nyawit_launcher.shared.generated.resources.nyawit_music
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private fun iconOf(fullName: String): DrawableResource? = when (fullName.lowercase()) {
    "nyawit-dev/nyawit-launcher" -> Res.drawable.nyawit_launcher
    "nyawit-dev/nyawit-music" -> Res.drawable.nyawit_music
    "nyawit-dev/garis-arcade" -> Res.drawable.garis_arcade
    "nyawit-dev/garis_game" -> Res.drawable.garis_game
    "nyawit-dev/monopoly-kmp" -> Res.drawable.monopoly_kmp
    else -> null
}

/**
 * Ikon aplikasi dari aset lokal. Bila tidak tersedia, tampilkan inisial huruf.
 * Warna & bentuk disesuaikan dengan tema yang aktif.
 */
@Composable
fun AppIcon(
    fullName: String,
    fallbackName: String,
    size: Dp,
    shape: Shape,
    fallbackColor: Color = NyawitColors.Primary,
    containerColor: Color = Color.Transparent,
    borderColor: Color? = null,
    modifier: Modifier = Modifier,
) {
    val icon = iconOf(fullName)
    var boxModifier = modifier.size(size).clip(shape).background(containerColor)
    if (borderColor != null) boxModifier = boxModifier.border(1.dp, borderColor, shape)
    Box(boxModifier, Alignment.Center) {
        if (icon != null) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            androidx.compose.material3.Text(
                fallbackName.first().uppercase(),
                color = fallbackColor,
                style = NyawitType.Title,
                fontWeight = FontWeight.Black,
            )
        }
    }
}
