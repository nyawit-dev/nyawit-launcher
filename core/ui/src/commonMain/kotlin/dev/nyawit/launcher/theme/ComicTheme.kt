package dev.nyawit.launcher.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pilihan Tema Nyawit Launcher.
 */
enum class ThemeMode(val label: String, val description: String) {
    CYBER_OBSIDIAN("Cyber Obsidian", "Mode gelap presisi dengan aksen neon emerald"),
    MIDNIGHT_SAPPHIRE("Midnight Studio", "Latar indigo kosmik dengan aksen sapphire cyan"),
    OLED_BLACK("OLED Pure Black", "Kontras ultra tinggi, hitam absolut hemat daya"),
    NORDIC_LIGHT("Nordic Clean Light", "Mode terang berkelas dan kontras tajam"),
}

/**
 * Skema warna token Nyawit Design System.
 */
open class NyawitColors(
    val Background: Color,
    val Surface: Color,
    val SurfaceRaised: Color,
    val SurfaceHighlight: Color,
    val Border: Color,
    val BorderHighlight: Color,
    val Primary: Color,
    val PrimaryMuted: Color,
    val OnPrimary: Color,
    val Warning: Color,
    val OnWarning: Color = Color(0xFF0F172A),
    val Danger: Color,
    val OnDanger: Color = Color(0xFFFFFFFF),
    val Text: Color,
    val Muted: Color,
    val Faint: Color,
    val isDark: Boolean = true,
) {
    // Token warna khusus estetika komik & pop-art
    val ComicInk: Color get() = if (isDark) Color(0xFF64748B) else Color(0xFF0F172A)
    val ComicShadow: Color get() = if (isDark) Color(0xFF000000) else Color(0xFF0F172A)
    val ComicShadowRim: Color get() = if (isDark) Primary.copy(alpha = 0.55f) else Color(0xFF0F172A)
    val ComicBurstYellow: Color get() = Color(0xFFFFDE00)
    val ComicActionRed: Color get() = Color(0xFFFF4757)
    val ComicCyan: Color get() = Color(0xFF00D2D3)

    companion object : NyawitColors(
        Background = Color(0xFF090C10),
        Surface = Color(0xFF111620),
        SurfaceRaised = Color(0xFF181F2C),
        SurfaceHighlight = Color(0xFF222B3D),
        Border = Color(0xFF1F293D),
        BorderHighlight = Color(0xFF2F3E5C),
        Primary = Color(0xFF00F0A8),
        PrimaryMuted = Color(0xFF092E22),
        OnPrimary = Color(0xFF02160F),
        Warning = Color(0xFFFFB020),
        OnWarning = Color(0xFF090C10),
        Danger = Color(0xFFFF4D5E),
        OnDanger = Color(0xFFFFFFFF),
        Text = Color(0xFFF8FAFC),
        Muted = Color(0xFF94A3B8),
        Faint = Color(0xFF52617A),
        isDark = true,
    )
}

val CyberObsidianColors = NyawitColors(
    Background = Color(0xFF090C10),
    Surface = Color(0xFF111620),
    SurfaceRaised = Color(0xFF181F2C),
    SurfaceHighlight = Color(0xFF222B3D),
    Border = Color(0xFF1F293D),
    BorderHighlight = Color(0xFF2F3E5C),
    Primary = Color(0xFF00F0A8),
    PrimaryMuted = Color(0xFF092E22),
    OnPrimary = Color(0xFF02160F),
    Warning = Color(0xFFFFB020),
    OnWarning = Color(0xFF090C10),
    Danger = Color(0xFFFF4D5E),
    OnDanger = Color(0xFFFFFFFF),
    Text = Color(0xFFF8FAFC),
    Muted = Color(0xFF94A3B8),
    Faint = Color(0xFF52617A),
    isDark = true,
)

val MidnightSapphireColors = NyawitColors(
    Background = Color(0xFF080C16),
    Surface = Color(0xFF0F1728),
    SurfaceRaised = Color(0xFF162035),
    SurfaceHighlight = Color(0xFF202C48),
    Border = Color(0xFF1E2B45),
    BorderHighlight = Color(0xFF31436B),
    Primary = Color(0xFF38BDF8),
    PrimaryMuted = Color(0xFF0C2742),
    OnPrimary = Color(0xFF041322),
    Warning = Color(0xFFFBBF24),
    OnWarning = Color(0xFF080C16),
    Danger = Color(0xFFF43F5E),
    OnDanger = Color(0xFFFFFFFF),
    Text = Color(0xFFF1F5F9),
    Muted = Color(0xFF94A3B8),
    Faint = Color(0xFF475569),
    isDark = true,
)

