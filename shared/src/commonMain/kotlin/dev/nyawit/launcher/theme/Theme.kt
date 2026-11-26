package dev.nyawit.launcher.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
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
import dev.nyawit.launcher.core.ui.stringsFor

/**
 * LauncherTheme: Mengintegrasikan pengaturan sistem, tema visual, dan responsivitas
 * ke seluruh hierarki Compose aplikasi.
 */
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
