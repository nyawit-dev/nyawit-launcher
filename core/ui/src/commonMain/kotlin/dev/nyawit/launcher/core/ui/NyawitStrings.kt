package dev.nyawit.launcher.core.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Pilihan bahasa yang didukung di Nyawit Launcher.
 */
enum class AppLanguage(val code: String, val label: String, val nativeName: String) {
    ID("id", "Bahasa Indonesia", "Bahasa Indonesia"),
    EN("en", "English", "English"),
}

/**
 * Pengontrol bahasa aktif yang dapat dialihkan secara runtime.
 */
class LanguageController(initialLanguage: AppLanguage = AppLanguage.ID) {
    var language by mutableStateOf(initialLanguage)
        private set

    var onLanguageChanged: ((AppLanguage) -> Unit)? = null

    fun set(newLang: AppLanguage) {
        language = newLang
        NyawitStrings.active = stringsFor(newLang)
        onLanguageChanged?.invoke(newLang)
    }

    fun toggle() {
        val next = if (language == AppLanguage.ID) AppLanguage.EN else AppLanguage.ID
        set(next)
    }
}

val LocalLanguageController = compositionLocalOf { LanguageController() }

/**
 * Kontrak terpusat string antarmuka pengguna.
 */
open class LanguageStrings(
    val BRAND_NAME: String,
    val BRAND_SUBTITLE: String,
    val BRAND_FULL_TITLE: String,
    val BRAND_TAGLINE: String,
    val NAV_TITLE: String,
    val NAV_APPS: String,
    val NAV_SETTINGS: String,
    val NAV_ABOUT: String,
    val THEME_SECTION: String,
    val THEME_TAP_TO_CHANGE: String,
    val THEME_ACTIVE_BADGE: String,
    val LANGUAGE_SECTION: String,
    val LANGUAGE_TAP_TO_CHANGE: String,
    val STATUS_TITLE: String,
    val STATUS_UP_TO_DATE: String,
    val BTN_CHECK_UPDATES: String,
    val BTN_OPEN: String,
    val BTN_OPEN_APP: String,
    val BTN_INSTALL: String,
    val BTN_INSTALL_APP: String,
    val BTN_UPDATE: String,
    val BTN_UPDATE_NOW: String,
    val BTN_UNINSTALL: String,
    val BTN_CLOSE: String,
    val BTN_CANCEL: String,
    val BTN_RETRY: String,
    val BTN_LATER: String,
    val BTN_DONE: String,
    val BTN_DETAIL: String,
    val SEARCH_PLACEHOLDER: String,
    val KEY_ESC: String = "ESC",
    val CATEGORY_ALL: String,
    val SYMBOL_CLOSE: String = "✕",
    val CHIP_AVAILABLE: String,
    val CHIP_INSTALLED: String,
    val CHIP_UPDATE: String,
    val CHIP_UPDATE_AVAILABLE: String,
    val CHIP_UNKNOWN: String = "Unknown",
    val CHIP_FEATURED: String,
    val CHIP_PINNED: String = "PINNED",
    val PROGRESS_DOWNLOADING: String,
    val PROGRESS_PROCESSING: String,
    val NOTICE_TIMEOUT: String,
    val NOTICE_CHECK_FAILED: String,
    val ACTION_CONTINUE: String,
    val ERROR_CATALOG_LOAD: String,
    val EMPTY_APPS_TITLE: String,
    val EMPTY_APPS_DESC: String,
    val SECTION_ECOSYSTEM: String,
    val SECTION_ALL_APPS: String,
    val DETAIL_DEVELOPER: String,
    val DETAIL_CATEGORY: String,
    val DETAIL_PLATFORM: String,
    val DETAIL_STATUS: String,
    val DETAIL_PACKAGE_ID: String = "PACKAGE ID",
    val DETAIL_VERIFICATION_NOTICE: String,
    val ABOUT_TITLE: String,
    val ABOUT_THEME_TITLE: String,
    val ABOUT_LANGUAGE_TITLE: String,
    val ABOUT_DESC: String,
    val ABOUT_LABEL_VERSION: String,
    val ABOUT_LABEL_PLATFORMS: String,
    val ABOUT_LABEL_IDENTIFIER: String = "Identifier",
    val ABOUT_LABEL_OWNER: String,
    val ABOUT_LABEL_LICENSE: String,
    val ABOUT_LABEL_FRAMEWORK: String,
    val ABOUT_VAL_PLATFORMS: String = "Android · Android TV · Desktop JVM",
    val ABOUT_VAL_LICENSE: String = "GPL-3.0",
    val ABOUT_VAL_FRAMEWORK: String = "Kotlin Multiplatform · Compose Multiplatform",
    val ABOUT_LINKS_TITLE: String,
    val ABOUT_LINK_REPO: String,
    val ABOUT_LINK_CATALOG: String,
    val ABOUT_LINK_ORG: String,
    val ABOUT_SECURITY_FOOTER: String,
    val SETTINGS_TITLE: String,
    val SETTINGS_SUBTITLE: String,
    val SETTINGS_SEC_APPEARANCE: String,
    val SETTINGS_SEC_APPEARANCE_DESC: String,
    val SETTINGS_SEC_LANGUAGE: String,
    val SETTINGS_SEC_LANGUAGE_DESC: String,
    val SETTINGS_SEC_SCALE: String,
    val SETTINGS_SEC_SCALE_DESC: String,
    val SETTINGS_SCALE_AUTO: String,
    val SETTINGS_SCALE_COMPACT: String,
    val SETTINGS_SCALE_STANDARD: String,
    val SETTINGS_SCALE_LARGE: String,
    val SETTINGS_SCALE_XLARGE: String,
    val SETTINGS_SEC_STORAGE: String,
    val SETTINGS_SEC_STORAGE_DESC: String,
    val SETTINGS_BTN_CLEAR_CACHE: String,
    val SETTINGS_CACHE_CLEARED: String,
    val SETTINGS_SEC_UPDATES: String,
    val SETTINGS_SEC_UPDATES_DESC: String,
    val SETTINGS_AUTO_CHECK_TITLE: String,
    val SETTINGS_AUTO_CHECK_DESC: String,
    val SETTINGS_STORAGE_SAVED_NOTICE: String,
    val SETTINGS_BTN_OPEN: String,
    val UPDATE_CHECKING: String,
    val UPDATE_DOWNLOADING: String,
    val UPDATE_READY: String,
    val UPDATE_FAILED: String,
    val UPDATE_PREPARING: String,
    val UPDATE_UP_TO_DATE_BADGE: String,
    val UPDATE_AVAILABLE_BADGE: String,
    val UPDATE_UP_TO_DATE_TITLE: String,
    val UPDATE_PACKAGE_LABEL: String,
    val statusAppsRegistered: (Int) -> String,
    val statusUpdatesAvailable: (Int) -> String,
    val statusLauncherUpdate: (String) -> String,
    val progressDownloadingPercent: (Int) -> String,
    val noticeUpToDate: (String) -> String,
    val noticeUpdatingLauncher: (String) -> String,
    val noticeUpdateInstalled: (String) -> String,
    val noticeUpdateFailed: (String) -> String,
    val noticeDownloadingApp: (String) -> String,
    val noticeOpeningInstaller: (String) -> String,
    val noticePlatformNotSupported: (String) -> String,
    val noticeOpenFailed: (String, String?) -> String,
    val sectionCategory: (String) -> String,
    val sectionAppCount: (Int) -> String,
    val sectionUpdateCount: (Int) -> String,
    val updateUpgradePrompt: (String) -> String,
    val updateCurrentVersionDesc: (String) -> String,
    val updateUpToDateDesc: (String) -> String,
    val updateReadyToUse: (String) -> String,
)

