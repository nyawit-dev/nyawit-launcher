package dev.nyawit.launcher.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Palet Cybernetic Studio:
 * Latar obsidian dalam, permukaan beraksen hardware plate, dan neon mint terarah.
 */
object NyawitColors {
    val Background = Color(0xFF080A0E)
    val Surface = Color(0xFF11151D)
    val SurfaceRaised = Color(0xFF171D27)
    val SurfaceHighlight = Color(0xFF222938)
    val Border = Color(0xFF1E2635)
    val BorderHighlight = Color(0xFF2E3B52)
    val Primary = Color(0xFF00F0A8)
    val PrimaryMuted = Color(0xFF0A2B20)
    val OnPrimary = Color(0xFF03170F)
    val Warning = Color(0xFFFFB020)
    val Danger = Color(0xFFFF4D5E)
    val Text = Color(0xFFF8FAFC)
    val Muted = Color(0xFF94A3B8)
    val Faint = Color(0xFF4E5D78)
}

val LocalNyawitColors = staticCompositionLocalOf { NyawitColors }

object NyawitType {
    val Wordmark = TextStyle(
        fontSize = 22.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
    )
    val Overline = TextStyle(
        fontSize = 10.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.6.sp,
    )
    val Title = TextStyle(
        fontSize = 18.sp,
        lineHeight = 23.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.2).sp,
    )
    val Body = TextStyle(
        fontSize = 13.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.Normal,
    )
    val Caption = TextStyle(
        fontSize = 11.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.Medium,
    )
}

private val DarkScheme = darkColorScheme(
    primary = NyawitColors.Primary,
    onPrimary = NyawitColors.OnPrimary,
    background = NyawitColors.Background,
    onBackground = NyawitColors.Text,
    surface = NyawitColors.Surface,
    onSurface = NyawitColors.Text,
    surfaceVariant = NyawitColors.SurfaceRaised,
    onSurfaceVariant = NyawitColors.Muted,
    outline = NyawitColors.Border,
    error = NyawitColors.Danger,
)

@Composable
fun LauncherTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNyawitColors provides NyawitColors) {
        MaterialTheme(colorScheme = DarkScheme, content = content)
    }
}
