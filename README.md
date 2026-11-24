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


## Katalog aplikasi

Launcher mengambil daftar aplikasi dari GitHub tanpa server. Sumber diatur di [`catalog.json`](catalog.json):

```json
{
  "sources": [
    { "type": "user", "name": "nyawit-dev" },
    { "type": "org", "name": "organisasi-lain" },
    { "type": "repo", "name": "owner/repo-aplikasi" }
  ]
}
```

- `user` / `org` = semua repo public dari akun atau organisasi
- `repo` = satu repo tertentu
- Repo `nyawit-dev` selalu diurutkan lebih dulu
- Hanya repo yang terdeteksi sebagai aplikasi yang ditampilkan, dan hanya untuk platform yang didukungnya (Android, Android TV, Desktop)

Edit `catalog.json` di `main`, lalu launcher akan membaca otomatis saat dibuka.


## Menjalankan di JVM / Desktop

Dua cara:

1. **JAR portabel (paling gampang)**
   ```
   java -jar nyawit-launcher-linux-x64-1.2.6.jar
   ```
2. **Installer native** dari rilis (`.deb`, `.msi`, `.dmg`)

Setelah install/update lewat launcher, status & jalur JAR disimpan di:
```
~/.nyawit-launcher/installed/
```
Jika ikon menu tidak muncul, cari biner `nyawit-launcher` atau jalankan JAR-nya — JAR selalu bisa dijalankan mandiri.