val IndonesianStrings = LanguageStrings(
    BRAND_NAME = "NYAWIT",
    BRAND_SUBTITLE = "LAUNCHER",
    BRAND_FULL_TITLE = "NYAWIT LAUNCHER",
    BRAND_TAGLINE = "Ekosistem aplikasi open-source tanpa server perantara",
    NAV_TITLE = "NAVIGASI",
    NAV_APPS = "Katalog Aplikasi",
    NAV_SETTINGS = "Pengaturan",
    NAV_ABOUT = "Tentang Sistem",
    THEME_SECTION = "TEMA VISUAL",
    THEME_TAP_TO_CHANGE = "Ketuk ganti tema",
    THEME_ACTIVE_BADGE = "AKTIF",
    LANGUAGE_SECTION = "BAHASA SISTEM",
    LANGUAGE_TAP_TO_CHANGE = "Ketuk ganti bahasa",
    STATUS_TITLE = "STATUS SISTEM",
    STATUS_UP_TO_DATE = "Semua sudah mutakhir",
    BTN_CHECK_UPDATES = "Cek Pembaruan",
    BTN_OPEN = "Buka",
    BTN_OPEN_APP = "Buka Aplikasi",
    BTN_INSTALL = "Pasang",
    BTN_INSTALL_APP = "Pasang Aplikasi",
    BTN_UPDATE = "Perbarui",
    BTN_UPDATE_NOW = "Perbarui Sekarang",
    BTN_UNINSTALL = "Hapus",
    BTN_CLOSE = "Tutup",
    BTN_CANCEL = "Batal",
    BTN_RETRY = "Coba lagi",
    BTN_LATER = "Nanti",
    BTN_DONE = "Selesai",
    BTN_DETAIL = "Detail Rilis",
    SEARCH_PLACEHOLDER = "Cari aplikasi atau kategori…",
    CATEGORY_ALL = "Semua",
    CHIP_AVAILABLE = "Tersedia",
    CHIP_INSTALLED = "Terpasang",
    CHIP_UPDATE = "Update",
    CHIP_UPDATE_AVAILABLE = "Pembaruan Tersedia",
    CHIP_FEATURED = "★ UNGGULAN EKOSISTEM",
    PROGRESS_DOWNLOADING = "MENGUNDUH PAKET",
    PROGRESS_PROCESSING = "Memproses…",
    NOTICE_TIMEOUT = "Pembaruan tidak merespons — coba lagi",
    NOTICE_CHECK_FAILED = "Gagal mengecek rilis terbaru",
    ACTION_CONTINUE = "Lanjutkan",
    ERROR_CATALOG_LOAD = "Katalog tidak dapat dimuat",
    EMPTY_APPS_TITLE = "Tidak ada aplikasi",
    EMPTY_APPS_DESC = "Coba gunakan kata kunci atau pilih kategori lain.",
    SECTION_ECOSYSTEM = "KATALOG EKOSISTEM",
    SECTION_ALL_APPS = "Semua Aplikasi",
    DETAIL_DEVELOPER = "PENGEMBANG",
    DETAIL_CATEGORY = "KATEGORI",
    DETAIL_PLATFORM = "PLATFORM",
    DETAIL_STATUS = "STATUS",
    DETAIL_VERIFICATION_NOTICE = "Paket diunduh langsung dari GitHub Releases dengan verifikasi checksum SHA-256 tanpa perantara.",
    ABOUT_TITLE = "INFORMASI SISTEM",
    ABOUT_THEME_TITLE = "TEMA & TAMPILAN",
    ABOUT_LANGUAGE_TITLE = "BAHASA & REGIONAL",
    ABOUT_DESC = "Launcher ringan untuk menemukan, memasang, dan memperbarui aplikasi ekosistem Nyawit langsung dari GitHub Releases. Berjalan di Android, Android TV, dan desktop dengan satu pengalaman terpadu.",
    ABOUT_LABEL_VERSION = "Versi Aplikasi",
    ABOUT_LABEL_PLATFORMS = "Platform Target",
    ABOUT_LABEL_OWNER = "Organisasi GitHub",
    ABOUT_LABEL_LICENSE = "Lisensi",
    ABOUT_LABEL_FRAMEWORK = "Framework",
    ABOUT_LINKS_TITLE = "TAUTAN RESMI",
    ABOUT_LINK_REPO = "Repositori",
    ABOUT_LINK_CATALOG = "Katalog",
    ABOUT_LINK_ORG = "Organisasi",
    ABOUT_SECURITY_FOOTER = "Seluruh paket diverifikasi dengan SHA-256 bila tersedia. Tidak ada server perantara — katalog dan rilis berasal langsung dari GitHub API.",
    SETTINGS_TITLE = "PENGATURAN LAUNCHER",
    SETTINGS_SUBTITLE = "Kelola estetika tema, bahasa, penskalaan layar, dan preferensi aplikasi",
    SETTINGS_SEC_APPEARANCE = "TAMPILAN & TEMA",
    SETTINGS_SEC_APPEARANCE_DESC = "Pilih palet visual antarmuka launcher sesuai kenyamanan mata Anda",
    SETTINGS_SEC_LANGUAGE = "BAHASA & REGIONAL",
    SETTINGS_SEC_LANGUAGE_DESC = "Pilih bahasa pengantar antarmuka di seluruh aplikasi",
    SETTINGS_SEC_SCALE = "PENSKALAAN LAYAR",
    SETTINGS_SEC_SCALE_DESC = "Sesuaikan proporsi elemen antarmuka agar optimal di ukuran monitor/layar Anda",
    SETTINGS_SCALE_AUTO = "Otomatis (Adaptif)",
    SETTINGS_SCALE_COMPACT = "Kompak (0.85x)",
    SETTINGS_SCALE_STANDARD = "Standar (1.0x)",
    SETTINGS_SCALE_LARGE = "Besar (1.15x)",
    SETTINGS_SCALE_XLARGE = "Sangat Besar (1.30x)",
    SETTINGS_SEC_STORAGE = "PENYIMPANAN & CACHE",
    SETTINGS_SEC_STORAGE_DESC = "Kelola berkas cache katalog lokal yang tersimpan di perangkat",
    SETTINGS_BTN_CLEAR_CACHE = "Bersihkan Cache Katalog",
    SETTINGS_CACHE_CLEARED = "Cache katalog berhasil dibersihkan",
    SETTINGS_SEC_UPDATES = "SISTEM & PEMBARUAN",
    SETTINGS_SEC_UPDATES_DESC = "Pengaturan otomatisasi untuk memeriksa versi terbaru aplikasi",
    SETTINGS_AUTO_CHECK_TITLE = "Periksa Pembaruan Saat Mulai",
    SETTINGS_AUTO_CHECK_DESC = "Secara otomatis memeriksa versi baru dari katalog saat aplikasi dibuka",
    SETTINGS_STORAGE_SAVED_NOTICE = "Seluruh preferensi tersimpan otomatis dan persisten di perangkat",
    SETTINGS_BTN_OPEN = "Buka Pengaturan",
    UPDATE_CHECKING = "Mengecek rilis",
    UPDATE_DOWNLOADING = "Mengunduh pembaruan",
    UPDATE_READY = "Pembaruan siap",
    UPDATE_FAILED = "Pembaruan gagal",
    UPDATE_PREPARING = "Menyiapkan pembaruan launcher.",
    UPDATE_UP_TO_DATE_BADGE = "SUDAH MUTAKHIR",
    UPDATE_AVAILABLE_BADGE = "PEMBARUAN TERSEDIA",
    UPDATE_UP_TO_DATE_TITLE = "Aplikasi Sudah Versi Terbaru",
    UPDATE_PACKAGE_LABEL = "Paket rilis baru",
    statusAppsRegistered = { "$it aplikasi terdaftar" },
    statusUpdatesAvailable = { "$it pembaruan tersedia" },
    statusLauncherUpdate = { "Update launcher $it" },
    progressDownloadingPercent = { "Mengunduh $it%" },
    noticeUpToDate = { "Aplikasi sudah versi terbaru (v$it)" },
    noticeUpdatingLauncher = { "Memasang pembaruan launcher $it…" },
    noticeUpdateInstalled = { "Pembaruan launcher $it dipasang otomatis" },
    noticeUpdateFailed = { "Gagal memperbarui launcher: $it" },
    noticeDownloadingApp = { "Mengunduh $it…" },
    noticeOpeningInstaller = { "Membuka installer $it…" },
    noticePlatformNotSupported = { "$it tidak menyediakan installer untuk platform ini" },
    noticeOpenFailed = { name, detail -> if (detail != null) "Gagal membuka $name: $detail" else "Gagal membuka $name" },
    sectionCategory = { "Kategori $it" },
    sectionAppCount = { "$it Aplikasi" },
    sectionUpdateCount = { "$it update" },
    updateUpgradePrompt = { "Perbarui ke v$it?" },
    updateCurrentVersionDesc = { "Versi terpasang v$it" },
    updateUpToDateDesc = { "Versi saat ini v$it adalah rilis terbaru yang tersedia." },
    updateReadyToUse = { "Versi v$it sudah terpasang dan siap digunakan." },
)

