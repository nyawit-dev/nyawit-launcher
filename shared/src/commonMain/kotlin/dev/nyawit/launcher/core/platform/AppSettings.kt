package dev.nyawit.launcher.core.platform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import dev.nyawit.launcher.core.ui.AppLanguage
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

    init {
        load()
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
            )
            writeLauncherSettings(json.encodeToString(data), platformContext)
        } catch (_: Throwable) {}
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
}

val LocalSettingsController = staticCompositionLocalOf<SettingsController> {
    error("SettingsController not provided")
}
