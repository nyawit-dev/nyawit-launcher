package dev.nyawit.launcher.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.platform.LocalSettingsController
import dev.nyawit.launcher.core.platform.SettingsController
import dev.nyawit.launcher.core.platform.currentAppPlatform
import dev.nyawit.launcher.core.platform.isTvPlatform
import dev.nyawit.launcher.core.ui.DeviceProfile
import dev.nyawit.launcher.core.ui.LanguageController
import dev.nyawit.launcher.core.ui.LocalDeviceProfile
import dev.nyawit.launcher.core.ui.LocalLanguageController
import dev.nyawit.launcher.core.ui.LocalResponsiveScale
import dev.nyawit.launcher.core.ui.LocalStrings
import dev.nyawit.launcher.core.ui.LocalUiScale
import dev.nyawit.launcher.core.ui.LocalWindowSize
import dev.nyawit.launcher.core.ui.ResponsiveScale
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.WindowSize
import dev.nyawit.launcher.core.ui.calculateUiScale
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
import dev.nyawit.launcher.core.ui.rememberResponsiveScale
import dev.nyawit.launcher.core.ui.rememberScaledDensity
import dev.nyawit.launcher.core.ui.stringsFor

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
}

val LocalNyawitTypography = compositionLocalOf { NyawitTypography() }

object NyawitType {
    val Wordmark: TextStyle @Composable get() = LocalNyawitTypography.current.Wordmark
    val Overline: TextStyle @Composable get() = LocalNyawitTypography.current.Overline
    val Title: TextStyle @Composable get() = LocalNyawitTypography.current.Title
    val Subtitle: TextStyle @Composable get() = LocalNyawitTypography.current.Subtitle
    val Body: TextStyle @Composable get() = LocalNyawitTypography.current.Body
    val Caption: TextStyle @Composable get() = LocalNyawitTypography.current.Caption
}

@Composable
fun LauncherTheme(
    platformContext: Any? = null,
    windowSize: WindowSize? = null,
    themeController: ThemeController = remember { ThemeController() },
    languageController: LanguageController = remember { LanguageController() },
    content: @Composable () -> Unit,
) {
    val settingsController = remember(platformContext) {
        SettingsController(platformContext, themeController, languageController)
    }
    val currentColors = colorsFor(themeController.mode)
    val currentStrings = stringsFor(languageController.language)

    val baseDensity = LocalDensity.current
    val effectiveWindowSize = windowSize ?: WindowSize(1280, 720)
    val isTv = isTvPlatform() || currentAppPlatform() == AppPlatform.TV
    val scale = remember(effectiveWindowSize, isTv) {
        calculateUiScale(effectiveWindowSize.widthDp, effectiveWindowSize.heightDp, isTv)
    }

    val scaledDensity = remember(baseDensity, scale) {
        Density(
            density = baseDensity.density * scale,
            fontScale = baseDensity.fontScale,
        )
    }

    val profile = remember(effectiveWindowSize, isTv) {
        val minDim = minOf(effectiveWindowSize.widthDp, effectiveWindowSize.heightDp)
        val isLandscape = effectiveWindowSize.widthDp > effectiveWindowSize.heightDp
        val isDesktop = currentAppPlatform() == AppPlatform.DESKTOP
        val kind = when {
            isTv -> ScreenKind.TV
            effectiveWindowSize.widthDp < 600 || (!isLandscape && effectiveWindowSize.widthDp < 768) -> ScreenKind.PHONE
            minDim in 600..899 -> ScreenKind.TABLET
            isDesktop -> ScreenKind.DESKTOP
            minDim >= 900 -> ScreenKind.DESKTOP
            else -> ScreenKind.PHONE
        }
        DeviceProfile(
            kind = kind,
            widthDp = effectiveWindowSize.widthDp.toFloat(),
            heightDp = effectiveWindowSize.heightDp.toFloat(),
            isLandscape = isLandscape,
        )
    }

    val responsiveScale = remember(profile, scale) {
        val sidebarWidth = when (profile.kind) {
            ScreenKind.TV -> 250.dp
            ScreenKind.DESKTOP -> when {
                profile.widthDp >= 2560f -> 320.dp
                profile.widthDp >= 1920f -> 290.dp
                profile.widthDp >= 1440f -> 270.dp
                profile.widthDp >= 1100f -> 250.dp
                else -> 230.dp
            }
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
            factor = scale,
            columns = columns,
            sidebarWidth = sidebarWidth,
            isTv = isTv,
        )
    }

    val typography = remember { NyawitTypography() }

    val colorScheme = if (currentColors.isDark) {
        darkColorScheme(
            primary = currentColors.Primary,
            onPrimary = currentColors.OnPrimary,
            background = currentColors.Background,
            onBackground = currentColors.Text,
            surface = currentColors.Surface,
            onSurface = currentColors.Text,
            surfaceVariant = currentColors.SurfaceRaised,
            onSurfaceVariant = currentColors.Muted,
            outline = currentColors.Border,
            error = currentColors.Danger,
        )
    } else {
        lightColorScheme(
            primary = currentColors.Primary,
            onPrimary = currentColors.OnPrimary,
            background = currentColors.Background,
            onBackground = currentColors.Text,
            surface = currentColors.Surface,
            onSurface = currentColors.Text,
            surfaceVariant = currentColors.SurfaceRaised,
            onSurfaceVariant = currentColors.Muted,
            outline = currentColors.Border,
            error = currentColors.Danger,
        )
    }

    CompositionLocalProvider(
        LocalWindowSize provides effectiveWindowSize,
        LocalDeviceProfile provides profile,
        LocalUiScale provides scale,
        LocalDensity provides scaledDensity,
        LocalResponsiveScale provides responsiveScale,
        LocalNyawitTypography provides typography,
        LocalNyawitColors provides currentColors,
        LocalThemeController provides themeController,
        LocalLanguageController provides languageController,
        LocalSettingsController provides settingsController,
        LocalStrings provides currentStrings,
    ) {
        MaterialTheme(colorScheme = colorScheme) {
            androidx.compose.material3.Surface(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                color = currentColors.Background,
            ) {
                content()
            }
        }
    }
}