val EnglishStrings = LanguageStrings(
    BRAND_NAME = "NYAWIT",
    BRAND_SUBTITLE = "LAUNCHER",
    BRAND_FULL_TITLE = "NYAWIT LAUNCHER",
    BRAND_TAGLINE = "Open-source application ecosystem with no intermediary server",
    NAV_TITLE = "NAVIGATION",
    NAV_APPS = "App Catalog",
    NAV_SETTINGS = "Settings",
    NAV_ABOUT = "About System",
    THEME_SECTION = "VISUAL THEME",
    THEME_TAP_TO_CHANGE = "Tap to change theme",
    THEME_ACTIVE_BADGE = "ACTIVE",
    LANGUAGE_SECTION = "SYSTEM LANGUAGE",
    LANGUAGE_TAP_TO_CHANGE = "Tap to switch language",
    STATUS_TITLE = "SYSTEM STATUS",
    STATUS_UP_TO_DATE = "Everything is up to date",
    BTN_CHECK_UPDATES = "Check for Updates",
    BTN_OPEN = "Open",
    BTN_OPEN_APP = "Open App",
    BTN_INSTALL = "Install",
    BTN_INSTALL_APP = "Install App",
    BTN_UPDATE = "Update",
    BTN_UPDATE_NOW = "Update Now",
    BTN_UNINSTALL = "Uninstall",
    BTN_CLOSE = "Close",
    BTN_CANCEL = "Cancel",
    BTN_RETRY = "Try again",
    BTN_LATER = "Later",
    BTN_DONE = "Done",
    BTN_DETAIL = "Release Details",
    SEARCH_PLACEHOLDER = "Search apps or categories…",
    CATEGORY_ALL = "All",
    CHIP_AVAILABLE = "Available",
    CHIP_INSTALLED = "Installed",
    CHIP_UPDATE = "Update",
    CHIP_UPDATE_AVAILABLE = "Update Available",
    CHIP_FEATURED = "★ FEATURED ECOSYSTEM",
    PROGRESS_DOWNLOADING = "DOWNLOADING PACKAGE",
    PROGRESS_PROCESSING = "Processing…",
    NOTICE_TIMEOUT = "Update unresponsive — please try again",
    NOTICE_CHECK_FAILED = "Failed to check for latest release",
    ACTION_CONTINUE = "Continue",
    ERROR_CATALOG_LOAD = "Unable to load catalog",
    EMPTY_APPS_TITLE = "No apps found",
    EMPTY_APPS_DESC = "Try using a different keyword or selecting another category.",
    SECTION_ECOSYSTEM = "ECOSYSTEM CATALOG",
    SECTION_ALL_APPS = "All Applications",
    DETAIL_DEVELOPER = "DEVELOPER",
    DETAIL_CATEGORY = "CATEGORY",
    DETAIL_PLATFORM = "PLATFORM",
    DETAIL_STATUS = "STATUS",
    DETAIL_VERIFICATION_NOTICE = "Packages are downloaded directly from GitHub Releases with SHA-256 checksum verification without intermediaries.",
    ABOUT_TITLE = "SYSTEM INFORMATION",
    ABOUT_THEME_TITLE = "THEME & APPEARANCE",
    ABOUT_LANGUAGE_TITLE = "LANGUAGE & REGION",
    ABOUT_DESC = "Lightweight launcher to discover, install, and update apps in the Nyawit ecosystem directly from GitHub Releases. Runs on Android, Android TV, and desktop with a unified experience.",
    ABOUT_LABEL_VERSION = "App Version",
    ABOUT_LABEL_PLATFORMS = "Target Platforms",
    ABOUT_LABEL_OWNER = "GitHub Organization",
    ABOUT_LABEL_LICENSE = "License",
    ABOUT_LABEL_FRAMEWORK = "Framework",
    ABOUT_LINKS_TITLE = "OFFICIAL LINKS",
    ABOUT_LINK_REPO = "Repository",
    ABOUT_LINK_CATALOG = "Catalog",
    ABOUT_LINK_ORG = "Organization",
    ABOUT_SECURITY_FOOTER = "All packages are verified with SHA-256 when available. No intermediary server — catalog and releases originate directly from GitHub API.",
    SETTINGS_TITLE = "LAUNCHER SETTINGS",
    SETTINGS_SUBTITLE = "Manage theme aesthetics, language, display scaling, and app preferences",
    SETTINGS_SEC_APPEARANCE = "APPEARANCE & THEME",
    SETTINGS_SEC_APPEARANCE_DESC = "Choose a visual color palette tailored to your comfort",
    SETTINGS_SEC_LANGUAGE = "LANGUAGE & REGION",
    SETTINGS_SEC_LANGUAGE_DESC = "Select interface language across the entire application",
    SETTINGS_SEC_SCALE = "DISPLAY SCALING",
    SETTINGS_SEC_SCALE_DESC = "Adjust UI component proportions to fit your screen size",
    SETTINGS_SCALE_AUTO = "Automatic (Adaptive)",
    SETTINGS_SCALE_COMPACT = "Compact (0.85x)",
    SETTINGS_SCALE_STANDARD = "Standard (1.0x)",
    SETTINGS_SCALE_LARGE = "Large (1.15x)",
    SETTINGS_SCALE_XLARGE = "Extra Large (1.30x)",
    SETTINGS_SEC_STORAGE = "STORAGE & CACHE",
    SETTINGS_SEC_STORAGE_DESC = "Manage offline local catalog cache files stored on this device",
    SETTINGS_BTN_CLEAR_CACHE = "Clear Catalog Cache",
    SETTINGS_CACHE_CLEARED = "Catalog cache cleared successfully",
    SETTINGS_SEC_UPDATES = "SYSTEM & UPDATES",
    SETTINGS_SEC_UPDATES_DESC = "Automation settings for checking newer application versions",
    SETTINGS_AUTO_CHECK_TITLE = "Check Updates on Startup",
    SETTINGS_AUTO_CHECK_DESC = "Automatically check for new catalog versions when app opens",
    SETTINGS_STORAGE_SAVED_NOTICE = "All preferences are saved automatically and persist across restarts",
    SETTINGS_BTN_OPEN = "Open Settings",
    UPDATE_CHECKING = "Checking releases",
    UPDATE_DOWNLOADING = "Downloading update",
    UPDATE_READY = "Update ready",
    UPDATE_FAILED = "Update failed",
    UPDATE_PREPARING = "Preparing launcher update.",
    UPDATE_UP_TO_DATE_BADGE = "UP TO DATE",
    UPDATE_AVAILABLE_BADGE = "UPDATE AVAILABLE",
    UPDATE_UP_TO_DATE_TITLE = "App is Up to Date",
    UPDATE_PACKAGE_LABEL = "New release package",
    statusAppsRegistered = { "$it apps registered" },
    statusUpdatesAvailable = { "$it updates available" },
    statusLauncherUpdate = { "Launcher update $it" },
    progressDownloadingPercent = { "Downloading $it%" },
    noticeUpToDate = { "App is up to date (v$it)" },
    noticeUpdatingLauncher = { "Installing launcher update $it…" },
    noticeUpdateInstalled = { "Launcher update $it installed automatically" },
    noticeUpdateFailed = { "Failed to update launcher: $it" },
    noticeDownloadingApp = { "Downloading $it…" },
    noticeOpeningInstaller = { "Opening installer for $it…" },
    noticePlatformNotSupported = { "$it does not provide an installer for this platform" },
    noticeOpenFailed = { name, detail -> if (detail != null) "Failed to open $name: $detail" else "Failed to open $name" },
    sectionCategory = { "Category $it" },
    sectionAppCount = { "$it Apps" },
    sectionUpdateCount = { "$it updates" },
    updateUpgradePrompt = { "Update to v$it?" },
    updateCurrentVersionDesc = { "Installed version v$it" },
    updateUpToDateDesc = { "Current version v$it is the latest release available." },
    updateReadyToUse = { "Version v$it is installed and ready to use." },
)

