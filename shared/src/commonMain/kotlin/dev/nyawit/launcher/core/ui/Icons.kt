package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.LocalNyawitColors

/**
 * ComicIcon: Wrapper ikon Material 3 bertema komik pop-art.
 * Menggabungkan siluet resmi Material 3 dengan efek bayangan jatuh tinta keras
 * (hard comic drop shadow offset) khas cetakan komik retro/manga.
 */
@Composable
fun ComicIcon(
    imageVector: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color,
    shadowColor: Color? = null,
    shadowOffset: Dp = 1.5.dp,
) {
    val c = LocalNyawitColors.current
    val shadow = shadowColor ?: if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Comic hard offset shadow layer
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = shadow,
            modifier = Modifier
                .fillMaxSize()
                .offset(x = shadowOffset, y = shadowOffset),
        )
        // Foreground pop color icon
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun SearchIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Search,
        contentDescription = "Search",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun DownloadIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Download,
        contentDescription = "Download",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun ArrowIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Arrow",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun GridIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.GridView,
        contentDescription = "Grid",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun GamepadIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.SportsEsports,
        contentDescription = "Games",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun MusicIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.MusicNote,
        contentDescription = "Music",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun RocketIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.RocketLaunch,
        contentDescription = "Rocket",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun SyncIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Refresh,
        contentDescription = "Sync",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun MenuIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Menu,
        contentDescription = "Menu",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun InfoIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Info,
        contentDescription = "Info",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun PaletteIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Palette,
        contentDescription = "Theme",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun CheckIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Check,
        contentDescription = "Check",
        modifier = modifier,
        tint = color,
        shadowOffset = 1.dp,
    )
}

@Composable
fun GlobeIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Public,
        contentDescription = "Language",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun SettingsIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Settings,
        contentDescription = "Settings",
        modifier = modifier,
        tint = color,
    )
}

@Composable
fun CrossIcon(modifier: Modifier = Modifier, color: Color) {
    ComicIcon(
        imageVector = Icons.Filled.Close,
        contentDescription = "Close",
        modifier = modifier,
        tint = color,
    )
}
