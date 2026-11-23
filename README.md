# Nyawit Launcher

Launcher Kotlin Multiplatform dengan Compose Multiplatform untuk **Android, Android TV, dan JVM/desktop**.

Tanpa server sendiri: katalog aplikasi diambil langsung dari GitHub REST API.

## Sumber katalog
- Profil organisasi / semua aplikasi: https://github.com/storytold
- Launcher & katalog: https://github.com/storytold/craft-launcher
- Aset katalog & pratinjau: https://github.com/storytold/craft-launcher-assets

## Modul
- `shared` — UI Compose bersama
- `androidApp` — activity Android + launcher/Leanback
- `desktopApp` — window JVM

## Build
Semua build berjalan di GitHub Actions:
```bash
./gradlew :androidApp:assembleDebug :desktopApp:jar
```

Lisensi: GPL-3.0.