fun stringsFor(lang: AppLanguage): LanguageStrings = when (lang) {
    AppLanguage.ID -> IndonesianStrings
    AppLanguage.EN -> EnglishStrings
}

val LocalStrings = compositionLocalOf<LanguageStrings> { IndonesianStrings }

/**
 * Akses ke kumpulan string aktif baik dari Composable maupun fungsi closure.
 */
object NyawitStrings {
    var active: LanguageStrings = IndonesianStrings

    val BRAND_NAME: String get() = active.BRAND_NAME
    val BRAND_SUBTITLE: String get() = active.BRAND_SUBTITLE
    val BRAND_FULL_TITLE: String get() = active.BRAND_FULL_TITLE
    val BRAND_TAGLINE: String get() = active.BRAND_TAGLINE
    val NAV_TITLE: String get() = active.NAV_TITLE
    val NAV_APPS: String get() = active.NAV_APPS
    val NAV_SETTINGS: String get() = active.NAV_SETTINGS
    val NAV_ABOUT: String get() = active.NAV_ABOUT
    val THEME_SECTION: String get() = active.THEME_SECTION
    val THEME_TAP_TO_CHANGE: String get() = active.THEME_TAP_TO_CHANGE
    val THEME_ACTIVE_BADGE: String get() = active.THEME_ACTIVE_BADGE
    val LANGUAGE_SECTION: String get() = active.LANGUAGE_SECTION
    val LANGUAGE_TAP_TO_CHANGE: String get() = active.LANGUAGE_TAP_TO_CHANGE
    val STATUS_TITLE: String get() = active.STATUS_TITLE
    val STATUS_UP_TO_DATE: String get() = active.STATUS_UP_TO_DATE
    val BTN_CHECK_UPDATES: String get() = active.BTN_CHECK_UPDATES
    val BTN_OPEN: String get() = active.BTN_OPEN
    val BTN_OPEN_APP: String get() = active.BTN_OPEN_APP
    val BTN_INSTALL: String get() = active.BTN_INSTALL
    val BTN_INSTALL_APP: String get() = active.BTN_INSTALL_APP
    val BTN_UPDATE: String get() = active.BTN_UPDATE
    val BTN_UPDATE_NOW: String get() = active.BTN_UPDATE_NOW
    val BTN_UNINSTALL: String get() = active.BTN_UNINSTALL
    val BTN_CLOSE: String get() = active.BTN_CLOSE
    val BTN_CANCEL: String get() = active.BTN_CANCEL
    val BTN_RETRY: String get() = active.BTN_RETRY
    val BTN_LATER: String get() = active.BTN_LATER
    val BTN_DONE: String get() = active.BTN_DONE
    val BTN_DETAIL: String get() = active.BTN_DETAIL
    val SEARCH_PLACEHOLDER: String get() = active.SEARCH_PLACEHOLDER
    const val KEY_ESC: String = "ESC"
    val CATEGORY_ALL: String get() = active.CATEGORY_ALL
    const val SYMBOL_CLOSE: String = "✕"
    val CHIP_AVAILABLE: String get() = active.CHIP_AVAILABLE
    val CHIP_INSTALLED: String get() = active.CHIP_INSTALLED
    val CHIP_UPDATE: String get() = active.CHIP_UPDATE
    val CHIP_UPDATE_AVAILABLE: String get() = active.CHIP_UPDATE_AVAILABLE
    const val CHIP_UNKNOWN: String = "Unknown"
    val CHIP_FEATURED: String get() = active.CHIP_FEATURED
    const val CHIP_PINNED: String = "PINNED"
    val PROGRESS_DOWNLOADING: String get() = active.PROGRESS_DOWNLOADING
    val PROGRESS_PROCESSING: String get() = active.PROGRESS_PROCESSING
    val NOTICE_TIMEOUT: String get() = active.NOTICE_TIMEOUT
    val NOTICE_CHECK_FAILED: String get() = active.NOTICE_CHECK_FAILED
    val ACTION_CONTINUE: String get() = active.ACTION_CONTINUE
    val ERROR_CATALOG_LOAD: String get() = active.ERROR_CATALOG_LOAD
    val EMPTY_APPS_TITLE: String get() = active.EMPTY_APPS_TITLE
    val EMPTY_APPS_DESC: String get() = active.EMPTY_APPS_DESC
    val SECTION_ECOSYSTEM: String get() = active.SECTION_ECOSYSTEM
    val SECTION_ALL_APPS: String get() = active.SECTION_ALL_APPS
    val DETAIL_DEVELOPER: String get() = active.DETAIL_DEVELOPER
    val DETAIL_CATEGORY: String get() = active.DETAIL_CATEGORY
    val DETAIL_PLATFORM: String get() = active.DETAIL_PLATFORM
    val DETAIL_STATUS: String get() = active.DETAIL_STATUS
    const val DETAIL_PACKAGE_ID: String = "PACKAGE ID"
    val DETAIL_VERIFICATION_NOTICE: String get() = active.DETAIL_VERIFICATION_NOTICE
    val ABOUT_TITLE: String get() = active.ABOUT_TITLE
    val ABOUT_THEME_TITLE: String get() = active.ABOUT_THEME_TITLE
    val ABOUT_LANGUAGE_TITLE: String get() = active.ABOUT_LANGUAGE_TITLE
    val ABOUT_DESC: String get() = active.ABOUT_DESC
    val ABOUT_LABEL_VERSION: String get() = active.ABOUT_LABEL_VERSION
    val ABOUT_LABEL_PLATFORMS: String get() = active.ABOUT_LABEL_PLATFORMS
    const val ABOUT_LABEL_IDENTIFIER: String = "Identifier"
    val ABOUT_LABEL_OWNER: String get() = active.ABOUT_LABEL_OWNER
    val ABOUT_LABEL_LICENSE: String get() = active.ABOUT_LABEL_LICENSE
    val ABOUT_LABEL_FRAMEWORK: String get() = active.ABOUT_LABEL_FRAMEWORK
    const val ABOUT_VAL_PLATFORMS: String = "Android · Android TV · Desktop JVM"
    const val ABOUT_VAL_LICENSE: String = "GPL-3.0"
    const val ABOUT_VAL_FRAMEWORK: String = "Kotlin Multiplatform · Compose Multiplatform"
    val ABOUT_LINKS_TITLE: String get() = active.ABOUT_LINKS_TITLE
    val ABOUT_LINK_REPO: String get() = active.ABOUT_LINK_REPO
    val ABOUT_LINK_CATALOG: String get() = active.ABOUT_LINK_CATALOG
    val ABOUT_LINK_ORG: String get() = active.ABOUT_LINK_ORG
    val ABOUT_SECURITY_FOOTER: String get() = active.ABOUT_SECURITY_FOOTER

