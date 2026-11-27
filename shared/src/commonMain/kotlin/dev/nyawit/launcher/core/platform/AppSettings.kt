package dev.nyawit.launcher.core.platform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import dev.nyawit.launcher.core.ui.AppLanguage
import dev.nyawit.launcher.core.ui.ComicSoundPlayer
import dev.nyawit.launcher.core.ui.LanguageController
import dev.nyawit.launcher.core.ui.ScalePreference
import dev.nyawit.launcher.theme.ThemeController
import dev.nyawit.launcher.theme.ThemeMode
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class LauncherSettingsData(
    val themeMode: String = ThemeMode.CYBER_OBSIDIAN.name,
    val language: String = AppLanguage.ID.name,
    val scalePreference: String = ScalePreference.AUTO.name,
    val autoCheckUpdates: Boolean = true,
    val favorites: List<String> = emptyList(),
    val recentlyUsed: List<String> = emptyList(),
    val soundEffectsEnabled: Boolean = true,
    val customCatalogUrl: String = "",
    val minimizeToTray: Boolean = true,
    val startOnBoot: Boolean = false,
)

expect fun readLauncherSettings(platformContext: Any?): String?

expect fun writeLauncherSettings(content: String, platformContext: Any?)

class SettingsController(
    private val platformContext: Any?,
    val themeController: ThemeController,
    val languageController: LanguageController,
) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    var scalePreference by mutableStateOf(ScalePreference.AUTO)
        private set

    var autoCheckUpdates by mutableStateOf(true)
        private set

    var favorites by mutableStateOf<Set<String>>(emptySet())
        private set

    var recentlyUsed by mutableStateOf<List<String>>(emptyList())
        private set

    var soundEffectsEnabled by mutableStateOf(true)
        private set

    var customCatalogUrl by mutableStateOf("")
        private set

    var minimizeToTray by mutableStateOf(true)
        private set

    var startOnBoot by mutableStateOf(false)
        private set

    init {
        load()
        ComicSoundPlayer.enabled = soundEffectsEnabled
        themeController.onModeChanged = { save() }
        languageController.onLanguageChanged = { save() }
    }

    fun load() {
        val raw = readLauncherSettings(platformContext) ?: return
        try {
            val data = json.decodeFromString<LauncherSettingsData>(raw)
            val savedTheme = ThemeMode.entries.find { it.name.equals(data.themeMode, ignoreCase = true) }
            if (savedTheme != null) {
                themeController.set(savedTheme)
            }
            val savedLang = AppLanguage.entries.find { it.name.equals(data.language, ignoreCase = true) }
            if (savedLang != null) {
                languageController.set(savedLang)
            }
            scalePreference = ScalePreference.fromKey(data.scalePreference)
            autoCheckUpdates = data.autoCheckUpdates
            favorites = data.favorites.toSet()
            recentlyUsed = data.recentlyUsed
            soundEffectsEnabled = data.soundEffectsEnabled
            ComicSoundPlayer.enabled = data.soundEffectsEnabled
            customCatalogUrl = data.customCatalogUrl
            minimizeToTray = data.minimizeToTray
            startOnBoot = data.startOnBoot
        } catch (_: Throwable) {
            // Fallback ke default bila terjadi korupsi file konfigurasi
        }
    }

    fun save() {
        try {
            val data = LauncherSettingsData(
                themeMode = themeController.mode.name,
                language = languageController.language.name,
                scalePreference = scalePreference.name,
                autoCheckUpdates = autoCheckUpdates,
                favorites = favorites.toList(),
                recentlyUsed = recentlyUsed,
                soundEffectsEnabled = soundEffectsEnabled,
                customCatalogUrl = customCatalogUrl,
                minimizeToTray = minimizeToTray,
                startOnBoot = startOnBoot,
            )
            writeLauncherSettings(json.encodeToString(data), platformContext)
        } catch (_: Throwable) {}
    }

    fun isFavorite(packageName: String): Boolean = favorites.contains(packageName)

    fun toggleFavorite(packageName: String) {
        favorites = if (favorites.contains(packageName)) {
            favorites - packageName
        } else {
            favorites + packageName
        }
        save()
    }

    fun markUsed(packageName: String) {
        recentlyUsed = (listOf(packageName) + (recentlyUsed - packageName)).take(12)
        save()
    }

    fun setTheme(mode: ThemeMode) {
        themeController.set(mode)
        save()
    }

    fun setLanguage(lang: AppLanguage) {
        languageController.set(lang)
        save()
    }

    fun setScale(pref: ScalePreference) {
        scalePreference = pref
        save()
    }

    fun setAutoCheck(enabled: Boolean) {
        autoCheckUpdates = enabled
        save()
    }

    fun updateSoundEffects(enabled: Boolean) {
        soundEffectsEnabled = enabled
        ComicSoundPlayer.enabled = enabled
        save()
    }

    fun updateCustomCatalog(url: String) {
        customCatalogUrl = url.trim()
        save()
    }

    fun updateMinimizeToTray(enabled: Boolean) {
        minimizeToTray = enabled
        save()
    }

    fun updateStartOnBoot(enabled: Boolean) {
        startOnBoot = enabled
        save()
    }
}

val LocalSettingsController = staticCompositionLocalOf<SettingsController> {
    error("SettingsController not provided")
}
