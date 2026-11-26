# Daftar aset gambar

Status: ✅ lengkap · 🔧 perlu dibuat · ⚪ opsional

## A. Brand & identitas

| File | Ukuran | Format | Fungsi | Status |
|---|---|---|---|---|
| `assets/app-icon.png` | 1024×1024 | PNG transparan | Ikon utama, sumber untuk semua platform | ✅ |
| `assets/tv-banner.png` | 1280×720 | PNG | Banner Android TV, header hero | ✅ |
| `assets/logo.png` | 512×128 | PNG | Banner lama (wordmark + ikon) | ✅ |
| `assets/logo-new.png` | 1024×256 | PNG transparan | Wordmark baru untuk README/header | ✅ |
| `assets/brand-mark.svg` | vektor | SVG | Logo "N" sumber tunggal | ✅ |

## B. Ikon aplikasi di katalog

Ikon per aplikasi (PNG transparan 512×512, tajam untuk dialog maupun kartu katalog):

| File | Aplikasi | Status |
|---|---|---|
| `assets/app-icons/nyawit-launcher.png` | Nyawit Launcher | ✅ |
| `assets/app-icons/nyawit-music.png` | Nyawit Music | ✅ |
| `assets/app-icons/garis-arcade.png` | Garis Arcade | ✅ |
| `assets/app-icons/garis_game.png` | Garis Game | ✅ |
| `assets/app-icons/monopoly-kmp.png` | Monopoly KMP | ✅ |

> Catatan: menampilkan gambar di kartu memerlukan dukungan pemuat gambar (mis. Coil KMP) atau field `icon` di `catalog.json` yang berisi URL.

## C. UI ilustrasi

| File | Ukuran | Fungsi | Status |
|---|---|---|---|
| `assets/ui/empty-state.png` | 400×300 | Ilustrasi saat daftar kosong (sekarang digambar Canvas) | ✅ |
| `assets/ui/update-illustration.png` | 256×256 | Ilustrasi dialog konfirmasi update | ✅ |
| `assets/ui/hero-desktop.png` | 1600×600 | Banner area konten desktop/tablet | ✅ |
| `assets/ui/tv-focus-frame.png` | 320×180 | Bingkai fokus TV (opsional, sekarang via `tvFocusHighlight`) | ✅ |

## D. Google Play / Android

| File | Ukuran | Fungsi | Status |
|---|---|---|---|
| `play/ic_launcher-512.png` | 512×512 | Ikon Play Store | ✅ |
| `play/feature-graphic.png` | 1024×500 | Feature graphic | ✅ |
| `play/screenshot-phone-1..4.png` | 1080×1920 | Screenshot ponsel (4 layar) | ✅ |
| `play/screenshot-tablet-1..2.png` | 1920×1200 | Screenshot tablet 7″-10″ (2 layar) | ✅ |
| `play/screenshot-tv-1..2.png` | 1920×1080 | Screenshot Android TV (2 layar) | ✅ |
| `play/promo-graphic.png` | 1280×720 | Promo graphic | ✅ |

## E. GitHub & dokumentasi

| File | Ukuran | Fungsi | Status |
|---|---|---|---|
| `docs/images/social-preview.png` | 1280×640 | Open Graph saat link repo dibagikan | ✅ |
| `docs/images/readme-hero.png` | 1280×400 | Header README | ✅ |
| `docs/images/release-windows.png` | 1920×1080 | Screenshot rilis Windows (.msi) | ✅ |
| `docs/images/release-linux.png` | 1920×1080 | Screenshot rilis Linux (.deb/.rpm) | ✅ |
| `docs/images/release-macos.png` | 1920×1080 | Screenshot rilis macOS (.dmg) | ✅ |

## F. Ikon platform (sudah lengkap)

| File | Ukuran/format | Status |
|---|---|---|
| `desktopApp/icons/windows/nyawit.ico` | multi-ukuran 16–256 | ✅ |
| `desktopApp/icons/linux/nyawit.png` | 512×512 | ✅ |
| `desktopApp/icons/mac/nyawit.icns` | 128/256/512 | ✅ |
| `androidApp/.../tv_banner.png` | 1280×720 | ✅ |
| `ic_launcher` adaptive (XML) | vektor | ✅ |

## G. Wizard installer Windows

| File | Ukuran | Fungsi | Status |
|---|---|---|---|
| `installer/windows/wizard-image.bmp` | 336×643 | Gambar panel kiri wizard Inno Setup | ✅ |
| `installer/windows/wizard-small-image.png` | 294×294 | Ikon kecil di kanan atas wizard | ✅ |

Keduanya adalah aset statis yang disiapkan manual dan dipakai oleh
`installer/windows/nyawit-launcher.iss`. Workflow Release **tidak** mengubah keduanya, jadi
perubahan gambar cukup dengan mengganti file di folder tersebut.

`wizard-image.bmp` wajib berformat BMP 24-bit dengan rasio 164:314; ukuran 336×643 dipakai
agar tajam di DPI tinggi. `wizard-small-image.png` harus persegi.

## Script Generator

Aset otomatis dihasilkan ulang melalui script di folder `scripts/`:
- `scripts/render_helper.js` — Helper rendering headless browser beresolusi tinggi. Mencari Edge/Chrome otomatis; bisa ditimpa dengan `NYAWIT_BROWSER_PATH`.
- `scripts/generate_brand_and_icons.js` — Wordmark baru, ikon aplikasi katalog, dan UI ilustrasi.
- `scripts/process_icon.js` — Ikon launcher bersih untuk Android/desktop dan banner TV dari `ic_launcher-playstore.png`.
- `scripts/generate_store_and_docs.js` — Ikon Play Store, feature graphic, promo, dan social preview.
- `scripts/generate_screenshots.js` — Seluruh screenshot Phone, Tablet, Android TV, dan mockup desktop rilis (Windows, Linux, macOS).