    val SETTINGS_TITLE: String get() = active.SETTINGS_TITLE
    val SETTINGS_SUBTITLE: String get() = active.SETTINGS_SUBTITLE
    val SETTINGS_SEC_APPEARANCE: String get() = active.SETTINGS_SEC_APPEARANCE
    val SETTINGS_SEC_APPEARANCE_DESC: String get() = active.SETTINGS_SEC_APPEARANCE_DESC
    val SETTINGS_SEC_LANGUAGE: String get() = active.SETTINGS_SEC_LANGUAGE
    val SETTINGS_SEC_LANGUAGE_DESC: String get() = active.SETTINGS_SEC_LANGUAGE_DESC
    val SETTINGS_SEC_SCALE: String get() = active.SETTINGS_SEC_SCALE
    val SETTINGS_SEC_SCALE_DESC: String get() = active.SETTINGS_SEC_SCALE_DESC
    val SETTINGS_SCALE_AUTO: String get() = active.SETTINGS_SCALE_AUTO
    val SETTINGS_SCALE_COMPACT: String get() = active.SETTINGS_SCALE_COMPACT
    val SETTINGS_SCALE_STANDARD: String get() = active.SETTINGS_SCALE_STANDARD
    val SETTINGS_SCALE_LARGE: String get() = active.SETTINGS_SCALE_LARGE
    val SETTINGS_SCALE_XLARGE: String get() = active.SETTINGS_SCALE_XLARGE
    val SETTINGS_SEC_STORAGE: String get() = active.SETTINGS_SEC_STORAGE
    val SETTINGS_SEC_STORAGE_DESC: String get() = active.SETTINGS_SEC_STORAGE_DESC
    val SETTINGS_BTN_CLEAR_CACHE: String get() = active.SETTINGS_BTN_CLEAR_CACHE
    val SETTINGS_CACHE_CLEARED: String get() = active.SETTINGS_CACHE_CLEARED
    val SETTINGS_SEC_UPDATES: String get() = active.SETTINGS_SEC_UPDATES
    val SETTINGS_SEC_UPDATES_DESC: String get() = active.SETTINGS_SEC_UPDATES_DESC
    val SETTINGS_AUTO_CHECK_TITLE: String get() = active.SETTINGS_AUTO_CHECK_TITLE
    val SETTINGS_AUTO_CHECK_DESC: String get() = active.SETTINGS_AUTO_CHECK_DESC
    val SETTINGS_STORAGE_SAVED_NOTICE: String get() = active.SETTINGS_STORAGE_SAVED_NOTICE
    val SETTINGS_BTN_OPEN: String get() = active.SETTINGS_BTN_OPEN