val OledBlackColors = NyawitColors(
    Background = Color(0xFF000000),
    Surface = Color(0xFF0A0A0C),
    SurfaceRaised = Color(0xFF141418),
    SurfaceHighlight = Color(0xFF222228),
    Border = Color(0xFF24242C),
    BorderHighlight = Color(0xFF3E3E4C),
    Primary = Color(0xFF00FFA3),
    PrimaryMuted = Color(0xFF042217),
    OnPrimary = Color(0xFF000000),
    Warning = Color(0xFFFFB703),
    OnWarning = Color(0xFF000000),
    Danger = Color(0xFFFF334B),
    OnDanger = Color(0xFFFFFFFF),
    Text = Color(0xFFFFFFFF),
    Muted = Color(0xFFA1A1AA),
    Faint = Color(0xFF52525B),
    isDark = true,
)

val NordicLightColors = NyawitColors(
    Background = Color(0xFFF4F6F9),
    Surface = Color(0xFFFFFFFF),
    SurfaceRaised = Color(0xFFE9EEF5),
    SurfaceHighlight = Color(0xFFDDE4EE),
    Border = Color(0xFFD3DCE8),
    BorderHighlight = Color(0xFFB4C4D8),
    Primary = Color(0xFF059669),
    PrimaryMuted = Color(0xFFE0F7EF),
    OnPrimary = Color(0xFFFFFFFF),
    Warning = Color(0xFFD97706),
    OnWarning = Color(0xFFFFFFFF),
    Danger = Color(0xFFE11D48),
    OnDanger = Color(0xFFFFFFFF),
    Text = Color(0xFF0F172A),
    Muted = Color(0xFF475569),
    Faint = Color(0xFF64748B),
    isDark = false,
)

fun colorsFor(mode: ThemeMode): NyawitColors = when (mode) {
    ThemeMode.CYBER_OBSIDIAN -> CyberObsidianColors
    ThemeMode.MIDNIGHT_SAPPHIRE -> MidnightSapphireColors
    ThemeMode.OLED_BLACK -> OledBlackColors
    ThemeMode.NORDIC_LIGHT -> NordicLightColors
}

/**
 * Controller untuk mengelola pemilihan tema saat aplikasi berjalan.
 */
class ThemeController(initialMode: ThemeMode = ThemeMode.CYBER_OBSIDIAN) {
    var mode by mutableStateOf(initialMode)
        private set

    var onModeChanged: ((ThemeMode) -> Unit)? = null

    fun set(newMode: ThemeMode) {
        mode = newMode
        onModeChanged?.invoke(newMode)
    }

    fun next() {
        val modes = ThemeMode.values()
        val nextIdx = (mode.ordinal + 1) % modes.size
        set(modes[nextIdx])
    }
}

val LocalNyawitColors = compositionLocalOf<NyawitColors> { NyawitColors }
val LocalThemeController = compositionLocalOf { ThemeController() }

/**
 * ComicTheme: Provider tema komik/manga multiplatform.
 */
@Composable
fun ComicTheme(
    mode: ThemeMode = ThemeMode.CYBER_OBSIDIAN,
    controller: ThemeController = remember(mode) { ThemeController(mode) },
    content: @Composable () -> Unit,
) {
    val colors = colorsFor(controller.mode)
    val typography = remember { NyawitTypography() }
    CompositionLocalProvider(
        LocalNyawitColors provides colors,
        LocalThemeController provides controller,
        LocalNyawitTypography provides typography,
        content = content,
    )
}

class NyawitTypography {
    val Wordmark: TextStyle = TextStyle(
        fontSize = 22.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
    )
    val Overline: TextStyle = TextStyle(
        fontSize = 10.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.6.sp,
    )
    val Title: TextStyle = TextStyle(
        fontSize = 18.sp,
        lineHeight = 23.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.2).sp,
    )
    val Subtitle: TextStyle = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
    )
    val Body: TextStyle = TextStyle(
        fontSize = 13.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.Normal,
    )
    val Caption: TextStyle = TextStyle(
        fontSize = 11.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.Medium,
    )
    val ComicSoundEffect: TextStyle = TextStyle(
        fontSize = 12.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
    )
}

val LocalNyawitTypography = compositionLocalOf { NyawitTypography() }

object NyawitType {
    val Wordmark: TextStyle @Composable get() = LocalNyawitTypography.current.Wordmark
    val Overline: TextStyle @Composable get() = LocalNyawitTypography.current.Overline
    val Title: TextStyle @Composable get() = LocalNyawitTypography.current.Title
    val Subtitle: TextStyle @Composable get() = LocalNyawitTypography.current.Subtitle
    val Body: TextStyle @Composable get() = LocalNyawitTypography.current.Body
    val Caption: TextStyle @Composable get() = LocalNyawitTypography.current.Caption
    val ComicSoundEffect: TextStyle @Composable get() = LocalNyawitTypography.current.ComicSoundEffect
}

object ComicDimens {
    val BorderWidth: Dp = 2.5.dp
    val ThinBorderWidth: Dp = 1.5.dp
    val ShadowOffset: Dp = 4.dp
    val SmallShadowOffset: Dp = 2.5.dp
    val LargeShadowOffset: Dp = 6.dp
}
