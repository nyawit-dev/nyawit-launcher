# Core functions & alur lintas platform

Dokumen ini adalah hasil audit fungsi inti launcher setelah perbaikan. Status: ✅ berfungsi, ⚠️ berfungsi dengan syarat, ❌ tidak didukung.

## Matriks fungsi

| Fungsi inti | Android | Desktop (Win/Linux/Mac) | Android TV |
|---|---|---|---|
| Memuat katalog (grup `owner + repo`) | ✅ GitHub → cache lokal | ✅ GitHub → cache lokal | ✅ |
| Filter platform & kategori & pencarian | ✅ | ✅ | ✅ |
| Deteksi aplikasi terpasang | ✅ resolve package dari label/ID | ✅ registry JAR + installer native | ✅ |
| Cek versi/update aplikasi | ✅ `versionName` | ✅ registry / `dpkg` / registry Windows / `Info.plist` | ✅ |
| Pasang dari katalog | ✅ `.apk` → installer Android | ✅ `.msi/.exe`, `.deb/.rpm`, `.dmg/.pkg` → OS | ✅ |
| Buka aplikasi terpasang | ✅ `getLaunchIntentForPackage` | ✅ JAR registry / Start Menu / `.desktop` / `.app` | ✅ |
| Update launcher | ✅ dialog konfirmasi → `.apk` | ✅ dialog konfirmasi → installer native | ✅ |
| Fokus D-pad TV | — | ✅ `tvFocusHighlight` | ✅ |
| Layout responsif | ✅ phone/tablet | ✅ tablet/desktop | ✅ 3 kolom |

⚠️ Syarat: aplikasi harus merilis installer untuk platform yang bersangkutan. `Nyawit Music` saat ini hanya memiliki `.apk`, jadi tidak bisa dipasang di desktop.

## Alur 1 — Memuat katalog

```
[Buka launcher]
   ↓
1. Ambil catalog.json dari GitHub raw (online)
   ├─ Berhasil → simpan ke cache lokal
   │             Desktop: ~/.nyawit-launcher/cache/catalog.json
   │             Android: <filesDir>/catalog.json
   └─ Gagal → baca cache lokal
              ├─ ada → pakai cache (offline tetap berfungsi)
              └─ tidak ada → file lokal project (dev) → katalog bawaan aplikasi
   ↓
Parsing CatalogConfig { sources: [ grup owner+repo | entri type+name ] }
   ↓
resolveSources(): grup diubah jadi daftar sumber dengan owner/url/detail
   ↓
Entri dengan detail dibangun langsung dari config; user/org diambil dari GitHub API
   ↓
Terapkan detail (label, description, category, platforms, packageName, accent, pinned)
   ↓
Urutan: pinned → pemilik utama → nama
```

Jika semua gagal: `fallbackApps()` dipakai.

## Alur 2 — Status aplikasi

```
installedVersion(packageName, displayName)
   ├─ Desktop: registry ~/.nyawit-launcher/installed/<key>.txt
   │            → ada: versi + path JAR
   │            → tidak ada: cari installer native per OS
   └─ Android: cocokkan packageName persis → label aplikasi → token nama

status = NOT_INSTALLED | INSTALLED | UPDATE_AVAILABLE
UPDATE_AVAILABLE hanya jika tag rilis lebih baru dari versi terpasang
```

## Alur 3 — Pasang aplikasi dari katalog

```
[Klik Pasang / Perbarui]
   ↓
UpdateRepository.latestFor(fullName)
   ↓
pickAsset(): .msi/.exe | .deb/.rpm | .dmg/.pkg | .apk
   ↓
Unduh ke ~/.nyawit-launcher/downloads/ (progress + tombol Batal)
   ↓
Verifikasi checksum SHA-256 bila file .sha256 tersedia
   ↓
Android → FileProvider + ACTION_VIEW → installer Android
Desktop → msiexec / xdg-open / open → installer sistem operasi
```

Tidak ada `exitProcess` untuk pemasangan aplikasi biasa.

## Alur 4 — Update launcher

```
[Klik Cek update]
   ↓
GET releases/latest
   ↓
Ada rilis baru + installer tersedia?
   ├─ Tidak → notifikasi error
   └─ Ya → DIALOG KONFIRMASI (versi saat ini → versi baru + nama installer)
          ├─ Nanti → tutup, tidak mengubah apa pun
          └─ Perbarui → dialog progres → unduh → verifikasi → installer native → restart manual
```

## Alur 5 — Rilis CI/CD

```
Actions → Release → input tag vX.Y.Z
   ↓
validate: format tag + tag belum ada
   ↓
android: assembleRelease + apksigner + cek versionCode/versionName
desktop: matrix linux / windows / mac-x64 / mac-arm64
   ↓
native installer saja: .apk, .msi, .deb, .rpm, .dmg
   ↓
checksum .sha256 per artifact
   ↓
publish GitHub Release (hanya installer native; tidak ada JAR)
```

## Keterbatasan yang masih ada

1. `Nyawit Music` belum punya installer desktop di GitHub Releases.
2. Keystore Android masih di repo dengan password hardcode (keputusan produk, agar signature lama tetap kompatibel).
3. Checksum dilewati bila file `.sha256` tidak bisa diambil (termasuk gangguan jaringan).
4. GitHub API tanpa token dibatasi ~60 request/jam; belum ada cache.
5. Belum ada test otomatis.