    val UPDATE_CHECKING: String get() = active.UPDATE_CHECKING
    val UPDATE_DOWNLOADING: String get() = active.UPDATE_DOWNLOADING
    val UPDATE_READY: String get() = active.UPDATE_READY
    val UPDATE_FAILED: String get() = active.UPDATE_FAILED
    val UPDATE_PREPARING: String get() = active.UPDATE_PREPARING
    val UPDATE_UP_TO_DATE_BADGE: String get() = active.UPDATE_UP_TO_DATE_BADGE
    val UPDATE_AVAILABLE_BADGE: String get() = active.UPDATE_AVAILABLE_BADGE
    val UPDATE_UP_TO_DATE_TITLE: String get() = active.UPDATE_UP_TO_DATE_TITLE
    val UPDATE_PACKAGE_LABEL: String get() = active.UPDATE_PACKAGE_LABEL

    fun statusAppsRegistered(total: Int): String = active.statusAppsRegistered(total)
    fun statusUpdatesAvailable(count: Int): String = active.statusUpdatesAvailable(count)
    fun statusLauncherUpdate(tag: String): String = active.statusLauncherUpdate(tag)
    fun progressDownloadingPercent(pct: Int): String = active.progressDownloadingPercent(pct)
    fun noticeUpToDate(version: String): String = active.noticeUpToDate(version)
    fun noticeUpdatingLauncher(tag: String): String = active.noticeUpdatingLauncher(tag)
    fun noticeUpdateInstalled(tag: String): String = active.noticeUpdateInstalled(tag)
    fun noticeUpdateFailed(msg: String): String = active.noticeUpdateFailed(msg)
    fun noticeDownloadingApp(name: String): String = active.noticeDownloadingApp(name)
    fun noticeOpeningInstaller(name: String): String = active.noticeOpeningInstaller(name)
    fun noticePlatformNotSupported(name: String): String = active.noticePlatformNotSupported(name)
    fun noticeOpenFailed(name: String, detail: String?): String = active.noticeOpenFailed(name, detail)
    fun sectionCategory(name: String): String = active.sectionCategory(name)
    fun sectionAppCount(count: Int): String = active.sectionAppCount(count)
    fun sectionUpdateCount(count: Int): String = active.sectionUpdateCount(count)
    fun updateUpgradePrompt(tag: String): String = active.updateUpgradePrompt(tag)
    fun updateCurrentVersionDesc(ver: String): String = active.updateCurrentVersionDesc(ver)
    fun updateUpToDateDesc(ver: String): String = active.updateUpToDateDesc(ver)
    fun updateReadyToUse(ver: String): String = active.updateReadyToUse(ver)
}
